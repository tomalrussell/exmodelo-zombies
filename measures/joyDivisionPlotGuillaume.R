library(ggplot2)
library(ggridges)
library(dplyr)
library(reshape2)
mydf <- read.csv("~/Téléchargements/step27.csv")

ggplot(mydf) +
  geom_density(mapping=aes(x=theta0,  stat(density)), color="green", alpha=0.3) +
  geom_density(mapping=aes(x=theta1, stat(density)), color="blue", alpha=0.3) +
  geom_density(mapping=aes(x=theta2, stat(density)), color="red", alpha=0.3)

ggplot(mydf) +
  geom_histogram(mapping=aes(x=theta0, weight=weight), color="green", alpha=0.1) +
  geom_histogram(mapping=aes(x=theta1, weight=weight), color="blue", alpha=0.1) +
  geom_histogram(mapping=aes(x=theta2, weight=weight), color="red", alpha=0.1)


# on ne garde que les colonnes weight , t0, t1, t2
mydf <-  mydf [, 6:9]


melted_df <-  mydf %>%  melt(id.vars="weight", value.name = "theta")

pp <- ggplot(melted_df, aes(x= theta, y=variable,  color= variable, fill=variable))+
  geom_density_ridges2( aes(weight=weight),  alpha=0.1, stat = "binline"  )
pp

ppp <- ggplot(melted_df)+
  geom_density(aes(x=theta,weight=weight, color=variable) , alpha=0.1  )+
  facet_wrap(~variable, ncol = 1)
ppp


