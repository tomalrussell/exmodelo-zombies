library(dplyr)
library(readr)
library(reshape2)
library(ggplot2)
library(parallel)

nbcores <-  detectCores() -2

#loading data
setwd("~/tmp/")
df <-  read_csv("lhs_grid_new.csv")
names(df)
last_param_index <-  17
simu_duration <-  50
dfParams <-  subset(df, select = c(1:last_param_index))
names(dfParams)[17]
##Identification of unique parameterization 

#SIMULATION seed vary from a replication to another -> removed collumn of seed before unique() 
uniqueParam <- unique(dfParams[,-1])
uniqueParam$paramID <-  1:nrow(uniqueParam)
uniqueParam <-  as.data.frame(uniqueParam)


#JOIN uniqueParam with df then split into human history records and zombies history records
# join is made on param values without seed so start from 2nd column to last column of humans/zombies
firstHDynCol <-  which(names(df)=="humansDynamic0")
dfHumans  <- subset(df,select = c(1:(simu_duration+firstHDynCol-1)))
dfHumans <-  inner_join(dfHumans, uniqueParam)
names(dfHumans)


#idem we  take parameters without seed and zombies dynamics before joining with unique param
firstZDynCol <-  which(names(df)=="zombiesDynamic0")
dfZombies  <- subset(df,select = c(1:last_param_index, firstZDynCol   : (simu_duration+firstZDynCol-1) ))
dfZombies <-  inner_join(dfZombies, uniqueParam)
names(dfZombies)

#chunk the data from the first colname to 50+ columns , and the parameters
chunker <-  function(firstColname, df){
  firstDynCol   <-  which(names(df)==firstColname)
  dfChunked <-  subset(df,select = c(1:last_param_index, firstDynCol:(simu_duration+firstDynCol-1) ))
  dfChunked <-  inner_join(dfChunked, uniqueParam)
  dfChunked <-  subset(dfChunked, select=c(1, (last_param_index+1) :(simu_duration+last_param_index+1)))
  dfFinal <-  data.frame()
  dfFinal <-  melt(dfChunked, id.vars = c("seed", "paramID"))
  return(dfFinal)
}

dfHlong <-  chunker(firstColname = "humansDynamic0", df)
dfZlong <-  chunker(firstColname = "zombiesDynamic0", df)
dfGonelong <-  chunker(firstColname = "goneDynamic0", df)
dfKilledlong <-  chunker(firstColname = "killedDynamic0", df)
dfZombifiedlong <-  chunker(firstColname = "zombifiedDynamic0", df)
dfZpursuelong <-  chunker(firstColname = "pursueDynamic0", df)
dfFleelong <-  chunker(firstColname = "fleeDynamic0", df)



str(dfHlong)

dfHlong$variable <-  as.character(dfHlong$variable)
  substr(dfHlong$variable,nchar(dfHlong$variable)-1,nchar(dfHlong$variable))

addstepNumber <-   function(df){
  
  
  
  
}



# write.csv(dfZombieslong, "ZombiePopulation.csv")
# write.csv(dfHumanslong, "HumanPopulation.csv")

head(dfHlong)

rm(ph1)
ph1 <-  ggplot(dfHumanslong, aes(x=stepnumber, y=value, group=seed, color=factor(paramID)) )+
  geom_line()+
  scale_color_discrete()
ph1




pz1 <-  ggplot(dfZombieslong , aes(x=stepnumber, y=value, group=seed, color=factor(paramID)))+
   geom_line()+
  scale_color_discrete()
pz1


# ph1 <-  ggplot(dfHumanslong, aes(x=stepnumber, y=value, group=seed, color=) )+
#   geom_line()+
#   scale_color_continuous()
# ph1


medmeanZ <-  dfZombieslong %>%  
  group_by(paramID, stepnumber) %>% 
  summarise(med=median(value), moy=mean(value), sd=sd(value))

medmeanH <-  dfHumanslong %>%  
  group_by(paramID, stepnumber) %>% 
  summarise(med=median(value), moy=mean(value), sd=sd(value))


