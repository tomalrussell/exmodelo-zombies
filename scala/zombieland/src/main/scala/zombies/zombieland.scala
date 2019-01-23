package zombies

import zombies.guitutils.parameters._
import scala.scalajs.js.annotation.JSExportTopLevel

object zombieland {
  @JSExportTopLevel("zombies")
  def zombies(): Unit = {
    simulate.buildGUI(
      infectionRange,
      walkSpeed,
      humanMaxRotation,
      humanPerception,
      humanStamina,
      humanRunSpeed,
      numberHumans,
      zombieMaxRotation,
      zombiePerception,
      zombieStamina,
      zombieRunSpeed,
      numberZombies,
      followModeProbability
    )
  }
}