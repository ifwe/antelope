library(ggplot2)

datadir <- "~/dev/aml/bestbuy/training_large/"
getPath <- function(filename) {
  return(paste(dateDir,filename,""))
}
# search activity by day of week
dayofweek_searches <- read.csv(paste(datadir,"day-of-week_searches.csv",sep=""))
dayofweek_searches$day.of.week=c("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
p <- ggplot(dayofweek_searches, aes(x=factor(day.of.week,as.character(day.of.week)),y=searches))
p + geom_bar(stat="identity")

#search activity by hour
hour_searches <- read.csv(pasts(datadir,"hour_searches.csv",sep=""))
p <- ggplot(hour_searches, aes(x=hour, y=searches))
p + geom_bar(stat="identity")

# update and search activity by day
date_searches <- read.csv(paste(datadir,"date_searches.csv",sep=""),colClasses=c("character","numeric"))
date_searches$date <-as.Date(date_searches$date,"%Y%m%d")
date_updates <- read.csv()
p <- ggplot(date_searches,aes(x=date,y=searches)) 
p + geom_point()
View(date_searches)
