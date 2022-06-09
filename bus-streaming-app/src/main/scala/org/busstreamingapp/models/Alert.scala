package org.busstreamingapp.models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class Alert(
    timestampStart: Long,
    timestampFinish: Long,
    vehicleId: String,
    eventTimestamp: Long,
    cumAvgSpeed: Double
)

object Alert {
  implicit val AlertDecoder: Decoder[Alert] = deriveDecoder
  implicit val AlertEncoder: Encoder[Alert] = deriveEncoder
}
