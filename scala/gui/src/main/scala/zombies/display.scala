package zombies

import org.scalajs.dom.raw.SVGElement
import scalatags.JsDom.all._
import scalatags.JsDom.styles
import scalatags.JsDom.svgAttrs.{height, style, width, x, y}
import scalatags.JsDom.svgTags
import scalatags.JsDom.svgAttrs
import zombies.agent.{Agent, Human}

import scala.scalajs.js.annotation._
import scala.util.Random
import scaladget.tools
import scaladget.bootstrapnative.bsn._
import zombies.simulation.Simulation
import zombies.world.{Wall, World}
import rx._
import scaladget.svg.path._
import scaladget.tools._

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

    val side = 40

    val rng = new Random(42)

    def initialize = {
      Simulation.initialize(
        World.jaude,
        controls.values(0).asInstanceOf[Double],
        controls.values(1).asInstanceOf[Double],
        controls.values(2).asInstanceOf[Double],
        controls.values(3).asInstanceOf[Double],
        controls.values(4).asInstanceOf[Int],
        controls.values(5).asInstanceOf[Double],
        controls.values(6).asInstanceOf[Double],
        controls.values(7).asInstanceOf[Double],
        controls.values(8).asInstanceOf[Int],
        controls.values(9).asInstanceOf[Double],
        controls.values(10).asInstanceOf[Int],
        random = rng
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
      controls.list.map { p =>
        span(styles.display.flex, flexDirection.row, paddingTop := 10)(span(minWidth := 130)(p.name), span(p.element, paddingLeft := 10), span(p.valueElement, paddingLeft := 10, fontWeight := "bold")).render
      },
      span(styles.display.flex, styles.justifyContent.center)(buttonGroup(paddingTop := 20)(setupButton, stepButton))
    )

    org.scalajs.dom.document.body.appendChild(div(styles.display.flex, flexDirection.row)(controllers, scene))

  }
}