package org.busstreamingapp.serdes

import io.circe._
import io.circe.generic.semiauto._
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala._
import org.busstreamingapp.models.VehicleDataEvent

import java.nio.charset.StandardCharsets
import scala.util.Try

class VehicleDataEventDeserializationSchema(implicit decode: Decoder[VehicleDataEvent])
    extends DeserializationSchema[VehicleDataEvent] {
  override def deserialize(message: Array[Byte]): VehicleDataEvent = {
    Option(message).fold(null.asInstanceOf[VehicleDataEvent]) { contents =>
      (for {
        str <- Try(new String(contents, StandardCharsets.UTF_8)).toEither
        decoded <- parser.decode(str)
      } yield decoded) match {
        case Left(error) =>
          throw new Exception(s"Deserialization error: $error.")
        case Right(decodedEvent) =>
          decodedEvent
      }
    }
  }

  override def isEndOfStream(nextElement: VehicleDataEvent): Boolean = false

  def getProducedType: TypeInformation[VehicleDataEvent] = TypeInformation.of(classOf[VehicleDataEvent])
}
