package optimization

import models.Asset
import breeze.linalg.{DenseMatrix, DenseVector}

@main
def testPortfolioOptimizer(): Unit =
  println("Testing Portfolio Optimizer")
  println("==========================")
  
  val assets = Seq(
    Asset("AAPL", 0.15, "Technology"),
    Asset("MSFT", 0.12, "Technology"),
    Asset("GOOGL", 0.10, "Technology"),
    Asset("AMZN", 0.14, "Consumer"),
    Asset("JNJ", 0.08, "Healthcare")
  )
  
  val covData = Array(
    0.04, 0.02, 0.01, 0.02, 0.01,
    0.02, 0.05, 0.02, 0.01, 0.01,
    0.01, 0.02, 0.03, 0.01, 0.00,
    0.02, 0.01, 0.01, 0.06, 0.01,
    0.01, 0.01, 0.00, 0.01, 0.02
  )
  val covMatrix = new DenseMatrix(5, 5, covData)
  
  val optimizer = new PortfolioOptimizer(assets, covMatrix)

  // Test efficient frontier generation
  println("\nGenerating efficient frontier...")
  val frontier = optimizer.generateEfficientFrontier(10)

  println("\nEfficient Frontier:")
  println("-------------------")
  println("Risk\tReturn\tSharpe")
  frontier.foreach { p =>
    println(f"${p.risk}%.4f\t${p.expectedReturn}%.4f\t${p.sharpeRatio(0.02)}%.4f")
  }

  // Test optimal portfolio for a given risk aversion
  val riskAversion = 3.0
  println(s"\nOptimal Portfolio (Risk Aversion = $riskAversion):")
  val optimalPortfolio = optimizer.efficientPortfolio(riskAversion)
  printPortfolio(optimalPortfolio)

  // Test minimum variance portfolio
  println("\nMinimum Variance Portfolio:")
  val minVarPortfolio = optimizer.minimumVariancePortfolio()
  printPortfolio(minVarPortfolio)

  // Test maximum Sharpe ratio portfolio
  println("\nMaximum Sharpe Ratio Portfolio (Rf = 0.02):")
  val maxSharpePortfolio = optimizer.maximumSharpeRatioPortfolio(0.02)
  printPortfolio(maxSharpePortfolio)

def printPortfolio(portfolio: models.Portfolio): Unit =
  println(f"Expected Return: ${portfolio.expectedReturn * 100}%.2f%%")
  println(f"Risk: ${portfolio.risk * 100}%.2f%%")
  println(f"Sharpe Ratio (Rf=0.02): ${portfolio.sharpeRatio(0.02)}%.4f")
  println("Weights:")
  portfolio.weights.toSeq.sortBy(-_._2).foreach { case (asset, weight) =>
    println(f"$asset: ${weight * 100}%.2f%%")
  }