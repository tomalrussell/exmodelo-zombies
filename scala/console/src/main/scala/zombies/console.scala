package zombies

import zombies.agent._
import zombies.world._
import zombies.space._

object console {
  def display(world: World, agents: Vector[Agent] = Vector.empty, levels: Boolean = false) = {
    val index = Agent.index(agents, world.side)
    def toChar(c: Cell, x: Int, y: Int) = c match {
      case Wall => '+'
      case Flor(l, _) =>
        val agents = Index.get(index, x, y)
        if(!agents.isEmpty && agents.forall(Agent.isHuman)) 'H'
        else if(!agents.isEmpty && agents.forall(Agent.isZombie)) 'Z'
        else if(!agents.isEmpty) 'M'
        else if(levels) math.round(l * 10).toInt.toString.take(1) else ' '
      case _ => '?'
    }

    world.cells.zipWithIndex.map { case (l, x) => l.zipWithIndex.map { case (c, y) => toChar(c, x, y) }.mkString }.mkString("\n")
  }
}