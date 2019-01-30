package zombies

import world._
import space._
import simulation._

import scala.collection.mutable.ListBuffer
import scala.util.Random

object agent {


  sealed trait Agent
  case class Human(position: Position, velocity: Velocity, metabolism: Metabolism, perception: Double, maxRotation: Double, followRunningProbability: Double, fightBackProbability: Double, rescue: Rescue) extends Agent
  case class Zombie(position: Position, velocity: Velocity, metabolism: Metabolism, perception: Double, maxRotation: Double) extends Agent
  case class Metabolism(walkSpeed: Double, runSpeed: Double, exhaustionProbability: Double, run: Boolean, exhausted: Boolean)

  case class Rescue(informed: Boolean = false, alerted: Boolean = false, awarenessProbability: Double = 0.0)

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

    def towardsWall(world: World, position: Position, velocity: Velocity) = {
      val (x, y) = space.positionToLocation(sum(position, velocity), world.side)
      World.get(world, x, y) match {
        case Some(Wall) => true
        case _ => false
      }
    }

    def move(world: World, granularity: Int, rng: Random)(agent: Agent) = {

      def computeVelocity(position: Position, velocity: Velocity, maxRotation: Double, speed: Double) = {
        val (px, py) = sum(position, velocity)
        val (cx, cy) = positionToLocation((px, py), world.side)

        val newDirection =
          World.get(world, cx, cy) match {
            case None => None
            case Some(Wall) =>
              val velocities = projectedVelocities(granularity, maxRotation, velocity, speed)
              rng.shuffle(velocities).find(v => !towardsWall(world, position, v)) match {
                case Some(v) => Some(v)
                case None => Some(opposite(velocity))
              }
            case Some(f: Floor) =>
              randomElement(f.wallSlope, rng) match {
                case Some(s) =>  Some(sum(velocity, normalize((s.x, s.y), s.intensity * speed)))
                case None => Some(velocity)
              }

          }

        newDirection.map(d => normalize(d, speed))
      }

      def computePosition(position: Position, velocity: Velocity) = {
        val newPosition = sum(position, velocity)
        val (px, py) = newPosition
        if (px < 0 || px > 1 || py < 0 || py > 1) None else Some(newPosition)
      }

      agent match {
        case h: Human =>
          for {
            v <- computeVelocity(h.position, h.velocity, h.maxRotation, Metabolism.effectiveSpeed(h.metabolism))
            p <- computePosition(h.position, v)
          } yield h.copy(position = p, velocity = v)
        case z: Zombie =>
          for {
            v <- computeVelocity(z.position, z.velocity, z.maxRotation, Metabolism.effectiveSpeed(z.metabolism))
            p <- computePosition(z.position, v)
          } yield z.copy(position = p, velocity = v)
        case a => Some(a)
      }

    }

    def rescue(world: World, agents: Vector[Agent]) = {
      val rescued = ListBuffer[Human]()
      val newAgents = ListBuffer[Agent]()

      for {
        a <- agents
      } a match {
        case h: Human =>
          val (x, y) = positionToLocation(h.position, world.side)
          if(h.rescue.informed && h.rescue.alerted && World.isRescueCell(world, x, y)) rescued += h else newAgents += h
        case a => newAgents += a
      }

      (newAgents.toVector, rescued.toVector)
    }


    def metabolism(rng: Random)(a: Agent) =
      a match  {
        case human: Human => Human.metabolism(human, rng)
        case zombie: Zombie => Zombie.metabolism(zombie, rng)
        case a => a
      }

    def inform(neighbors: Array[Agent], rng: Random)(a: Agent) =
      a match {
        case human: Human if !human.rescue.informed && human.rescue.alerted =>
          val informedNeighbors = neighbors.collect(Agent.human).count(_.rescue.informed)
          if(rng.nextDouble() < human.rescue.awarenessProbability * informedNeighbors) human.copy(rescue = human.rescue.copy(informed = true)) else human
        case a => a
      }

    def alert(neighbors: Array[Agent], rng: Random)(a: Agent) =
      a match {
        case h: Human if neighbors.exists(Agent.isZombie) => Human.alerted(h)
        case a => a
      }

