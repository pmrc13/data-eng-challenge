package org.busstreamingapp.models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation

case class VehicleDataEvent(
    timestamp: Long,
    line_id: String,
    direction: String,
    journey_pattern_id: String,
    time_frame: String,
    vehicle_journey_id: String,
    operator: String,
    congestion: Double,
    lon: Double,
    lat: Double,
    delay: Double,
    block_id: String,
    vehicle_id: String,
    stop_id: String,
    at_stop: Int
) extends Serializable

object VehicleDataEvent {
  implicit val VehicleDataEventDecoder: Decoder[VehicleDataEvent] = deriveDecoder
  implicit val VehicleDataEventEncoder: Encoder[VehicleDataEvent] = deriveEncoder
  implicit val typeInfo: TypeInformation[VehicleDataEvent] = createTypeInformation[VehicleDataEvent]
}
