---
layout: doc-page
title: Getting Started with the Best Buy Demo
---

You've arrived in the right place!  The best way to understand what Antelope does is probably just
to give it a quick try.  This project contains ships with demo code that works with the
[ACM SF Chapter's Best Buy data set](https://www.kaggle.com/c/acm-sf-chapter-hackathon-small).
Just follow the steps below.

Should you have any issues please reach out at our [Google Group]({{ site.googlegroupurl }}).

## Download necessary files

### Start out by creating a working directory in a location of your choice

    export ANTELOPE_DEMO=$HOME/are
    mkdir $ANTELOPE_DEMO
    cd $ANTELOPE_DEMO

### Create additional directories you will need

    export ANTELOPE_DATA=$ANTELOPE_DEMO/data
    export ANTELOPE_TRAINING=$ANTELOPE_DEMO/training
    export ANTELOPE_CACHE=/tmp/antelope
    mkdir $ANTELOPE_DATA
    mkdir $ANTELOPE_TRAINING
    mkdir $ANTELOPE_CACHE

### Grab the Antelope source code from Github

    git clone https://github.com/ifweco/antelope.git antelope

### Download data sets from Kaggle

You will need to create an account with [Kaggle](https://www.kaggle.com/) ahead of
downloading the data sets.  Then visit the
[data download page](https://www.kaggle.com/c/acm-sf-chapter-hackathon-small/data).

Use your browser to download the following files, placing them in $ANTELOPE_DATA:

* <https://www.kaggle.com/c/acm-sf-chapter-hackathon-small/download/small_product_data.xml>
* <https://www.kaggle.com/c/acm-sf-chapter-hackathon-small/download/train.csv>

Place the training data in the appropriate folder, e.g.,

    mv ~/Downloads/train.csv $ANTELOPE_DATA
    mkdir -p $ANTELOPE_DATA/product_data/products
    mv ~/Downloads/small_product_data.xml $ANTELOPE_DATA/product_data/products

Sort the training data set so that events are ordered according to click time

    cd $ANTELOPE_DATA

    head -n1 train.csv > train_sorted.csv
    tail -n +2 train.csv | sort -t',' -k5 >> train_sorted.csv

## Generate training data

You will now use Antelope to generate training data. To proceed you must first install [Scala](www.scala-lang.org) with sbt by following the [sbt installation instructions for your platform](http://www.scala-sbt.org/release/tutorial/Setup.html). Then go ahead and start sbt.

    cd $ANTELOPE_DEMO/antelope
    sbt

Within sbt execute the following commands to generate training data

    project demo-best-buy
    runMain co.ifwe.antelope.bestbuy.exec.LearnedRankerTraining

Now you should have training data in a file.  You can have a quick look at it

    less $ANTELOPE_TRAINING/training_data.csv

## Train a model

These instructions use R.  You can also [train your model using Vowpal Wabbit](train_vw.html).  Training a model
is quick and easy but if you don't have either of these tools installed you might want to skip ahead to the next
section.  This demo ships with effective model parameters.

    cd $ANTELOPE_TRAINING
    r --no-save < $ANTELOPE_DEMO/antelope/scripts/r/train.r
    cat r_logit_coef.txt

Your coefficients should look like the following

    51.50027,40.53215,0.1162305,-0.02048718,1.174933,0.1279296

The coefficients you have generated will be read from this file when scoring the model.

## Score the model

Now back in sbt execute the following

    project demo-best-buy
    runMain co.ifwe.antelope.bestbuy.exec.LearnedRankerScoring

You should see something like the following final output

    Miss Analysis
    Candidates for spelling correction
    extra space: 144
    missingSpace: 35
    extraLetters: 5
    missingLetters: 3
    changeLetters: 4
    transposeLetters: 0
    total correctable: 191
    total missed 2790
    ()
    Overall success rate: 8605/10000 hits for 86.1%
    [success] Total time: 12 s, completed Aug 10, 2015 5:31:13 PM

That's it, you've successfully trained and scored a model!  In this case the hit percentage represents the
fraction of rankings that contain the product viewed by the user within the first top 5 results.

## Creating your own model

The demo model is implemented in [co.ifwe.antelope.bestbuy.model.BestBuyModel]({{"/demo-best-buy/src/main/scala/co/ifwe/antelope/bestbuy/model/BestBuyModel.scala" | prepend: site.githubsrc }}).
You might first try removing features and comparing performance.  After that, try writing your own features.

The demo framework instantiates the model in [co.ifwe.antelope.bestbuy.Env]({{"/demo-best-buy/src/main/scala/co/ifwe/antelope/bestbuy/Env.scala" | prepend: site.githubsrc }}).
If you want to swap out model classes, go ahead and modify this file.  You may find it more intuitive to start out
using the simpler [co.ifwe.antelope.bestbuy.model.DemoBestBuyModel]({{"/demo-best-buy/src/main/scala/co/ifwe/antelope/bestbuy/model/DemoBestBuyModel.scala" | prepend: site.githubsrc }}),
which uses nested classes to define features, which can make simple models simpler to follow.

## What did you think?

Please join the [Google Group]({{ site.googlegroupurl }}) to let us know.

## Next

You're all set up to see product search results live in the web app. See the [Best Buy demo web app](demo-best-buy-web.html).