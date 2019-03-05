package zombies

import world._
import space._
import simulation._
import zombies.world.World.copyCells

import scala.collection.mutable.ListBuffer
import scala.util.Random

object agent {


  sealed trait Agent
  case class Human(position: Position, velocity: Velocity, metabolism: Metabolism, perception: Double, maxRotation: Double, followRunningProbability: Double, fight: Fight, rescue: Rescue, canLeave: Boolean, antidote: AntidoteMechanism) extends Agent
  case class Zombie(position: Position, velocity: Velocity, walkSpeed: Double, runSpeed: Double, perception: Double, maxRotation: Double, pursuing: Boolean = false) extends Agent
  case class Metabolism(walkSpeed: Double, runSpeed: Double, exhaustionProbability: Double, run: Boolean, exhausted: Boolean)

  case class Rescue(informed: Boolean = false, alerted: Boolean = false, reach: Boolean = false, informProbability: Double = 0.0, noFollow: Boolean = false)
  case class Fight(fightBackProbability: Double, aggressive: Boolean = false)

  sealed trait PheromoneMechanism
  case object NoPheromone extends PheromoneMechanism
  case class Pheromone(evaporation: Double) extends PheromoneMechanism


  sealed trait AntidoteMechanism
  case object NoAntidote extends AntidoteMechanism

  object Antidote {
    def activated(antidote: Antidote) = antidote.activationDelay <= 0
  }

  case class Antidote(activationDelay: Int, efficiencyProbability: Double, exhaustionProbability: Double, taken: Boolean = false) extends AntidoteMechanism

  object Agent {

    def isHuman(agent: Agent) = agent match {
      case _: Human => true
      case _ => false
    }

    def isZombie(agent: Agent) = agent match {
      case _: Zombie => true
      case _ => false
    }

    def human: PartialFunction[Agent, Human] = {
      case h: Human => h
    }

    def zombie: PartialFunction[Agent, Zombie] = {
      case z: Zombie => z
    }

    def zombify(zombie: Zombie, human: Human) =
      zombie.copy(
        position = human.position,
        velocity = human.velocity
      )

    def position(agent: Agent) = agent match {
      case h: Human => h.position
      case z: Zombie => z.position
    }

    def velocity(agent: Agent) = agent match {
      case h: Human => h.velocity
      case z: Zombie => z.velocity
    }

    def perception(agent: Agent) = agent match {
      case h: Human => h.perception
      case z: Zombie => z.perception
    }

    def canLeave(agent: Agent) = agent match {
      case h: Human => h.canLeave
      case z: Zombie => true
    }

    def location(agent: Agent, side: Int): Location = positionToLocation(position(agent), side)

    def index(agents: Vector[Agent], side: Int) = Index[Agent](agents, location(_, side), side)

    def randomPosition(world: World, rng: Random) = World.randomPosition(world, rng)
    def randomVelocity(maxSpeed: Double, rng: Random) = {
      val (x, y) = randomUnitVector(rng)
      normalize((x * 2 - 1, y * 2 - 1), maxSpeed)
    }


    def visibleNeighbors(index: Index[Agent], agent: Agent, range: Double, world: World) = {
      val neighborhoodSize = math.ceil(range / space.cellSide(index.side)).toInt
      val location = Agent.location(agent, index.side)
      shadow.visible(location, World.isWall(world, _, _), (index.side, index.side), neighborhoodSize).
        flatMap { case(x, y) => Index.get(index, y, y) }.
        filter(n => distance(Agent.position(n), Agent.position(agent)) < range)
    }

    def neighbors(index: Index[Agent], agent: Agent, range: Double) = {
      val neighborhoodSize = math.ceil(range / space.cellSide(index.side)).toInt
      val (x, y) = Agent.location(agent, index.side)
      space.neighbors(Index.get(index, _, _), x, y, neighborhoodSize).filter(n => distance(Agent.position(n), Agent.position(agent)) < range)
    }

    def neighbors(index: Index[Agent], agent: Agent, range: Double, neighborhood: NeighborhoodCache) = {
      val (x, y) = Agent.location(agent, index.side)
      neighborhood(x)(y).
        flatMap { case(x, y) => Index.get(index, x, y) }.
        filter(n => distance(Agent.position(n), Agent.position(agent)) < range)
    }

    def projectedVelocities(granularity: Int, maxRotation: Double, velocity: Velocity, speed: Double) =
      (-granularity to granularity).map(_ * maxRotation).map(r => normalize(rotate(velocity, r), speed))

    def towardsWall(world: World, position: Position, velocity: Velocity, outsideWall: Boolean) = {
      val (x, y) = space.positionToLocation(sum(position, velocity), world.side)
      World.get(world, x, y) match {
        case Some(Wall) => true
        case None => outsideWall
        case _ => false
      }
    }

