SIR <- function(t, pop, param) {

    S <- pop[1]
    I <- pop[2]
    R <- pop[3]

    beta   <- param[[1]]
    lambda <- param[[2]]
    gamma  <- param[[3]]

    N <- S + I + R

    dS <- -beta * S + lambda * I
    dI <- beta * S - (lambda + gamma) * I
    dR <- gamma * I

    res <- c(dS, dI, dR)
    list(res)
}
