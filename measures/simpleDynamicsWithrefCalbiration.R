


library(ggplot2)


df <-  read.csv("/home/chap/Téléchargements/calibratedReplications.csv")
df <-  df[,-26]
meanAll <-  colMeans(df)
df %>% mutate() %>% 
str(names(df))
ref <- c(22.26, 55.47, 43.53, 24.39, 11.15, 4.19, 2.0, 0.8, 0.54, 0.24, 0.09, 0.01, 0.01, 0.01, 0.02, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
df$repli <- 1:100
refSteps <- as.factor(0:24)
head(df)


dfRef <-  data.frame(x=refSteps, y=ref, moy=meanAll)

dd <- melt(df,id.vars = "repli")
levels(dd$variable) <- as.factor(0:24)

str(dfRef)
str(dd)


levels(dfRef$x) <- paste0("rescuedDynamic", 0:24)



wholedf <-  dd

pp <-  ggplot(dd)+
  geom_boxplot(aes(y=value, x=variable), fill="white", outlier.shape = NA)+

  geom_line(aes(x=variable, y=value, group=repli), color= "forestgreen", alpha=0.1)+
  geom_line(data=dfRef, aes(x=x, y=y),group="ref", color= "orange", size=1)+
  geom_line(data=dfRef, aes(x=x, y=moy),group="moy", color= "blue", size=1, linetype=2)
pp



dfRef$y

library(plotly)
ggplotly(pp)
