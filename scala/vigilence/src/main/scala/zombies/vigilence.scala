package zombies

import zombies.guitutils.parameters._
import scala.scalajs.js.annotation.JSExportTopLevel

object vigilence {
  @JSExportTopLevel("zombies")
  def zombies(): Unit = {
    simulate.buildGUI(
      ()=> world.World.jaude,
      humanPerception,
      zombiePerception,
      humanRunSpeed asDefaultFrom(walkSpeed),
      zombieRunSpeed asDefaultFrom(walkSpeed),
      infectionRange.isDefault,
      humanExhaustionProbability isDefault,
      numberHumans isDefault,
      numberZombies isDefault,
      humanMaxRotation isOff,
      humanFollowProbability isOff,
      humanInformProbability isOff,
      humanInformedRatio isOff,
      zombieMaxRotation isOff,
      rotationGranularity isOff
    )
  }
}