    def move(world: World, granularity: Int, rng: Random)(agent: Agent) = {

      def computeVelocity(position: Position, velocity: Velocity, maxRotation: Double, speed: Double, canLeave: Boolean) = {
        val adaptedVelocity: Velocity = {
          val (lx, ly) = positionToLocation(position, world.side)
          World.get(world, lx, ly) match {
            case Some(f: Floor) =>
              randomElement(f.wallSlope, rng) match {
                case Some(s) => normalize(sum(velocity, normalize((s.x, s.y), s.intensity * speed)), speed)
                case None => velocity
              }
            case _ => velocity
          }
        }

        val (px, py) = sum(position, adaptedVelocity)
        val (cx, cy) = positionToLocation((px, py), world.side)

        def avoidWall(velocity: Velocity) = {
          val velocities = projectedVelocities(granularity, maxRotation, velocity, speed)
          rng.shuffle(velocities).find(v => !towardsWall(world, position, v, outsideWall = !canLeave)) match {
            case Some(v) => Some(v)
            case None => Some(opposite(velocity))
          }
        }

        val newDirection =
          World.get(world, cx, cy) match {
            case None if canLeave => None
            case None => avoidWall(adaptedVelocity)
            case Some(Wall) => avoidWall(adaptedVelocity)
            case Some(_: Floor) => Some(adaptedVelocity)
          }

        newDirection
      }

      def computePosition(position: Position, velocity: Velocity) = {
        val newPosition = sum(position, velocity)
        if (World.outsideOfTheWorld(world, positionToLocation(newPosition, world.side))) None else Some(newPosition)
      }

      agent match {
        case h: Human =>
          for {
            v <- computeVelocity(h.position, h.velocity, h.maxRotation, Human.speed(h), Agent.canLeave(h))
            p <- computePosition(h.position, v)
          } yield h.copy(position = p, velocity = v)
        case z: Zombie =>
          for {
            v <- computeVelocity(z.position, z.velocity, z.maxRotation, Zombie.speed(z), Agent.canLeave(z))
            p <- computePosition(z.position, v)
          } yield z.copy(position = p, velocity = v)
        case a => Some(a)
      }

    }

    def chooseRescue(agent: Agent) =
      agent match {
        case h: Human => if(h.rescue.informed && h.rescue.alerted && !h.fight.aggressive && h.canLeave) h.copy(rescue = h.rescue.copy(reach = true)) else h
        case a => a
      }

    def rescue(world: World, agents: Vector[Agent]) = {
      val rescued = ListBuffer[Human]()
      val newAgents = ListBuffer[Agent]()

      for {
        a <- agents
      } a match {
        case h: Human =>
          val (x, y) = positionToLocation(h.position, world.side)
          if(h.rescue.informed && h.rescue.alerted && h.canLeave && World.isRescueCell(world, x, y)) rescued += h else newAgents += h
        case a => newAgents += a
      }

      (newAgents.toVector, rescued.toVector)
    }


    def metabolism(rng: Random)(a: Agent) =
      a match  {
        case human: Human => Human.metabolism(human, rng)
        case a => a
      }

    def inform(neighbors: Array[Agent], world: World, rng: Random)(a: Agent) = {
      def lookForInformation(h: Human) = {
        val (x, y) = positionToLocation(h.position, world.side)
        World.get(world, x, y) match {
          case Some(floor: Floor) => if(rng.nextDouble() < floor.information) h.copy(rescue = h.rescue.copy(informed = true)) else h
          case _ => h
        }
      }

      a match {
        case human: Human if !human.rescue.informed && human.rescue.alerted =>
          val informedNeighbors = neighbors.collect(Agent.human).filter(_.rescue.informed)
          val transmit = informedNeighbors.exists(h => rng.nextDouble() < h.rescue.informProbability)
          if (transmit) human.copy(rescue = human.rescue.copy(informed = true)) else lookForInformation(human)
        case human: Human => lookForInformation(human)
        case a => a
      }
    }

    def alert(neighbors: Array[Agent], rng: Random)(a: Agent) =
      a match {
        case h: Human if neighbors.exists(Agent.isZombie) => Human.alerted(h)
        case h: Human =>
          val alertedNeighbors = neighbors.collect(Agent.human)
          val transmit = alertedNeighbors.exists(h => rng.nextDouble() < h.rescue.informProbability)
          if (transmit) h.copy(rescue = h.rescue.copy(alerted = true)) else h
        case a => a
      }

    def takeAntidote(a: Agent) =
      a match {
        case h: Human if h.rescue.alerted =>
          h.antidote match {
            case ant: Antidote if !ant.taken => h.copy(antidote = ant.copy(taken = true))
            case ant: Antidote if ant.activationDelay > 0 => h.copy(antidote = ant.copy(activationDelay = ant.activationDelay - 1))
            case _ => a
          }
        case a => a
      }

