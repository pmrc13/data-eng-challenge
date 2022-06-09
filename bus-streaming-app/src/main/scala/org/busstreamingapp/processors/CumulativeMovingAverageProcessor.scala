package org.busstreamingapp.processors

import org.busstreamingapp.models.{DataPerVehicleStateEntry, VehicleDataEvent}

class CumulativeMovingAverageProcessor(speedLimit: Double) extends DataPerVehicleAlertProcessor {

  override def updateDataPerVehicleAlert(
      currentDataPerVehicleAlertValue: DataPerVehicleStateEntry,
      newVehicleDataEvent: VehicleDataEvent
  ): DataPerVehicleStateEntry = {
    val currentCumulativeMovingAverage: Double = currentDataPerVehicleAlertValue.currentCumAvg
    val currentEventsByVehicleCount: Int = currentDataPerVehicleAlertValue.count

    val newCumulativeMovingAverage: Double = computeCumulativeMovingAverage(
      currentCumulativeMovingAverage,
      currentEventsByVehicleCount,
      currentDataPerVehicleAlertValue.currentEvent,
      newVehicleDataEvent
    )

    val newTimestampStart = calculateNewTimestampStart(
      newCumulativeMovingAverage,
      currentCumulativeMovingAverage,
      newVehicleDataEvent.timestamp,
      currentDataPerVehicleAlertValue.timestampStart
    )

    val newTimestampFinish = calculateNewTimestampFinish(
      newCumulativeMovingAverage,
      newVehicleDataEvent.timestamp
    )

    DataPerVehicleStateEntry(
      newVehicleDataEvent.vehicle_id,
      newCumulativeMovingAverage,
      currentEventsByVehicleCount + 1,
      newVehicleDataEvent,
      newTimestampStart,
      newTimestampFinish
    )
  }

  private def calculateNewTimestampStart(
      newCumulativeMovingAverage: Double,
      currentCumulativeMovingAverage: Double,
      newEventTimestamp: Long,
      currentTimestampStart: Long
  ): Long = {
    if (newCumulativeMovingAverage >= speedLimit) {
      if (currentCumulativeMovingAverage < speedLimit) {
        newEventTimestamp
      } else { currentTimestampStart }
    } else { 0 }
  }

  private def calculateNewTimestampFinish(
      newCumulativeMovingAverage: Double,
      newEventTimestamp: Long
  ): Long = {
    if (newCumulativeMovingAverage >= speedLimit) {
      newEventTimestamp
    } else { 0 }
  }

  private def computeCumulativeMovingAverage(
      currentCumulativeMovingAverage: Double,
      currentEventsByVehicleCount: Int,
      currentEvent: VehicleDataEvent,
      newVehicleDataEvent: VehicleDataEvent
  ): Double = {
    val dDistance: Double = calculateDistanceInKmBetweenEarthCoordinates(
      currentEvent.lat,
      currentEvent.lon,
      newVehicleDataEvent.lat,
      newVehicleDataEvent.lon
    )

    // microseconds -> seconds -> hours
    val dTime: Double =
      (newVehicleDataEvent.timestamp.asInstanceOf[Double] - currentEvent.timestamp
        .asInstanceOf[Double]) / 1000000 / 3600

    val newSpeed = dDistance / dTime

    currentCumulativeMovingAverage + ((newSpeed - currentCumulativeMovingAverage) / (currentEventsByVehicleCount + 1))
  }

  private def calculateDistanceInKmBetweenEarthCoordinates(
      lat1: Double,
      lon1: Double,
      lat2: Double,
      lon2: Double
  ): Double = {
    val earthRadiusKm = 6371

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val lat1InRadians = Math.toRadians(lat1)
    val lat2InRadians = Math.toRadians(lat2)

    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1InRadians) * Math.cos(lat2InRadians)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    earthRadiusKm * c
  }
}
