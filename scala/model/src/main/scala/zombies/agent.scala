package zombies

import move._
import world._
import space._

import scala.util.Random

object agent {

  sealed trait Agent
  case class Human(position: Position, velocity: Velocity, maxSpeed: Double, vision: Double, maxRotation: Double) extends Agent
  case class Zombie(position: Position, velocity: Velocity, maxSpeed: Double, vision: Double, maxRotation: Double) extends Agent

  object Agent {

    def isHuman(agent: Agent) = agent match {
      case _: Human => true
      case _ => false
    }

    def isZombie(agent: Agent) = agent match {
      case _: Zombie => true
      case _ => false
    }

    def zombify(human: Human, zombieMaxSpeed: Double, zombieVision: Double, zombieMaxRotation: Double, rng: Random) =
      Zombie(human.position, normalize(randomVector(rng), zombieMaxSpeed), zombieMaxSpeed, zombieVision, zombieMaxRotation)

    def position(agent: Agent) = agent match {
      case h: Human => h.position
      case z: Zombie => z.position
    }

    def velocity(agent: Agent) = agent match {
      case h: Human => h.velocity
      case z: Zombie => z.velocity
    }

    def setPosition(agent: Agent, position: Position) = agent match {
      case h: Human => h.copy(position = position)
      case z: Zombie => z.copy(position = position)
    }

    def setVelocity(agent: Agent, velocity: Velocity) = agent match {
      case h: Human => h.copy(velocity = velocity)
      case z: Zombie => z.copy(velocity = velocity)
    }

    def maxSpeed(agent: Agent) = agent match {
      case h: Human => h.maxSpeed
      case z: Zombie => z.maxSpeed
    }

    def vision(agent: Agent) = agent match {
      case h: Human => h.vision
      case z: Zombie => z.vision
    }

    def location(agent: Agent, side: Int): Location = positionToLocation(position(agent), side, side)

    def index(agents: Vector[Agent], side: Int) = Index[Agent](agents, location(_, side), side)

    def move(agent: Agent, world: World) = {
      val v = {
        val (px, py) = sum(position(agent), velocity(agent))
        val (cx, cy) = positionToLocation((px, py), world.side, world.side)

        val v =
          World.cell(world, cx, cy) match {
            case None => None
            case Some(Wall) => Some(opposite(velocity(agent)))
            case Some(f: Flor) => Some(sum(velocity(agent), normalize((f.slope.x, f.slope.y), (1 + f.slope.intensity) * maxSpeed(agent))))
          }

        v.map(normalize(_, maxSpeed(agent)))
      }

      v.flatMap { v =>
        val na = setVelocity(setPosition(agent, sum(position(agent), v)), v)
        val (px, py) = position(na)
        if (px < 0 || px > 1 || py < 0 || py > 1) None else Some(na)
      }
    }

    def visibleNeighbors(index: Index[Agent], agent: Agent, range: Double, world: World) = {
      val neighborhoodSize = math.ceil(range / space.cellSide(index.side)).toInt + 1
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

//    def adaptDirectionAbsolute(index: Index[Agent], agent: Agent) = agent match {
//      case h: Human =>
//        neighbors(index, agent, vision(h)).filter(Agent.isZombie) match {
//          case ns if !ns.isEmpty =>
//            val closestZombie = closest(agent, ns, position)
//            if(position(closestZombie) != position(h)) {
//              val fleeDirection = normalize(opposite(direction(position(agent), position(closestZombie))), maxSpeed(agent))
//              h.copy(velocity = fleeDirection)
//            } else h
//          case _ => h
//        }
//      case z: Zombie =>
//        neighbors(index, agent, vision(z)).filter(Agent.isHuman) match {
//          case ns if !ns.isEmpty =>
//            val closestHuman = closest(agent, ns, position)
//
//            if(position(closestHuman) != position(agent)) {
//              val rushDirection = normalize(direction(position(agent), position(closestHuman)), maxSpeed(agent))
//              z.copy(velocity = rushDirection)
//            } else z
//          case _ => z
//        }
//
//    }

    def adaptDirectionRotate(index: Index[Agent], agent: Agent, granularity: Int, world: World) = agent match {
      case h: Human =>
        //visibleNeighbors(index, agent, vision(h), world).filter(Agent.isZombie) match {
        neighbors(index, agent, vision(h)).filter(Agent.isZombie) match {
          case ns if !ns.isEmpty =>
            val projectedVelocities = (-granularity to granularity).map(_ * h.maxRotation).map(r => rotate(h.velocity, r))
            val nv = projectedVelocities.maxBy { v => ns.map(n => distance(position(n), sum(h.position, v))).min }
            setVelocity(h, nv)
          case _ => h
        }
      case z: Zombie =>
        //visibleNeighbors(index, agent, vision(z), world).filter(Agent.isHuman) match {
        neighbors(index, agent, vision(z)).filter(Agent.isHuman) match {
          case ns if !ns.isEmpty =>
            val projectedVelocities =  (-granularity to granularity).map(_ * z.maxRotation).map(r => rotate(z.velocity, r))
            val nv = projectedVelocities.minBy { v => ns.map(n => distance(position(n), sum(z.position, v))).min }
            setVelocity(z, nv)
          case _ => z
        }

    }

    def infect(index: Index[Agent], agents: Vector[Agent], range: Double, zombify: Human => Zombie) = {
      val (humansAgents, others) = agents.partition(Agent.isHuman)
      val humans = humansAgents.collect { case h: Human => h }
      humans.map { h => if(infectable(index, h, range)) zombify(h) else h } ++ others
    }

    def infectable(index: Index[Agent], agent: Human, range: Double) =
      neighbors(index, agent, range).exists(Agent.isZombie)

  }

  object Human {
    def generate(world: World, maxSpeed: Double, vision: Double, maxRotation: Double, rng: Random) = {
      val p = generatePosition(world, rng)
      val v = normalize(randomVector(rng), maxSpeed)
      Human(p, v, maxSpeed, vision, maxRotation)
    }
  }

  object Zombie {
    def generate(world: World, maxSpeed: Double, vision: Double, maxRotation: Double, rng: Random) = {
      val p = generatePosition(world, rng)
      val v = normalize(randomVector(rng), maxSpeed)
      Zombie(p, v, maxSpeed, vision, maxRotation)
    }
  }
}
