package org.busstreamingapp.utils

import org.busstreamingapp.models.VehicleDataEvent

object TestAlertEvents {
  // dDistance: 0.027798731661272415 km
  // dTime: 2.777777777777778E-4 hours
  // cumAvgSpeed: 100.0754339805807 km/h
  val vehicleHavingCumAvgSpeedHigherThanLimitEvents = List(
    new VehicleDataEvent(
      timestamp = 1352160008000000L,
      line_id = "line_id",
      direction = "0",
      journey_pattern_id = "journey_pattern_id",
      time_frame = "2012-11-05",
      vehicle_journey_id = "vehicle_journey_id",
      operator = "D2",
      congestion = 0,
      lon = -7.0,
      lat = 53.287000,
      delay = 0.0,
      block_id = "block_id",
      vehicle_id = "12345",
      stop_id = "stop_id",
      at_stop = 0
    ),
    new VehicleDataEvent(
      timestamp = 1352160009000000L,
      line_id = "line_id",
      direction = "0",
      journey_pattern_id = "journey_pattern_id",
      time_frame = "2012-11-05",
      vehicle_journey_id = "vehicle_journey_id",
      operator = "D2",
      congestion = 0,
      lon = -7.0,
      lat = 53.287250,
      delay = 0.0,
      block_id = "block_id",
      vehicle_id = "12345",
      stop_id = "stop_id",
      at_stop = 0
    )
  )

  // dDistance: 0.0011119492667985354 km
  // dTime: 2.777777777777778E-4 hours
  // cumAvgSpeed: 4.003017360474727 km/h
  val vehicleHavingCumAvgSpeedLowerThanLimitEvents = List(
    new VehicleDataEvent(
      timestamp = 1352160008000000L,
      line_id = "line_id",
      direction = "0",
      journey_pattern_id = "journey_pattern_id",
      time_frame = "2012-11-05",
      vehicle_journey_id = "vehicle_journey_id",
      operator = "D2",
      congestion = 0,
      lon = -7.257329,
      lat = 53.287521,
      delay = 0.0,
      block_id = "block_id",
      vehicle_id = "12345",
      stop_id = "stop_id",
      at_stop = 0
    ),
    new VehicleDataEvent(
      timestamp = 1352160009000000L,
      line_id = "line_id",
      direction = "0",
      journey_pattern_id = "journey_pattern_id",
      time_frame = "2012-11-05",
      vehicle_journey_id = "vehicle_journey_id",
      operator = "D2",
      congestion = 0,
      lon = -7.257329,
      lat = 53.287531,
      delay = 0.0,
      block_id = "block_id",
      vehicle_id = "12345",
      stop_id = "stop_id",
      at_stop = 0
    )
  )
}
