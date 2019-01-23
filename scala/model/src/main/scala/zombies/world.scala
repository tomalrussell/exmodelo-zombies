package zombies

import space._

import scala.util.Random

import scala.scalajs.js.annotation._

@JSExportTopLevel("world")
object world {

  sealed trait Cell
  case object Wall extends Cell
  case class Floor(wallSlope: Vector[Slope] = Vector(), rescueSlope: Vector[Slope] = Vector(), rescueZone: Boolean = false) extends Cell
  case class Slope(x: Double = 0.0, y: Double = 0.0, intensity: Double = 0)

  object World {
    def get(world: World, x: Int, y: Int) = space.get(world.cells, x, y)

    def parse(altitudeLambdaDecay: Double = 1.0, slopeIntensity: Double = 0.1)(worldDescription: String) = {
      def parse(s: String) = {
        def toWall(c: Char): Option[Cell] = c match {
          case '0' => Some(Floor())
          case '+' => Some(Wall)
          case 'R' => Some(Floor(rescueZone = true))
          case _ => None
        }

        val cells = s.split("\n").map(l => l.flatMap(toWall).toArray).filter(!_.isEmpty)

        val xMax = cells.size
        val yMax = cells.map(_.size).max

        assert(cells.forall(_.size == yMax), s"All lines should have the same length: ${cells.map(_.size).mkString(" ")}")
        assert(xMax == yMax, s"World should be a square, wrong dimensions: $xMax x $yMax")

        World(cells, xMax)
      }
      val world = parse(worldDescription)
      World.computeRescueSlope(World.computeWallSlope(world, altitudeLambdaDecay, slopeIntensity))
    }


    def locationIsInTheWorld(world: World, x: Int, y: Int) =
      x >= 0 && y >= 0 && x < world.side && y < world.side

    def neighbors(w: World, x: Int, y: Int, neighborhoodSize: Int) =
      space.neighbors(get(w, _, _), x, y, neighborhoodSize)

    def computeDistance(world: World, isOrigin: (Int, Int) => Boolean) = {
      val distances = Array.tabulate(world.side, world.side) { (x, y) => if(isOrigin(x, y)) -1.0 else Double.PositiveInfinity }

      def pass(): Unit = {
        var finished = true

        for {
          x <- 0 until world.side
          y <- 0 until world.side
          previousDistance = distances(x)(y)
          if !isOrigin(x, y)
          if !isWall(world, x, y)
          newDistance = space.neighbors(space.get(distances, _, _), x, y, 1, center = false).min + 1.0
          if previousDistance != newDistance
        } {
          distances(x)(y) = newDistance
          finished = false
        }

        if(!finished) pass
      }

      pass()
      distances
    }


    def slope(world: World, x: Int, y: Int, levels: Array[Array[Double]], intensity: (Double, Double) => Double) = {
      val level = levels(x)(y)
      val slopes =
        for {
          ox <- -1 to 1
          oy <- -1 to 1
          if ox != 0 || oy != 0
          if locationIsInTheWorld(world, x + ox, y + oy)
          fLevel = levels(x + ox)(y + oy)
          if fLevel < level
        } yield Slope(ox, oy, intensity(level, fLevel))
      slopes.toVector
    }


    def computeRescueSlope(world: World) = {
      val levels = computeDistance(world, isRescueCell(world, _, _))
      val cells = copyCells(world.cells)

      for {
        x <- 0 until world.side
        y <- 0 until world.side
        c@Floor(_, _, _) <- Seq(cells(x)(y))
        if !levels(x)(y).isInfinite
      } cells(x)(y) = c.copy(rescueSlope = slope(world, x, y, levels, (_, _) => 1.0))

      world.copy(cells = cells)
    }

    def computeWallSlope(world: World, lambda: Double, intensity: Double) = {
      def computeLevel(world: World, lambda: Double) = {
        val distances = computeDistance(world, isWall(world, _, _))

        def toExponential(cells: Array[Array[Cell]]) =
          cells.zipWithIndex.map { case (l, x) =>
            l.zipWithIndex.map { case(c, y) =>
              c match {
                case f: Floor => math.exp(-lambda * distances(x)(y))
                case x => Double.PositiveInfinity
              }
            }
          }

        toExponential(world.cells)
      }

      val levels = computeLevel(world, lambda)
      val cells = copyCells(world.cells)

      def computeIntensity(levelFrom: Double, levelTo: Double) = (levelFrom - levelTo) * intensity

      for {
        x <- 0 until world.side
        y <- 0 until world.side
        c@Floor(_, _, _) <- Seq(cells(x)(y))
      } cells(x)(y) = c.copy(wallSlope = slope(world, x, y, levels, computeIntensity))

      world.copy(cells = cells)
    }

