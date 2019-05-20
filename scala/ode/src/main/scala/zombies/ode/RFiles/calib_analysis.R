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
