package zombies.ode

import better.files._

object ODE extends App {

    val simures = Model.run(
      // Real data
      //file = File("ode/realData.csv").toJava,
      // ODE parameters
      panic0 = 1,
      staminaH = 10,
      inf = 0.25,
      hunt0 = 0.5,
      staminaZ = 5,
      // Initial conditions
      statesInit = Vector(250.0, 0.0, 4.0, 0.0),
      // Time steps
      t0 = 1,
      dt = 0.01,
      tMax = 500,
      tWarp = 200
    )//.mkString("\n")

  //println(simures)
  //println(simures._1.size)

}

object Model {

  def interpolate(x: Vector[Double], doubleind: Double) = {
    if (doubleind.toInt==doubleind) x(doubleind.toInt)
    else {
      assert(doubleind > 0 && doubleind < x.size, s"Double index out of bounds : ${doubleind} for size ${x.size}")
      val ileft = math.floor(doubleind).toInt
      if (ileft == x.size-1) x(ileft)
      else {
        val (left, right) = (x(ileft), x(ileft + 1))
        val w = doubleind - math.floor(doubleind)
        left + w * (right - left)
      }
    }
  }

  def run(panic0: Double, staminaH: Int, inf: Double, hunt0: Double, staminaZ: Int,
          statesInit: Vector[Double],
          t0: Int, dt: Double, tMax: Int, tWarp: Int,
          ABMTimeSerieSteps: Int = 500
         ) = {
    val exhaustH = 1.0 / staminaH
    val exhaustZ = 1.0 / staminaZ
    val nbIntervals = ((tMax - t0) / dt).toInt

    // Simulation data
    val simul = integrate(dynamic(panic0, exhaustH, inf, hunt0, exhaustZ))(t0, dt, nbIntervals, List(statesInit))

    val Vector(humansWalking, humansRunning, zombifiedWalking, zombifiedRunning) = simul.toVector.transpose

//    val humans = (humansWalking zip humansRunning).map { case(a, b) => a + b }
//    val zombified = (zombifiedWalking zip zombifiedRunning).map { case (a, b) => a + b - statesInit(2) }

    // Sampling over simulation data
    val maxIndSampling = (tWarp - t0) / dt
    val samplingStep = maxIndSampling / ABMTimeSerieSteps
    val samplingSteps = (0.0 to maxIndSampling by samplingStep)

    val humansWalkingSampled = samplingSteps.map(interpolate(humansWalking,_))
    val humansRunningSampled = samplingSteps.map(interpolate(humansRunning,_))
    val zombifiedWalkingSampled = samplingSteps.map(interpolate(zombifiedWalking,_) - statesInit(2))
    val zombifiedRunningSampled = samplingSteps.map(interpolate(zombifiedRunning,_))

    (humansWalkingSampled, humansRunningSampled, zombifiedWalkingSampled, zombifiedRunningSampled)
  }


  // Description of the ODE system
  def dynamic(panic0: Double, exhaustH: Double, inf: Double, hunt0: Double, exhaustZ: Double)(t: Double, state: Vector[Double]): Vector[Double] = {
    // Param
    val N = state.sum
    val panic = panic0 * (state(2) + state(3)) / N
    val hunt = hunt0 * (state(0) + state(1)) / N

    // ODE system
    def dH_walk(state: Vector[Double]) =
      -(panic + inf) * state(0) + exhaustH * state(1)

    def dH_run(state: Vector[Double]) =
      panic * state(0) - (exhaustH + inf) * state(1)

    def dZ_walk(state: Vector[Double]) =
      inf * (state(0) + state(1)) - hunt * state(2) + exhaustZ * state(3)

    def dZ_run(state: Vector[Double]) =
      hunt * state(2) - exhaustZ * state(3)

    // Output
    Vector(
      dH_walk(state),
      dH_run(state),
      dZ_walk(state),
      dZ_run(state)
    )
  }


  // ODE solver
  def integrate(f: (Double, Vector[Double]) => Vector[Double])(t0: Double, dt: Double, counter: Int, ysol: List[Vector[Double]]): List[Vector[Double]] = {
    def multiply(v: Vector[Double], s: Double) = v.map(_ * s)
    def divide(v: Vector[Double], s: Double) = v.map(_ / s)
    def add(vs: Vector[Double]*) = {
      def add0(v1: Vector[Double], v2: Vector[Double]) = (v1 zip v2).map { case(a, b) => a + b }
      vs.reduceLeft(add0)
    }

    val yn = ysol.head

    if (counter > 0) {
      val dy1 = multiply(f(t0, yn), dt)
      val dy2 = multiply(f(t0 + dt / 2, add(yn, divide(dy1, 2))), dt)
      val dy3 = multiply(f(t0 + dt / 2, add(yn, divide(dy2, 2))), dt)
      val dy4 = multiply(f(t0 + dt, add(yn, dy3)), dt)
      val y = add(yn, divide(add(dy1, multiply(dy2, 2), add(multiply(dy3, 2), dy4)), 6))::ysol
      val t = t0 + dt
      integrate(f)(t, dt, counter - 1, y)
    } else ysol.reverse
  }
}
