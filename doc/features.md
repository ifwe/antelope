# Features #

Models are made of features, features are made from data. Antelope aims to make
designing and deploying new features easy. Building on top of a set of simple
state primitives, we demonstrate a flexible series of feature implementations
of varying degrees of complexity.

## Overall popularity ##

Overall popularity is one of the simplest model building blocks. We see that 
Antelope allows a commensurately simple implementation. The definition

    val ct = s.counter(ide)
    
creates a counter data structure that records the number of occurrences of a
certain identifier, that referenced by the id extractor *ide*.

Scoring is done with simple normalization

    id: Long => ct(id) div ct()

reflecting the fraction of all events occurring with the specified id.

Source: [OverallPopularityFeature](../antelope/src/main/scala/co/ifwe/antelope/feature/OverallPopularityFeature.scala)

## Recent popularity ##

Online systems that need to adapt to changing circumstances can benefit from a bit of
amnesia. A simple and clean solution is a counter that decays exponentially. Antelope's
State framework allows this implementation

    val ct = s.decayingCounter(ide, Math.log(2) / halfLife)
    
We can store and update decaying counters efficiently by simply storing a
(count,timestamp) pair and updating accordingly as new events come in.

When scoring using the decaying counter we need to provide the current time as
represented in the query context

    id: Long => ct(ctx.t, id) div ct(ctx.t)

Source: [RecentPopularityFeature](../antelope/src/main/scala/co/ifwe/antelope/feature/RecentPopularityFeature.scala)

## Term popularity ##

Direct measure of the frequency of the document given the term. In our implementation
we make use of a hierarchical counter state variable that allows us to get the count
at each level.

Source: [TermPopularityFeature](../antelope/src/main/scala/co/ifwe/antelope/feature/TermPopularityFeature.scala)

## Naive Bayes popularity ##

One could argue that this is more principled than the related term popularity
implementation, yet Naive Bayes is usually applied in situations where its
theoretical justifications do not apply.

Source: [NaiveBayesPopularityFeature](../antelope/src/main/scala/co/ifwe/antelope/feature/NaiveBayesPopularityFeature.scala)

## TF-IDF ##

Term frequency-inverse document frequency as is standard in information retrieval.

Source: [TfIdfFeature](../antelope/src/main/scala/co/ifwe/antelope/feature/TfIdfFeature.scala)
