package models

import models.Asset
import breeze.linalg.{DenseVector, sum}

/**
 * Represents a portfolio allocation with weights for different assets
 *
 * @param weights Map of asset symbols to their weights in the portfolio
 * @param expectedReturn The expected return of the portfolio
 * @param risk The risk (standard deviation) of the portfolio
 */
case class Portfolio(
                      weights: Map[String, Double],
                      expectedReturn: Double,
                      risk: Double
                    ):
  /**
   * Sharpe ratio
   *
   * @return The Sharpe ratio
   */
  def sharpeRatio: Double = expectedReturn / risk

  /**
   * Checks if the portfolio weights sum to approximately 1.0
   *
   * @return true if the weights sum to approximately 1.0 (within a small epsilon)
   */
  def isValid: Boolean =
    val sum = weights.values.sum
    (sum - 1.0).abs < 0.0001

/**
 * Companion object for Portfolio
 */
object Portfolio:
  /**
   * Creates a Portfolio from a vector of weights and corresponding assets
   *
   * @param weightVector Vector of weights
   * @param assets Corresponding assets
   * @param expectedReturn Portfolio expected return
   * @param risk Portfolio risk
   * @return A new Portfolio instance
   */
  def fromDenseVector(
                       weightVector: DenseVector[Double],
                       assets: Seq[Asset],
                       expectedReturn: Double,
                       risk: Double
                     ): Portfolio =
    val weights = assets.map(_.symbol).zip(weightVector.toArray).toMap
    Portfolio(weights, expectedReturn, risk)