    def copyCells(cells: Array[Array[Cell]]) = cells.map(_.map(identity))

    def isWall(world: World, x: Int, y: Int) = get(world, x, y) match {
      case Some(Wall) => true
      case _ => false
    }

    def isRescueCell(world: World, x: Int, y: Int) = get(world, x, y) match {
      case Some(f: Floor) => f.rescueZone
      case _ => false
    }

    def minCellSide(world: World) = 1.0 / world.side
    def cellDiagonal(world: World) = space.cellDiagonal(world.side)

    def visibleNeighborhoodCache(world: World, range: Double): NeighborhoodCache = {
      val neighborhoodSize = math.ceil(range / space.cellSide(world.side)).toInt
      def visible(location: Location) = shadow.visible(location, World.isWall(world, _, _), (world.side, world.side), neighborhoodSize)
      Array.tabulate(world.side, world.side) { (x, y) => if(isWall(world, x, y)) Array.empty else visible(x, y).toArray }
    }

    def randomPosition(world: World, rng: Random): Position = {
      val v = randomUnitVector(rng)
      val p = positionToLocation(v, world.side, world.side)
      if(World.isWall(world, p._1, p._2)) randomPosition(world, rng) else v
    }

    def jaude = parse() {
      """+++++++00000+++++++++++0000+++++++++++++
        |+++++++00000+++++++++++0000+++++++++++++
        |+++++++00000+++++++++++0000+++++++++++++
        |+++++++00000+++++++++++0000+++++++++++++
        |+++++++00000+++++++++++0000+++++++++++++
        |++++0000000000000++++++0000+++++++++++++
        |++++0000000000000++++++00000000000000000
        |++++0000000000000++++++00000000000000000
        |++++0000000000000++++++00000000000000000
        |++++0000000000000++++++0000+++++++++++++
        |++++0000000000000++++++0000+++++++++++++
        |++++0000000000000++++++0000+++++++++++++
        |++++0000000000000++++++0000+++++++++++++
        |++++++++++++00000++++++0000+++++++++++++
        |++++++++++++000000000000000+++++++++++++
        |++++++++++++00000000000000++++++++++++++
        |++++++++++++++++0000000000++++++++++++++
        |++++++++++++++++0000R00000++++++++++++++
        |++++++++++++++++0000000000++++++++++++++
        |++++++++++++++++0000000000++++++++++++++
        |++++++++++++++++0000000000++++++++++++++
        |++++++++++++++++0000000000++++++++++++++
        |0000000000000000000000000000000000000000
        |0000000000000000000++++00000000000000000
        |0000000000000000000++++00000000000000000
        |0000000000000000000000000000000000000000
        |++++++++++++++++00000000000+++++++++++++
        |++++++++++++++++00000000000+++++++++++++
        |++++++++++++++++00000000000+++++++++++++
        |++++++++++++++++++++++00000+++++++++++++
        |++++++++++++++++++++++00000+++++++++++++
        |+++++0000000000000++++00000+++++++++++++
        |+++++0000000000000++++00000+++++++++++++
        |+++++0000000000000++++00000+++++++++++++
        |+++++0000000000000000000000+++++++++++++
        |+++++0000000000000000000000+++++++++++++
        |+++++0000000000000000000000+++++++++++++
        |+++++0000000000000000000000+++++++++++++
        |+++++0000000000000++++00000+++++++++++++
        |++++++++++++++++++++++00000+++++++++++++
        |""".stripMargin
    }


    def square(side: Int) = parse() {
      s"""${"+" * side}\n""" +
        s"""+${"0" * (side - 2)}+\n""" * (side - 2) +
        s"""${"+" * side}\n"""
    }


    def place(side: Int, halfDoorSize: Int) = parse() {
      val doorSize = halfDoorSize * 2
      assert(side > doorSize)

      val wallSize = (side - doorSize) / 2

      s"""${"+" * wallSize}${"0" * doorSize}${"+" * wallSize}\n""" +
        s"""+${"0" * (side - 2)}+\n""" * (wallSize - 1) +
        s"""${"0" * side}\n""" * doorSize +
        s"""+${"0" * (side - 2)}+\n""" * (wallSize - 1) +
        s"""${"+" * wallSize}${"0" * doorSize}${"+" * wallSize}\n"""
    }

  }

  case class World(cells: Array[Array[Cell]], side: Int)

  type NeighborhoodCache = Array[Array[Array[(Int, Int)]]]



}