medmeanH <-  inner_join(medmeanH, uniqueParam)
medmeanZ <-  inner_join(medmeanZ, uniqueParam)




names(medmeanZ)







generateZombiesPlotsOverParams <- function(pIndex) {
  
  lili <-  as.data.frame(medmeanZ[,paramindex[pIndex]])[,1]
  currentparamName <-  paramNames[pIndex]
  cat(pIndex,":",currentparamName,"\n")
  
  
  ppmed <-  ggplot(medmeanZ, aes(x=stepnumber, y=med, group=paramID, color=lili))+
  geom_line(size=0.4, alpha=0.8)+
  labs(title = "Rise and Fall of the Undead", x = "step", y = "Zombies population\n (median over replications)", color=currentparamName)+
  scale_colour_viridis_c()

fifiname <-  paste0("ZombiesMedian_",currentparamName,".png")
ggsave(fifiname)

ppmean <-  ggplot(medmeanZ, aes(x=stepnumber, y=moy, group=paramID,color=lili))+
  geom_line(size=0.4, alpha=0.8)+
  labs(title = "Rise and Fall of the Undead", x = "step", y = "Zombies population\n (mean over replications)", color=currentparamName)+
  scale_colour_viridis_c()
ppmean
fifiname <-  paste0("ZombiesMean_",currentparamName,".png")
ggsave(fifiname)


ppsd <-  ggplot(medmeanZ, aes(x=stepnumber, y=sd, group=paramID,color=lili))+
  geom_line(size=0.4, alpha=0.8)+
  labs(title = "Rise and Fall of the Undead", x = "step", y = "Zombies population\n (standard deviation over replications)", color=currentparamName)+
  scale_colour_viridis_c()
fifiname <-  paste0("ZombiesSD_",currentparamName,".png")
ggsave(fifiname)
}

generateHumansPlotsOverParams <- function(pIndex) {
  
  lili <-  as.data.frame(medmeanH[,paramindex[pIndex]])[,1]
  currentparamName <-  paramNames[pIndex]
  cat(pIndex,":",currentparamName,"\n")
  
  
  ppmed <-  ggplot(medmeanH, aes(x=stepnumber, y=med, group=paramID, color=lili))+
    geom_line(size=0.4, alpha=0.8)+
    labs(title = "Humans Doom", x = "step", y = "Human population\n (median over replications)", color=currentparamName)+
    scale_colour_viridis_c()
  
  fifiname <-  paste0("HumansMedian",currentparamName,".png")
  ggsave(fifiname)
  
  ppmean <-  ggplot(medmeanH, aes(x=stepnumber, y=moy, group=paramID,color=lili))+
    geom_line(size=0.4, alpha=0.8)+
    labs(title = "Humans Doom", x = "step", y = "HUmans population\n (mean over replications)", color=currentparamName)+
    scale_colour_viridis_c()
  ppmean
  fifiname <-  paste0("HumansMean",currentparamName,".png")
  ggsave(fifiname)
  
  
  ppsd <-  ggplot(medmeanH, aes(x=stepnumber, y=sd, group=paramID,color=lili))+
    geom_line(size=0.4, alpha=0.8)+
    labs(title = "Humans Doom", x = "step", y = "Humans population\n (standard deviation over replications)", color=currentparamName)+
    scale_colour_viridis_c()
  fifiname <-  paste0("HumansSD_",currentparamName,".png")
  ggsave(fifiname)
}

paramindex <-  6:17
paramNames <-  names(medmeanH)[6:17]


# generation des plots 
sapply(1:11, generateZombiesPlotsOverParams)
sapply(1:11, generateHumansPlotsOverParams)




#boxplot par paramID
rm(df)
names(dfHumanslong)


displayHumansTrajByID <-function(id){
zz <-  dfHumanslong %>% filter(paramID==id)
ppp <-  ggplot(zz, aes(x=stepnumber,y=value))+
  geom_boxplot(aes(group=stepnumber), outlier.alpha = 0.5)+
  labs(title = "", x = "step", y = "Human population\n (boxplot over replications)")
print(ppp)
}

