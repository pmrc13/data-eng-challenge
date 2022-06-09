package org.busstreamingapp.processors

import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector
import org.busstreamingapp.models.{Alert, DataPerVehicleStateEntry, VehicleDataEvent}
import org.slf4j.LoggerFactory

import java.time.Instant

/**
  * The implementation of the ProcessFunction that processes the incoming GPS information per vehicle
  */
class CumAvgSpeedAlertProcessFunction(alertInterval: Long, speedLimit: Double)
    extends KeyedProcessFunction[String, VehicleDataEvent, Alert] {

  val logger = LoggerFactory.getLogger(this.getClass)

  lazy val state: ValueState[DataPerVehicleStateEntry] = getRuntimeContext
    .getState(new ValueStateDescriptor[DataPerVehicleStateEntry]("vehicle_state", classOf[DataPerVehicleStateEntry]))

  override def processElement(
      value: VehicleDataEvent,
      ctx: KeyedProcessFunction[String, VehicleDataEvent, Alert]#Context,
      out: Collector[Alert]
  ): Unit = {

    logger.info(s"Processing element with key ${ctx.getCurrentKey}")

    // TODO
    //val isValidEvent = preProcessElement()

    val vehicleData: Option[DataPerVehicleStateEntry] = Option(state.value())

    vehicleData match {
      case Some(data) =>
        val dataPerVehicleAlertProcessor = new CumulativeMovingAverageProcessor(speedLimit)
        val updatedDataPerVehicleAlert: DataPerVehicleStateEntry =
          dataPerVehicleAlertProcessor.updateDataPerVehicleAlert(data, value)
        state.update(updatedDataPerVehicleAlert)
      case None =>
        ctx.timerService.registerProcessingTimeTimer(ctx.timestamp() + alertInterval)
        state.update(DataPerVehicleStateEntry(value.vehicle_id, 0, 0, value))
    }
  }

  override def onTimer(
      timestamp: Long,
      ctx: KeyedProcessFunction[String, VehicleDataEvent, Alert]#OnTimerContext,
      out: Collector[Alert]
  ): Unit = {

    val vehicleStateEntry: DataPerVehicleStateEntry = state.value()

    logger.info(
      s"Triggering onTimer for vehicle with id ${vehicleStateEntry.vehicleID} after ${alertInterval / 1000} seconds"
    )

    val currentTime = Instant.now.toEpochMilli

    // send an alert to the Kafka sink if the current cumulative average speed is equal or above the speed limit
    if (vehicleStateEntry.currentCumAvg >= speedLimit)
      out.collect(
        Alert(
          vehicleStateEntry.timestampStart,
          vehicleStateEntry.timestampFinish,
          vehicleStateEntry.vehicleID,
          currentTime / 1000,
          vehicleStateEntry.currentCumAvg
        )
      )

    // always register a new timer every 30 seconds
    ctx.timerService.registerProcessingTimeTimer(currentTime + alertInterval)
  }
}
