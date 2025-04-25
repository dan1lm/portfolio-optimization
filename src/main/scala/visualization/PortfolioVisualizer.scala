package visualization

import breeze.plot._
import breeze.linalg._
import optimization.PortfolioOptimizer
import models.Portfolio
import java.io.File
import java.io.PrintWriter
/**
 * Visualizations
 */
class PortfolioVisualizer(optimizer: PortfolioOptimizer):
  /**
   * Plot the efficient frontier
   */
  def plotEfficientFrontier(
                             points: Int = 50,
                             outputPath: String = "output/charts/efficient_frontier.png"
                           ): Unit =
    // Generate efficient frontier points
    val portfolios = optimizer.generateEfficientFrontier(points)

    val risks = portfolios.map(_.risk).toArray
    val returns = portfolios.map(_.expectedReturn).toArray

    // Create figure and plot
    val f = Figure()
    val p = f.subplot(0)

    // Simple line plot for the efficient frontier
    p += plot(DenseVector(risks), DenseVector(returns))

    // Customize plot
    p.xlabel = "Risk (Standard Deviation)"
    p.ylabel = "Expected Return"
    p.title = "Efficient Frontier"

    val outputFile = new File(outputPath)
    outputFile.getParentFile.mkdirs()

    f.saveas(outputPath)
    println(s"Efficient frontier saved to $outputPath")

  /**
   * Create a simple text-based representation
   */
  def savePortfolioWeights(
                            portfolio: Portfolio,
                            outputPath: String = "output/portfolio_weights.txt"
                          ): Unit =
    val outputFile = new File(outputPath)
    outputFile.getParentFile.mkdirs()

    // Create a text file with the weights
    val sortedWeights = portfolio.weights.toSeq.sortBy(-_._2)
    val content = sortedWeights.map { case (asset, weight) =>
      f"$asset: ${weight * 100}%.2f%%"
    }.mkString("\n")

    val writer = new PrintWriter(outputFile)
    try {
      writer.println("Portfolio Weights")
      writer.println("=================")
      writer.println(content)
    } finally {
      writer.close()
    }

    println(s"Portfolio weights saved to $outputPath")