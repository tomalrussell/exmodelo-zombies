import zombies.agent.{Agent, PheromoneMechanism}
import zombies.observable.defaultGroupSize
import zombies.simulation.{ArmyOption, NoArmy, NoRedCross, RedCrossOption, Simulation, SimulationResult}
import zombies.world.World

package object zombies extends DSL {

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
    def halfZombified= observable.halfTimeZombified(results)
    def peakTimeZombified(window: Int = defaultGroupSize) = observable.peakTimeZombified(results, window)
    def peakSizeZombified(window: Int = defaultGroupSize) = observable.peakSizeZombified(results, window)

    // spatial observables
    def spatialMoranZombified: Double = observable.spatialMoran(observable.zombified)(results)
    def spatialDistanceMeanZombified: Double = observable.spatialDistanceMean(observable.zombified)(results)
    def spatialEntropyZombified: Double = observable.spatialEntropy(observable.zombified)(results)
    def spatialSlopeZombified: Double = observable.spatialSlope(observable.zombified)(results)
    def spatialRipleyZombified: Double = observable.spatialRipley(observable.zombified)(results)
  }

}
