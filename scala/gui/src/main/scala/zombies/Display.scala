package zombies

import scalatags.JsDom.all._
import org.scalajs.dom.raw.{Event, SVGPathElement}
import scalatags.JsDom.svgAttrs.{height, style, width, x, y}
import scalatags.JsDom.svgTags
import scalatags.JsDom.svgAttrs._
import zombies.agent.{Agent, Human}

import scala.scalajs.js.annotation._
import scala.util.Random
import scaladget.svg._
import zombies.simulation.Simulation
import zombies.world.{Wall, World}

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

object Display {

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
    val side = 40

    val minSpeed = 0.1 * space.cellSide(side)
    val infectionRange = 0.2 * space.cellSide(side)
    val humanPerception = 0.7 * space.cellSide(side)
    val zombiePerception = 1.2 * space.cellSide(side)

    val humanSpeed = 0.5 * space.cellSide(side)
    val zombieSpeed = 0.3 * space.cellSide(side)

    val zombieMaxRotation = 180
    val humanMaxRotation = 360

    val humans = 250
    val zombies = 4

    val rng = new Random(42)
    val doorSize = 2
    val wallSize = (side - doorSize) / 2

    val simulation = Simulation.initialize(
      Simulation.parseWorld(World.jaude),
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
    )

    def worldToInts(world: World, lineIndex: Int): Seq[Int] = {
      world.cells(lineIndex).map { cell =>
        cell match {
          case Wall => 1
          case _ => 0
        }
      }
    }

    def buildWorld(nbCellsByDimension: Int, world: World) = {

      val gridSize = 800

      val cellDimension = gridSize.toDouble / nbCellsByDimension
      val values = (1 to nbCellsByDimension).foldLeft(Seq[Seq[Int]]())((elems, index) => elems :+ worldToInts(world, index - 1)).transpose


      val agentSize = cellDimension / 3
      val thirdAgentSize = (agentSize / 3)
      val offsetY = agentSize / 2
      val offsetX = agentSize / 3

      val agentPath = path().m(0, agentSize).l(thirdAgentSize, 0).l(2 * thirdAgentSize, agentSize).l(thirdAgentSize, agentSize * 5 / 6).z

      val scene = svgTags.svg(
        width := gridSize,
        height := gridSize
      ).render

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

      simulation.agents.foreach { agent =>
        val (ax, ay) = ((Agent.position(agent)._2 * gridSize), (Agent.position(agent)._1) * gridSize)
        val rotation = math.toDegrees(math.atan(Agent.velocity(agent)._2 / Agent.velocity(agent)._1))
        println("ROTATION " + rotation)
        val color = agent match {
          case h: Human => "green"
          case _ => "red"
        }

        //scene.appendChild(svgTags.rect(x := 0, y := 0, height := agentSize, width := 2*thirdAgentSize, fill := "grey", transform := s"rotate($rotation, ${ax}, ${ay}) translate(${ax - offsetX},${ay - offsetY})").render)
        scene.appendChild(agentPath.render(fill := color, transform := s"rotate($rotation, ${ax}, ${ay}) translate(${ax - offsetX},${ay - offsetY})").render)
       // scene.appendChild(svgTags.circle(cx := ax, cy := ay, r := cellDimension / 50,  fill := "orange").render)
      }


      org.scalajs.dom.document.body.appendChild(scene)
    }

    buildWorld(side, simulation.world)
  }

}