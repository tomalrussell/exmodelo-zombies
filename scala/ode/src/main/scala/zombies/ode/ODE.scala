package zombies.ode

import better.files._

object ODE extends App {
  //println(
    Model.run(
      // Real data
      file = File("ode/realData.csv").toJava,
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
      dt = 1,
      tMax = 100
    )
      //.mkString("\n")
  //)
}

object Model {

  def run(file: java.io.File,
          panic0: Double, staminaH: Double, inf: Double, hunt0: Double, staminaZ: Double,
          statesInit: Vector[Double],
          t0: Int, dt: Int, tMax: Int) = {
    val exhaustH = 1.0 / staminaH
    val exhaustZ = 1.0 / staminaZ
    val nbIntervals = (tMax - t0) / dt

    // Simulation data
    val simul = integrate(dynamic(panic0, exhaustH, inf, hunt0, exhaustZ))(t0, dt, nbIntervals, List(statesInit))

    val Vector(humansWalking, humansRunning, zombiesWalking, zombiesRunning) = simul.toVector.transpose

    val humans = (humansWalking zip humansRunning).map { case(a, b) => a + b }
    val zombified = (zombiesWalking zip zombiesRunning).map { case (a, b) => a + b - statesInit(2) }

    // Zombieland data
    val columns = File(file.getAbsolutePath).lines.map(_.split(",")).toVector

    val realHumans = columns.map(_(0).toDouble)
    val realZombies = columns.map(_(1).toDouble)

    // Likelihood calculation
    val likelihoodHumans =
      (realHumans zip humans).map { case(real, simu) => (real - simu) * (real - simu) }.sum
    val likelihoodZombies =
      (realZombies zip zombified).map { case(real, simu) => (real - simu) * (real - simu) }.sum

    likelihoodHumans + likelihoodZombies
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
