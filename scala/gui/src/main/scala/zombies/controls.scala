package zombies

import scaladget.bootstrapslider
import scaladget.tools._
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.all._
import zombies.parameters._
import scalajs.js.|
import rx._

object controls {

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

  val list = parameters.list.map { p => build(p) }

  def values = list.map {
    _.value
  }

  def reset = list.foreach {
    _.reset
  }
}
