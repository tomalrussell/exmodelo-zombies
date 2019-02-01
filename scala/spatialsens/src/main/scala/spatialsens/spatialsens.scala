package zombies

import zombies.guitutils.parameters._
import scala.util.Random
import zombies.generator.Generator._


import scala.scalajs.js.annotation.JSExportTopLevel

object vigilence {
  @JSExportTopLevel("zombies")
  def zombies(): Unit = {

  implicit val rng = new Random
    def generateWorld = world.World.parse() {
      wallsToString(bondPercolatedWorld(
        worldSize = 40,
        percolationProba = 0.2,
        bordPoints = 20,
        linkwidth = 3.0)
      )
    }

    simulate.buildGUI(
      ()=> generateWorld,
      humanPerception isDefault,
      zombiePerception isDefault,
      humanRunSpeed asDefaultFrom (walkSpeed),
      zombieRunSpeed asDefaultFrom (walkSpeed),
      infectionRange.isDefault,
      humanExhaustionProbability,
      numberHumans isDefault,
      zombieExhaustionProbability,
      numberZombies isDefault,
      humanMaxRotation isOff,
      humanFollowProbability isOff,
      humanAwarenessProbability isOff,
      humanInformedRatio isOff,
      zombieMaxRotation isOff,
      rotationGranularity isOff
    )
  }
}