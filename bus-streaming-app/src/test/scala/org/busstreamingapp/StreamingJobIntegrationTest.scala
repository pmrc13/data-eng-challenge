package org.busstreamingapp

import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.datastream.{DataStream, DataStreamSource}
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.busstreamingapp.models.{Alert, VehicleDataEvent}
import org.busstreamingapp.processors.CumAvgSpeedAlertProcessFunction
import org.busstreamingapp.utils.TestAlertEvents
import org.junit.ClassRule
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.concurrent.Eventually.{eventually, interval}
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.funsuite.AnyFunSuite

import java.util.concurrent.{Executors, ThreadPoolExecutor}

class StreamingJobIntegrationTest extends AnyFunSuite with Matchers with BeforeAndAfter {

  @ClassRule
  val flinkCluster = new MiniClusterWithClientResource(
    new MiniClusterResourceConfiguration.Builder()
      .setNumberSlotsPerTaskManager(2)
      .setNumberTaskManagers(1)
      .build
  )

  val executor: ThreadPoolExecutor = Executors.newFixedThreadPool(2).asInstanceOf[ThreadPoolExecutor]

  before {
    flinkCluster.before()
  }

  after {
    flinkCluster.after()
  }

  test("Emit an alert for a bus having a cumulative moving average higher than the limit") {

    val task = executor.submit(new Runnable() {
      override def run(): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        env.setParallelism(1)

        CollectSink.values.clear()

        val input: DataStreamSource[VehicleDataEvent] = env.fromElements(
          TestAlertEvents.vehicleHavingCumAvgSpeedHigherThanLimitEvents.head,
          TestAlertEvents.vehicleHavingCumAvgSpeedHigherThanLimitEvents.last
        )

        val result: DataStream[Alert] = input
          .keyBy(_.vehicle_id)
          .process(new CumAvgSpeedAlertProcessFunction(5000, 20))

        result.addSink(new CollectSink())

        result.print()

        env.execute()
      }
    })

    eventually(Timeout(15 seconds), interval(2 seconds)) {
      CollectSink.values.size() should equal(1)
    }

    task.cancel(true)
  }

  test("Do not emit an alert for a bus having a cumulative moving average lower than the limit") {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.setParallelism(1)

    CollectSink.values.clear()

    val input: DataStreamSource[VehicleDataEvent] = env.fromElements(
      TestAlertEvents.vehicleHavingCumAvgSpeedLowerThanLimitEvents.head,
      TestAlertEvents.vehicleHavingCumAvgSpeedLowerThanLimitEvents.last
    )

    val result: DataStream[Alert] = input
      .keyBy(_.vehicle_id)
      .process(new CumAvgSpeedAlertProcessFunction(5000, 20))

    result.addSink(new CollectSink())

    result.print()

    env.execute()

    eventually(Timeout(10 seconds), interval(2 seconds)) {
      CollectSink.values.size() should equal(0)
    }
  }
}
