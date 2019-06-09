package zombies

import zombies.api._
import scala.util.Random

import scala.scalajs.js.annotation.JSExportTopLevel

object apigui {

  @JSExportTopLevel("zombies")
  def zombies(): Unit = {
    val rng = new Random(42)

    def init(random: Random) =
      initialize(
        world = quarantine,
        zombies = 4,
        humans = 250,
        random = rng
      )

    display.init(() => init(rng), List.empty)
  }

}
