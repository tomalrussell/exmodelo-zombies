package zombies

import rx.{Ctx, Rx}
import spatialdata.grid.GridGeneratorLauncher
import zombies.guitutils.parameters._

import scala.util.Random
import zombies.guitutils.controls.Mecanism

import scala.scalajs.js.annotation.JSExportTopLevel

object spatialsens {
  @JSExportTopLevel("zombies")
  def zombies(): Unit = {
    implicit val rng = new Random
//    def generateWorld = world.World.parse() {
//      wallsToString(bondPercolatedWorld(
//        worldSize = 40,
//        percolationProba = 0.2,
//        bordPoints = 20,
//        linkwidth = 3.0)
//      )
//    }
    val generationMethod = Options("generationMethod", Seq("random","expMixture","blocks","percolation"), "random", "Jaude", Variable)
//    val gridSize = Range("gridSize", RangeValue(10, 100, 1, 40, 40), Variable)
    /**
      * Random
      */
    val randomDensity = Range("randomDensity", RangeValue(0.0, 1.0, 0.01, 0.5, 0.5), Variable)
    /**
      * ExpMixture
      */
    val expMixtureCenters = Range("expMixtureCenters", RangeValue(1, 100, 1, 5, 5), Variable)
    val expMixtureRadius = Range("expMixtureRadius", RangeValue(0.0, 10.0, 0.1, 5.0, 5.0), Variable)
    val expMixtureThreshold = Range("expMixtureThreshold", RangeValue(0.0, 1.0, 0.01, 0.5, 0.5), Variable)
    /**
      * blocks
      */
    val blocksNumber = Range("blocksNumber", RangeValue(1, 10, 1, 5, 5), Variable)
    val blocksMinSize = Range("blocksMinSize", RangeValue(1, 10, 1, 5, 5), Variable)
    val blocksMaxSize = Range("blocksMaxSize", RangeValue(1, 20, 1, 15, 15), Variable)
    /**
      * percolation
      */
    val percolationProba = Range("percolationProba", RangeValue(0.0, 1.0, 0.01, 0.2, 0.2), Variable)
    val percolationBordPoints = Range("percolationBordPoints", RangeValue(1, 100, 1, 20, 20), Variable)
    val percolationLinkWidth = Range("percolationLinkWidth", RangeValue(1.0, 5.0, 0.1, 3.0, 3.0), Variable)

    implicit val ctx: Ctx.Owner = Ctx.Owner.safe()

    def getController(name: String) = Rx{display.controllerSeq().find(_.name == name)}.now

    def getMecanismValue(name: String) = {
      getController(name).map(_.value.asInstanceOf[Mecanism]) match {
        case Some(v) => v
        case None =>
          println(s"$name not found")
          "random"
      }
    }

    def getIntValue(name: String) = {
      getController(name).map(_.value.asInstanceOf[Int]) match {
        case Some(v) => v
        case None =>
          println(s"$name not found")
          1
      }
    }
    def getDoubleValue(name: String) = {
      getController(name).map(_.value.asInstanceOf[Double]) match {
        case Some(v) => v
        case None =>
          println(s"$name not found")
          0.5
      }
    }

    simulate.buildGUI(
      ()=> {
        println("Init Function from Spatialsens!!!")
        getController("generationMethod") match {
          case None => world.World.jaude
          case Some(_) =>
            val gridString = spatialdata.grid.gridToString(GridGeneratorLauncher(
              getMecanismValue("generationMethod"),
              /*getIntValue("gridSize")*/40,// trop de choses à modifier dans le code de display
              getDoubleValue("randomDensity"),
              getIntValue("expMixtureCenters"),
              getDoubleValue("expMixtureRadius"),
              getDoubleValue("expMixtureThreshold"),
              getIntValue("blocksNumber"),
              getIntValue("blocksMinSize"),
              getIntValue("blocksMaxSize"),
              getDoubleValue("percolationProba"),
              getIntValue("percolationBordPoints"),
              getDoubleValue("percolationLinkWidth")).getGrid)
            println(gridString)
            world.World.parse() {gridString}
        }
      },
      generationMethod,
      //gridSize, // trop de choses à modifier dans le code de display
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