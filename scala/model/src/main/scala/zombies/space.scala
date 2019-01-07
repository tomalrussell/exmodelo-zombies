package zombies

import zombies.move.{Location, Position}

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.reflect.ClassTag

object space {

  def neighbors[T](get: (Int, Int) => Traversable[T], x: Int, y: Int, neighborhoodSize: Int) = {
    val res = ListBuffer[T]()

    for {
      ox <- -neighborhoodSize to neighborhoodSize
      oy <- -neighborhoodSize to neighborhoodSize
    } res ++= get(x + ox, y + oy)

    res.toList
  }


  def cellSide(n: Int) = (1.0 / n)
  def cellDiagonal(n: Int) = math.sqrt(2.0) * cellSide(n)

  def distance(x1: (Double, Double), x2: (Double, Double)) =
    math.sqrt(math.pow(x1._1 - x2._1, 2) + math.pow(x1._2 - x2._2, 2))

  def closest[T](t: T, v: Traversable[T], position: T => Position) =
    v.minBy(v => distance(position(t), position(v)))

  def direction(x1: (Double, Double), x2: (Double, Double)) =
    (x2._1 - x1._1, x2._2 - x1._2)

  def opposite(v: (Double, Double)) = (-v._1, -v._2)

  def rotate(v: (Double, Double), teta: Double) = {
    import math._
    val (x, y) = v
    (x * cos(teta) - y * sin(teta), x * sin(teta) + y * cos(teta))
  }

  object Index {

    def apply[T: ClassTag](content: Iterable[T], location: T => Location, side: Int): Index[T] = {
      val cellBuffer: Array[Array[ArrayBuffer[T]]] = Array.fill(side, side) {
        ArrayBuffer[T]()
      }

      for {
        s <- content
        (i, j) = location(s)
      } cellBuffer(i)(j) += s

      Index[T](cellBuffer.map(_.map(_.toArray)), side)
    }


    def get[T](index: Index[T], x: Int, y: Int) =
      if(x > 0 && x < index.side && y > 0 && y < index.side) index.cells(x)(y).toTraversable
      else Traversable.empty


  }

  case class Index[T](cells: Array[Array[Array[T]]], side: Int)

}
