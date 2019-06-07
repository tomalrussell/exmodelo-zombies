package zombies.guitutils

import scaladget.bootstrapslider
import scaladget.tools._
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.all._

import scalajs.js.|
import rx._
import scaladget.bootstrapnative.{BootstrapTags, bsn}
import zombies.guitutils.parameters._

object controls {

  type Mecanism = String
  type ControllerType = Double | Int | Boolean | Mecanism

  trait Controller {

    def name: ParameterName

    def element: HTMLElement

    def valueElement: HTMLElement

    def value: ControllerType
  }

  def onOffControllers: PartialFunction[Controller, OnOffController] = {
    case ooc: OnOffController => ooc
  }

  def optionalParameters: PartialFunction[Controller, Seq[ParameterName]] = {
    case ooc: OnOffController => ooc.name +: ooc.childs
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

  case class DoubleSlider(name: String, doubles: RangeValue[Double]) extends Controller {

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

  case class IntSlider(name: String, ints: RangeValue[Int]) extends Controller {

    val element = div.render

    val slider = Slider.build(element, ints.min, ints.max, ints.step, ints.default, (v: Double) => valueTag.update(v.toInt))

    lazy val valueTag: Var[Int] = Var(ints.default)

    lazy val valueElement = span(Rx {
      valueTag()
    }).render

    def value = slider.getValue.asInstanceOf[Int]

    def reset = slider.setValue(ints.default)

  }

  case class OptionController(name: String, mecanisms: Seq[Mecanism]) extends Controller {

    val radios = bsn.radios()(
      (for {
        m <- mecanisms
      } yield {
        bsn.selectableButton(m, onclick = () => valueTag.update(m))
      }): _*
    )

    val element = span(radios.render).render

    lazy val valueTag: Var[Mecanism] = Var(radios.active.head.text)

    val valueElement = span(Rx{valueTag()}).render

    def value = radios.active.head.text

  }

  case class OnOffController(name: ParameterName, childs: Seq[ParameterName] = Seq()) extends Controller {

    val isOn: Var[Boolean] = Var(false)

    val button = bsn.checkbox(false)(onchange := {()=> isOn.update(!isOn.now)}).render

    val element = button

    val valueElement = span.render

    def value = isOn.now

  }

  def build(parameter: Parameter): Controller = {
    parameter match {
      case Range.caseDouble(d) => DoubleSlider(d.name, d.value)
      case Range.caseInt(i) => IntSlider(i.name, i.value)
      case o: Options => OptionController(o.name, o.mecanisms)
      case OnOff(name, activeInSimulation, activation, childs) => OnOffController(name, childs)
      case _ => throw new RuntimeException(s"Unsupported parameter type ${parameter}")
    }
  }

}
