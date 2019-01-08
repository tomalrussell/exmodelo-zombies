package zombies

import scalatags.JsDom.all._
import org.scalajs.dom
import org.scalajs.dom.raw.Event
import scalatags.JsDom.svgAttrs.{height, style, width, x, y}
import scalatags.JsDom.svgTags

import scala.scalajs.js.annotation._
import scala.util.Random
import zombies.space._

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

  val zombieMaxRotation = 45
  val humanMaxRotation = 60

  val humans = 250
  val zombies = 4

  val rng = new Random(42)
  val doorSize = 16
  val wallSize = (side - doorSize) / 2


    def buildWorld(nbCellsByDimension: Int, worldDescription: String) = {

      val gridSize = 1000

      val cellDimension = gridSize.toDouble / nbCellsByDimension
      val values = (1 to nbCellsByDimension).foldLeft(Seq[Seq[Double]]())((elems, _) => elems) //:+ randomDoubles(nbCellsByDimension))

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

      org.scalajs.dom.document.body.appendChild(scene)
    }
  }
}