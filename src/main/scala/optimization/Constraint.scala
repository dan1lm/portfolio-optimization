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
 * Constraint that enforces sector allocation limits
 *
 * @param assetSectors Map of asset indices to their sectors
 * @param sectorLimits Map of sectors to their (min, max) allocation limits
 */
case class SectorConstraint(
                             assetSectors: Map[Int, String],
                             sectorLimits: Map[String, (Double, Double)]
                           ) extends Constraint:
  override def isSatisfied(weights: DenseVector[Double]): Boolean =

    val sectorWeights = scala.collection.mutable.Map[String, Double]().withDefaultValue(0.0)

    for (i <- 0 until weights.length) {
      val sector = assetSectors.getOrElse(i, "Unknown")
      sectorWeights(sector) += weights(i)
    }


    sectorLimits.forall { case (sector, (min, max)) =>
      val weight = sectorWeights(sector)
      weight >= min && weight <= max
    }

/**
 * Container
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