ODE <- function(t, pop, param) {

    H_walk <- pop[1]
    H_run  <- pop[2]
    Z_walk <- pop[3]
    Z_run  <- pop[4]

    panic0    <- param[[1]]
    exhaustH  <- param[[2]]
    hunt0     <- param[[3]]
    exhaustZ  <- param[[4]]
    inf0      <- param[[5]]
    out0      <- param[[6]]
    fightback <- param[[7]]
    die0      <- param[[8]]

    N <- H_walk + H_run + Z_walk + Z_run

    panic <- panic0 * (Z_walk + Z_run) / N
    hunt  <- hunt0 * (H_walk + H_run) / N
    inf   <- inf0 * (1 - fightback)
    out   <- out0 * (H_walk + H_run) / N
    die   <- die0 * (H_walk + H_run) / N

    dH_walk <- -(panic + inf + out) * H_walk + exhaustH * H_run
    dH_run  <- panic * H_walk - (exhaustH + inf + out) * H_run
    dZ_walk <- inf * (H_walk + H_run) - (hunt + die) * Z_walk + exhaustZ * Z_run
    dZ_run  <- hunt * Z_walk - (exhaustZ + die) * Z_run

    res <- c(dH_walk, dH_run, dZ_walk, dZ_run)
    list(res)
}
