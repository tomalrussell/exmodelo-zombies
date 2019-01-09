package zombies

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag
import scala.util.Random

object move {

  type Location = (Int, Int)
  type Position = (Double, Double)
  type Velocity = (Double, Double)

  def length(v: (Double, Double)) = {
    val (x, y) = v
    math.sqrt(x * x + y * y)
  }

  def normalize(v: (Double, Double), norm: Double = 1.0) = {
    val (x, y) = v
    val l =  length(v)
    if(l == 0) v else (x * norm / l, y * norm / l)
  }

  def bound(v: (Double, Double), min: Double, max: Double): (Double, Double) = {
    val l = length(v)
    if (l < min) normalize(v, min)
    else if (l > max) normalize(v, max)
    else v
  }

  def sum(v1: (Double, Double), v2: (Double, Double)): (Double, Double) = {
    val (x, y) = v1
    val (dx, dy) = v2
    (x + dx, y + dy)
  }

  def sum(v: Seq[(Double, Double)]): (Double, Double) = v.foldLeft((0.0, 0.0))(move.sum(_, _))

  def diff(v1: (Double, Double), v2: (Double, Double)) = (v2._1 - v1._1, v2._2 - v1._2)

  def randomUnitVector(rng: Random) = (2 * rng.nextDouble() - 1, 2 * rng.nextDouble() - 1)

  def positionToLocation(v: Position, xSize: Int, ySize: Int): Location = {
    val (x, y) = v
    ((xSize * x).toInt, (ySize * y).toInt)
  }


}
