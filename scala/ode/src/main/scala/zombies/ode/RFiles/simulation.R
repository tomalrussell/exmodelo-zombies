# Execution avec le jeu de paramètres donne

simulation <- function(estim_par, condInit, temps, modele)
{
    param = list(panic0 = estim_par[1],
                 exhaustH = estim_par[2],
                 hunt0 = estim_par[3],
                 exhaustZ = estim_par[4],
                 inf0 = estim_par[5],
                 out0 = estim_par[6],
                 fightback = estim_par[7],
                 die0 = estim_par[8])

    # Execute
    result <- lsoda(y = condInit, times = temps, func = modele, parms = param)
    tab <- as.data.frame(result)
    names(tab) = c("time", "H_walk", "H_run", "Z_walk", "Z_run")

    return(tab)
}
