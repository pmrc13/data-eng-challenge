package org.busstreamingapp

import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.busstreamingapp.models.{Alert, VehicleDataEvent}
import org.busstreamingapp.processors.CumAvgSpeedAlertProcessFunction
import org.busstreamingapp.serdes.{AlertSerializationSchema, VehicleDataEventDeserializationSchema}
import org.slf4j.LoggerFactory

import java.util.Properties

object BusSpeedMonitoringStream {
  def main(args: Array[String]): Unit = {

    val logger = LoggerFactory.getLogger(BusSpeedMonitoringStream.getClass)
    logger.info("Starting BusSpeedMonitoringStream")

    val alertInterval: Long = 30000
    val speedLimit: Double = 20

    val kafkaSourceProperties = new Properties()
    kafkaSourceProperties.setProperty("bootstrap.servers", "localhost:49092")

    val kafkaSinkProperties = new Properties()
    kafkaSinkProperties.setProperty("bootstrap.servers", "localhost:49092")

    // set up the streaming execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // define the Kafka source that will receive the events
    val streamWithKafkaSource: DataStream[VehicleDataEvent] =
      env
        .addSource(
          new FlinkKafkaConsumer[VehicleDataEvent](
            "source",
            new VehicleDataEventDeserializationSchema,
            kafkaSourceProperties
          )
        )

    // apply the process function onto a keyed stream
    val processedStream: DataStream[Alert] = streamWithKafkaSource
      .keyBy(_.vehicle_id)
      .process(new CumAvgSpeedAlertProcessFunction(alertInterval, speedLimit))

    // define the Kafka sink that will output the alert
    val kafkaSink: FlinkKafkaProducer[Alert] = new FlinkKafkaProducer[Alert](
      "sink", // target topic
      new AlertSerializationSchema,
      kafkaSinkProperties
    )

    processedStream.addSink(kafkaSink)

    processedStream.print()

    env.execute("Cumulative average speed monitoring stream")
  }
}
