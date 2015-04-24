package co.ifwe.antelope.datingdemo.exec

import co.ifwe.antelope.datingdemo.ModelBase
import co.ifwe.antelope.datingdemo.gen.SimulationBase

object Score extends App with SimulationBase with ModelBase {
  val endTime = scoringEndTime
  doSimulation()
}
