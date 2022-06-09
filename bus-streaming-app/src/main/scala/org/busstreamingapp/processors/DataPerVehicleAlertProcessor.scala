package org.busstreamingapp.processors

import org.busstreamingapp.models.{DataPerVehicleStateEntry, VehicleDataEvent}

trait DataPerVehicleAlertProcessor {
  def updateDataPerVehicleAlert(
      currentDataPerVehicleAlertValue: DataPerVehicleStateEntry,
      newVehicleDataEvent: VehicleDataEvent
  ): DataPerVehicleStateEntry
}
