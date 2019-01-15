
package zombies


object spatialindicators {





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




}
