
package zombies

import zombie.network.Network


object spatialindicators {


  case class SpatialMeasure(
                           detours: Double,
                           components: Double,
                           density: Double,
                           moran: Double,
                           dilationSteps: Double,
                           avgBlockArea: Double
                           )

  object SpatialMeasure {
    def apply(world: Array[Array[Double]]): SpatialMeasure = {
      val cachedNetwork = Network.gridToNetwork(world)
      println(cachedNetwork)
      SpatialMeasure(
        averageDetour(Array.empty,Some(cachedNetwork)),
        components(Array.empty,Some(cachedNetwork)),
        density(world),
        moran(world),
        fullDilationSteps(world),
        avgBlockArea(world)
      )
    }
  }


  /**
    * avg detour between all pairs of accessible points
    *  - pairs are taken on components
    * @param world
    * @param cachedNetwork avoid recomputation of the network
    *   FIXME cache also shortest paths if these are used by different indicators
    * @return
    */
  def averageDetour(world: Array[Array[Double]],cachedNetwork: Option[Network] = None): Double = {
    val network = cachedNetwork match {case None => Network.gridToNetwork(world);case n => n.get}
    val shortestPaths = Network.allPairsShortestPath(network)
    val avgdetour = shortestPaths.values.map{_.map{_.weight}.sum}.zip(shortestPaths.keys.map{case (n1,n2)=> math.sqrt((n1.x-n2.x)*(n1.x-n2.x)+(n1.y-n2.y)*(n1.y-n2.y))}).map{case (dn,de)=>dn/de}.sum/shortestPaths.size
    println("avgdetour = "+avgdetour)
    avgdetour
  }

  /**
    * Number of connected components
    * @param world
    * @param cachedNetwork
    * @return
    */
  def components(world: Array[Array[Double]],cachedNetwork: Option[Network] = None): Double = {
    val network = cachedNetwork match {case None => Network.gridToNetwork(world);case n => n.get}
    val components = Network.connectedComponents(network)
    println("components = "+components.size)
    components.size
  }

  /**
    * Average size of a block
    *  (computed using components of the network of the inverted world)
    * @param world
    * @return
    */
  def avgBlockArea(world: Array[Array[Double]]): Double = {
    val inversedNetwork = Network.gridToNetwork(world.map{_.map{case x => 1.0 - x}})
    val components = Network.connectedComponents(inversedNetwork)
    val avgblockarea = components.map{_.nodes.size}.sum/components.size
    println("avgblockarea = "+avgblockarea)
    avgblockarea
  }


  /**
    * Density
    * @param world
    * @return
    */
  def density(world: Array[Array[Double]]): Double = world.flatten.map{x => if(x>0.0)1.0 else 0.0}.sum / world.flatten.size


  def distance(p1: (Int,Int), p2: (Int,Int)): Double = {
    val (i1, j1) = p1
    val (i2, j2) = p2
    val a = i2 - i1
    val b = j2 - j1
    math.sqrt(a * a + b * b)
  }

  def zipWithPosition(m :Array[Array[Double]]): Seq[(Double, (Int,Int))] = {
    for {
      (row, i) <- m.zipWithIndex
      (content, j) <- row.zipWithIndex
    } yield (content,(i, j))
  }

  /**
    * unoptimized moran (no dep so no convolution)
    * @param world
    * @return
    */
  def moran(world: Array[Array[Double]]): Double = {
    def flatCells = world.flatten
    val totalPop = flatCells.sum
    val averagePop = totalPop / world.flatten.length


    def vals =
      for {
        (c1, p1) <- zipWithPosition(world)
        (c2, p2) <- zipWithPosition(world)
      } yield (decay(p1, p2) * (c1 - averagePop) * (c2 - averagePop),decay(p1, p2))



    def numerator : Double = vals.map{case (n,_)=>n}.sum
    def totalWeight : Double = vals.map{case(_,w)=>w}.sum

    def denominator =
      flatCells.map {
        p =>
          if (p == 0) 0
          else math.pow(p - averagePop.toDouble, 2)
      }.sum

    if (denominator == 0) 0
    else (world.flatten.length / totalWeight) * (numerator / denominator)
  }

  def decay(p1:(Int,Int),p2:(Int,Int)) = {
    if (p1==p2) 0.0
    else 1/distance(p1,p2)
  }


  /**
    * Naive two dimensional convolution for morpho math - default operator is average (dilation) - replace by product for erosion
    *   (not efficient at all but no math commons to work in the gui)
    * @param matrix
    * @param mask should be of uneven size
    * @return
    */
  def convolution(matrix: Array[Array[Double]],mask: Array[Array[Double]],operator: Array[Double]=>Double = {case a => if(a.filter(_>0.0).size>0)1.0 else 0.0}): Array[Array[Double]] = {
    assert(mask.length%2==1&&mask(0).length%2==1,"mask should be of uneven size")
    val sizes = matrix.map(_.length);assert(sizes.max==sizes.min,"array should be rectangular")
    val masksizes = mask.map(_.length);assert(masksizes.max==masksizes.min,"mask should be rectangular")
    val (paddingx,paddingy) = ((mask.length-1)/2,(mask(0).length-1)/2)
    val res = Array.fill(matrix.length+2*paddingx,matrix(0).length+2*paddingy)(0.0)
    for(i <- paddingx until res.length - paddingx;j <- paddingy until res(0).length-paddingy){
      val masked = Array.fill(mask.size,mask(0).size)(0.0)
      for(k <- - paddingx until paddingx;l <- - paddingy until paddingy){
        masked(k+paddingx)(l+paddingy)=matrix(i+k)(j+l)*mask(k+paddingx)(l+paddingy)
      }
      res(i)(j) = operator(masked.flatten)
    }
    res
  }

  /**
    * Dilation with default cross mask
    * @param matrix
    * @return
    */
  def dilation(matrix: Array[Array[Double]]): Array[Array[Double]] = convolution(matrix,Array(Array(0.0,1.0,0.0),Array(1.0,1.0,1.0),Array(0.0,1.0,0.0)))

  def erosion(matrix: Array[Array[Double]]): Array[Array[Double]] = {
    val mask = Array(Array(0.0, 1.0, 0.0), Array(1.0, 1.0, 1.0), Array(0.0, 1.0, 0.0))
    convolution(matrix,
      mask,
      { case a => if (a.filter(_ > 0.0).sum == mask.flatten.sum) 1.0 else 0.0 }
    )
  }

  /**
    * Number of steps to fully close the image (morpho maths)
    *
    * @param matrix
    * @return
    */
  def fullDilationSteps(matrix: Array[Array[Double]]): Double = {
    var steps = 0
    var complete = false
    var currentworld = matrix
    while(!complete){
      println("dilating "+steps)
      currentworld = dilation(currentworld)
      complete = currentworld.flatten.sum == currentworld.flatten.length
      steps = steps + 1
    }
    steps
  }




}
