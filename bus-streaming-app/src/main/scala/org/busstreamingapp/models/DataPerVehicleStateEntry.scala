package org.busstreamingapp.models

/**
  * The data type stored in the state
  */
case class DataPerVehicleStateEntry(
    vehicleID: String,
    currentCumAvg: Double,
    count: Int,
    currentEvent: VehicleDataEvent,
    timestampStart: Long = 0,
    timestampFinish: Long = 0
)
