library(deSolve)
library(tidyverse)


# Parameters
beta = 0.5
lambda = 1/3
gamma = 1/7
# Initial conditions
statesInit = c(1000.0, 0.0, 0.0)
# Time steps
t0 = 0
dt = 1
tMax = 100
Time = seq(t0, tMax, dt)


## Modele compartimental
source("modele.R")
## Simulation
source("simulation.R")

# Parametres
estim <- c(beta, lambda, gamma)

simu <- simulation(estim, statesInit, Time, SIR)

## Visualisation
plot_dynamics <- simu %>%
    gather(S:R, key = "comp", value = "nb") %>%
    ggplot(aes(x = time, y = nb, color = comp)) +
    geom_line() +
    xlab("time step") +
    ylab("Nb of people per compartment") +
    theme_bw()
plot_dynamics
