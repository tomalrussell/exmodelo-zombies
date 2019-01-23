package zombies

import zombies.guitutils.parameters._
import scala.scalajs.js.annotation.JSExportTopLevel

object vigilence {
  @JSExportTopLevel("zombies")
  def zombies(): Unit = {
    simulate.buildGUI(
      humanPerception,
      zombiePerception,
      humanRunSpeed asDefaultFrom(walkSpeed),
      zombieRunSpeed asDefaultFrom(walkSpeed),
      infectionRange.isDefault,
      humanStamina isDefault,
      numberHumans isDefault,
      zombieStamina isDefault,
      numberZombies isDefault,
      humanMaxRotation isOff,
      humanFollowModeProbability isOff,
      humanAwarenessProbability isOff,
      humanInformedRatio isOff,
      zombieMaxRotation isOff,
      rotationGranularity isOff
    )
  }
}