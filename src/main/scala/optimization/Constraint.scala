package optimization

import breeze.linalg.DenseVector

/**
 * Constraint on portfolio optimization
 */
trait Constraint:
  /**
   * Weight vector satisfies the constraint?
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

/**
 * Constraint that enforces a max weight for each asset
 *
 * @param maxWeight The maximum weight allowed for any asset
 */
case class MaxWeightConstraint(maxWeight: Double) extends Constraint:
  override def isSatisfied(weights: DenseVector[Double]): Boolean =
    weights.forall(_ <= maxWeight)

/**
 * Constraint that enforces the sum of weights equals 1.0
 */
case class SumToOneConstraint() extends Constraint:
  override def isSatisfied(weights: DenseVector[Double]): Boolean =
    val sum = weights.toArray.sum
    (sum - 1.0).abs < 0.0001

/**
 * Collection of multiple constraints
 *
 * @param constraints List of constraints
 */
case class ConstraintSet(constraints: List[Constraint]):
  /**
   * Checks if all constraints are satisfied
   *
   * @param weights The weights to check
   * @return true if all constraints are satisfied
   */
  def areAllSatisfied(weights: DenseVector[Double]): Boolean =
    constraints.forall(_.isSatisfied(weights))