    def run(neighbors: Array[Agent])(a: Agent) =
      a match {
        case h: Human if neighbors.exists(Agent.isZombie) || h.rescue.reach => Human.run(h)
        case z: Zombie if neighbors.exists(Agent.isHuman) => Zombie.pursue(z)
        case z: Zombie => Zombie.stopPursuit(z)
        case a => a
      }

    def releasePheromone(agents: Index[Agent], world: World, pheromoneMechanism: PheromoneMechanism) =
      pheromoneMechanism match {
        case Pheromone(evaporation) =>
          val newCells =
            Array.tabulate[Cell] (world.side, world.side) { (x, y) =>
              val pursuingZombies = agents.cells (x) (y).collect (Agent.zombie).count (_.pursuing)
              val cell = world.cells (x) (y)
              cell match {
                case f: Floor => f.copy (pheromone = math.max (f.pheromone + pursuingZombies - evaporation, 0.0) )
                case c => c
              }
            }
          world.copy (cells = newCells)
        case NoPheromone => world
      }


    def fight(index: Index[Agent], agents: Vector[Agent], infectionRange: Double, zombify: (Zombie, Human) => Zombie, rng: Random) = {

      def attackers(index: Index[Agent], agent: Human, range: Double) =
        neighbors(index, agent, range).collect(Agent.zombie)

      val hasDied = collection.mutable.Set[Zombie]()
      val infected = collection.mutable.Map[Human, Zombie]()

      for {
        a <- agents
      } a match  {
        case h: Human =>
          val assailants = attackers(index, h, infectionRange)
          def humanWins() = rng.nextDouble() < h.fight.fightBackProbability
          val lost = assailants.filter(a => !hasDied.contains(a)).filter(_ => !humanWins())

          (rng.shuffle(lost), h.antidote) match {
            case (z :: _, NoAntidote) => infected.put(h, z)
            case (z :: _, a: Antidote) =>
              def worked = rng.nextDouble() < a.efficiencyProbability
              if(Antidote.activated(a) && !worked) infected.put(h, z)
            case (Nil, _) => hasDied ++= assailants
          }
        case _ =>
      }

      val newAgents =
        agents.flatMap {
          case h: Human =>
            infected.get(h) match {
              case Some(z) => Some(zombify(z, h))
              case None => Some(h)
            }
          case z: Zombie =>
            if(!hasDied.contains(z)) Some(z) else None
        }

      (newAgents, infected.keys.toVector, hasDied.toVector)
    }


    def changeDirection(world: World, granularity: Int, neighbors: Array[Agent], rng: Random)(agent: Agent) = {

      def fleeZombies(h: Human, nz: Array[Zombie], rng: Random) = {
        val pv = projectedVelocities(granularity, h.maxRotation, h.velocity, Human.speed(h)).filter(pv => !towardsWall(world, h.position, pv, outsideWall = !Agent.canLeave(h)))
        if (!pv.isEmpty) {
          val nv = rng.shuffle(pv)
          h.copy(velocity = nv.maxBy { v => nz.map(n => distance(position(n), sum(h.position, v))).min })
        } else h
      }

      def pursueHuman(z: Zombie, nh: Array[Human], rng: Random) = {
        val pv = projectedVelocities(granularity, z.maxRotation, z.velocity, Zombie.speed(z))
        val nv = rng.shuffle(pv.filter(pv => !towardsWall(world, z.position, pv, outsideWall = !Agent.canLeave(z))))
        if (nv.isEmpty) z else z.copy(velocity = nv.minBy { v => nh.map(n => distance(position(n), sum(z.position, v))).min })
      }

      def pursueZombie(h: Human, nz: Array[Zombie], rng: Random) = {
        val pv = projectedVelocities(granularity, h.maxRotation, h.velocity, Human.speed(h))
        val nv = rng.shuffle(pv.filter(pv => !towardsWall(world, h.position, pv, outsideWall = !Agent.canLeave(h))))
        if (nv.isEmpty) h else h.copy(velocity = nv.minBy { v => nz.map(n => distance(position(n), sum(h.position, v))).min })
      }

      def runningHumans(agents: Array[Agent]) =
        agents.collect(Agent.human).filter { h => h.metabolism.run && !h.rescue.noFollow }

      def towardsRescue(h: Human, rng: Random) = {
        val (x, y) = location(h, world.side)
        World.get(world, x, y) match {
          case Some(f: Floor) =>
            randomElement(f.rescueSlope, rng) match {
              case Some(s) => h.copy(velocity = normalize((s.x, s.y), Human.speed(h)))
              case None => h
            }
          case _ => followRunning(h, rng)
        }
      }

      def followRunning(h: Human, rng: Random) =
        if (h.followRunningProbability > 0.0) {
          val runningNeighbors = runningHumans(neighbors)
          if (!runningNeighbors.isEmpty && rng.nextDouble() < h.followRunningProbability) Human.run(h.copy(velocity = average(runningNeighbors.map(_.velocity))))
          else h
        } else h

      def followPheromone(z: Zombie, world: World, rng: Random) = {
        val pv = projectedVelocities(granularity, z.maxRotation, z.velocity, Zombie.speed(z))
        val nv = rng.shuffle(pv.filter(pv => !towardsWall(world, z.position, pv, outsideWall = !Agent.canLeave(z))))

        if (!nv.isEmpty) {
          val currentPheromone = World.pheromone(world, positionToLocation(z.position, world.side))

          val reachablePheromones =
            nv.map { v =>
              val projectedPheromone = World.pheromone(world, positionToLocation(sum(z.position, v), world.side))
              projectedPheromone - currentPheromone
            }

          if(reachablePheromones.exists(_ > 0.0)) {
            val newVelocity = (nv zip reachablePheromones).maxBy(_._2)._1
            z.copy(velocity = newVelocity)
          } else z
        } else z
      }

      agent match {
        case h: Human =>
          neighbors.collect(Agent.zombie) match {
            case nz if !nz.isEmpty =>
              if(!h.fight.aggressive) (fleeZombies(h, nz, rng), Some(FleeZombie(h))) else (pursueZombie(h, nz, rng), None)
            case _ if h.rescue.reach => (towardsRescue(h, rng), None)
            case _ => (followRunning(h, rng), None)
          }
        case z: Zombie =>
          neighbors.collect(Agent.human) match {
            case nh if !nh.isEmpty => (pursueHuman(z, nh, rng), Some(PursueHuman(z)))
            case _ => (followPheromone(z, world, rng), None)
          }
      }
    }

  }


