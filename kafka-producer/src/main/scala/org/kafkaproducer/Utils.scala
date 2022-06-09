package org.kafkaproducer
import java.io.File

object Utils {
  def getListOfFiles(dir: String): List[String] = {
    val file = new File(dir)
    file.listFiles
      .filter(_.isFile)
      .filter(_.getName.endsWith("csv"))
      .sortBy(_.getName)
      .map(_.getPath)
      .toList
  }
}
