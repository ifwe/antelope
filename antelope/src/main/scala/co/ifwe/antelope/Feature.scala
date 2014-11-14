package co.ifwe.antelope

/**
 * Features are what models are made of.  This is the base trait for
 * all Features.
 *
 * @tparam T context type in which we will be scoring this feature
 */
trait Feature[T <: ScoringContext] {
   def score(implicit ctx: T): Long => Double
}
