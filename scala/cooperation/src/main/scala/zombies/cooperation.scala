package zombies

import zombies.guitutils.parameters._
import scala.scalajs.js.annotation.JSExportTopLevel

object cooperation {
  @JSExportTopLevel("zombies")
  def zombies(): Unit = {
    simulate.buildGUI(
      ()=> simulation.environment.quarantine,
      humanPerception isDefault,
      zombiePerception isDefault,
      humanRunSpeed asDefaultFrom(walkSpeed),
      zombieRunSpeed asDefaultFrom(walkSpeed),
      infectionRange.isDefault,
      humanExhaustionProbability isDefault,
      humanFightBackProbability isDefault,
      humanFollowProbability,
      humanInformProbability isDefault,
      numberHumans isDefault,
      numberZombies isDefault,
      humanMaxRotation isOff,
      humanFollowProbability,
      humanInformProbability,
      humanInformedRatio isOff,
      zombieMaxRotation isOff
    )
  }
}
