# Plot top products over time, show spikes in popularity that can be used
# to improve predictions

library(ggplot2)
popularity = read.csv("~/dev/aml/bestbuy/training_small/popularity_trends.csv",
                      header=TRUE, colClasses=c("date"="character"))
popularity[[2]] <- as.Date(popularity[[2]],"%Y%m%d")
p <- ggplot(popularity, aes(x=date,y=ct, group=title, color=title))
p + geom_line()
