import zombies.agent.{Agent, PheromoneMechanism}
import zombies.observable.defaultGroupSize
import zombies.simulation.{ArmyOption, NoArmy, NoRedCross, RedCrossOption, Simulation, SimulationResult}
import zombies.world.World

package object zombies {

  implicit def stringToWorld(s: String) = World.parse()(s)

  implicit class ResultDecorator(results: SimulationResult) {
    def humansDynamic(by: Int = defaultGroupSize) = observable.humansDynamic(results, by)
    def walkingHumansDynamic(by: Int = defaultGroupSize) = observable.walkingHumansDynamic(results, by)
    def runningHumansDynamic(by: Int = defaultGroupSize) = observable.runningHumansDynamic(results, by)
    def zombiesDynamic(by: Int = defaultGroupSize) = observable.zombiesDynamic(results, by)
    def walkingZombiesDynamic(by: Int = defaultGroupSize) = observable.walkingZombiesDynamic(results, by)
    def runningZombiesDynamic(by: Int = defaultGroupSize) = observable.runningZombiesDynamic(results, by)
    def rescuedDynamic(by: Int = defaultGroupSize) = observable.rescuedDynamic(results, by)
    def killedDynamic(by: Int = defaultGroupSize) = observable.killedDynamic(results, by)
    def zombifiedDynamic(by: Int = defaultGroupSize) = observable.zombifiedDynamic(results, by)
    def fleeDynamic(by: Int = defaultGroupSize) = observable.fleeDynamic(results, by)
    def pursueDynamic(by: Int = defaultGroupSize) = observable.pursueDynamic(results, by)
    def humansGoneDynamic(by: Int = defaultGroupSize) = observable.humansGoneDynamic(results, by)
    def zombiesGoneDynamic(by: Int = defaultGroupSize) = observable.zombiesGoneDynamic(results, by)

    def totalRescued = observable.totalRescued(results)
    def halfTimeRescued = observable.halfTimeRescued(results)
    def peakTimeRescued(window: Int = defaultGroupSize) = observable.peakTimeRescued(results, window)
    def peakSizeRescued(window: Int = defaultGroupSize) = observable.peakSizeRescued(results, window)

    def totalZombified = observable.totalZombified(results)
    def halfZombified= observable.halfZombified(results)
    def peakTimeZombified(window: Int = defaultGroupSize) = observable.peakTimeZombified(results, window)
    def peakSizeZombified(window: Int = defaultGroupSize) = observable.peakSizeZombified(results, window)

    // spatial observables
    def spatialMoranZombified: Double = observable.spatialMoran(observable.zombified)(results)
    def spatialDistanceMeanZombified: Double = observable.spatialDistanceMean(observable.zombified)(results)
    def spatialEntropyZombified: Double = observable.spatialEntropy(observable.zombified)(results)
    def spatialSlopeZombified: Double = observable.spatialSlope(observable.zombified)(results)
    def spatialRipleyZombified: Double = observable.spatialRipley(observable.zombified)(results)

  }

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

    val state =
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

    simulation.simulate(state, random, steps)
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
