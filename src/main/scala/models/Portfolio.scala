package models

/**
 * Portfolio of assets with specific weights
 *
 * @param weights Map of asset symbols to their weights
 * @param expectedReturn The expected return
 * @param risk Standard deviation a.k.a risk
 */
case class Portfolio(
                      weights: Map[String, Double],
                      expectedReturn: Double,
                      risk: Double
                    ):
  /**
   * Sharpe ratio calculation 
   * @param riskFreeRate The risk-free rate
   * @return The Sharpe ratio
   */
  def sharpeRatio(riskFreeRate: Double = 0.0): Double =
    (expectedReturn - riskFreeRate) / risk

  /**
   * Checks if the portfolio weights sum to approximately 1.0
   */
  def isValid: Boolean =
    val sum = weights.values.sum
    (sum - 1.0).abs < 0.0001

  override def toString: String =
    val sortedWeights = weights.toSeq.sortBy(-_._2)
      .map { case (sym, w) => f"$sym: ${w * 100}%.2f%%" }
      .mkString(", ")

    f"Portfolio(Return: ${expectedReturn * 100}%.2f%%, Risk: ${risk * 100}%.2f%%, Weights: $sortedWeights)"