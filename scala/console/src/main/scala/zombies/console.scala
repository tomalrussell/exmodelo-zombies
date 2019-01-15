package zombies

import com.github.tomaslanger.chalk._
import zombies.agent._
import zombies.world._
import zombies.space._

object console {
  def display(world: World, agents: Vector[Agent] = Vector.empty) = {
    val index = Agent.index(agents, world.side)
    def toChar(c: Cell, x: Int, y: Int) = c match {
      case Wall => '+'
      case f: Floor =>
        val agents = Index.get(index, x, y)
        if(!agents.isEmpty && agents.forall(Agent.isHuman)) 'H'
        else if(!agents.isEmpty && agents.forall(Agent.isZombie)) 'Z'
        else if(!agents.isEmpty) 'M'
      case _ => '?'
    }

    world.cells.zipWithIndex.map { case (l, x) => l.zipWithIndex.map { case (c, y) => toChar(c, x, y) }.mkString }.mkString("\n")
  }

  def clear(world: World) = {
    print(Ansi.cursorUp(world.side - 1))
    print(Ansi.cursorLeft(world.side))
  }
}
