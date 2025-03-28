package optimization

import breeze.linalg.{DenseVector, DenseMatrix, sum}
import breeze.optimize.{DiffFunction, LBFGSB}
import models.{Asset, Portfolio}

import breeze.linalg.operators.{OpMulScalar, BinaryRegistry}
import breeze.math.Semiring

/**
 * Core portfolio optimization engine
 *
 * @param assets List of assets to consider
 * @param covarianceMatrix Covariance matrix of asset returns
 */
class PortfolioOptimizer(
                          val assets: Seq[Asset],
                          val covarianceMatrix: DenseMatrix[Double]
                        ):
  // Number of assets
  val n: Int = assets.length

  /**
   * Find the optimal portfolio for a given risk aversion level
   *
   * @param riskAversion Risk aversion parameter (higher values prefer lower risk)
   * @param constraints Optional set of constraints
   * @return Optimal portfolio allocation
   */
  def efficientPortfolio(
                          riskAversion: Double,
                          constraints: Option[ConstraintSet] = None
                        ): Portfolio =
    // Define objective function (mean-variance utility)
    def objectiveFunction(weights: DenseVector[Double]): Double =
      val expectedReturns = DenseVector(assets.map(_.expectedReturn).toArray)
      // Explicitly use dot product
      val portfolioReturn = (weights.t * expectedReturns)
      val portfolioVariance = (weights.t * covarianceMatrix * weights)

      // Mean-variance utility: return - (riskAversion/2) * variance
      portfolioReturn - (riskAversion / 2) * portfolioVariance

    // Define gradient for faster optimization
    def gradient(weights: DenseVector[Double]): DenseVector[Double] =
      val expectedReturns = DenseVector(assets.map(_.expectedReturn).toArray)
      val portfolioRiskGradient = covarianceMatrix * weights

      expectedReturns - (portfolioRiskGradient * DenseVector.fill(portfolioRiskGradient.length)(riskAversion))

    // Set up optimization problem
    val f = new DiffFunction[DenseVector[Double]]:
      def calculate(weights: DenseVector[Double]) =
        (-objectiveFunction(weights), -gradient(weights))

    // Initial weights (equal allocation)
    val initialWeights = DenseVector.fill(n){1.0 / n}

    // Apply constraints
    val optimizer = new LBFGSB(
      DenseVector.fill(n)(0.0),  // Lower bounds (no short selling)
      DenseVector.fill(n)(1.0),  // Upper bounds
      maxIter = 100,
      m = 5
    )

    // Solve the optimization problem
    val optimalWeights = optimizer.minimize(f, initialWeights)

    // Weights normalization
    val weightSum = sum(optimalWeights)
    val normalizedWeights = DenseVector(optimalWeights.toArray.map(_ / weightSum))

    // Calculate portfolio metrics
    val expectedReturns = DenseVector(assets.map(_.expectedReturn).toArray)
    val expectedReturn = (normalizedWeights.t * expectedReturns).toDouble
    val risk = math.sqrt((normalizedWeights.t * covarianceMatrix * normalizedWeights).toDouble)

    // Return the portfolio
    Portfolio.fromDenseVector(normalizedWeights, assets, expectedReturn, risk)

  /**
   * Generate the entire efficient frontier
   *
   * @param points Number of points to calculate
   * @return Sequence of portfolios along the efficient frontier
   */
  def generateEfficientFrontier(points: Int): Seq[Portfolio] =
    // Find min and max risk aversion for a reasonable frontier
    val minRiskAversion = 0.1
    val maxRiskAversion = 50.0

    // Generate risk aversion parameters on a log scale
    val riskAversionLevels = Vector.tabulate(points) { i =>
      val t = i.toDouble / (points - 1)
      math.exp(math.log(minRiskAversion) * (1 - t) + math.log(maxRiskAversion) * t)
    }

    // Calculate efficient portfolios for each risk aversion
    riskAversionLevels.map(efficientPortfolio(_))

  /**
   * Find the minimum variance portfolio
   *
   * @return The minimum variance portfolio
   */
  def minimumVariancePortfolio(): Portfolio =
  // Use a very high risk aversion to approximate minimum variance
    efficientPortfolio(1000.0)

  /**
   * Find the maximum Sharpe ratio portfolio
   *
   * @param riskFreeRate The risk-free rate
   * @return The portfolio with maximum Sharpe ratio
   */
  def maximumSharpeRatioPortfolio(riskFreeRate: Double = 0.0): Portfolio =
    val portfolios = generateEfficientFrontier(100)

    // Calculate Sharpe ratios
    val sharpeRatios = portfolios.map(p =>
      (p.expectedReturn - riskFreeRate) / p.risk
    )

    // Find portfolio with maximum Sharpe ratio
    val maxIndex = sharpeRatios.zipWithIndex.maxBy(_._1)._2
    portfolios(maxIndex)