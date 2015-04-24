# R script for logistic regression, supervised
# training data from Antelope Realtime Events
#
# e.g., run as:
#   r --no-save < train_dating.r
#

data <- read.csv('demo_dating_training_data.csv')

# normalize all columns except for the first one to zero mean
# and unit standard deviation
colsd <- apply(data,2,sd)[-c(1)]
data[, -c(1)] <- scale(data[, -c(1)], center=TRUE, scale=colsd)

# run logistic regression for outcome against all feature columns
mylogit <- glm(as.formula(paste("outcome ~", paste(names(data)[-c(1)], collapse=" + "))),
  data=data, family='binomial')
summary(mylogit)

# save output to text file
write(as.vector(coef(mylogit)[-c(1)]) / as.vector(colsd),
  file='r_logit_coef_dating.txt', sep=",", ncolumns=1000)