  object Metabolism {
    def effectiveSpeed(speed: Metabolism) =  if(speed.run) speed.runSpeed else speed.walkSpeed

    def exhaustionProbability(metabolism: Metabolism, antidote: AntidoteMechanism) =
      antidote match {
        case antidote: Antidote if antidote.taken && !Antidote.activated(antidote) => antidote.exhaustionProbability
        case _ => metabolism.exhaustionProbability
      }

    def metabolism(metabolism: Metabolism, antidote: AntidoteMechanism, rng: Random, timeScale: Int = 10) =
      (metabolism.exhausted, metabolism.run) match {
        case (false, true) if rng.nextDouble() < exhaustionProbability(metabolism, antidote) / timeScale => metabolism.copy(run = false, exhausted = true)
        case (true, _) if rng.nextDouble() < (1 - exhaustionProbability(metabolism, antidote)) / timeScale => metabolism.copy(exhausted = false)
        case (_, _) => metabolism
      }

    def canRun(speed: Metabolism) = !speed.exhausted
  }

  object Human {
    def random(world: World, walkSpeed: Double, runSpeed: Double, exhaustionProbability: Double, perception: Double, maxRotation: Double, followRunningProbability: Double, fight: Fight, rescue: Rescue, rng: Random, canLeave: Boolean) = {
      val p = Agent.randomPosition(world, rng)
      val v = Agent.randomVelocity(walkSpeed, rng)
      Human(p, v, Metabolism(walkSpeed, runSpeed, exhaustionProbability, false, false), perception, maxRotation, followRunningProbability, fight, rescue = rescue, canLeave = canLeave, antidote = NoAntidote)
    }

    def run(h: Human) =
      if(Metabolism.canRun(h.metabolism)) h.copy(velocity = normalize(h.velocity, h.metabolism.runSpeed), metabolism = h.metabolism.copy(run = true))
      else h

    def alerted(h: Human) = run(h).copy(rescue = h.rescue.copy(alerted = true))

    def metabolism(h: Human, rng: Random) = {
      val newSpeed = Metabolism.metabolism(h.metabolism, h.antidote, rng)
      val newVelocity = normalize(h.velocity, Metabolism.effectiveSpeed(newSpeed))
      h.copy(velocity = newVelocity, metabolism = newSpeed)
    }

    def speed(h: Human) = Metabolism.effectiveSpeed(h.metabolism)
  }

  object Zombie {
    def random(world: World, walkSpeed: Double, runSpeed: Double, vision: Double, maxRotation: Double, rng: Random) = {
      val p = Agent.randomPosition(world, rng)
      val v = Agent.randomVelocity(walkSpeed, rng)
      Zombie(p, v, walkSpeed, runSpeed, vision, maxRotation, false)
    }

    def pursue(z: Zombie) = z.copy(pursuing = true)
    def stopPursuit(z: Zombie) = z.copy(pursuing = false)
    def speed(z: Zombie) = if(z.pursuing) z.runSpeed else z.walkSpeed

  }
}
