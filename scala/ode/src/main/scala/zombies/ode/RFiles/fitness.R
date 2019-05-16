logLik <- function(simul, real)
{
    LL <- tibble(simul, real) %>%
        mutate(loglik = (simul - real) * (simul - real))

    return(sum(LL$loglik))
}
