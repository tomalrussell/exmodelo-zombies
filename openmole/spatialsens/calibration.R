library(dplyr)
library(ggplot2)

setwd(paste0(Sys.getenv('CS_HOME'),'/OpenMole/zombies/openmole/spatialsens'))

source(paste0(Sys.getenv('CS_HOME'),'/Organisation/Models/Utils/R/plots.R'))

resprefix = '20190418_153943_CALIB_GENERATORS_GRID/'
resdir = paste0('calibration/',resprefix)
figdir = paste0('results/',resprefix);dir.create(figdir)

latestgen <- function(dir){max(as.integer(sapply(strsplit(sapply(strsplit(list.files(dir,pattern=".csv"),"population"),function(s){s[2]}),".csv"),function(s){s[1]})))}

front <- as.tbl(read.csv(paste0(resdir,'population',latestgen(resdir),'.csv')))

summary(front$evolution.samples)

g=ggplot(front[front$evolution.samples>5,],aes(x=finalZombies,y=oppFinalHumans,col=generatorType,size=evolution.samples))
g+geom_point(alpha=0.5)+xlab('Final zombies')+ylab('Opposite of final humans')+stdtheme
ggsave(paste0(figdir,'pareto_samplesgt5.png'),width=20,height=18,units='cm')

#g=ggplot(front[abs(front$oppFinalHumans)>50&front$evolution.samples>20,],aes(x=finalZombies,y=oppFinalHumans,col=generatorType,size=evolution.samples))
#g+geom_point(alpha=0.5)


