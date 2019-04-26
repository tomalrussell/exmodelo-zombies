package zombies

import zombies.agent.Agent
import zombies.simulation.{Event, SimulationResult}
import zombies.space.Position

object observable {

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


  def zombiesDynamic(results: SimulationResult, by: Int = defaultGroupSize) = {
    val (simulations, _) = results
    simulations.take(1).map(_.agents.collect(Agent.zombie).size).toArray ++  simulations.map(_.agents.collect(Agent.zombie).size).grouped(by).map(_.last)
  }


  def rescuedDynamic(results: SimulationResult, by: Int = defaultGroupSize) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.rescued).size).grouped(by).map(_.sum)
  }

  def killedDynamic(results: SimulationResult, by: Int = defaultGroupSize) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.killed).size).grouped(by).map(_.sum)
  }


  def zombifiedDynamic(results: SimulationResult, by: Int = defaultGroupSize) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.zombified).size).grouped(by).map(_.sum)
  }

  def fleeDynamic(results: SimulationResult, by: Int = defaultGroupSize) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.flee).size).grouped(by).map(_.sum)
  }

  def pursueDynamic(results: SimulationResult, by: Int = defaultGroupSize) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.pursue).size).grouped(by).map(_.sum)
  }

  def humansGoneDynamic(results: SimulationResult, by: Int = defaultGroupSize) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.humanGone).size).grouped(by).map(_.sum)
  }

  def zombiesGoneDynamic(results: SimulationResult, by: Int = defaultGroupSize) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.zombieGone).size).grouped(by).map(_.sum)
  }



}
