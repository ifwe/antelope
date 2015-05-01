---
layout: doc-page
title: Feature Engineering
---

Models are made of features, features define transformations for data, they tell Antelope what to maintain as in-memory state, and they tell Antelope how in-memory state relates to parameters of the model.

Building on top of a set of simple state primitives, we demonstrate a flexible series of feature implementations of varying degrees of complexity. Antelope aims to provide an expressive DSL-like experience for shaping raw streams of input events into the signals directly useful to machine learning. In doing so, we are always guided by the principle that concepts that are easy to explain should also be easy to implement.

## A simple feature: Overall Popularity

A basic yet very useful feature is *Overall Popularity*, a measure of the all-time popularity of an item. Overall popularity proves to be a powerful feature in the Best Buy product search challenge.

Here is overall popularity as implemented in Antelope:

```scala
class OverallPopularityFeature[T <: ScoringContext](ide: IdExtractor)(implicit val s: State[T])
          extends Feature[T] {

  import co.ifwe.antelope.util._

  val ct = s.counter(ide)

  override def score(implicit ctx: T) = {
    id: Long => ct(id) div ct()
  }
}
```

Full source: [OverallPopularityFeature.scala]({{ "/antelope/src/main/scala/co/ifwe/antelope/feature/OverallPopularityFeature.scala" | prepend: site.githubsrc}} )

*OverallPopularityFeature* implements the interface of *Feature*, which requires just one method

```scala
trait Feature[T <: ScoringContext] {
   def score(implicit ctx: T): Long => Double
}
```

the method *score* takes as input a context of type *T*. In our product search example the context includes the search query, whereas in the dating recommendations example it contains the identifier of the user requesting the recommendation. *score* returns the scoring function for this feature, a mapping from candidate identifiers to numeric (Double) values.

For the overall popularity feature the *score* implementation is simple, returning the fraction of observations that match the provided *id*. To keep track of this *OverallPopularityFeature* maintains a counter, provided by the *State* object.

Let's look at the internals of *State* to see what a counter represents

```scala
trait Counter {
  def increment: PartialFunction[Event, Unit]
  def apply(): Long
}

trait Counter1[T1] extends Counter {
  def apply(k: T1): Long
  def toMap: Map[T1, Long]
}

trait Counter2[T1,T2] extends Counter {
  def apply(k: (T1,T2)): Long
  def apply(k: T1): Long
  def mapAt(k: T1): Map[T2, Long]
}
```

Antelope's state implements a "hierarchical counter" concept, supporting not just a single total count of events, but also providing a breakdown of counts according to key, or, if provided with a pair of keys, by either the first key alone or the two keys in combination.

Antelope uses Scala's [partial functions](http://www.scala-lang.org/api/current/index.html#scala) to bind state variables to incoming data. The domain of a partial function does not necessarily include all values of the domain's type, allowing us to attach a feature's state updates only to certain events.

The *IdExtractor* parameter of the *OverallPopularityFeature* class allows us to pass in a partial function describing which events types to count; leveraging Scala's composability allows the flexibility to re-use feature definition in different models.

In the Best Buy  product search model, the id extracted from the event provides the key to a map of individual, per-key counters.

## Additional examples from the Best Buy product search model ##

### Recent popularity ###

Online systems that need to adapt to changing circumstances can benefit from a bit of
amnesia. A simple and clean solution is a counter that decays exponentially. Antelope's
State framework allows this implementation

```scala
val ct = s.decayingCounter(ide, Math.log(2) / halfLife)
```

We can store and update decaying counters efficiently by simply storing a
(count,timestamp) pair and updating accordingly as new events come in.

When scoring using the decaying counter we need to provide the current time as
represented in the query context

```scala
id: Long => ct(ctx.t, id) div ct(ctx.t)
```

Full source: [RecentPopularityFeature.scala]({{ "/antelope/src/main/scala/co/ifwe/antelope/feature/RecentPopularityFeature.scala" | prepend: site.githubsrc}} )

### Term popularity ###

Direct measure of the frequency of the document given the term. In our implementation we make use of a hierarchical counter state variable that allows us to get the count at each level.

```scala
val ct = s.counter(t.termsFromUpdate, ide)
```

The scoring function first extracts terms from the query, then returns a term-by-term product of the fraction of occurrences of the term that occur in the scored document

```
  val queryTerms = t.termsFromQueryContext(ctx)
  id: Long => queryTerms.map(term => {
    ct(term, id) div ct(term)
  }).product
```

Full source: [TermPopularityFeature.scala]({{ "/antelope/src/main/scala/co/ifwe/antelope/feature/TermPopularityFeature.scala" | prepend: site.githubsrc}} )


### Naive Bayes popularity ###

One could assert that Naive Bayes rests on better foundations that the *TermPopularityFeature*, that it is similar but more principled. Yet Naive Bayes is usually applied in situations where its theoretical justifications do not apply. Our advice to the user: see what works best.

Here's the scoring function used

```scala
val queryTerms = t.termsFromQueryContext(ctx)
id: Long => queryTerms.map(term => {
  ct(id, term) div ct(id)
}).product
```

