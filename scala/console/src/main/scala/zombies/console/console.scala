package zombies

import com.github.tomaslanger.chalk.Ansi
import zombies.agent.Agent
import zombies.simulation.{Event, Simulation}
import zombies.space.Index
import zombies.world.{Cell, Floor, Wall}

object Console {
  def display(simulation: Simulation, events: Iterable[Event]) = {
    val index = Agent.index(simulation.agents, simulation.world.side)
    def toChar(c: Cell, x: Int, y: Int) = c match {
      case Wall => '+'
      case f: Floor =>
        val agents = Index.get(index, x, y)
        if(!agents.isEmpty && agents.forall(Agent.isHuman)) 'H'
        else if(!agents.isEmpty && agents.forall(Agent.isZombie)) 'Z'
        else if(!agents.isEmpty) 'M'
        else if(f.rescueZone) 'X'
        else ' '
      case _ => '?'
    }

    val rescued = events.collect(Event.rescued).size
    val killed = events.collect(Event.killed).size
    val zombified = events.collect(Event.zombified).size

    simulation.world.cells.zipWithIndex.map { case (l, x) => l.zipWithIndex.map { case (c, y) => toChar(c, x, y) }.mkString }.mkString("\n") +
      s"\nHumans: ${simulation.agents.count(Agent.isHuman)}, Informed: ${simulation.agents.collect{Agent.human}.count(_.rescue.informed)}, Alerted: ${simulation.agents.collect{Agent.human}.count(_.rescue.alerted)}, Zombies: ${simulation.agents.count(Agent.isZombie)}, Rescued: ${rescued}, Killed zombies: ${killed}, Zombified: ${zombified}"
  }

  def clear(simulation: Simulation) = {
    print(Ansi.eraseLine())
    print(Ansi.cursorUp(simulation.world.side))
    print(Ansi.cursorLeft(simulation.world.side * 20))
  }
}