    def run(neighbors: Array[Agent])(a: Agent) =
      a match {
        case h: Human if neighbors.exists(Agent.isZombie) || (h.rescue.alerted && h.rescue.informed) => Human.run(h)
        case z: Zombie if neighbors.exists(Agent.isHuman) => Zombie.run(z)
        case a => a
      }

    def pheromon(agents: Index[Agent], world: World, evaporation: Double) = {
      val newCells =
        world.cells.zipWithIndex.map { case (l, i) =>
          l.zipWithIndex.map {
            case (c: Floor, j) =>
              val runningZombies = agents.cells(i)(j).collect(Agent.zombie).count(_.metabolism.run)
              c.copy(pheromone = math.max(c.pheromone + runningZombies - evaporation, 0))
            case (x, _) => x
          }
        }

      world.copy(cells = newCells)
    }

    def fight(index: Index[Agent], agents: Vector[Agent], infectionRange: Double, zombify: (Zombie, Human) => Zombie, rng: Random) = {

      def attackers(index: Index[Agent], agent: Human, range: Double) =
        neighbors(index, agent, range).collect(Agent.zombie)

      val hasDied = collection.mutable.Set[Zombie]()
      val infected = collection.mutable.Map[Human, Zombie]()
      val hasEaten = collection.mutable.Set[Zombie]()

      for {
        a <- agents
      } a match  {
        case (h: Human) =>
          val assailants = attackers(index, h, infectionRange)
          def humanWins() = rng.nextDouble() < h.fightBackProbability
          val lost = assailants.filter(a => !hasDied.contains(a)).filter(_ => !humanWins())

          rng.shuffle(lost) match {
            case z :: t =>
              hasEaten += z
              infected.put(h, z)
            case _ => hasDied ++= assailants
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
            if(!hasDied.contains(z))
              if(hasEaten.contains(z)) {
                val newMetabolism = z.metabolism.copy(run = false, exhausted = false)
                Some(z.copy(metabolism = newMetabolism))
              } else Some(z)
            else None
        }

      (newAgents, infected.keys.toVector, hasDied.toVector)
    }


    def changeDirection(world: World, granularity: Int, neighbors: Array[Agent], rng: Random)(agent: Agent) = {

      def fleeZombies(h: Human, nz: Array[Zombie], rng: Random) = {
        val pv = projectedVelocities(granularity, h.maxRotation, h.velocity, Metabolism.effectiveSpeed(h.metabolism)).filter(pv => !towardsWall(world, h.position, pv))
        if (!pv.isEmpty) {
          val nv = rng.shuffle(pv)
          h.copy(velocity = nv.maxBy { v => nz.map(n => distance(position(n), sum(h.position, v))).min })
        } else h
      }

      def pursueHuman(z: Zombie, nh: Array[Human], rng: Random) = {
        val pv = projectedVelocities(granularity, z.maxRotation, z.velocity, Metabolism.effectiveSpeed(z.metabolism))
        val nv = rng.shuffle(pv.filter(pv => !towardsWall(world, z.position, pv)))
        if (nv.isEmpty) z else z.copy(velocity = nv.minBy { v => nh.map(n => distance(position(n), sum(z.position, v))).min })
      }

      def runningHumans(agents: Array[Agent]) =
        agents.collect(Agent.human).filter { _.metabolism.run }

      def towardsRescue(h: Human, rng: Random) = {
        val (x, y) = location(h, world.side)
        World.get(world, x, y) match {
          case Some(f: Floor) =>
            randomElement(f.rescueSlope, rng) match {
              case Some(s) => h.copy(velocity = normalize((s.x, s.y), Metabolism.effectiveSpeed(h.metabolism)))
              case None => h
            }
          case _ => followRunning(h, rng)
        }
      }

      def followRunning(h: Human, rng: Random) = {
        if(h.followRunningProbability > 0.0) {
          val runningNeighbors = runningHumans(neighbors)
          if (!runningNeighbors.isEmpty && rng.nextDouble() < h.followRunningProbability) Human.run(h.copy(velocity = average(runningNeighbors.map(_.velocity))))
          else h
        } else h
      }

      def followPheromon(z: Zombie, world: World, rng: Random) = {
        val pv = projectedVelocities(granularity, z.maxRotation, z.velocity, Metabolism.effectiveSpeed(z.metabolism))
        val nv = rng.shuffle(pv.filter(pv => !towardsWall(world, z.position, pv)))
        if (nv.isEmpty) z else {
          val currentPheromon = World.pheromon(world, positionToLocation(z.position, world.side))
          val ph = nv.flatMap { v =>
            val projectedPheromon = World.pheromon(world, positionToLocation(sum(z.position, v), world.side))
            val deltaPheromon = projectedPheromon - currentPheromon
            if(deltaPheromon > 0.0) Some(v -> deltaPheromon)
            else None
          }

          if(!ph.isEmpty) {
            val newVelocity = (nv zip ph).maxBy(_._2)._1
            z.copy(velocity = newVelocity)
          } else z
        }
      }

      agent match {
        case h: Human =>
          neighbors.collect(Agent.zombie) match {
            case nz if !nz.isEmpty => (fleeZombies(h, nz, rng), Some(Flee(h)))
            case _ if h.rescue.informed && h.rescue.alerted => (towardsRescue(h, rng), None)
            case _ => (followRunning(h, rng), None)
          }
        case z: Zombie =>
          neighbors.collect(Agent.human) match {
            case nh if !nh.isEmpty => (pursueHuman(z, nh, rng), Some(Pursue(z)))
            case _ => (followPheromon(z, world, rng), None)
          }
      }
    }




  }


