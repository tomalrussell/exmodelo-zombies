library(dplyr)
library(ggplot2)
library(reshape2)

setwd(paste0(Sys.getenv('CS_HOME'),'/OpenMole/zombies/openmole/spatialsens'))

source(paste0(Sys.getenv('CS_HOME'),'/Organisation/Models/Utils/R/plots.R'))

# ! this experiment was no world id
res <- as.tbl(read.csv('exploration/20190417_171253_ONEFACTOR_SPATIALSENS_PHASEDIAG_GRID.csv'))

resdir='results/20190417_171253_ONEFACTOR_SPATIALSENS_PHASEDIAG_GRID';dir.create(resdir)

finalTime = 50


# variability accross all replications
sres = res %>% group_by(replication) %>% summarize(count=n(),finalHumansPhasdiagSd = sd(humansDynamic50),
                                            finalHumansPhasediagAmplitude = max(humansDynamic50) - min(humansDynamic50),
                                            finalHumansPhasediagMin =  min(humansDynamic50),
                                            finalHumansPhasediagMax =  max(humansDynamic50)
                                            )
summary(sres)

sreslocal = res %>% group_by(id) %>% summarize(count=n(),finalHumansPhasdiagSd = sd(humansDynamic50),
                                                   finalHumansPhasediagAmplitude = max(humansDynamic50) - min(humansDynamic50),
                                                   finalHumansPhasediagMin =  min(humansDynamic50),
                                                   finalHumansPhasediagMax =  max(humansDynamic50)
)

summary(sreslocal)
# -> some param values make up to half ampl variation across the different worlds !

# TODO distances between phase diags
#
# ref = Jaude ?
# -> need world ids !








