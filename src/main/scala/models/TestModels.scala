package models

@main
def testModels(): Unit =
  println("Testing Asset and Portfolio models")
  println("=================================")

  // Create some test assets
  val assets = List(
    Asset("AAPL", 0.15, "Technology"),
    Asset("MSFT", 0.12, "Technology"),
    Asset("JPM", 0.08, "Financial")
  )

  println("Assets:")
  assets.foreach(println)

  // Create a test portfolio
  val weights = Map("AAPL" -> 0.4, "MSFT" -> 0.35, "JPM" -> 0.25)
  val portfolio = Portfolio(weights, 0.12, 0.18)

  println("\nPortfolio:")
  println(portfolio)
  println(f"Sharpe Ratio: ${portfolio.sharpeRatio(0.02)}%.4f")
  println(f"Is Valid: ${portfolio.isValid}")