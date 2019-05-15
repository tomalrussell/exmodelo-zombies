library(deSolve)
library(tidyverse)

## Get the data
dataFiles <- list.files(path = "calibration/", full.names = T)
dataList <- vector("list", length = length(dataFiles))

for (i in seq_along(dataFiles)) {
    dataList[[i]] <- read_csv(file = dataFiles[[i]], col_names = T) %>%
        rename(generation = "evolution$generation")
}

raw_data <- bind_rows(dataList) %>%
    arrange(generation)



simulOMS <- read_csv("simulODE.csv")

plot_data <- simu %>%
    bind_cols(simulOMS) %>%
    bind_cols(file) %>%
    gather(H_walk:walkingZombiesAvg, key = "category", value = "nb") %>%
    mutate(species = ifelse(grepl("H", category, ignore.case = T), "human", "zombified"),
           originData = ifelse(grepl("Avg", category), "ABM", ifelse(grepl("ing", category), "ODE_OMS", "ODE_R")),
           speed = ifelse(grepl("walk", category, ignore.case = T), "walking", "running")) %>%
    select(-category)

plot_dynamics <- plot_data %>%
    filter(originData != "ODE_R") %>%
    ggplot(aes(x = time, y = nb, color = speed, linetype = originData)) +
    geom_line() +
    scale_linetype_manual(values = c("dashed", "solid", "dotted")) +
    xlab("time step") +
    facet_wrap(~ species, nrow = 2) +
    theme_bw()
plot_dynamics

LL <- logLik(simulOMS$humansWalking, file$walkingHumansAvg) +
    logLik(simulOMS$humansRunning, file$runningHumansAvg) +
    logLik(simulOMS$zombifiedWalking, file$walkingZombiesAvg) +
    logLik(simulOMS$zombifiedRunning, file$runningZombiesAvg)

print(LL)


sommes <- plot_data %>%
    spread(key = speed, value = nb) %>%
    group_by(originData, species) %>%
    mutate(total = walking + running)

plot_dynamics_sum <- sommes %>%
    filter(originData != "ODE_OMS") %>%
    ggplot(aes(x = time, y = total, color = originData)) +
    geom_line() +
    # scale_linetype_manual(values = c("dashed", "solid", "dotted")) +
    xlab("time step") +
    facet_wrap(~ species, nrow = 2) +
    theme_bw()
plot_dynamics_sum
