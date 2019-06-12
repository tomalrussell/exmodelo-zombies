library(deSolve)
library(tidyverse)

data <- read_csv("parcimonie_population20000.csv")
save(data, file = "calib_parcimony.RData")

pareto_front <- data %>%
    ggplot(aes(x = parcimony, y = fitness)) +
    geom_point() +
    geom_line() +
    theme_bw()
pareto_front

ggsave("plot_parcimony.png", pareto_front)
