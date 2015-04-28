---
layout: page
---

<p align="center">
  <img src="images/antelope_logo_400.png" alt="Antelope Realtime Events logo"/>
</p>

The *Antelope Realtime Events* project aims to make iterative and agile machine learning
a practical reality, especially in systems that must respond immediately to new
circumstances.  It derives from a proprietary framework used by
[if(we)](http://www.ifwe.co/) to develop recommendation engines for our social products.
By unifying the software that data scientists use to extract data for training a
machine learning model with the software used to run that model in production, a much
faster progress cycle becomes possible.

We're releasing Antelope now because we want your feedback!  Please join the
[Google Group](https://groups.google.com/forum/#!forum/antelope-rte) and please
don't hesitate to post your questions, suggestions, or ideas.

## Getting Started ##

Perhaps the best way to understand Antelope is to give it a quick try.
We have provided two main examples, one that works with [Kaggle](https://www.kaggle.com/) competition data, the [ACM SF Chapter's Best Buy data set](https://www.kaggle.com/c/acm-sf-chapter-hackathon-small), and another that simulates dating site user behavior, attempting to replicate some of what we observe on our products.

1. First see the [Getting Started with the Best Buy Demo](doc/demo-best-buy.html) instructions to download the Kaggle data and to run a simple machine learning exercise.
2. Check out [Running the Best Buy Web Demo](doc/demo-best-buy-web.html) to see the predictive engine embedded in a working web application.
3. Try the [Dating Simulation](doc/demo-dating-simulation.html) to further explore the richness of Antelope's feature engineering capabilities.

The Antelope [Documentation](doc/) remains a work in progress but provides additional perspectives. Be sure to check out the [Feature Engineering](doc/features.html) section to understand the core elements of working with Antelope. You may also wish to review the [Frequently Asked Questions](doc/faq.html).

## What's new in v0.2 ##

This remains a demonstration release designed to expose the API and programming style, but things have moved along quite a bit since the first release (v0.1, November 2014).

Highlights in v0.2 include:

* Web front-end for Best Buy Product Search demo -- you can now interact directly with the Antelope application in a lightweight but realistic implementation that includes predictive input completion.
* Spell correction in product search demo -- we found that the most apparent cause of recommendation errors in the Best Buy Product Search demo was spelling mistakes, so we implemented simple spelling correction to improve predictive accuracy. The web front-end supports spelling correction as well.
 * Expanded feature support -- we introduce a greater variety of state primitives, and feature implementation examples, describing them in the guide to [feature engineering](doc/features.html).
* Dating simulation -- we introduce a second example to complement the product search with dating recommendations. Rather than sharing sensitive user data we instead construct a simulation based on our experience with [if(we)](http://www.ifwe.co/) products. In this context we showcase a broad variety of model features similar to those that we have used internally.
* Cached event history -- this performance improvement saves events to a cache in a binary-serialized format. Eliminating the overheads of input text parsing and merging speeds up development cycles and makes practical working with larger data sets.

## Status ##

We encourage you to explore the framework and to run the demos, but please understand
that production needs have not been yet provided for. Among other things, the in-memory
state representation uses JVM objects whereas our production implementation uses
packed arrays, an optimization that allows for larger models and practical checkpoints.

## Applicability

### What Antelope does (or will do... eventually)
* Make it easy to derive useful features from event history
* Allow models to reflect new events within seconds, or even in under a second
* Produce top recommendations from millions of candidates in a fraction of a second
* Enable rapid iterative cycle around feature ideas, model training, and production deployment
* Allow one definition of model features to be used in training, offline validation, and production
* Provide access to powerful programming abstractions by integrating with Scala language
* Accept a variety of machine learning techniques
* Works with scalable stream processing frameworks (not yet, but that's our vision)

### What Antelope doesn't do (never... almost surely)
* Accept data that is not structured in form of events -- that's our requirement!
* Train machine learning models -- there are plenty of implementations for those
* Collect or store event logs -- integrate with Kafka, Hadoop, or related technologies
* Store model state -- we build on databases and stream processing frameworks



