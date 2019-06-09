package zombies

import zombies.agent.Agent
import zombies.simulation.{ArmyOption, NoArmy, NoRedCross, RedCrossOption, Simulation}
import zombies.world.World

/*
 * Copyright (C) 2019 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

trait DSL {

  def physic = zombies.simulation.physic

  def stadium = simulation.environment.stadium
  def jaude = simulation.environment.jaude
  def quarantine = simulation.environment.quarantine
  def square = simulation.environment.square

  def zombieInvasion(
    world: World = quarantine,
    infectionRange: Double = physic.infectionRange,
    humanRunSpeed: Double = physic.humanRunSpeed,
    humanPerception: Double = physic.humanPerception,
    humanMaxRotation: Double = physic.humanMaxRotation,
    humanExhaustionProbability: Double = physic.humanExhaustionProbability,
    humanFollowProbability: Double = physic.humanFollowProbability,
    humanInformedRatio: Double = physic.humanInformedRatio,
    humanInformProbability: Double = physic.humanInformProbability,
    humanFightBackProbability: Double = physic.humanFightBackProbability,
    humans: Int = 250,
    zombieRunSpeed: Double = physic.zombieRunSpeed,
    zombiePerception: Double = physic.zombiePerception,
    zombieMaxRotation: Double = physic.zombieMaxRotation,
    zombiePheromoneEvaporation: Double = physic.zombiePheromoneEvaporation,
    zombieCanLeave: Boolean = physic.zombieCanLeave,
    zombies: Int = 4,
    walkSpeed: Double = physic.walkSpeed,
    rotationGranularity: Int = 5,
    army: ArmyOption = NoArmy,
    redCross: RedCrossOption = NoRedCross,
    agents: Seq[(World, scala.util.Random) => Agent] = Seq(),
    steps: Int = 500,
    random: scala.util.Random) = {

    val state = initialize(
      world = world,
      infectionRange = infectionRange,
      humanRunSpeed = humanRunSpeed,
      humanPerception = humanPerception,
      humanMaxRotation = humanMaxRotation,
      humanExhaustionProbability = humanExhaustionProbability,
      humanFollowProbability = humanFollowProbability,
      humanInformedRatio = humanInformedRatio,
      humanInformProbability = humanInformProbability,
      humanFightBackProbability = humanFightBackProbability,
      humans = humans,
      zombieRunSpeed = zombieRunSpeed,
      zombiePerception = zombiePerception,
      zombieMaxRotation = zombieMaxRotation,
      zombiePheromoneEvaporation = zombiePheromoneEvaporation,
      zombieCanLeave = zombieCanLeave,
      zombies = zombies,
      walkSpeed = walkSpeed,
      rotationGranularity = rotationGranularity,
      army = army,
      redCross = redCross,
      agents = agents,
      random = random)

    simulation.simulate(state, random, steps)
  }

  def initialize(
    world: World = quarantine,
    infectionRange: Double = physic.infectionRange,
    humanRunSpeed: Double = physic.humanRunSpeed,
    humanPerception: Double = physic.humanPerception,
    humanMaxRotation: Double = physic.humanMaxRotation,
    humanExhaustionProbability: Double = physic.humanExhaustionProbability,
    humanFollowProbability: Double = physic.humanFollowProbability,
    humanInformedRatio: Double = physic.humanInformedRatio,
    humanInformProbability: Double = physic.humanInformProbability,
    humanFightBackProbability: Double = physic.humanFightBackProbability,
    humans: Int = 250,
    zombieRunSpeed: Double = physic.zombieRunSpeed,
    zombiePerception: Double = physic.zombiePerception,
    zombieMaxRotation: Double = physic.zombieMaxRotation,
    zombiePheromoneEvaporation: Double = physic.zombiePheromoneEvaporation,
    zombieCanLeave: Boolean = physic.zombieCanLeave,
    zombies: Int = 4,
    walkSpeed: Double = physic.walkSpeed,
    rotationGranularity: Int = 5,
    army: ArmyOption = NoArmy,
    redCross: RedCrossOption = NoRedCross,
    agents: Seq[(World, scala.util.Random) => Agent] = Seq(),
    steps: Int = 500,
    random: scala.util.Random) = {
    Simulation.initialize(
      world = world,
      infectionRange = infectionRange,
      humanRunSpeed = humanRunSpeed,
      humanPerception = humanPerception,
      humanMaxRotation = humanMaxRotation,
      humanExhaustionProbability = humanExhaustionProbability,
      humanFollowProbability = humanFollowProbability,
      humanInformedRatio = humanInformedRatio,
      humanInformProbability = humanInformProbability,
      humanFightBackProbability = humanFightBackProbability,
      humans = humans,
      zombieRunSpeed = zombieRunSpeed,
      zombiePerception = zombiePerception,
      zombieMaxRotation = zombieMaxRotation,
      zombiePheromoneEvaporation = zombiePheromoneEvaporation,
      zombieCanLeave = zombieCanLeave,
      zombies = zombies,
      walkSpeed = walkSpeed,
      rotationGranularity = rotationGranularity,
      army = army,
      redCross = redCross,
      agents = agents.map(_(world, random)),
      random = random)
  }


  def Army(
    size: Int,
    fightBackProbability: Double = 1.0,
    exhaustionProbability: Double = physic.humanExhaustionProbability,
    perception: Double = physic.humanPerception,
    runSpeed: Double = physic.humanRunSpeed,
    followProbability: Double = physic.humanFollowProbability,
    maxRotation: Double = physic.humanMaxRotation,
    informProbability: Double = 0.0,
    aggressive: Boolean = true) =
    simulation.Army(
      size,
      fightBackProbability = 1.0,
      exhaustionProbability = exhaustionProbability,
      perception = perception,
      runSpeed = runSpeed,
      followProbability = followProbability,
      maxRotation = maxRotation,
      informProbability = informProbability,
      aggressive = aggressive
    )


  def RedCross(
    size: Int,
    exhaustionProbability: Option[Double] = None,
    followProbability: Double = 0.0,
    informProbability: Double = physic.humanInformProbability,
    aggressive: Boolean = true,
    activationDelay: Int = 10,
    efficiencyProbability: Double = 1.0) =
    simulation.RedCross(
      size,
      exhaustionProbability = exhaustionProbability,
      followProbability = followProbability,
      informProbability = informProbability,
      aggressive = aggressive,
      activationDelay = activationDelay,
      efficiencyProbability = efficiencyProbability
    )

  //
  //  def Human(walkSpeed: Double, runSpeed: Double, exhaustionProbability: Double, perception: Double, maxRotation: Double, followRunningProbability: Double, fight: Fight, rescue: Rescue, canLeave: Boolean, antidote: AntidoteMechanism = NoAntidote, function: Function = Civilian, rng: Random) = {
  //      val p = Agent.randomPosition(world, rng)
  //      val v = Agent.randomVelocity(walkSpeed, rng)
  //      (world: World)Human(p, v, Metabolism(walkSpeed, runSpeed, exhaustionProbability, false, false), perception, maxRotation, followRunningProbability, fight, rescue = rescue, canLeave = canLeave, antidote = antidote, function = function)
  //    }


}

object api extends DSL