package zombies

import org.scalajs.dom.raw.SVGElement
import scalatags.JsDom.all._
import scalatags.JsDom.styles
import scalatags.JsDom.svgAttrs.{height, style, width, x, y}
import scalatags.JsDom.svgTags
import scalatags.JsDom.svgAttrs
import zombies.agent._

import scala.scalajs.js.annotation._
import scala.util.Random
import scaladget.tools
import scaladget.bootstrapnative.bsn._
import zombies.simulation.{Event, Simulation}
import zombies.world.{Floor, NeighborhoodCache, Wall, World}
import rx._
import scaladget.svg.path._
import scaladget.tools._
import zombies.guitutils.controls._

import scala.scalajs.js.timers

/*
 * Copyright (C) 24/03/16 // mathieu.leclaire@openmole.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 */

object display {

  object Color {

    val coldColor = (255, 238, 170)
    val hotColor = (255, 100, 0)
    val rescueColor = (55, 170, 200)

    val baseR = (hotColor._1 - coldColor._1)
    val baseG = (hotColor._2 - coldColor._2)
    val baseB = (hotColor._3 - coldColor._3)

    def color(value: Double) = value match {
      case 10.0 => rescueColor
      case _ => (
        (baseR * value + coldColor._1).toInt,
        (baseG * value + coldColor._2).toInt,
        (baseB * value + coldColor._3).toInt
      )
    }
  }

  implicit val ctx: Ctx.Owner = Ctx.Owner.safe()

  val rng = new Random(42)

  case class People(
    humans: Int = 0,
    zombies: Int = 0,
    rescued: Int = 0,
    alerted: Int = 0,
    killed: Int = 0,
    informed: Int = 0,
    zombified: Int = 0)

  def init(initFunction: () => Simulation, controllerList: Seq[Controller]) = {

    val simulation = initFunction()
    val side = simulation.world.side

    val doorSize = 2
    val wallSize = (side - doorSize) / 2

    val stepBuffer: Var[Option[(Simulation, List[Event], Int, NeighborhoodCache)]] = Var(None)
    val stepState: Var[Option[(Simulation, List[Event], Int, NeighborhoodCache)]] = Var(None)

    val timeOut: Var[Option[Int]] = Var(None)
    val people: Var[People] = Var(People())


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
          case f: Floor => if (f.rescueZone) 10 else 0
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
          a <- stepState().map { _._1.agents }.getOrElse(Vector())
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

    def step: Unit = stepBuffer.now match {
      case state@Some((simulationBuffer, eventBuffer, stateNumberBuffer, neighborhoodCache)) =>
        stepState.update(state)
        stepBuffer.update(None)

        val p = People(
          humans = simulationBuffer.agents.count(Agent.isHuman),
          zombies = simulationBuffer.agents.count(Agent.isZombie),
          rescued = eventBuffer.collect(Event.rescued).size,
          killed = eventBuffer.collect(Event.killed).size,
          informed = simulationBuffer.agents.collect(Agent.human).count(_.rescue.informed),
          alerted = simulationBuffer.agents.collect(Agent.human).count(_.rescue.alerted),
          zombified = eventBuffer.collect(Event.zombified).size,
        )

        people.update(p)
        timeOut.now.foreach(to => timers.setTimeout(to) { step })

        val (ns, ev) = _root_.zombies.simulation.step(stateNumberBuffer + 1, simulationBuffer, neighborhoodCache, rng)
        stepBuffer.update(Some(ns, eventBuffer ++ ev, stateNumberBuffer + 1, neighborhoodCache))
      case None => timeOut.now.foreach(to => timers.setTimeout(to) { step })
    }

    val setupButton = button("Setup", btn_default, onclick := { () =>
      val simulation = initFunction()
      val stepNumber = 0
      val neighborhoodCache =  World.visibleNeighborhoodCache(simulation.world, math.max(simulation.humanPerception, simulation.zombiePerception))

      stepBuffer.update(Some(simulation, List(), stepNumber, neighborhoodCache))
      stepState.update(Some(simulation, List(), stepNumber, neighborhoodCache))

      buildWorld(side, simulation.world)
      buildAgents
      people.update(People())
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
      timeOut.now.foreach { _ => step }
    })

    val stats = span(marginLeft := 20, styles.display.flex, flexDirection.column, styles.justifyContent.center)(
      span(Rx { s"# humans: ${people().humans}" }),
      span(Rx { s"# rescued: ${people().rescued}" }),
      span(Rx { s"# informed: ${people().informed}" }),
      span(Rx { s"# alerted: ${people().alerted}" }),
      span(Rx { s"# zombified:  ${people().zombified}" }),
      span(Rx { s"# zombies: ${people().zombies}" }),
      span(Rx { s"# killed zombies: ${people().killed}" }),
    )

    val controllers = div(marginTop := 50, marginLeft := 40, marginRight := 30, maxWidth := 500, styles.display.flex, flexDirection.column, styles.justifyContent.center)(
      controllerList.map { p =>
        span(styles.display.flex, flexDirection.row, paddingTop := 10)(span(minWidth := 130)(p.name), span(p.element, paddingLeft := 10), span(p.valueElement, paddingLeft := 10, fontWeight := "bold")).render
      },
      span(styles.display.flex, styles.justifyContent.center)(buttonGroup(paddingTop := 20)(setupButton, stepButton))
    )

    org.scalajs.dom.document.body.appendChild(div(styles.display.flex, flexDirection.row)(controllers, scene, stats))

  }
}