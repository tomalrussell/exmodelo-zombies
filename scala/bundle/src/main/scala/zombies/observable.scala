package zombies

import zombies.agent.{Agent, Metabolism}
import zombies.simulation.{Event, SimulationResult}
import zombies.space.Position

object observable {

  def cumSum(v: Seq[Int]) =
    v.foldLeft(List(0)) { (acc, el)=> (el + acc.head) :: acc  }.reverse.toVector


  def zombified (results : SimulationResult): Vector[Position] = {
    results._2.flatten.collect(Event.zombified).map{ case s => s.human.position }.toVector
  }

  def hitmap(results : SimulationResult, eventLocation: SimulationResult => Vector[Position]) = {
    val world = results._1.head.world
    val worldIntPos = eventLocation(results).map{ p => space.positionToLocation(p, world.side) }.groupBy(identity).mapValues(_.size)

    Array.tabulate[Int](world.side, world.side) { (x, y) => worldIntPos.getOrElse((x, y), 0) }
  }


  val defaultGroupSize = 20

  def humansDynamic(results : SimulationResult, by: Int = defaultGroupSize) = {
    val (simulations, _) = results
    simulations.take(1).map(_.agents.collect(Agent.human).size).toArray ++ simulations.map(_.agents.collect(Agent.human).size).grouped(by).map(_.last)
  }

  def walkingHumansDynamic(results: SimulationResult, by: Int = defaultGroupSize) = {
    val (simulations, _) = results
    val walkingHumans = simulations.map(_.agents.collect(Agent.human).count { h => !Metabolism.isRunning(h.metabolism) })
    walkingHumans.take(1).toArray ++ walkingHumans.grouped(by).map(_.last)
  }

  def runningHumansDynamic(results: SimulationResult, by: Int = defaultGroupSize) = {
    val (simulations, _) = results
    val runningHumans = simulations.map(_.agents.collect(Agent.human).count { h => Metabolism.isRunning(h.metabolism) })
    runningHumans.take(1).toArray ++ runningHumans.grouped(by).map(_.last)
  }

  def zombiesDynamic(results: SimulationResult, by: Int = defaultGroupSize) = {
    val (simulations, _) = results
    simulations.take(1).map(_.agents.collect(Agent.zombie).size).toArray ++ simulations.map(_.agents.collect(Agent.zombie).size).grouped(by).map(_.last)
  }

  def walkingZombiesDynamic(results: SimulationResult, by: Int = defaultGroupSize) = {
    val (simulations, _) = results
    val walkingZombies = simulations.map(_.agents.collect(Agent.zombie).count { z => !z.pursuing })
    walkingZombies.take(1).toArray ++ walkingZombies.grouped(by).map(_.last)
  }

  def runningZombiesDynamic(results: SimulationResult, by: Int = defaultGroupSize) = {
    val (simulations, _) = results
    val runningZombies = simulations.map(_.agents.collect(Agent.zombie).count { z => z.pursuing })
    runningZombies.take(1).toArray ++ runningZombies.grouped(by).map(_.last)
  }

  def rescuedDynamic(results: SimulationResult, by: Int = defaultGroupSize) = eventDynamic(results, by, Event.rescued)
  def killedDynamic(results: SimulationResult, by: Int = defaultGroupSize) = eventDynamic(results, by, Event.killed)
  def zombifiedDynamic(results: SimulationResult, by: Int = defaultGroupSize) = eventDynamic(results, by, Event.zombified)
  def fleeDynamic(results: SimulationResult, by: Int = defaultGroupSize) = eventDynamic(results, by, Event.flee)
  def pursueDynamic(results: SimulationResult, by: Int = defaultGroupSize) = eventDynamic(results, by, Event.pursue)
  def humansGoneDynamic(results: SimulationResult, by: Int = defaultGroupSize) = eventDynamic(results, by, Event.humanGone)
  def zombiesGoneDynamic(results: SimulationResult, by: Int = defaultGroupSize) = eventDynamic(results, by, Event.zombieGone)


  def totalRescued(results: SimulationResult) = totalEvents(results, Event.rescued)
  def halfRescued(results: SimulationResult) = halfEvents(results, Event.rescued)
  def peakRescued(results: SimulationResult, window: Int = defaultGroupSize) = peakEvents(results, window, Event.rescued)

  def totalZombified(results: SimulationResult) = totalEvents(results, Event.zombified)
  def halfZombified(results: SimulationResult) = halfEvents(results, Event.zombified)
  def peakZombified(results: SimulationResult, window: Int = defaultGroupSize) = peakEvents(results, window, Event.zombified)

  private def eventDynamic(results: SimulationResult, by: Int, e: PartialFunction[Event, Any]) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(e).size).grouped(by).map(_.sum)
  }

  private def totalEvents(results: SimulationResult, e: PartialFunction[Event, Any]) = {
    val (_, events) = results
    events.map(_.collect(e).size).sum
  }

  private def halfEvents(results: SimulationResult, e: PartialFunction[Event, Any]) = {
    val (_, events) = results
    val rescuedCum = cumSum(events.map(_.collect(e)).map(_.size))
    val rescued = rescuedCum.last
    val half = rescued / 2
    rescuedCum.indexWhere(c => c >= half)
  }

  /** Return the step where number rescued is maximum over @window simulation steps */
  private def peakEvents(results: SimulationResult, window: Int, e: PartialFunction[Event, Any]) = {
    val dyn = eventDynamic(results, 1, e).sliding(window).map(_.sum)
    val maxRescued = dyn.max
    dyn.indexWhere(_ == maxRescued) + window / 2
  }


}
