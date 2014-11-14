package co.ifwe.antelope

/**
 * Training data representation
 * @param outcome binary outcome variable
 * @param features feature vector
 */
class TrainingExample(val outcome: Boolean, val features: Iterable[(String,Double)])
