package zombies

import zombies.api._

import scala.util.Random
import scala.scalajs.js.annotation.JSExportTopLevel

object apigui {

  @JSExportTopLevel("zombies")
  def zombies(): Unit = {


    def world =
      World {
        "++++++++++++++\n" +
        "+++++++0000000\n" +
        "+++++++0000000\n" +
        "+0000000000000\n" +
        "+000000000++++\n" +
        "++++000000++++\n" +
        "++++00RR00++++\n" +
        "0000000000++++\n" +
        "0000000000++++\n" +
        "0000000000++++\n" +
        "0000000000++++\n" +
        "++++000000++++\n" +
        "++++TT00ee++++\n" +
        "++++++++++++++"
      }

    val rng = new Random(42)

    def init(random: Random) =
      initialize(
        world = world,
        humanInformedRatio = 0.6,
        zombies = 4,
        humans = 250,
        random = rng
      )

    display.init(() => init(rng), List.empty)
  }

}
