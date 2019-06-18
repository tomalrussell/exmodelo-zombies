# Execution avec le jeu de paramètres donne

simulation <- function(estim_par, condInit, temps, modele)
{
    param = list(beta = estim_par[1],
                 lambda = estim_par[2],
                 gamma = estim_par[3])

    # Execute
    result <- lsoda(y = condInit, times = temps, func = modele, parms = param)
    tab <- as.data.frame(result)
    names(tab) = c("time", "S", "I", "R")

    return(tab)
}
