package visualization

import breeze.plot._
import breeze.linalg._
import optimization.PortfolioOptimizer
import models.Portfolio
import scala.collection.mutable.ArrayBuffer
import java.io.File

/**
 * Visualizations for portfolio optimization results
 *
 * @param optimizer The portfolio optimizer
 */
class PortfolioVisualizer(optimizer: PortfolioOptimizer):
  /**
   * Plot the efficient frontier
   *
   * @param points Number of points to calculate
   * @param outputPath Path to save the image
   * @return The Figure object
   */
  def plotEfficientFrontier(points: Int, outputPath: String = "efficient_frontier.png"): Figure =
    // Generate efficient frontier points
    val portfolios = optimizer.generateEfficientFrontier(points)

    val risks = portfolios.map(_.risk).toArray
    val returns = portfolios.map(_.expectedReturn).toArray

    // Generate random portfolios for comparison
    val randomPortfolios = generateRandomPortfolios(1000)
    val randomRisks = randomPortfolios.map(_.risk).toArray
    val randomReturns = randomPortfolios.map(_.expectedReturn).toArray

    val f = Figure()
    val p = f.subplot(0)

    p += scatter(DenseVector(randomRisks), DenseVector(randomReturns), _ => 0.5, _ => java.awt.Color.GRAY)
    p += plot(DenseVector(risks), DenseVector(returns), name = "Efficient Frontier")

    p.xlabel = "Risk (Standard Deviation)"
    p.ylabel = "Expected Return"
    p.title = "Efficient Frontier"
    p.legend = true

    val outputFile = new File(outputPath)
    outputFile.getParentFile.mkdirs()

    f.saveas(outputPath)
    f

  /**
   * Generate random portfolios for visualization
   *
   * @param count Number of random portfolios to generate
   * @return Sequence of random portfolios
   */
  def generateRandomPortfolios(count: Int): Seq[Portfolio] =
    val n = optimizer.n
    val results = ArrayBuffer[Portfolio]()

    for _ <- 0 until count do
      // Generate random weights
      val rawWeights = DenseVector.rand(n)
      // Fix: Avoid ambiguous division operator
      val sumWeights = sum(rawWeights)
      val weights = DenseVector(rawWeights.toArray.map(_ / sumWeights))

      val expectedReturns = DenseVector(optimizer.assets.map(_.expectedReturn).toArray)
      val expectedReturn = (weights.t * expectedReturns).toDouble
      val risk = math.sqrt((weights.t * optimizer.covarianceMatrix * weights).toDouble)

      val portfolio = Portfolio.fromDenseVector(weights, optimizer.assets, expectedReturn, risk)
      results += portfolio

    results.toSeq

  /**
   * Plot portfolio weights as a bar chart
   *
   * @param portfolio The portfolio to visualize
   * @param outputPath Path to save the image
   * @return The Figure object
   */
  def plotPortfolioWeights(portfolio: Portfolio, outputPath: String = "portfolio_weights.png"): Figure =
    val sortedWeights = portfolio.weights.toSeq.sortBy(-_._2)
    val assetNames = sortedWeights.map(_._1).toArray
    val weights = sortedWeights.map(_._2).toArray

    val f = Figure()
    val p = f.subplot(0)

    p.ylim = (0.0, weights.head * 1.2)  // Set y-axis limit with some headroom

    for (i <- assetNames.indices) {
      val x = DenseVector(Array(i.toDouble, i + 0.8))
      val y = DenseVector(Array(0.0, weights(i)))
      p += plot(x, y, name = assetNames(i))
    }

    p.xlabel = "Assets"
    p.ylabel = "Weight"
    p.title = "Portfolio Weights"

    val outputFile = new File(outputPath)
    outputFile.getParentFile.mkdirs()

    f.saveas(outputPath)
    f

  /**
   * Plot the efficient frontier with Sharpe ratio
   *
   * @param riskFreeRate The risk-free rate
   * @param outputPath Path to save the image
   * @return The Figure object
   */
  def plotEfficientFrontierWithSharpeRatio(
                                            riskFreeRate: Double = 0.02,
                                            outputPath: String = "efficient_frontier_sharpe.png"
                                          ): Figure =
    val portfolios = optimizer.generateEfficientFrontier(100)

    val risks = portfolios.map(_.risk).toArray
    val returns = portfolios.map(_.expectedReturn).toArray

    // Calculate Sharpe ratios
    val sharpeRatios = portfolios.map(p => (p.expectedReturn - riskFreeRate) / p.risk)

    // Find max Sharpe ratio portfolio
    val maxSharpeIdx = sharpeRatios.zipWithIndex.maxBy(_._1)._2
    val maxSharpePortfolio = portfolios(maxSharpeIdx)

    val f = Figure()
    val p = f.subplot(0)

    p += plot(DenseVector(risks), DenseVector(returns), name = "Efficient Frontier")

    // Highlight max Sharpe ratio portfolio
    p += plot(DenseVector(Array(maxSharpePortfolio.risk)),
      DenseVector(Array(maxSharpePortfolio.expectedReturn)),
      name = "Max Sharpe Ratio")

    // Add capital market line
    val x = linspace(0.0, risks.max * 1.2, 100)
    val y = DenseVector.tabulate(x.length) { i =>
      riskFreeRate + (maxSharpePortfolio.expectedReturn - riskFreeRate) /
        maxSharpePortfolio.risk * x(i)
    }
    p += plot(x, y, name = "Capital Market Line")

    p.xlabel = "Risk (Standard Deviation)"
    p.ylabel = "Expected Return"
    p.title = "Efficient Frontier with Maximum Sharpe Ratio"
    p.legend = true

    // Create output directory if it doesn't exist
    val outputFile = new File(outputPath)
    outputFile.getParentFile.mkdirs()

    f.saveas(outputPath)
    f