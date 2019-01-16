package zombies.ode

object ODE extends App {
  // Model parameters
  val panic0 = 1
  val staminaH = 10
  val inf = 0.25
  val hunt0 = 0.5
  val staminaZ = 5

  val exhaustH = 1 / staminaH
  val exhaustZ = 1 / staminaZ

  // Time steps
  val T0 = 0
  val DT = 1
  val Tmax = 500

  val nbIntervals = (Tmax - T0) / DT

  // Initial conditions
  val H_walk0 = 250.0
  val H_run0 = 0.0
  val Z_walk0 = 4.0
  val Z_run0 = 0.0

  val condInit = Vector(H_walk0, H_run0, Z_walk0, Z_run0)

  println(
    Model.integrate(Model.dynamic(panic0, exhaustH, inf, hunt0, exhaustZ))(T0, DT, condInit, nbIntervals).mkString("\n")
  )
}

object Model {

  /*def run(file: java.io.File, panic0: Double, staminaH: Double, inf: Double, hunt0: Double, staminaZ: Double, state: Vector[Double], integrationStep: Double = 0.01) = {
    //val steps = Model.dynamic(panic0, exhaustH, inf, hunt0, exhaustZ, state, integrationStep, (0 until 100).map(_.toDouble).toVector)

    vaIs = state(2) :: steps.map(_ (2)).sliding(2).map { case Vector(t1, t2) => t2 - t1 }.toList
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
    }
  }*/


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


  def integrate(f: (Double, Vector[Double]) => Vector[Double])(t0: Double, dt: Double, yn: Vector[Double], counter: Int): Vector[Double] = {
    def multiply(v: Vector[Double], s: Double) = v.map(_ * s)
    def divide(v: Vector[Double], s: Double) = v.map(_ / s)
    def add(vs: Vector[Double]*) = {
      def add0(v1: Vector[Double], v2: Vector[Double]) = (v1 zip v2).map { case(a, b) => a + b }
      vs.reduceLeft(add0)
    }

    if (counter > 0) {
      val dy1 = multiply(f(t0, yn), dt)
      val dy2 = multiply(f(t0 + dt / 2, add(yn, divide(dy1, 2))), dt)
      val dy3 = multiply(f(t0 + dt / 2, add(yn, divide(dy2, 2))), dt)
      val dy4 = multiply(f(t0 + dt, add(yn, dy3)), dt)
      val y = add(yn, divide(add(dy1, multiply(dy2, 2), add(multiply(dy3, 2), dy4)), 6))
      val t = t0 + dt
      integrate(f)(t, dt, y, counter - 1)
    } else yn
  }
}