displayZombiesTrajByID <-function(id){
  zz <-  dfZombieslong %>% filter(paramID==id)
  ppp <-  ggplot(zz, aes(x=stepnumber,y=value))+
    geom_boxplot(aes(group=stepnumber), outlier.alpha = 0.5)+
    labs(title = "", x = "step", y = "Zombies population\n (boxplot over replications)")
  print(ppp)
}

displayHumansTrajByID(12)
displayZombiesTrajByID(12)

# 25 premiers paramétrages à la somme des écarts types  la simu minimale 
id25LowSD <- medmeanH %>% 
  group_by(paramID) %>% 
  summarise(sumsd= sum(sd)) %>%  
  arrange((sumsd)) %>% head(25)

#somme des sd le long de la simu
sumSDbyParamID_H <-  medmeanH %>% group_by(paramID) %>% summarise(sumsd= sum(sd)) 
uniqueParam$sumSD_H <-  sumSDbyParamID_H$sumsd
gaga <- uniqueParam %>% melt(id.vars=c("sumSD_H", "paramID")) 


lowSDparamIDs <-  gaga %>%  arrange(sumSD_H) %>%  group_by(paramID) 

p1p2 <-  ggplot(gaga, aes(x=sumSD_H, y=value, label=paramID))+
  geom_point(color="grey")+
  geom_text()+
  facet_wrap(~variable,scales = 'free')
p1p2

p3p4 <-  ggplot(uniqueParam, aes(x=panicDuration, y=zombieAcuteness, color=sumSD_H))+
  geom_point()+
  scale_color_viridis_c(direction = -1)
p3p4


p5p6 <-  ggplot(uniqueParam, aes(x=zombieLifespan, y=zombieSpeedFactor, color=sumSD_H))+
  geom_point()+
  scale_color_viridis_c(direction = -1)
p5p6


p3p6 <-  ggplot(uniqueParam, aes(x=panicDuration, y=zombieSpeedFactor, color=sumSD_H))+
  geom_point()+
  scale_color_viridis_c(direction = -1)
p3p6





#### représentation de la population H et Z en diagramme de phase


dfwithParamID <-  inner_join(df, uniqueParam)

H_Traj_Extraction <-  function(dfwithParamID, myparamID ){
  lili <-  filter(dfwithParamID, paramID == myparamID)[, 14:63]
  return((lili))
}
Z_Traj_Extraction <-  function(dfwithParamID, myparamID ){
  lili <-  filter(dfwithParamID, paramID == myparamID)[, 64:113]
  return((lili))
}

for ( parametrage in 1:20){
liliH <-  H_Traj_Extraction(dfwithParamID, parametrage)
liliZ <- Z_Traj_Extraction(dfwithParamID, parametrage)
xx <-t(liliH)
yy <-t(liliZ)
   


turnIntolongdf <-  function(xx){
names(xx) <- c("step", "rep")
xx <-  melt(xx)
names(xx) <-  c("step", "repli", "value")
return(xx)
}

xx <- turnIntolongdf(xx)
yy <- turnIntolongdf(yy)

PhasDiag  <- data.frame(xx,yy)



png(filename = paste0(paste("param",parametrage), ".png"), width = 1000, height = 1000)
ph1 <-  ggplot(PhasDiag, aes(x=value, y=value.1, color=factor(repli)))+
  geom_polygon(aes(fill=factor(repli)),alpha=0.4)+
  #geom_point()+
  #geom_text(label=rep(as.character(1:50),20), color= "black", nudge_y = -0.1 + runif(50*20)*0.1)+
  scale_color_discrete()
print(ph1)
dev.off()
}


id25LowSD




   # displayHumansTrajByID(13)
# displayZombiesTrajByID(13)
# ph1 <-  ggplot(dfZombieslong %>% filter(paramID==13), aes(x=stepnumber, y=value, group=seed, color=factor(paramID)) )+
#   geom_line()+
#   scale_color_discrete()
# ph1

phaseDiag <-  data.frame(xx,yy)



ph1 <-  ggplot(head(phaseDiag,50), aes(x= xx , y = yy))+
  geom_polygon(fill="#000000FF")
ph1



names(dfwithParamID)
humans_by_step












