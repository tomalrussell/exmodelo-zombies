ODE <- function(t, pop, param) {

    H_walk <- pop[1]
    H_run  <- pop[2]
    Z_walk <- pop[3]
    Z_run  <- pop[4]

    panic0   <- param[[1]]
    exhaustH <- param[[2]]
    inf      <- param[[3]]
    hunt0    <- param[[4]]
    exhaustZ <- param[[5]]

    tt <- floor(t)

    panic <- panic0*(Z_walk + Z_run)
    hunt  <- hunt0*(H_walk + H_run)

    dH_walk <- -(panic + inf) * H_walk + exhaustH * H_run
    dH_run  <- panic * H_walk - (exhaustH + inf) * H_run
    dZ_walk <- inf * (H_walk + H_run) - hunt * Z_walk + exhaustZ * Z_run
    dZ_run  <- hunt * Z_walk - exhaustZ * Z_run

    res <- c(dH_walk, dH_run, dZ_walk, dZ_run)
    list(res)
}
