package zombies

import zombies.guitutils.parameters._
import scala.scalajs.js.annotation.JSExportTopLevel

object zombieland {
  @JSExportTopLevel("zombies")
  def zombies(): Unit = {
    simulate.buildGUI(
      () => simulation.environment.stadium,
      infectionRange,
      walkSpeed,

      humanMaxRotation,
      humanPerception,
      humanExhaustionProbability,
      humanFightBackProbability,
      humanRunSpeed,
      humanFollowProbability,
      humanAwarenessProbability,
      humanInformedRatio,
      numberHumans,

      zombieMaxRotation,
      zombiePerception,
      zombieRunSpeed,
      zombiePheromoneEvaporation,
      numberZombies
    )
  }



}