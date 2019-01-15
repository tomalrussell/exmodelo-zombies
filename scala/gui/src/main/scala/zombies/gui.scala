package zombies

import zombies.guitutils.controls._
import zombies.guitutils.parameters._
import zombies.simulation._

import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.Random

object gui {

  val parameters = Seq(
    Parameter(infectionRange, Doubles(0.0, 1.0, 0.1, 0.2)),
    Parameter(walkSpeed, Doubles(0.0, 1.0, 0.1, 0.1)),
    Parameter(humanRunSpeed, Doubles(0.0, 1.0, 0.1, 0.5)),
    Parameter(humanStamina, Ints(0, 50, 1, 10)),
    Parameter(humanPerception, Doubles(0.0, 5.0, 0.1, 0.7)),
    Parameter(humanMaxRotation, Doubles(0.0, 180.0, 1.0, 60.0)),
    Parameter(numberHumans, Ints(0, 1500, 1, 250)),
    Parameter(zombieRunSpeed, Doubles(0.0, 1.0, 0.1, 0.3)),
    Parameter(zombieStamina, Ints(0, 50, 1, 10)),
    Parameter(zombiePerception, Doubles(0.0, 5.0, 0.1, 1.2)),
    Parameter(zombieMaxRotation, Doubles(0.0, 180.0, 1.0, 45.0)),
    Parameter(numberZombies, Ints(0, 1500, 1, 4)),
    Parameter(rotationGranularity, Ints(0, 10, 1, 5)),
    Parameter(followMode, Options(FollowMode, FollowRunning)),
    Parameter(followModeProbability, Doubles(0.0, 1.0, 0.1, 0.5))
  )

  val controlList = parameters.map { p => build(p) }

  def controlValues = controlList.map { p =>
    p.name -> p.value
  }.toMap

  @JSExportTopLevel("zombies")
  def zombies(): Unit = {


    val rng = new Random(42)

    def initialize(rng: Random) = {
      Simulation.initialize(
        world.World.jaude,
        infectionRange = controlValues(infectionRange).asInstanceOf[Double],
        walkSpeed = controlValues(walkSpeed).asInstanceOf[Double],
        humanRunSpeed = controlValues(humanRunSpeed).asInstanceOf[Double],
        humanStamina = controlValues(humanStamina).asInstanceOf[Int],
        humanPerception = controlValues(humanPerception).asInstanceOf[Double],
        humanMaxRotation = controlValues(humanMaxRotation).asInstanceOf[Int],
        humans = controlValues(numberHumans).asInstanceOf[Int],
        zombieRunSpeed = controlValues(zombieRunSpeed).asInstanceOf[Double],
        zombieStamina = controlValues(zombieStamina).asInstanceOf[Int],
        zombiePerception = controlValues(zombiePerception).asInstanceOf[Int],
        zombieMaxRotation = controlValues(zombieMaxRotation).asInstanceOf[Double],
        zombies = controlValues(numberZombies).asInstanceOf[Int],
        rotationGranularity = controlValues(rotationGranularity).asInstanceOf[Int],
        random = rng,
        humanFollowMode = toFollowMode(controlValues(followMode).asInstanceOf[Mecanism], controlValues(followModeProbability).asInstanceOf[Double])
      )
    }

    display.init(()=> initialize(rng), controlList)
  }
}