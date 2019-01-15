package zombies

import zombie.network.Network

import scala.util.Random

object spatialsens {


  object Simulation {



  }


  object Generator {

    /**
      * Convert an array of spatial layout to a string parsable by the world generator
      * @param world
      * @return
      */
    def wallsToString(world: Array[Array[Double]]): String = {
      world.map{_.map(_ match {case x if x > 0.0 => "0"; case x if x == 0 => "+"; case _ => "+"}).mkString("")}.mkString("\n")
    }


    /**
      * Generator based on Diffusion Limited Aggregation
      *
      * generalized dla :
      * Vicsek, T. (1984). Pattern formation in diffusion-limited aggregation. Physical review letters, 53(24), 2281.
      *
      * @return
      */
    def dlaWorld(): Array[Array[Double]] = {
      Array.empty
    }

    /**
      * Basic bond percolation in an overlay network
      * (iterated until having one connected component with a specified number of points on the boundary,
      * keep the largest component at each step)
      * @param worldSize
      * @param percolationProba
      * @return
      */
    def bondPercolatedWorld(worldSize: Int,percolationProba: Double,bordPoints: Int)(implicit rng: Random): Array[Array[Double]] = {
      var network = Network.gridNetwork(worldSize/10,worldSize/10,worldSize)
      var bordConnected = 0
      val xmin = network.nodes.map{_.x}.min;val xmax = network.nodes.map{_.x}.max
      val ymin = network.nodes.map{_.y}.min;val ymax = network.nodes.map{_.y}.max
      while(bordConnected<bordPoints){
        network = Network.percolate(network,percolationProba)
        val giantcomp =  Network.largestConnectedComponent(Network(network.nodes,network.links.filter{_.weight>0}))
        //println(giantcomp.links.size)
        val nodesOnBord = giantcomp.nodes.filter{case n => n.x==xmin||n.x==xmax||n.y==ymin||n.y==ymax}
        bordConnected =nodesOnBord.size
        println("Percolated links prop : "+(network.links.toSeq.map{_.weight}.sum/network.links.toSeq.size))
        println("bordConnected = "+bordConnected)
        //println("nodesOnBord="+nodesOnBord)
      }
      Network.networkToGrid(network)
    }

  }


  object Morphology {

  }



}
