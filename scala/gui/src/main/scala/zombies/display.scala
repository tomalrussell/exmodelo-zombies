package zombies

import org.scalajs.dom.raw.SVGElement
import scalatags.JsDom.all._
import scalatags.JsDom.svgAttrs.{height, style, width, x, y}
import scalatags.JsDom.svgTags
import scalatags.JsDom.svgAttrs
import zombies.agent.{Agent, Human, Zombie}

import scala.scalajs.js.annotation._
import scala.util.Random
import scaladget.svg._
import scaladget.tools
import scaladget.bootstrapnative.bsn._
import zombies.simulation.Simulation
import zombies.world.{Wall, World}
import rx._

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

  implicit def seqAgentToSeqReactiveAgent(s: Seq[Agent]): Seq[ReactiveAgent] = s.map {
    agentToReactiveAgent
  }

  implicit def agentToReactiveAgent(agent: Agent): ReactiveAgent = {
    agent match {
      case zombie: Zombie => ReactiveZombie(Var(zombie.position), Var(zombie.velocity))
      case human: Human => ReactiveHuman(Var(human.position), Var(human.velocity))
    }
  }

  trait ReactiveAgent

  case class ReactiveHuman(position: Var[move.Position], velocity: Var[move.Velocity]) extends ReactiveAgent

  case class ReactiveZombie(position: Var[move.Position], velocity: Var[move.Velocity]) extends ReactiveAgent

  object ReactiveAgent {
    def position(reactiveAgent: ReactiveAgent) = reactiveAgent match {
      case h: ReactiveHuman => h.position
      case z: ReactiveZombie => z.position
    }

    def velocity(reactiveAgent: ReactiveAgent) = reactiveAgent match {
      case h: ReactiveHuman => h.velocity
      case z: ReactiveZombie => z.velocity
    }
  }

  @JSExportTopLevel("zombies")
  def zombies(): Unit = {

    implicit val ctx: Ctx.Owner = Ctx.Owner.safe()

    val side = 40

    val minSpeed = 0.1
    val infectionRange = 0.2
    val humanPerception = 0.7
    val zombiePerception = 1.2

    val humanSpeed = 0.5
    val zombieSpeed = 0.3

    val zombieMaxRotation = 45
    val humanMaxRotation = 60

    val humans = 250
    val zombies = 4

    val rng = new Random(42)
    val doorSize = 2
    val wallSize = (side - doorSize) / 2

    val simulation = Var(Simulation.initialize(
      World.jaude,
      infectionRange = infectionRange,
      humanSpeed = humanSpeed,
      humanPerception = humanPerception,
      humanMaxRotation = humanMaxRotation,
      humans = humans,
      zombieSpeed = zombieSpeed,
      zombiePerception = zombiePerception,
      zombieMaxRotation = zombieMaxRotation,
      zombies = zombies,
      minSpeed = minSpeed,
      random = rng
    ))

    val neighborhoodCache = World.visibleNeighborhoodCache(simulation.now.world, math.max(simulation.now.humanPerception, simulation.now.zombiePerception))

    val agents: Var[Seq[ReactiveAgent]] = Var(simulation.now.agents)

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

    val agentPath = path().m(0, agentSize).l(thirdAgentSize, 0).l(2 * thirdAgentSize, agentSize).l(thirdAgentSize, agentSize * 5 / 6).z

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
        //svgTags.g(
        svgTags.g((for {
          a <- agents()
        } yield {
          val ax = (ReactiveAgent.position(a)()._2 * gridSize) + 1
          val ay = (ReactiveAgent.position(a)()._1) * gridSize + 1
          val rotation = math.atan2(ReactiveAgent.velocity(a)()._2, -ReactiveAgent.velocity(a)()._1).toDegrees
          val color = a match {
            case h: ReactiveHuman => "green"
            case _ => "red"
          }
          agentPath.render(svgAttrs.fill := color, svgAttrs.transform := s"rotate($rotation, ${ax}, ${ay}) translate(${ax - offsetX},${ay - offsetY})")
        }): _*)
      })
      scene.appendChild(element)
    }

    def step: Unit = {
      val tmp = _root_.zombies.simulation.step(simulation.now, neighborhoodCache, rng)
      simulation.update(tmp)
      agents.update(simulation.now.agents)
      timers.setTimeout(100) {
        step
      }
    }

    val stepButton = button("Start", btn_danger, onclick := { () => step })

    buildWorld(side, simulation.now.world)
    buildAgents


    org.scalajs.dom.document.body.appendChild(stepButton)
    org.scalajs.dom.document.body.appendChild(scene)

  }
}