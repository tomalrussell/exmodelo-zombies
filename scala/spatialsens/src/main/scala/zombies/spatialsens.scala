package zombies

import rx.{Ctx, Rx}
import zombies.guitutils.parameters._
import zombies.world.World

import scala.util.Random
import zombies.guitutils.controls.Mecanism

import scala.scalajs.js.annotation.JSExportTopLevel

object spatialsens {

  @JSExportTopLevel("zombies")
  def zombies(): Unit = {
    simulate.buildGUI(
      ()=> World.dummyWorld,
      generationMethod,
      randomDensity,
      expMixtureCenters,
      expMixtureRadius,
      expMixtureThreshold,
      blocksNumber,
      blocksMinSize,
      blocksMaxSize,
      percolationProba,
      percolationBordPoints,
      percolationLinkWidth,
      humanPerception isDefault,
      zombiePerception isDefault,
      humanRunSpeed asDefaultFrom (walkSpeed),
      zombieRunSpeed asDefaultFrom (walkSpeed),
      infectionRange.isDefault,
      humanExhaustionProbability isDefault,
      humanFightBackProbability isDefault,
      humanFollowProbability isDefault,
      humanInformProbability isDefault,
      numberHumans isDefault,
      numberZombies isDefault,
      humanMaxRotation isOff,
      humanFollowProbability isOff,
      humanInformProbability isOff,
      humanInformedRatio isOff,
      zombieMaxRotation isOff
    )
  }
}