# Execution avec le jeu de paramètres donne

simulation <- function(estim_par, condInit, temps, modele)
{
    # param = list(B0 = estim_par[1], X = estim_par[2], P0 = estim_par[3], P = 0, L = lambda, data_flu = data_flu)
    param = list(panic0 = estim_par[1],
                 exhaustH = estim_par[2],
                 inf = estim_par[3],
                 hunt0 = estim_par[4],
                 exhaustZ = estim_par[5])

    # Execute
    result <- lsoda(y = condInit, times = temps, func = modele, parms = param)
    tab <- as.data.frame(result)
    names(tab) = c("time", "H_walk", "H_run", "Z_walk", "Z_run")

    return(tab)
}
