library(deSolve)
library(tidyverse)


# Real data
file = read_csv("ZombielandData_1000repli.csv") %>%
    select(contains("Avg"))
# ODE parameters
panic0 = 7.252818
staminaH = 0.9997521
inf = 0.01977554
hunt0 = 10.153002
staminaZ = 1.280096
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

# simu_sums <- simu %>%
#     transmute(humans = H_walk + H_run,
#               zombies = Z_walk + Z_run - 4)

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
    # geom_line(aes(y = humans), col = "green") +
    # geom_line(aes(y = zombies), col = "red") +
    # geom_line(aes(y = humansAvg), col = "green", linetype = "dashed") +
    # geom_line(aes(y = zombifiedAvg), col = "red", linetype = "dashed") +
    xlab("time step") +
    # ylab("# humans (green) or zombies (red)") +
    facet_wrap(~ species, nrow = 2) +
    theme_bw()
plot_dynamics

## Log-Likelihood calculation for every combination
source("fitness.R")
LL <- logLik(simu$H_walk, file$walkingHumansAvg) +
    logLik(simu$H_run, file$runningHumansAvg) +
    logLik(simu$Z_walk, file$walkingZombiesAvg) +
    logLik(simu$Z_run, file$runningZombiesAvg)

print(LL)
