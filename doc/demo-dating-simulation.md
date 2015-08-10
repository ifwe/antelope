---
layout: doc-page
title: Dating Simulation
---

The dating simulation demo most closely mirrors the primary application of agile machine learning at if(we). While simulated data can never mimic the full richness of user behavior, it provides a convenient encapsulation and allows us to share our work without putting at risk user privacy.

## Configure environment ##

If you haven't done so in the context of other demos you will need to set the environment variable *$ANTELOPE_TRAINING* to a path on the local filesystem.

    mkdir $ANTELOPE_TRAINING
    export ANTELOPE_TRAINING=$ANTELOPE_DEMO/training

## Generate training data ##

First create an empty weights file&mdash;we'll fill it in later.

    touch $ANTELOPE_TRAINING/r_logit_coef_dating.txt

Within sbt execute the following commands:

    project demo-dating-simulation
    runMain co.ifwe.antelope.datingdemo.exec.Train

The simulation will begin to simulate a users joining a dating site and interacting with one another. The model has access to user profile information and observed user behavior, but it cannot access the randomized parameters describing user preferences or user activity levels. Periodically users the model for recommendations, which it must select, aiming to optimize mutual compatibility.

Training runs from timestamp 0 up to 2,000,000. During this time period the model provides random recommendations. You can see the data by viewing the training file on the command line:

    less $ANTELOPE_TRAINING/demo_dating_training_data.csv

We have provided a script to train the model using R. Execute it on the command line and view the output at follows

    cd $ANTELOPE_TRAINING
    r --no-save < $ANTELOPE_DEMO/antelope/scripts/r/train_dating.r
    cat r_logit_coef_dating.txt

Your coefficients should look like the following

    0.8846154,-0.01354816,-1.190565e-07,0.06699019,-0.008815056,-0.002402504,-0.09513831,0.02153472

## Score the model ##

Back within sbt execute the following commands:

    runMain co.ifwe.antelope.datingdemo.exec.Score

this runs quickly for the until the timestamp reaches 2,000,000, slowing after that as the simulation calls on the model recommendations rather than on random recommendations for a fraction of the requests. The simulation is configured to run up to timestamp 2,500,000.

Final output shows something like the following.

    DatingModel 6362|1390|1318|79 : 21.8% 94.8% 6.0% : 1.2%
    RandomRecommendation 1894625|102066|100173|13672 : 5.4% 98.1% 13.6% : 0.7%

The number that matters most is the final percentage: the DatingModel makes matches at a rate of 1.2% whereas the RandomRecommendation model makes matches only 0.7% of the time.

**TODO: section to be extended with further discussion of simulation internals.**