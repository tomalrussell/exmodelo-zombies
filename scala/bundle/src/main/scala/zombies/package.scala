import zombies.observable.defaultGroupSize
import zombies.simulation.SimulationResult

package object zombies {
  implicit class ResultDecorator(results: SimulationResult) {
    def humansDynamic(by: Int = defaultGroupSize) = observable.humansDynamic(results)
    def zombiesDynamic(by: Int = defaultGroupSize) = observable.zombiesDynamic(results)
    def rescuedDynamic(by: Int = defaultGroupSize) = observable.rescuedDynamic(results)
    def killedDynamic(by: Int = defaultGroupSize) = observable.rescuedDynamic(results)
    def zombifiedDynamic(by: Int = defaultGroupSize) = observable.zombiesDynamic(results, by = by)
    def fleeDynamic(by: Int = defaultGroupSize) = observable.fleeDynamic(results, by = by)
    def pursueDynamic(by: Int = defaultGroupSize) = observable.pursueDynamic(results, by = by)
    def humansGoneDynamic(by: Int = defaultGroupSize) = observable.humansGoneDynamic(results, by = by)
    def zombiesGoneDynamic(by: Int = defaultGroupSize) = observable.zombiesGoneDynamic(results, by = by)
  }
}