  object Metabolism {
    def effectiveSpeed(speed: Metabolism) =  if(speed.run) speed.runSpeed else speed.walkSpeed
    def metabolism(speed: Metabolism, rng: Random) =
      (speed.exhausted, speed.run) match {
        case (false, true) if rng.nextDouble() < speed.exhaustionProbability => speed.copy(run = false, exhausted = true)
        case (true, _) if rng.nextDouble() < 1 - speed.exhaustionProbability => speed.copy(exhausted = false)
        case (_, _) => speed
      }

    def canRun(speed: Metabolism) = !speed.exhausted
  }

  object Human {
    def random(world: World, walkSpeed: Double, runSpeed: Double, exhaustionProbability: Double, perception: Double, maxRotation: Double, followRunningProbability: Double, fightBackProbability: Double, rescue: Rescue, rng: Random) = {
      val p = Agent.randomPosition(world, rng)
      val v = Agent.randomVelocity(walkSpeed, rng)
      Human(p, v, Metabolism(walkSpeed, runSpeed, exhaustionProbability, false, false), perception, maxRotation, followRunningProbability, fightBackProbability, rescue = rescue)
    }

    def run(h: Human) =
      if(Metabolism.canRun(h.metabolism)) h.copy(velocity = normalize(h.velocity, h.metabolism.runSpeed), metabolism = h.metabolism.copy(run = true))
      else h

    def alerted(h: Human) = run(h).copy(rescue = h.rescue.copy(alerted = true))

    def metabolism(h: Human, rng: Random) = {
      val newSpeed = Metabolism.metabolism(h.metabolism, rng)
      val newVelocity = normalize(h.velocity, Metabolism.effectiveSpeed(newSpeed))
      h.copy(velocity = newVelocity, metabolism = newSpeed)
    }
  }

  object Zombie {
    def random(world: World, walkSpeed: Double, runSpeed: Double, exhaustionProbability: Double, vision: Double, maxRotation: Double, rng: Random) = {
      val p = Agent.randomPosition(world, rng)
      val v = Agent.randomVelocity(walkSpeed, rng)
      Zombie(p, v, Metabolism(walkSpeed, runSpeed, exhaustionProbability, false, false), vision, maxRotation)
    }

    def run(z: Zombie) =
      if(Metabolism.canRun(z.metabolism)) z.copy(velocity = normalize(z.velocity, z.metabolism.runSpeed), metabolism = z.metabolism.copy(run = true))
      else z

    def metabolism(z: Zombie, rng: Random) = {
      val newSpeed = Metabolism.metabolism(z.metabolism, rng)
      val newVelocity = normalize(z.velocity, Metabolism.effectiveSpeed(newSpeed))
      z.copy(velocity = newVelocity, metabolism = newSpeed)
    }

  }
}
