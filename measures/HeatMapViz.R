library(tidyverse)
library(stringr)
library(reshape2)

specialRead <- function(x) {
  t <- read.table(x,sep=",", header=FALSE)
  xy <- strsplit(str_extract(x,"(\\d+,\\d+)"), ",")
  return(list(xy,as.matrix(t)))
} 

create_heatmap <- function(m){ 
  
  x <- as.numeric(m[[1]][[1]][1])
  y <- as.numeric(m[[1]][[1]][2])
  
  orderedM <- m[[2]]
  nbZombification <- sum(orderedM)
  
  dimnames(orderedM) <- list(0:39,0:39)
  test <- orderedM %>% melt() %>% as.data.frame
  test$trap <- FALSE 
  
  test$trap[test$Var1 == x & test$Var2 == y] <- TRUE
  
  p1 <- ggplot(test, aes(x = Var2, y = Var1)) +
    geom_tile(aes(fill = value), colour = "grey20") +
    xlab("X") + ylab("Y") +
    scale_fill_gradient2(low = "darkgreen", 
                         mid = "white", 
                         high = "darkred",
                         breaks = c(0,10,20,30),
                         labels = c(0,10,20,30),
                         limits=c(0,30),
                         guide = "legend") +
    geom_tile(data=test, aes(colour=factor(trap, c(TRUE, FALSE)),size=factor(trap, c(TRUE,FALSE))), alpha=0.1) + 
    geom_tile(data=jaudeDF, aes(colour=value))+
    
    scale_colour_manual("z", values=c("blue4", "white")) + 
    scale_size_manual("z", values=c(1,0)) +
    # to 
    scale_y_reverse()+
    labs( title = paste0("Plot of zombification at trap location (",x,",",y,")")) +
    labs(subtitle = paste("Nb Zombified = " , nbZombification) ) 
  
  #ggsave(paste0(nbZombification,"z_heatmap_(",x,",",y,").png"))
print(p1)
}

base <- "/home/chap/Téléchargements/"
results <- list.files(path = base, full.names = TRUE, pattern = "Hitmap") %>% lapply(specialRead)
orderedResults <- results[order(sapply(results, function(x) sum(x[[2]])), decreasing = TRUE)]
#lapply(orderedResults, create_heatmap)



jaude <-  read.table("/home/chap/dev/zombies/measures/jaude.csv",sep = ",", header = F )
jaude <-  as.matrix(jaude, nrow= 40, ncol=40)
jaudeDF <-  jaude %>% melt() %>% as.data.frame


trapLocation <-  data.frame(x =  as.numeric(unlist(orderedResults[[1]][[1]][1])[1]), 
                            y = as.numeric(unlist(orderedResults[[1]][[1]][1])[2]))


# extract the heatmap matrix and melt it into a dataframe 
orderedResultsMat <-  orderedResults[[1]][[2]]
base::rownames(orderedResultsMat) <- c(0:39)
base::colnames(orderedResultsMat) <-  c(0:39)
orderedResultsDF <- orderedResultsMat %>%  melt() %>%  as.data.frame()
orderedResultsDF$tileType <- jaudeDF$value  


# add traplocation in the df and a level
levels(orderedResultsDF$tileType) <- c("Wall", "Soil", "RESCUE", "TRAP")
orderedResultsDF[orderedResultsDF$Var1==trapLocation$y & orderedResultsDF$Var2==trapLocation$x,"tileType"] <- "TRAP"

pp <- ggplot(orderedResultsDF, aes(x = Var2, y = Var1)) +
  geom_tile(aes(fill = value, shape=), colour = "grey20") +
  xlab("X") + ylab("Y") +
  scale_fill_gradient2(low = "darkgreen", 
                       mid = "white", 
                       high = "darkred",
                       breaks = c(0,10,20,30),
                       labels = c(0,10,20,30),
                       limits=c(0,30),
                       guide = "legend") +
  # dessin des tiles fonction du type 
  geom_point(aes(shape=tileType, color=tileType,size=tileType))+
  scale_shape_manual(values= c(13, NA, 10,7), name="tile")+
  scale_colour_manual(name="tile", values=c("#222222", NA,"#3fad1d", "red")) + 
  scale_size_manual("", values=c(2,0,4,4), guide=FALSE) +
  coord_equal()+
  labs(color  = "tile", shape = "tile")+
  # to 
  scale_y_reverse()+
 labs( title = paste0("Zombifications occurences with trap located at (",trapLocation$x,",",trapLocation$y,")")) +
  labs(subtitle = paste("Nb Zombification = " ,  sum(orderedResultsDF$value)) ) 
pp

