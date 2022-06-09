package org.busstreamingapp

import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.busstreamingapp.models.Alert

import java.util
import java.util.Collections

class CollectSink extends SinkFunction[Alert] {

  override def invoke(value: Alert, context: SinkFunction.Context): Unit = {
    CollectSink.values.add(value)
  }
}

object CollectSink {
  val values: util.List[Alert] = Collections.synchronizedList(new util.ArrayList())
}
