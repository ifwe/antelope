<p align="center">
  <img src="images/antelope-logo-1.png?raw=true" alt="Antelope Realtime Events logo"/>
</p>

# Antelope Realtime Events #

The *Antelope Realtime Events* project aims to make iterative and agile machine learning
a practical reality in realtime applications.  It derives from a proprietary framework used 
by [if(we)](http://www.ifwe.co/) to develop recommendation engines.  By unifying the software
that data scientists use to extract data for training a machine learning model with the software 
used to run that model in production, a much faster progress cycle becomes possible.

## What's new in v0.2 ##

This remains a demonstration release designed to expose the API and programming style.

Updates in v0.2 include:
* Spell correction in product search demo - we found that the most apparent cause of 
  recommendation errors in the demo was spelling mistakes.
* [Web demo](doc/demo-web.md) - front-end for the product search demo so you can
  interact with the product directly
* Cached events - this performance improvement saves events to a cache using binary
  serialization. Eliminating input parsing and merging overheads speeds up development
  cycles and makes practical larger data sets.

We encourage you to explore the framework and to run the demos, but please understand
that production needs have not been yet provided for. Among other things, the in-memory
state representation uses JVM objects whereas our production implementation uses
packed arrays, an optimization that allows for larger models and practical checkpoints.

We're releasing Antelope now because we want your feedback!  Please join the
[Google Group](https://groups.google.com/forum/#!forum/antelope-rte) and please
don't hesitate to post your questions, suggestions, or ideas.

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

### What Antelope doesn't do
* Accept data that is not structured in form of events - that's our requirement!
* Train machine learning models - there are plenty of implementations for those
* Collect or store event logs - integrate with Kafka, Hadoop, or related technologies
* Store model state - we build on databases and stream processing frameworks

## Running the Kaggle Demo

Perhaps the best way to understand what Antelope does is by giving it a quick try.
This project contains examples that work with the 
[ACM SF Chapter's Best Buy data set](https://www.kaggle.com/c/acm-sf-chapter-hackathon-small).

Please see the [Getting Started with the Demo](doc/demo.md) documentation and
follow the instructions to download the data and to go through a simple machine
learning exercise. To see this demonstration embedded in a working web application
check out instructions at [Running the Web Demo](doc/demo-web.md).

## Frequently Asked Questions

### Why doesn't if(we) release the machine learning framework they are actively using? 

Antelope is derived from a proprietary in-house framework that's tied to a legacy
custom logging infrastructure and that lacks clean separation from some of our
domain-specific needs.

We're building Antelope on broadly-used open-source foundations.  Today, if(we) is
migrating the logging infrastructure to [Kafka](https://kafka.apache.org/) and we
have introduced stream processing frameworks such as [Storm](https://storm.apache.org/)
and [Spark](https://spark.apache.org/).  Antelope is designed to work well with these
technologies.

Also, there are so many things we would do differently today, and a rewrite gives
us that opportunity.

### When will I be able to use Antelope in production?

That depends on how much support we get from people like you.  Please join the
[Google Group](https://groups.google.com/forum/#!forum/antelope-rte) and let's talk about
how you can help.
  
### Will Antelope work with my favorite machine learning algorithm?

Antelope is largely agnostic of choice of machine learning technique as it creates the feature
vectors that can serve as inputs to arbitrary supervised learning implementations.  The
techniques we deploy most commonly at if(we) are logistic regression and random forests.  We
also sometimes implement purely heuristic decision criteria.

### Will Antelope work with my programming favorite language, stream processing framework, database, or other technology?

The Antelope approach is framework-agnostic when it comes to sourcing, processing, or storing
data.  We are designing for pluggable implementations, so if we're not compatible with your
favorite technologies, then please talk to us about helping make that happen.

Antelope, at it's core, is a Scala-based implementation.  Early on, we considered developing a
custom language for expressing features.  It's an idea we come back to from time to time, but
each time we've revisited it we've concluded that it makes more sense to build a library that
works in concert with a full-featured and powerful programming environment.

### What if I don't program in Scala?

Scala programmers will find working with Antelope very comfortable.  If you're not a Scala
developer, rest assured that you can use Antelope effectively with only very limited
Scala knowledge.  Once you learn the Antelope primitives and the basics of Scala syntax
you should be well on your way.

-----

README.md: Copyright 2015 Ifwe Inc.

README.md is licensed under a Creative Commons Attribution-ShareAlike 4.0
International License.

You should have received a copy of the license along with this work. If not,
see <http://creativecommons.org/licenses/by-sa/4.0/>.