Full source: [NaiveBayesPopularityFeature.scala]({{ "/antelope/src/main/scala/co/ifwe/antelope/feature/NaiveBayesPopularityFeature.scala" | prepend: site.githubsrc}} )


### TF-IDF ###

Term frequency-inverse document frequency is a standard information retrieval measure. It is quite simple to implement using Antelope's state primitives. Here *ide* represents the document identifier while *t.termsFromUpdate* represents a series of tokes from the update of indexed text

```scala
val terms = counter(ide,t.termsFromUpdate)
val docs = set(ide)
val docsWithTerm = set(t.termsFromUpdate,ide)
```

The scoring function is defined as follows

```scala
val queryTerms: Iterable[String] = t.termsFromQueryContext(ctx)    // get the query terms from the query

val n = docs.size()    // total number of documents

// scoring function returned
id: Long => (queryTerms map { t: String =>
  val tf = terms(id, t)                               // number of times the term occurs within the document
  val df = docsWithTerm.size(t)                       // number of documents that contain the term
  Math.sqrt(tf) * sq(1D + Math.log(n / (1D + df)))    // TF-IDF as implemented in Lucene
}).sum
```

Full source: [TfIdfFeature.scala]({{ "/antelope/src/main/scala/co/ifwe/antelope/feature/TfIdfFeature.scala" | prepend: site.githubsrc}} )

## Additional examples from the dating simulation model ##

In the DatingModel we use an inline representation whereby features are defined in the model where they are used. We explore these features in detail here, whereas the full source is available in [DatingModel.scala]({{"demo-dating-simulation/src/main/scala/co/ifwe/antelope/datingdemo/model/DatingModel.scala" | prepend: site.githubsrc}}).



### Binary indicators ###

Are users in the same region?

```scala
feature(new Feature[DatingScoringContext]() {
  val userRegion = map(userId, userRegionUpdate)
  override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
    val srcRegion = userRegion(ctx.id)
    id: Long => if (srcRegion == userRegion(id)) 1 else 0
  }
})
```

Do regions share a border?

```scala
feature(new Feature[DatingScoringContext]() {
  val userRegion = map(userId, userRegionUpdate)
  override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
    val srcRegion = userRegion(ctx.id)
    id: Long => if (Region.borders(srcRegion, userRegion(id))) 1 else 0
  }
})
```

### Most recent value, with transformation relative to current time ###

When was the user most recently active?

```scala
feature(new Feature[DatingScoringContext]() {
  val lastActivity = map(userId, userActivityTime)
  override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
    id: Long => {
      lastActivity.get(id) match {
        case Some(ts) => ctx.t - ts
        case None => 0D
      }
    }
  }
})
```

we need to pair the above feature with another that merely indicates whether we have an activity history for this user

```scala
feature(new Feature[DatingScoringContext]() {
  val lastActivity = map(userId, userActivityTime)
  override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
    id: Long => {
      lastActivity.get(id) match {
        case Some(_) => 1D
        case None => 0D
      }
    }
  }
})
```

### Arithmetic features ###

The age difference between two users is one of the most straightforward features for dating recommendations

```scala
feature(new Feature[DatingScoringContext]() {
  val userAge = map(userId, userAgeUpdate)
  override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
    val srcAge: Int = userAge(ctx.id)
    id: Long => {
      val tgtAge = userAge(id)
      math.abs(srcAge - tgtAge)
    }
  }
})
```

we also introduce a feature for the square of the age difference

```scala
feature(new Feature[DatingScoringContext]() {
  val userAge = map(userId, userAgeUpdate)
  override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
    val srcAge = userAge(ctx.id)
    id: Long => {
      val tgtAge = userAge(id)
      (srcAge - tgtAge) * (srcAge - tgtAge)
    }
  }
})
```

### Ratio features ###

For users with sufficient voting history we use the historical click rates to estimate how likely the user is to vote positively. Recommending less-selective users can increase match rates, though there are limits to how much inbound interest a user can respond to.

```scala
val thresholdVotes = 5
feature(new Feature[DatingScoringContext]() {
  class VoteRatio extends Updatable[Boolean] {
    var ct = 0
    var yesCt = 0
    override def update(x: Boolean): Unit = {
      ct += 1
      if (x) {
        yesCt += 1
      }
    }
  }
  val voteRatios = mapUpdatable(userId, userVote, new VoteRatio)
  override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
    id: Long => {
      voteRatios.get(id) match {
        case Some(vr: VoteRatio) => if (vr.ct >= thresholdVotes) {
            vr.yesCt.toDouble / vr.ct.toDouble
          } else {
            0D
          }
        case None => 0D
      }
    }
  }
})
```

for features like the vote ratio, which are defined only for some users, we need to provide a complementary indicator feature that indicates availability of the ratio

```scala
feature(new Feature[DatingScoringContext]() {
  val voteCounts = counter(userIdVoted)
  override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
    id: Long => if (voteCounts(id) >= thresholdVotes) 1D else 0D
  }
})
```