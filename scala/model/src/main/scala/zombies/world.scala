package zombies

import move._
import space._

import scala.util.Random

object world {

  sealed trait Cell
  case object Wall extends Cell
  case class Flor(altitude: Double, slope: (Double, Double)) extends Cell

  object World {
    def cell(world: World, x: Int, y: Int) =
      if(x < 0 || x >= world.side || y < 0 || y >= world.side) None
      else Some(world.cells(x)(y))


    def parse(s: String) = {
      def toWall(c: Char): Option[Cell] = c match {
        case '0' => Some(Flor(0.0, (0.0, 0.0)))
        case '+' => Some(Wall)
        case _ => None
      }

      val cells = s.split("\n").map(l => l.flatMap(toWall).toArray).filter(!_.isEmpty)

      val xMax = cells.size
      val yMax = cells.map(_.size).max

      assert(cells.forall(_.size == yMax), s"All lines should have the same length: ${cells.map(_.size).mkString(" ")}")
      assert(xMax == yMax, s"World should be a square, wrong dimensions: $xMax x $yMax")

      World(cells, xMax)
    }

    def locationIsInTheWorld(world: World, x: Int, y: Int) =
      x >= 0 && y >= 0 && x < world.side && y < world.side

    def neighbors(w: World, x: Int, y: Int, neighborhoodSize: Int) =
      space.neighbors(cell(w, _, _), x, y, neighborhoodSize)

    def computeAltitude(world: World, decay: Double) = {
      val cells = copyCells(world.cells)

      def pass(): Unit = {
        var finished = true

        for {
          x <- 0 until world.side
          y <- 0 until world.side
          f@Flor(cellLevel, _) <- Seq(cells(x)(y))
          newLevel =
            neighbors(world.copy(cells = cells), x, y, 1).map {
              case Flor(l, _) => l
              case Wall => 1.0
            }.max - decay
          if newLevel >= decay
          if cellLevel != newLevel
        } {
          cells(x)(y) = f.copy(altitude =  newLevel)
          finished = false
        }

        if(!finished) pass
      }

      pass()
      world.copy(cells = cells)
    }

    def computeSlope(world: World, norm: Double) = {
      val cells = copyCells(world.cells)

      def slope(x: Int, y: Int, level: Double) = {
        val slopes =
          for {
            ox <- -1 to 1
            oy <- -1 to 1
            if locationIsInTheWorld(world, x + ox, y + oy)
            f@Flor(cellLevel, _) <- Seq(cells(x + ox)(y + oy))
          } yield (ox * (level - cellLevel), oy * (level - cellLevel))

        val (slopesX, slopesY) = slopes.unzip

        normalize((slopesX.sum / slopesX.size, slopesY.sum / slopesY.size), norm)
      }

      for {
        x <- 0 until world.side
        y <- 0 until world.side
        f@Flor(cellLevel, _) <- Seq(cells(x)(y))
      } cells(x)(y) = f.copy(slope = slope(x, y, cellLevel))

      world.copy(cells = cells)
    }

    def copyCells(cells: Array[Array[Cell]]) = cells.map(_.map(identity))

    def isWall(world: World, x: Int, y: Int) = cell(world, x, y) match {
      case Some(Wall) => true
      case _ => false
    }

    def minCellSide(world: World) = 1.0 / world.side
    def cellDiagonal(world: World) = space.cellDiagonal(world.side)
  }

  case class World(cells: Array[Array[Cell]], side: Int)

  def generatePosition(world: World, rng: Random): Position = {
    val v = randomVector(rng)
    val p = positionToLocation(v, world.side, world.side)
    if(World.isWall(world, p._1, p._2)) generatePosition(world, rng) else v
  }


}
