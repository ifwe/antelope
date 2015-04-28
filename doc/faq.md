---
layout: doc-page
title: Frequently Asked Questions
---

## Will Antelope work with my programming favorite language, stream processing framework, database, or other technology?

The Antelope approach is framework-agnostic when it comes to sourcing, processing, or storing
data.  We are designing for pluggable implementations, so if we're not compatible with your
favorite technologies, then please talk to us about helping make that happen.

Antelope, at it's core, is a Scala-based implementation.  Early on, we considered developing a
custom language for expressing features.  It's an idea we come back to from time to time, but
each time we've revisited it we've concluded that it makes more sense to build a library that
works in concert with a full-featured and powerful programming environment.

## Will Antelope work with my favorite machine learning algorithm?

Antelope is largely agnostic of choice of machine learning technique as it creates the feature
vectors that can serve as inputs to arbitrary supervised learning implementations.  The
techniques we deploy most commonly at if(we) are logistic regression and random forests.  We
also sometimes implement purely heuristic decision criteria.

## Why doesn't if(we) release the original machine learning framework they developed for their products?

Antelope is derived from a proprietary in-house framework that's tied to a legacy
application infrastructure and that lacks clean separation from our
domain-specific needs.

We're building Antelope on broadly-used open-source foundations.  Today, if(we) is
migrating the logging infrastructure to [Kafka](https://kafka.apache.org/) and we
have introduced stream processing frameworks such as [Storm](https://storm.apache.org/)
and [Spark](https://spark.apache.org/). Antelope is conceived to work well with these technologies.

Also, there are so many things we would do differently today, and a rewrite gives
us that opportunity.

## When will I be able to use Antelope in production?

That depends on how much support we get from people like you.  Please join the
[Google Group]({{ site.googlegroupurl }}) and let's talk about
how you can help.

## What if I don't program in Scala?

Scala programmers will find working with Antelope very comfortable.  If you're not a Scala
developer, rest assured that you can use Antelope effectively with only very limited
Scala knowledge.  Once you learn the Antelope primitives and the basics of Scala syntax
you should be well on your way.

## Still have questions?

We welcome questions, suggestions, or comments. Please ask over in the [Google Group]({{ site.googlegroupurl }}).