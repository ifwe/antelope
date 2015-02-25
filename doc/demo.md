# Getting Started with the Demo #

You've arrived in the right place!  The best way to understand what Antelope does is probably just
to give it a quick try.  This project contains ships with demo code that works with the
[ACM SF Chapter's Best Buy data set](https://www.kaggle.com/c/acm-sf-chapter-hackathon-small).
Just follow the steps below.

Should you have any issues please reach out at our [Google Group](https://groups.google.com/forum/#!forum/antelope-rte).

## Download necessary files

### Start out by creating a working directory in a location of your choice

    export ANTELOPE_DEMO=$HOME/are
    mkdir $ANTELOPE_DEMO
    cd $ANTELOPE_DEMO

### Create additional directories you will need

    export ANTELOPE_DATA=$ANTELOPE_DEMO/data
    export ANTELOPE_TRAINING=$ANTELOPE_DEMO/training
    mkdir $ANTELOPE_DATA
    mkdir $ANTELOPE_TRAINING

### Grab the Antelope source code from Github

    git clone https://github.com/ifweco/antelope.git antelope

### Download data sets from Kaggle

You will need to create an account with [Kaggle](https://www.kaggle.com/) ahead of
downloading the data sets.  Then visit the
[data download page](https://www.kaggle.com/c/acm-sf-chapter-hackathon-small/data).

Use your browser to download the following files, placing them in $ANTELOPE_DATA:
- <https://www.kaggle.com/c/acm-sf-chapter-hackathon-small/download/small_product_data.xml>
- <https://www.kaggle.com/c/acm-sf-chapter-hackathon-small/download/train.csv>

Sort the training data set so that events are ordered according to click time

    cd $ANTELOPE_DATA

    head -n1 train.csv > train_sorted.csv
    tail -n +2 train.csv | sort -t',' -k5 >> train_sorted.csv

## Generate training data

You will now use Antelope to generate training data

    cd $ANTELOPE_DEMO/antelope
    sbt

Within sbt execute the following commands to generate training data

    project demo
    runMain co.ifwe.antelope.bestbuy.exec.LearnedRankerTraining

Now you should have training data in a file.  You can have a quick look at it

    less $ANTELOPE_TRAINING/training_data.csv

## Train a model

These instructions use R.  You can also [train your model using Vowpal Wabbit](train_vw.md).  Training a model
is quick and easy but if you don't have either of these tools installed you might want to skip ahead to the next
section.  This demo ships with effective model parameters.

    cd $ANTELOPE_TRAINING
    r --no-save < $ANTELOPE_DEMO/antelope/scripts/r/train.r
    cat r_logit_coef.txt

Your coefficients should look like the following

    88.77053,2437.086,0.1170896,7670.614,-0.02810121

You can verify that these are hardcoded in the class
[co.ifwe.antelope.bestbuy.exec.LearnedRankerScoring](../demo/src/main/scala/co/ifwe/antelope/bestbuy/exec/LearnedRankerScoring.scala).
There is no need to change these now, but if you change the model definition you will need to do so.

## Score the model

Now back in sbt execute the following

    project demo
    runMain co.ifwe.antelope.bestbuy.exec.LearnedRankerScoring

You should see something like the following final output

    progress 38500
    progress 39000
    progress 39500
    progress 40000
    co.ifwe.antelope.bestbuy.model.BestBuyModel@65851435 finishing with stats: 8472/10000 hits for 84.7%
    completed 40000 in 17337 ms, rate of 2307/s
    [success] Total time: 20 s, completed Feb 25, 2015 1:23:22 PM

That's it, you've successfully trained and scored a model!  In this case the hit percentage represents the
fraction of rankings that contain the product viewed by the user within the first top 5 results.

## Creating your own model

The demo model is implemented in [co.ifwe.antelope.bestbuy.model.BestBuyModel](../demo/src/main/scala/co/ifwe/antelope/bestbuy/model/BestBuyModel.scala).
You might first try removing features and comparing performance.  After that, try writing your own features.

The demo framework instantiates the model in [co.ifwe.antelope.bestbuy.ModelEventProcessor](../demo/src/main/scala/co/ifwe/antelope/bestbuy/ModelEventProcessor.scala).
If you want to swap out model classes, go ahead and modify this file.  You may find it more intuitive to start out
using the simpler [co.ifwe.antelope.bestbuy.model.DemoBestBuyModel](../demo/src/main/scala/co/ifwe/antelope/bestbuy/model/DemoBestBuyModel.scala),
which uses nested classes to define features, which can make simple models simpler to follow.

## What did you think?

Please join the [Google Group](https://groups.google.com/forum/#!forum/antelope-rte) to let us know.
