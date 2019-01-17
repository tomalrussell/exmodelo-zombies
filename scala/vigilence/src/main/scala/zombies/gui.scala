package zombies

import zombies.guitutils.parameters._
import scala.scalajs.js.annotation.JSExportTopLevel

object gui {
  @JSExportTopLevel("zombies")
  def zombies(): Unit = {
    simulate.buildGUI(
      variableParameters = Seq(humanPerception, zombiePerception),
      parametersOff = Seq(humanRunSpeed, zombieRunSpeed, humanMaxRotation, zombieMaxRotation, rotationGranularity, followMode, followModeProbability))
  }
}