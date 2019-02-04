package zombies

import zombies.guitutils.parameters._
import scala.scalajs.js.annotation.JSExportTopLevel

object zombieland {
  @JSExportTopLevel("zombies")
  def zombies(): Unit = {
    simulate.buildGUI(
      ()=> world.World.jaude,
      infectionRange,
      walkSpeed,
      rotationGranularity,

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
      zombiePheromonEvaporation,
      numberZombies
    )
  }



}