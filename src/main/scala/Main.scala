import models.Asset
import optimization.{PortfolioOptimizer, MinWeightConstraint, MaxWeightConstraint, ConstraintSet}
import data.DataLoader
import visualization.PortfolioVisualizer
import breeze.linalg.*
import java.io.File

@main
def main(): Unit =
  println("Portfolio Optimization Tool")
  println("===========================")

  // Create sample data file if doesn't exist
  val dataFile = new File("data/prices.csv")
  if !dataFile.exists() then
    println("No data file found. Creating sample data...")
    createSampleData()

  // Load market data
  val dataLoader = new DataLoader()
  val (assets, covMatrix) = dataLoader.loadMarketData("data/prices.csv")

  println(s"Loaded data for ${assets.length} assets")

  // Create directories for output if they don't exist
  new File("output/charts").mkdirs()

  val optimizer = new PortfolioOptimizer(assets, covMatrix)

  val visualizer = new PortfolioVisualizer(optimizer)

  val efficientFrontier = optimizer.generateEfficientFrontier(20)
  println("\nEfficient Frontier:")
  println("-------------------")
  println("Risk\tReturn\tSharpe")
  efficientFrontier.foreach { p =>
    println(f"${p.risk}%.4f\t${p.expectedReturn}%.4f\t${p.sharpeRatio}%.4f")
  }

  // Find optimal portfolio based on risk preference
  val riskAversion = 3.0 // Moderate risk aversion
  val optimalPortfolio = optimizer.efficientPortfolio(riskAversion)

  println("\nOptimal Portfolio (Risk Aversion = 3.0):")
  println("----------------------------------------")
  println(f"Expected Return: ${optimalPortfolio.expectedReturn}%.4f")
  println(f"Risk: ${optimalPortfolio.risk}%.4f")
  println(f"Sharpe Ratio: ${optimalPortfolio.sharpeRatio}%.4f")
  println("\nWeights:")
  optimalPortfolio.weights.toSeq.sortBy(-_._2).foreach { case (asset, weight) =>
    println(f"$asset: ${weight * 100}%.2f%%")
  }

  val minVarPortfolio = optimizer.minimumVariancePortfolio()
  println("\nMinimum Variance Portfolio:")
  println("--------------------------")
  println(f"Expected Return: ${minVarPortfolio.expectedReturn}%.4f")
  println(f"Risk: ${minVarPortfolio.risk}%.4f")


  val maxSharpePortfolio = optimizer.maximumSharpeRatioPortfolio(0.02)
  println("\nMaximum Sharpe Ratio Portfolio (Rf = 0.02):")
  println("------------------------------------------")
  println(f"Expected Return: ${maxSharpePortfolio.expectedReturn}%.4f")
  println(f"Risk: ${maxSharpePortfolio.risk}%.4f")
  println(f"Sharpe Ratio: ${maxSharpePortfolio.sharpeRatio}%.4f")


  println("\nGenerating visualizations...")
  visualizer.plotEfficientFrontier(50, "output/charts/efficient_frontier.png")
  visualizer.plotPortfolioWeights(optimalPortfolio, "output/charts/portfolio_weights.png")
  visualizer.plotEfficientFrontierWithSharpeRatio(0.02, "output/charts/efficient_frontier_sharpe.png")
  println("Visualizations saved to output/charts directory")


def createSampleData(): Unit =
  import java.io.{File, PrintWriter}
  import java.time.LocalDate
  import java.time.format.DateTimeFormatter
  import scala.util.Random

  new File("data").mkdirs()

  val writer = new PrintWriter(new File("data/prices.csv"))

  try
    val assets = Array(
      "AAPL|Technology",
      "MSFT|Technology",
      "AMZN|Technology",
      "JPM|Financial",
      "BAC|Financial",
      "XOM|Energy",
      "CVX|Energy",
      "PG|Consumer",
      "KO|Consumer",
      "JNJ|Healthcare"
    )

    writer.println("Date," + assets.mkString(","))

    val random = new Random(42)
    val startDate = LocalDate.now().minusDays(1000)
    val formatter = DateTimeFormatter.ISO_DATE

    val initialPrices = Array.fill(assets.length)(100.0)
    val prices = initialPrices.clone()

    for i <- 0 until 1000 do
      val date = startDate.plusDays(i)

      for j <- prices.indices do
        val dailyReturn = random.nextGaussian() * 0.015 + 0.0005  // mean=0.05%, sd=1.5%
        prices(j) = prices(j) * (1 + dailyReturn)

      writer.println(s"${date.format(formatter)},${prices.map(p => f"$p%.2f").mkString(",")}")
  finally
    writer.close()

  println("Sample data created at data/prices.csv")