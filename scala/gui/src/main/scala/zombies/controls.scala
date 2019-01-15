package zombies

import scaladget.bootstrapslider
import scaladget.tools._
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.all._
import zombies.parameters._

import scalajs.js.|
import rx._
import scaladget.bootstrapnative.{bsn, SelectableButtons}

object controls {

  type Mecanism = String
  val FollowMode: Mecanism = "No follow"
  val FollowRunning: Mecanism = "Follow running"

  trait Controller {

    def name: ParameterName

    def element: HTMLElement

    def valueElement: HTMLElement

    def value: Double | Int | Mecanism

    //def reset: Unit
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

    lazy val valueElement = span(Rx {
      valueTag()
    }).render

    def value = slider.getValue.asInstanceOf[Double]

    def reset = slider.setValue(doubles.default)
  }

  case class IntSlider(name: String, ints: Ints) extends Controller {

    val element = div.render

    val slider = Slider.build(element, ints.min, ints.max, ints.step, ints.default, (v: Double) => valueTag.update(v.toInt))

    lazy val valueTag: Var[Int] = Var(ints.default)

    lazy val valueElement = span(Rx {
      valueTag()
    }).render

    def value = slider.getValue.asInstanceOf[Int]

    def reset = slider.setValue(ints.default)
  }

  case class OptionController(name: String, mecanisms: Mecanism*) extends Controller {

    val radios = bsn.radios()(
      (for {
        m <- mecanisms
      } yield {
        bsn.selectableButton(m)
      }): _*
    )

    val element = span(radios.render).render

    val valueElement = span.render

    def value = radios.active.head.text

  }

  def build(parameter: Parameter): Controller = {
    parameter.parameterType match {
      case d: Doubles => DoubleSlider(parameter.name, d)
      case i: Ints => IntSlider(parameter.name, i)
      case o: Options=> OptionController(parameter.name, o.mecanisms: _*)
    }
  }

  val list = parameters.list.map { p => build(p) }

  def values= list.map {p=>
    p.name -> p.value
  }.toMap

//  def reset = list.foreach {
//    _.reset
//  }
}
