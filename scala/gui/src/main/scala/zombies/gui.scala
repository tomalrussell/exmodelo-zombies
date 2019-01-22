package zombies

import zombies.guitutils.parameters._
import scala.scalajs.js.annotation.JSExportTopLevel

object gui {
  @JSExportTopLevel("zombies")
  def zombies(): Unit = {
    simulate.buildGUI(Seq(
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
      followMode,
      followModeProbability
    ))
  }
}