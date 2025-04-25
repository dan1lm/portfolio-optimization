import models.{Asset, Portfolio}
import optimization.PortfolioOptimizer
import data.MarketDataFetcher
import input.UserInputHandler
import java.io.File
import visualization.PortfolioVisualizer
@main
def portfolioApp(): Unit =
  println("Portfolio Optimization Tool")
  println("===========================")

  // Step 1: Get user input for assets
  val tickers = UserInputHandler.getAssetList()
  if tickers.isEmpty then
    println("No assets specified. Exiting.")
    return

  // Get risk aversion parameter
  val riskAversion = UserInputHandler.getRiskAversion()

  println(s"Analyzing portfolio with the following assets: ${tickers.mkString(", ")}")
  println(s"Using risk aversion parameter: $riskAversion")

  // Step 2: Fetch real-time market data
  println("Enter your Alpha Vantage API key:")
  val apiKey = scala.io.StdIn.readLine()

  val dataFetcher = new MarketDataFetcher(apiKey)

  try
    println("Fetching market data...")
    val (assets, covMatrix) = dataFetcher.fetchMarketData(tickers)

    println(s"Successfully retrieved data for ${assets.length} assets")

    // Step 3: Create the portfolio optimizer
    val optimizer = new PortfolioOptimizer(assets, covMatrix)

    // Generate the efficient frontier
    val efficientFrontier = optimizer.generateEfficientFrontier(20)
    println("\nEfficient Frontier:")
    println("-------------------")
    println("Risk\tReturn\tSharpe")
    efficientFrontier.foreach { p =>
      println(f"${p.risk * 100}%.2f%%\t${p.expectedReturn * 100}%.2f%%\t${p.sharpeRatio(0.02)}%.4f")
    }

    // Find optimal portfolio based on user's risk preference
    val optimalPortfolio = optimizer.efficientPortfolio(riskAversion)

    println("\nOptimal Portfolio:")
    println("------------------")
    println(f"Expected Return: ${optimalPortfolio.expectedReturn * 100}%.2f%%")
    println(f"Risk: ${optimalPortfolio.risk * 100}%.2f%%")
    println(f"Sharpe Ratio (Rf=0.02): ${optimalPortfolio.sharpeRatio(0.02)}%.4f")
    println("\nWeights:")
    optimalPortfolio.weights.toSeq.sortBy(-_._2).foreach { case (asset, weight) =>
      println(f"$asset: ${weight * 100}%.2f%%")
    }

    // Calculate and display other important portfolios
    val minVarPortfolio = optimizer.minimumVariancePortfolio()
    println("\nMinimum Variance Portfolio:")
    println("--------------------------")
    println(f"Expected Return: ${minVarPortfolio.expectedReturn * 100}%.2f%%")
    println(f"Risk: ${minVarPortfolio.risk * 100}%.2f%%")
    println(f"Sharpe Ratio (Rf=0.02): ${minVarPortfolio.sharpeRatio(0.02)}%.4f")

    val maxSharpePortfolio = optimizer.maximumSharpeRatioPortfolio(0.02)
    println("\nMaximum Sharpe Ratio Portfolio (Rf = 0.02):")
    println("------------------------------------------")
    println(f"Expected Return: ${maxSharpePortfolio.expectedReturn * 100}%.2f%%")
    println(f"Risk: ${maxSharpePortfolio.risk * 100}%.2f%%")
    println(f"Sharpe Ratio (Rf=0.02): ${maxSharpePortfolio.sharpeRatio(0.02)}%.4f")

    // If you want to add visualization, you could adapt the PortfolioVisualizer from your original code
    println("\nGenerating visualizations...")
    val visualizer = new PortfolioVisualizer(optimizer)
    visualizer.plotEfficientFrontier(50)
    visualizer.savePortfolioWeights(optimalPortfolio)
    println("Visualizations saved to output directory")

  catch
    case e: Exception =>
      println(s"Error: ${e.getMessage}")
      e.printStackTrace()