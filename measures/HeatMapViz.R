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



## animation results calibration


res_calib_dir <- "/home/chap/Téléchargements/results/results/"
calib_files <- list.files(path = res_calib_dir) 

calib_files_sample <-  head(calib_files, 300)
setwd(res_calib_dir)


############################################################"
# make a dataframe with most replicated line in each population file
##########################################################"

extract_max_rep <-  function (fifi){
df <-  read_csv(fifi)
max_replicated_obs <-  df %>%  top_n(1,`evolution$samples`)
return(list(max_replicated_obs))
}

final_df <-  sapply(calib_files_sample,extract_max_rep)



locs <- c("trapLocation1","trapLocation2","trapLocation3")





lili <- (extract_max_rep(calib_files[1]))
lili <- lili[[1]]
lili



extractCoord <-  function(col, x){
  cat("col:", col)
  bloc <- gsub(pattern = ")", replacement = "",gsub(pattern = "\\(", replacement = "", x[col]))
  coord <- str_split(bloc,pattern = "," )
  return(as.numeric(unlist(coord)))
}


long_df_by_lili <-  function(){
trap_coords <-  lapply(locs, extractCoord,lili)
XYtrapcoords <- unlist(trap_coords)
long_generation_df <-  data.frame(matrix(XYtrapcoords,nrow = 3, byrow = T))
long_generation_df$generation <- lili$`evolution$generation`
long_generation_df$trap <- c("TRAP 1", "TRAP 2", "TRAP 3")
long_generation_df$nbsample <- lili$`evolution$samples`
long_generation_df$nbZ <-  lili$countZombifie

return(long_generation_df)
}




# long format tibble /data.frame
final_df <-  bind_rows(final_df)
final_df[1,]

##################################
# prepare background plot
#################################

background <-  orderedResultsDF
#remove the trap of previous plot
background[background$tileType=="TRAP","tileType"] <-  "Soil"


library(stringr)


final_df$trapLocation1 <- gsub(pattern = "\\(", replacement = "",final_df$trapLocation1)
final_df$trapLocation1 <- gsub(pattern = ")", replacement = "",final_df$trapLocation1)
final_df<-  separate(final_df, col = trapLocation1, into = c("trap1X","trap1Y"), sep=",")

final_df$trapLocation2 <- gsub(pattern = "\\(", replacement = "",final_df$trapLocation2)
final_df$trapLocation2 <- gsub(pattern = ")", replacement = "",final_df$trapLocation2)
final_df <-  separate(final_df, col = trapLocation2, into = c("trap2X","trap2Y"), sep=",")

final_df$trapLocation3 <- gsub(pattern = "\\(", replacement = "",final_df$trapLocation3)
final_df$trapLocation3 <- gsub(pattern = ")", replacement = "",final_df$trapLocation3)
final_df <-  separate(final_df, col = trapLocation3, into = c("trap3X","trap3Y"), sep=",")

final_df$tileType <- "TRAP"



names(orderedResultsDF)



library(gganimate)



ppp <- ggplot(background, aes(x = Var2, y = Var1)) +
  geom_tile( colour = "grey20", fill="white") +
  xlab("X") + ylab("Y") +
  # dessin des tiles fonction du type 
  geom_point(aes(shape=tileType, color=tileType,size=tileType))+
  scale_shape_manual(values= c(13, NA, 10,7), name="tile")+
  scale_colour_manual(name="tile", values=c("#222222", NA,"#3fad1d", "red")) + 
  scale_size_manual("", values=c(2,0,4,4), guide=FALSE) +
  coord_equal()+
  labs(color  = "tile", shape = "tile")+
  # 

  #
  scale_y_reverse()+
  labs( title = paste0("Zombifications occurences with 3 traps ")) +
  labs(subtitle = paste("Nb Zombification = " ,  "tutututu")) 
ppp



final_df[1,]

p4 <-  ppp+
  geom_poi



transition_time(gener)



