package zombies

import zombie.network.Network
import zombies.spatialindicators.SpatialMeasure
import zombies.world._

import scala.util.Random
import zombies.spatialsens.Generator._


object TestSpatialSens extends App {

  implicit val rng = new Random

  val worldSize =50
  val percolationProba = 0.2
  val bordPoints = 20
  val linkwidth=3.0

  val world = bondPercolatedWorld(worldSize=worldSize,percolationProba=percolationProba,bordPoints=bordPoints,linkwidth=linkwidth)
  println(wallsToString(world))
  println(SpatialMeasure(world))

  /*
  var network = Network.gridNetwork(worldSize/10,worldSize/10,worldSize)
  //println("unique nodes : "+network.nodes.map{_.id}.size/network.nodes.size)
  val xmin = network.nodes.map{_.x}.min;val xmax = network.nodes.map{_.x}.max
  val ymin = network.nodes.map{_.y}.min;val ymax = network.nodes.map{_.y}.max
  var bordConnected = 0
  for(_ <- 0 to 15)
  {
    network = Network.percolate(network, 0.2)
    //println(network.nodes.size)
    //println(network.links.toSeq.map(_.weight).sum)
    //println(network.nodes.map{_.id}.size)
    val giantcomp = Network.largestConnectedComponent(Network(network.nodes, network.links.filter {
      _.weight > 0.0
    }))
    println("prop giant comp = "+(giantcomp.nodes.size.toDouble/network.nodes.size.toDouble))
    val nodesOnBord = giantcomp.nodes.filter{case n => n.x==xmin||n.x==xmax||n.y==ymin||n.y==ymax}
    println("Nodes on bord : "+nodesOnBord)
    bordConnected =nodesOnBord.size
    println("bordConnected = "+bordConnected)
  }
  */
}
