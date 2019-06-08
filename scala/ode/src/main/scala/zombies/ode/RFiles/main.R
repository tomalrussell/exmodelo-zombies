library(deSolve)
library(tidyverse)


# Real data
file = read_csv("ZombielandData_1000repli.csv") %>%
    select(contains("Avg"))
# ODE parameters
# calib no tWarp
panic0 = 7.252818
staminaH = 0.9997521
inf = 0.01977554
hunt0 = 10.153002
staminaZ = 1.280096
# calib tWarp
panic0 = 100
staminaH = 0.10866696052269618
inf = 0.004585325476695915
hunt0 = 7.974418405424284
staminaZ = 0.6890335279647442
# Initial conditions
statesInit = c(250.0, 0.0, 0.0, 4.0)
# Time steps
t0 = 0
dt = 1
tMax = 500
Time = seq(t0, tMax, dt)


## Modele compartimental
source("modele.R")
## Simulation
source("simulation.R")

# Parametres
exhaustH <- 1.0 / staminaH
exhaustZ <- 1.0 / staminaZ

estim <- c(panic0, exhaustH, inf, hunt0, exhaustZ)

simu <- simulation(estim, statesInit, Time, ODE)

## Log-Likelihood calculation for every combination
source("fitness.R")
LL <- logLik(simu$H_walk, file$walkingHumansAvg) +
    logLik(simu$H_run, file$runningHumansAvg) +
    logLik(simu$Z_walk, file$walkingZombiesAvg) +
    logLik(simu$Z_run, file$runningZombiesAvg)

print(LL)

## Visualisation
plot_data <- simu %>%
    bind_cols(file) %>%
    gather(H_walk:walkingZombiesAvg, key = "category", value = "nb") %>%
    mutate(species = ifelse(grepl("H", category), "human", "zombified"),
           originData = ifelse(grepl("Avg", category), "ABM", "ODE"),
           speed = ifelse(grepl("walk", category), "walking", "running"))

plot_dynamics <- plot_data %>%
    ggplot(aes(x = time, y = nb, color = speed, linetype = originData)) +
    geom_line() +
    scale_linetype_manual(values = c("dashed", "solid")) +
    xlab("time step") +
    facet_wrap(~ species, nrow = 2) +
    theme_bw()
plot_dynamics
