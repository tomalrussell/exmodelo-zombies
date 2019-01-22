

package zombies

/*
import org.scalajs.dom.raw.{HTMLElement, SVGElement}
import scalatags.JsDom.all._
import scalatags.JsDom.styles
import scalatags.JsDom.svgAttrs.{height, style, width, x, y}
import scalatags.JsDom.svgTags
import scalatags.JsDom.svgAttrs
import zombies.agent.{Agent, Human}

import scala.scalajs.js.annotation._
import scala.util.Random
import scaladget.{bootstrapslider, tools}
import scaladget.bootstrapnative.bsn._
import zombies.simulation.Simulation
import zombies.world.{Wall, World}
import rx._
import scaladget.svg.path._
import scaladget.tools._
import zombies.spatialsens.Generator.{bondPercolatedWorld, wallsToString}

import scala.scalajs.js.{timers, |}


object displayspatialsens {

  //import spatialsensparameters._

  //implicit val rng: Random = new Random

  object Color {

    val coldColor = (255, 238, 170)
    val hotColor = (255, 100, 0)

    val baseR = (hotColor._1 - coldColor._1)
    val baseG = (hotColor._2 - coldColor._2)
    val baseB = (hotColor._3 - coldColor._3)

    def color(value: Double) = (
      (baseR * value + coldColor._1).toInt,
      (baseG * value + coldColor._2).toInt,
      (baseB * value + coldColor._3).toInt
    )
  }

  @JSExportTopLevel("zombies")
  def zombies(): Unit = {

    implicit val ctx: Ctx.Owner = Ctx.Owner.safe()


    val worldSize =50
    val percolationProba = 0.2
    val bordPoints = 15
    val linkwidth=3.0

    val side = worldSize

    //implicit val rng: Random = new Random
    //val rng = new Random(42)
    val rng = new Random


    def initialize = {

      val worldInstance = World.parse()(wallsToString(bondPercolatedWorld(worldSize=worldSize,percolationProba=percolationProba,bordPoints=bordPoints,linkwidth=linkwidth)(rng)))


      Simulation.initialize (
        worldInstance,
        infectionRange = spatialsenscontrols.values(0).asInstanceOf[Double],
        walkSpeed = spatialsenscontrols.values(1).asInstanceOf[Double],
        humanRunSpeed = spatialsenscontrols.values(2).asInstanceOf[Double],
        humanStamina = spatialsenscontrols.values(3).asInstanceOf[Int],
        humanPerception = spatialsenscontrols.values(4).asInstanceOf[Double],
        humanMaxRotation = spatialsenscontrols.values(5).asInstanceOf[Int],
        humans = spatialsenscontrols.values(6).asInstanceOf[Int],
        zombieRunSpeed = spatialsenscontrols.values(7).asInstanceOf[Double],
        zombieStamina = spatialsenscontrols.values(8).asInstanceOf[Int],
        zombiePerception = spatialsenscontrols.values(9).asInstanceOf[Int],
        zombieMaxRotation = spatialsenscontrols.values(10).asInstanceOf[Double],
        zombies = spatialsenscontrols.values(11).asInstanceOf[Int],
        random = rng,
        humanFollowRunning = true
      )
    }

    val doorSize = 2
    val wallSize = (side - doorSize) / 2

    val simulation: Var[Option[Simulation]] = Var(None)

    val timeOut: Var[Option[Int]] = Var(None)

    def neighborhoodCache = simulation.now map { s =>
      World.visibleNeighborhoodCache(s.world, math.max(s.humanPerception, s.zombiePerception))
    }

    val gridSize = 800

    val scene = svgTags.svg(
      width := gridSize,
      height := gridSize
    ).render

    val cellDimension = gridSize.toDouble / side

    val agentSize = cellDimension / 3
    val thirdAgentSize = (agentSize / 3)
    val offsetY = agentSize / 2
    val offsetX = agentSize / 3

    val agentPath = Path(precisionPattern = "%1.2f").m(0, agentSize).l(thirdAgentSize, 0).l(2 * thirdAgentSize, agentSize).l(thirdAgentSize, agentSize * 5 / 6).z

    def worldToInts(world: World, lineIndex: Int): Seq[Int] = {
      world.cells(lineIndex).map { cell =>
        cell match {
          case Wall => 1
          case _ => 0
        }
      }
    }

    def buildWorld(nbCellsByDimension: Int, world: World) = {
      val values = (1 to nbCellsByDimension).foldLeft(Seq[Seq[Int]]())((elems, index) => elems :+ worldToInts(world, index - 1)).transpose

      for {
        col <- (0 to nbCellsByDimension - 1).toArray
        val colCoord = (col * cellDimension) + 1
        row <- (0 to nbCellsByDimension - 1).toArray
      } yield {
        scene.appendChild(
          svgTags.rect(x := ((row * cellDimension) + 1), y := colCoord, width := cellDimension, height := cellDimension,
            style := s"fill:rgb${Color.color(values(row)(col))};").render
        )
      }
    }

    def buildAgents = {
      val element: SVGElement = tools.rxSVGMod(Rx {
        svgTags.g((for {
          a <- simulation().map {
            _.agents
          }.getOrElse(Vector())
        } yield {
          val ax = "%1.2f".format((Agent.position(a)._2 * gridSize) + 1 - offsetX)
          val ay = "%1.2f".format((Agent.position(a)._1) * gridSize + 1 - offsetY)
          val rotation = "%1.2f".format(math.atan2(Agent.velocity(a)._2, -Agent.velocity(a)._1).toDegrees)
          val color = a match {
            case h: Human => "green"
            case _ => "red"
          }
          svgTags.g(agentPath.render(svgAttrs.fill := color), svgAttrs.transform := s"rotate($rotation, ${ax}, ${ay}) translate(${ax},${ay})")
          //agentPath.render(svgAttrs.fill := color, svgAttrs.transform := s"rotate($rotation, ${ax}, ${ay}) translate(${ax - offsetX},${ay - offsetY})")
        }): _*)
      })
      scene.appendChild(element)
    }

    def step: Unit = {
      timeOut.now match {
        case Some(to: Int) =>
          neighborhoodCache.foreach { nc =>
            simulation.now.foreach { s =>
              simulation.update(Some(_root_.zombies.simulation.step(s, nc, rng)))
            }
          }
          timers.setTimeout(to) {
            step
          }
        case _ =>
      }
    }

    val setupButton = button("Setup", btn_default, onclick := { () =>
      simulation.update(Some(initialize))
      simulation.now.foreach { s =>
        timeOut.update(None)
        buildWorld(side, s.world)
        buildAgents
      }
    })

    val stepButton = button(span(Rx {
      timeOut() match {
        case Some(_) => "Stop"
        case _ => "Start"
      }
    }), btn_danger, onclick := { () =>
      timeOut() = timeOut.now match {
        case None => Some(100)
        case _ => None
      }
      timeOut.now.foreach { _ =>
        step
      }
    })


    val controllers = div(marginTop := 50, marginLeft := 40, marginRight := 30, maxWidth := 500, styles.display.flex, flexDirection.column, styles.justifyContent.center)(
      spatialsenscontrols.list.map { p =>
        span(styles.display.flex, flexDirection.row, paddingTop := 10)(span(minWidth := 130)(p.name), span(p.element, paddingLeft := 10), span(p.valueElement, paddingLeft := 10, fontWeight := "bold")).render
      },
      span(styles.display.flex, styles.justifyContent.center)(buttonGroup(paddingTop := 20)(setupButton, stepButton))
    )

    org.scalajs.dom.document.body.appendChild(div(styles.display.flex, flexDirection.row)(controllers, scene))

  }
}


object spatialsenscontrols {

  import spatialsensparameters._

  trait Controller {

    def name: String

    def element: HTMLElement

    def valueElement: HTMLElement

    def value: Double | Int

    def reset: Unit
  }

  object Slider {
    def build(aDiv: HTMLElement, min: Double, max: Double, step: Double, default: Double, onChange: (Double) => Unit) = {
      val options = bootstrapslider.SliderOptions
        .max(max)
        .min(min)
        .step(step)
        .value(default)
        .tooltip(bootstrapslider.SliderOptions.HIDE)

      val slider = bootstrapslider.Slider(aDiv, options)
      slider.on(bootstrapslider.Slider.CHANGE, () => {
        onChange(slider.getValue.asInstanceOf[Double])
      })
    }
  }

  case class DoubleSlider(name: String, doubles: Doubles) extends Controller {

    implicit val ctx: Ctx.Owner = Ctx.Owner.safe()

    val element = div.render

    val slider = Slider.build(element, doubles.min, doubles.max, doubles.step, doubles.default, (v: Double) => valueTag.update(v))

    private lazy val valueTag: Var[Double] = Var(doubles.default)

    lazy val valueElement = span(Rx{valueTag()}).render

    def value = slider.getValue.asInstanceOf[Double]

    def reset = slider.setValue(doubles.default)
  }

  case class IntSlider(name: String, ints: Ints) extends Controller {

    val element = div.render

    val slider = Slider.build(element, ints.min, ints.max, ints.step, ints.default, (v: Double) => valueTag.update(v.toInt))

    lazy val valueTag: Var[Int] = Var(ints.default)

    lazy val valueElement = span(Rx{valueTag()}).render

    def value = slider.getValue.asInstanceOf[Int]

    def reset = slider.setValue(ints.default)
  }

  def build(parameter: Parameter): Controller = {
    parameter.parameterType match {
      case d: Doubles => DoubleSlider(parameter.name, d)
      case i: Ints => IntSlider(parameter.name, i)
    }
  }

  val list = spatialsensparameters.list.map { p => build(p) }

  def values = list.map {
    _.value
  }

  def reset = list.foreach {
    _.reset
  }
}



object spatialsensparameters {

  trait ParameterType

  case class Doubles(min: Double, max: Double, step: Double, default: Double) extends ParameterType
  case class Ints(min: Int, max: Int, step: Double, default: Int) extends ParameterType
  case class Booleans(default: Boolean) extends ParameterType

  case class Parameter(name: String, parameterType: ParameterType)


  val list = Seq(
    Parameter("infectionRange", Doubles(0.0, 1.0, 0.1, 0.2)),
    Parameter("walkSpeed", Doubles(0.0, 1.0, 0.1, 0.1)),
    Parameter("humanRunSpeed", Doubles(0.0, 1.0, 0.1, 0.5)),
    Parameter("humanStamina", Ints(0, 50, 1, 10)),
    Parameter("humanPerception", Doubles(0.0, 5.0, 0.1, 0.7)),
    Parameter("humanMaxRotation", Doubles(0.0, 180.0, 1.0, 60.0)),
    Parameter("# humans", Ints(0, 1500, 1, 250)),
    Parameter("zombieRunSpeed", Doubles(0.0, 1.0, 0.1, 0.3)),
    Parameter("zombiesStamina", Ints(0, 50, 1, 10)),
    Parameter("zombiePerception", Doubles(0.0, 5.0, 0.1, 1.2)),
    Parameter("zombieMaxRotation", Doubles(0.0, 180.0, 1.0, 45.0)),
    Parameter("# zombies", Ints(0, 1500, 1, 4)),
    Parameter("rotationGranularity", Ints(0, 10, 1, 5)),
  )
}

*/

