package zombies

import scaladget.bootstrapslider
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.tags._
import zombies.parameters._
import scalajs.js.|

object controls {

  trait Controller {

    def name: String

    def element: HTMLElement

    def value: Double | Int

    def reset: Unit
  }

  object Slider {
    def build(aDiv: HTMLElement, min: Double, max: Double, step: Double, default: Double) = {
      val options = bootstrapslider.SliderOptions
        .max(max)
        .min(min)
        .step(step)
        .value(default)
        .tooltip(bootstrapslider.SliderOptions.HIDE)

      bootstrapslider.Slider(aDiv, options)
    }
  }

  case class DoubleSlider(name: String, doubles: Doubles) extends Controller {

    val element = div.render

    val slider = Slider.build(element, doubles.min, doubles.max, doubles.step, doubles.default)

    def value = slider.getValue.asInstanceOf[Double]

    def reset = slider.setValue(doubles.default)
  }

  case class IntSlider(name: String, ints: Ints) extends Controller {
    
    val element = div.render

    val slider = Slider.build(element, ints.min, ints.max, ints.step, ints.default)

    def value = slider.getValue.asInstanceOf[Int]

    def reset = slider.setValue(ints.default)
  }

  def build(parameter: Parameter): Controller = {
    parameter.parameterType match {
      case d: Doubles => DoubleSlider(parameter.name, d)
      case i: Ints => IntSlider(parameter.name, i)
    }
  }

  val list = parameters.list.map{ p=> build(p)}

  def values = list.map{_.value}

  def reset = list.foreach{_.reset}
}
