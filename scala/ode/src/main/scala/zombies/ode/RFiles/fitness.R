logLik <- function(simul, real)
{
    LL <- tibble(simul, real) %>%
        transmute(loglik = (simul - real) * (simul - real)) %>%
        summarise(sum(loglik))

    return(LL)
}
