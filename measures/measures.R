library(dplyr)
library(readr)
library(reshape2)


#loading data
setwd("~/tmp/")
df <-  read_csv("lhs_grid.csv")

dfParams <-  subset(df, select = c(1:13))

##Identification of unique parameterization 
#SIMULATION seed vary from a replication to another -> removed before unique() 
uniqueParam <- unique(dfParams[,-1])
uniqueParam$paramID <-1:nrow(uniqueParam)


#initial human population 
all(df$humans == 250)
all(df$zombies == 4)

iniH <-  df$humans[1]
iniZ <-  df$zombies[1]



HCols <-  names(df)[14:63]
ZCols <-  names(df)[64:113]
nbsteps <-  50
lili <-  1

for(col in 1:nbsteps)
  {
  popCourante <-  df[lili, h]
  if (df[lili,col] > df[lili, ])
}







