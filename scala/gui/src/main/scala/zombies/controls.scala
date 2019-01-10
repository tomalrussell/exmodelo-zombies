package zombies

import scaladget.bootstrapslider
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.tags._
import zombies.parameters._
import scalajs.js.|

object controls {

  trait Controller {

    def element: HTMLElement

    def value: Double | Int

    def reset: Unit
  }

  object Slider {
    def build(aDiv: HTMLElement, min: Double, max: Double, default: Double) = {
      val options = bootstrapslider.SliderOptions
        .max(max)
        .min(min)
        .value(default)
        .tooltip(bootstrapslider.SliderOptions.ALWAYS)

      bootstrapslider.Slider(aDiv, options)
    }
  }

  class DoubleSlider(name: String, doubles: Doubles) extends Controller {

    val element = div.render

    val slider = Slider.build(element, doubles.min, doubles.max, doubles.default)

    def value = slider.getValue.asInstanceOf[Double]

    def reset = slider.setValue(doubles.default)
  }

  class IntSlider(name: String, ints: Ints) extends Controller {
    
    val element = div.render

    val slider = Slider.build(element, ints.min, ints.max, ints.default)

    def value = slider.getValue.asInstanceOf[Int]

    def reset = slider.setValue(ints.default)
  }

  def build(parameter: Parameter): Controller = {
    parameter.parameterType match {
      case d: Doubles => new DoubleSlider(parameter.name, d)
      case i: Ints => new IntSlider(parameter.name, i)
    }
  }

  def list = parameters.list.map{build}

  def values = list.map{_.value}

  def reset = list.foreach{_.reset}
}
