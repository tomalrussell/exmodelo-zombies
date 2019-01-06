package zombies

import monocle._
import monocle.macros._
import monocle.std.all._
import monocle.function.all._

/**
  * Created by reyman on 14/06/16.
  */
object shadow {


  @Lenses case class ShadowLine (shadows:Vector[Shadow] = Vector.empty)
  @Lenses case class Shadow (start:Double, end:Double)

  def projectTile(row:Double, col:Double) = Shadow(col / (row + 2), (col + 1) / (row + 1))
  def isInShadow(line: ShadowLine, projection: Shadow): Boolean = line.shadows.exists { s => contains(s, projection) }
  def contains(s:Shadow, projection:Shadow):Boolean = (s.start <= projection.start) && (s.end >= projection.end)

  def addShadow(shadowToTest: Shadow, shadowLine: ShadowLine): ShadowLine = {
    val i =
      shadowLine.shadows.indexWhere { s =>
        s.start >= shadowToTest.start} match {
        case -1 => if (shadowLine.shadows.nonEmpty) shadowLine.shadows.size else 0
        case x => x
      }

    def overlappingPrevious =
      if (i > 0 && shadowLine.shadows(i - 1).end > shadowToTest.start) Some(ShadowLine.shadows composeOptional index(i - 1)) else None

    def overlappingNext =
      if (i < shadowLine.shadows.length && shadowLine.shadows(i).start < shadowToTest.end)
        Some(ShadowLine.shadows composeOptional index(i))
      else None

    (overlappingPrevious, overlappingNext) match {
      case (Some(sp), Some(sn)) =>
        val snValue = sn.getOption(shadowLine).get
        ((sp composeLens Shadow.end set snValue.end) andThen (ShadowLine.shadows.modify(_.patch(i, Seq(), 1)))) (shadowLine)
      case(None, Some(sn)) =>
        val snValue = sn.getOption(shadowLine).get
        (sn composeLens Shadow.start set(math.min(snValue.start, shadowToTest.start)))(shadowLine)
      case(Some(sp),None ) =>
        val spValue = sp.getOption(shadowLine).get
        (sp composeLens Shadow.end set(math.max(spValue.end, shadowToTest.end)))(shadowLine)
      case(None, None) =>
        (ShadowLine.shadows modify(_.patch(i, Seq(shadowToTest),0)))(shadowLine)
    }

  }

  def visible(position: (Int, Int), isOpaque: (Int, Int) => Boolean, side: (Int, Int), maxRows:Int) =
    (0 until 8).map(o => visibleOctant(position, o, isOpaque, side, maxRows)).foldLeft(Set.empty[(Int, Int)]) { case (a, b) => a ++ b }

  def visibleOctant(position: (Int, Int), octant:Int, isOpaque: (Int, Int) => Boolean, side: (Int, Int), maxRows:Int) = {
    def contains(point: (Int, Int), pos: (Int, Int), size: (Int, Int)) =
      if (point._1 < pos._1) false
      else if (point._1 >= pos._1 + size._1)  false
      else if (point._2 < pos._2) false
      else if (point._2 >= pos._2 + size._2) false
      else true


    def transformOctant(row:Int, col:Int, octant:Int) = {
      octant % 8 match {
        case 0 => (col,-row)
        case 1 => (row, -col)
        case 2 => (row, col)
        case 3 => (col, row)
        case 4 => (-col, row)
        case 5 => (-row, col)
        case 6 => (-row, -col)
        case 7 => (-col, -row)
      }
    }

    def inBounds(point: (Int, Int)) = contains(point, (0,0), side)
    def sumPos(row: Int, col: Int) = {
      val (octX, octY) = transformOctant(row, col, octant)
      (position._1 + octX, position._2 + octY)
    }

    def computeCols(col:Int, row:Int, shadowLine: ShadowLine, visible: List[(Int, Int)]): (ShadowLine, List[(Int, Int)]) = {
      val pos = sumPos(row, col)
      if (col > row || !inBounds(pos)) (shadowLine, visible)
      else {
        val proj = projectTile(row, col)
        val isVisible = !isInShadow(shadowLine, proj)
        val newSL = if (isVisible && isOpaque(pos._1, pos._2)) addShadow(proj, shadowLine) else shadowLine
        computeCols(col + 1, row, newSL, if (isVisible) pos :: visible  else visible)
      }
    }

    def computeRows(row: Int, shadowLine: ShadowLine, visible: List[(Int, Int)]):  List[(Int, Int)] =
      if (row > maxRows || !inBounds(sumPos(row, 0))) visible
      else {
        val (sl, visibleCell) = computeCols(0, row, shadowLine, visible)
        computeRows(row + 1, sl, visibleCell ::: visible)
      }

    computeRows(1, ShadowLine(), List())
  }


}
