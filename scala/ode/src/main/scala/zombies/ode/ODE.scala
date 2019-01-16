package zombies.ode

object ODE extends App {

  println("coucou")
}

object Model {

  val staminaH = 10
  val staminaZ = 5

  def run(file: java.io.File, panic0: Double, exhaustH: Double, inf: Double, hunt0: Double, exhaustZ: Double, state: Vector[Double], integrationStep: Double = 0.01) = {
    //val steps = Model.dynamic(panic0, exhaustH, inf, hunt0, exhaustZ, state, integrationStep, (0 until 100).map(_.toDouble).toVector)

    /*val Is = state(2) :: steps.map(_ (2)).sliding(2).map { case Vector(t1, t2) => t2 - t1 }.toList
    val infections = columns.map(_ (6).toDouble).toList

    val likelihood =
      (Is zip infections).map { case (i, inf) =>
        if (i > 0) (inf * math.log(i) - i) else 0
      }.sum

    val portageMean = steps.map(_ (1)).sum / steps.size
    val llPortage = 0.3 * N * math.log(portageMean) - portageMean

    (likelihood + llPortage) match {
      case Double.NegativeInfinity | Double.NaN => Double.PositiveInfinity
      case x => -x
    }*/
  }


//  def dynamic(panic0: Double, exhaustH: Double, inf: Double, hunt0: Double, exhaustZ: Double, state: Vector[Double], integrationStep: Double, timeSteps: Vector[Double]) = {
//    var N = state.sum
//
//    var panic = panic0 * (state(3) + state(4)) / N
//
//    var hunt = hunt0 * (state(1) + state(2)) / N
//
//
//    def dH_walk(state: Array[Double]) =
//      -(panic + inf) * state(1) + exhaustH * state(2)
//
//    def dH_run(state: Array[Double]) =
//      panic * state(1) - (exhaustH + inf) * state(2)
//
//    def dZ_walk(state: Array[Double]) =
//      inf * (state(1) + state(2)) - hunt * state(3) + exhaustZ * state(4)
//
//    def dZ_run(state: Array[Double]) =
//      hunt * state(3) - exhaustZ * state(4)
//
//    val dynamic = Dynamic(dH_walk, dH_run, dZ_walk, dZ_run)
//    dynamic.integrate(state.toArray, integrationStep, timeSteps)
//  }
//
//

  
  def integrate(f: (Double, Vector[Double]) => Vector[Double])(t0: Double, dt: Double, yn: Vector[Double], counter: Int = 1): Vector[Double] = {
    if (counter > 0) {
      def multiply(v: Vector[Double], s: Double) = v.map(_ * s)
      def divide(v: Vector[Double], s: Double) = v.map(_ / s)
      def add(vs: Vector[Double]*) = {
        def add0(v1: Vector[Double], v2: Vector[Double]) = (v1 zip v2).map { case(a, b) => a + b }
        vs.reduceLeft(add0)
      }

      val dy1 = multiply(f(t0, yn), dt)
      val dy2 = multiply(f(t0 + dt / 2, add(yn, divide(dy1, 2))), dt)
      val dy3 = multiply(f(t0 + dt / 2, add(yn, divide(dy2, 2))), dt)
      val dy4 = multiply(f(t0 + dt, add(yn, dy3)), dt)
      val y = add(yn,  divide(add(dy1, multiply(dy2, 2), add(multiply(dy3, 2), dy4)), 6))
      val t = t0 + dt
      integrate(f)(t, dt, y, counter - 1)
    } else yn
  }




}