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
    gather(H_walk:zombifiedWalking, key = "category", value = "nb") %>%
    mutate(species = ifelse(grepl("H", category, ignore.case = T), "human", "zombified"),
           originData = ifelse(grepl("ing", category), "ODE_OMS", "ODE_R"),
           speed = ifelse(grepl("walk", category, ignore.case = T), "walking", "running"))

plot_data <- filter(plot_data, originData == "ABM") %>%
    full_join(test)

plot_dynamics <- plot_data %>%
    filter(originData != "ODE_R") %>%
    ggplot(aes(x = time, y = nb, color = speed, linetype = originData)) +
    geom_line() +
    scale_linetype_manual(values = c("dashed", "solid", "dotted")) +
    # geom_line(aes(y = humans), col = "green") +
    # geom_line(aes(y = zombies), col = "red") +
    # geom_line(aes(y = humansAvg), col = "green", linetype = "dashed") +
    # geom_line(aes(y = zombifiedAvg), col = "red", linetype = "dashed") +
    xlab("time step") +
    # ylab("# humans (green) or zombies (red)") +
    facet_wrap(~ species, nrow = 2) +
    theme_bw()
plot_dynamics
