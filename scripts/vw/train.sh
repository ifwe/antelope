#!/bin/bash

# What we actually do is to run bfgs.  The readable model
# in model_vw_bfgs.txt has the most accurate coefficients
# but features are specified as hash values rather than
# strings.  model_varinfo_vw_bfgs.txt contains approximate
# coefficients as well as the mapping from feature names
# to coefficients.
#

$VOWPAL_HOME/utl/vw-varinfo -v \
    --bfgs --loss_function=logistic \
    --passes=30 -k --cache \
    --holdout_period=5 \
    -f model_bfgs.vw --readable_model model_vw_bfgs.txt \
    training_data_vw.txt > model_varinfo_vw_bfgs.txt

# convert model parameters into simple vector format
cat model_varinfo_vw_bfgs.txt | grep feature | sort | \
    cut -f 2 | sed -n 's/  */ /gp' | cut -d ' ' -f 5 | \
    paste -s -d ',' - > vw_logit_coef.txt