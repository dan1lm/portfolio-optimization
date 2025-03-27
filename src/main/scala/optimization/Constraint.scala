package optimization

import breeze.linalg.DenseVector

/**
 * Constraint on portfolio optimization
 */
trait Constraint:
  /**
   * Determines if the weight vector satisfies the constraint
   *
   * @param weights The weights to check
   * @return true if the constraint is satisfied
   */
  def isSatisfied(weights: DenseVector[Double]): Boolean

/**
 * Constraint that enforces a minimum weight for each asset
 *
 * @param minWeight The minimum weight allowed for any asset
 */
case class MinWeightConstraint(minWeight: Double) extends Constraint:
  override def isSatisfied(weights: DenseVector[Double]): Boolean =
    weights.forall(_ >= minWeight)

