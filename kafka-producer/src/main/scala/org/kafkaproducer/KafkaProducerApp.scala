package org.kafkaproducer

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.{LoggerFactory}

import java.util.Properties

object KafkaProducerApp {

  val logger = LoggerFactory.getLogger(KafkaProducerApp.getClass)

  def main(args: Array[String]): Unit = {
    val kafkaProducerProps: Properties = {
      logger.info("Processing CSV files")

      val props = new Properties()
      props.put("bootstrap.servers", "localhost:49092")
      props.put("acks", "all")
      props.put("key.serializer", classOf[StringSerializer].getName)
      props.put("value.serializer", classOf[StringSerializer].getName)
      props
    }

    val inputCSVFileList: List[String] = Utils.getListOfFiles("./src/main/resources")

    val producer = new KafkaProducer[String, String](kafkaProducerProps)

    inputCSVFileList.foreach(file => sendKafkaEvents(producer, file))

    producer.close()
  }

  private def sendKafkaEvents(producer: KafkaProducer[String, String], file: String): Unit = {
    var lastTimestamp: Long = 1352160000000000L // beginning of dataset

    val bufferedSource = io.Source.fromFile(file)
    for (line <- bufferedSource.getLines) {
      val cols = line.split(",").map(_.trim)
      val currentTimestamp = cols(0)
      val json = s"""{
                    | "timestamp": "$currentTimestamp",
                    | "line_id": "${cols(1)}",
                    | "direction": "${cols(2)}",
                    | "journey_pattern_id": "${cols(3)}",
                    | "time_frame": "${cols(4)}",
                    | "vehicle_journey_id": "${cols(5)}",
                    | "operator": "${cols(6)}",
                    | "congestion": ${cols(7)},
                    | "lon": ${cols(8)},
                    | "lat": ${cols(9)},
                    | "delay": ${cols(10)},
                    | "block_id": "${cols(11)}",
                    | "vehicle_id": "${cols(12)}",
                    | "stop_id": "${cols(13)}",
                    | "at_stop": ${cols(14)}
                    | }""".stripMargin

      producer.send(new ProducerRecord[String, String]("source", json))

      val currentTimestampLong = currentTimestamp.toLong

      val timeDiffMilli = (currentTimestampLong - lastTimestamp) / 1000
      lastTimestamp = currentTimestampLong

      logger.info("Message sent")

      Thread.sleep(timeDiffMilli)
    }
    bufferedSource.close()
  }
}
