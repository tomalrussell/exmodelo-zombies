import zombies.agent.PheromoneMechanism
import zombies.observable.defaultGroupSize
import zombies.simulation.{ArmyOption, NoArmy, NoRedCross, RedCrossOption, Simulation, SimulationResult}
import zombies.world.World

package object zombies {

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
    def halfRescued = observable.halfRescued(results)
    def peakRescued(window: Int = defaultGroupSize) = observable.peakZombified(results, window)

    def totalZombified = observable.totalZombified(results)
    def halfZombified= observable.halfZombified(results)
    def peakZombified(window: Int = defaultGroupSize) = observable.peakZombified(results, window)
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
    zombiePheromone: PheromoneMechanism = physic.zombiePheromone,
    zombieCanLeave: Boolean = physic.zombieCanLeave,
    zombies: Int = 4,
    walkSpeed: Double = physic.walkSpeed,
    rotationGranularity: Int = 5,
    army: ArmyOption = NoArmy,
    redCross: RedCrossOption = NoRedCross,
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
        zombiePheromone = zombiePheromone,
        zombieCanLeave = zombieCanLeave,
        zombies = zombies,
        walkSpeed = walkSpeed,
        rotationGranularity = rotationGranularity,
        army = army,
        redCross = redCross,
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
    activationDelay: Int,
    efficiencyProbability: Double) =
    simulation.RedCross(
      size,
      exhaustionProbability = exhaustionProbability,
      followProbability = followProbability,
      informProbability = informProbability,
      aggressive = aggressive,
      activationDelay = activationDelay,
      efficiencyProbability = efficiencyProbability
    )

}
