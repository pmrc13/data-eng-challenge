package org.busstreamingapp.serdes

import io.circe.syntax.EncoderOps
import org.apache.flink.api.common.serialization.SerializationSchema
import org.busstreamingapp.models.Alert

import java.nio.charset.StandardCharsets

class AlertSerializationSchema extends SerializationSchema[Alert] {

  override def serialize(record: Alert): Array[Byte] = {
    record.asJson.noSpaces.getBytes(StandardCharsets.UTF_8)
  }
}
