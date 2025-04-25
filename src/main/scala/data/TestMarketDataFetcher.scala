package data

import breeze.linalg.{DenseMatrix, DenseVector}

@main
def testMarketDataFetcher(): Unit =
  println("Testing Market Data Fetcher")
  println("==========================")

  // Get API key and test fetching
  println("Enter your Alpha Vantage API key:")
  val apiKey = scala.io.StdIn.readLine()
  
  val fetcher = new MarketDataFetcher(apiKey)
  
  val tickers = List("AAPL", "MSFT", "GOOG")
  println(s"Fetching data for: ${tickers.mkString(", ")}")

  try
    val (assets, covMatrix) = fetcher.fetchMarketData(tickers, 30)

    println("\nRetrieved Assets:")
    assets.foreach { asset =>
      println(f"${asset.symbol}: Expected Return = ${asset.expectedReturn * 100}%.2f%%, Sector = ${asset.sector}")
    }

    println("\nCovariance Matrix:")
    println(covMatrix)

    println("\nCorrelation Matrix:")
    val corrMatrix = calculateCorrelation(covMatrix)
    println(corrMatrix)

    // Test portfolio construction
    if assets.nonEmpty then
      println("\nTest Portfolio with Equal Weights:")
      val equalWeight = 1.0 / assets.length
      val weights = assets.map(_.symbol).map(sym => (sym, equalWeight)).toMap
      val portfolioReturn = assets.map(a => a.expectedReturn * weights(a.symbol)).sum

      println(f"Expected Return: ${portfolioReturn * 100}%.2f%%")
      println("Weights:")
      weights.foreach { case (sym, w) => println(f"$sym: ${w * 100}%.2f%%") }
  catch
    case e: Exception =>
      println(s"Error: ${e.getMessage}")
      e.printStackTrace()

/**
 * Calculation of correlation matrix from covariance matrix
 */
def calculateCorrelation(covMatrix: DenseMatrix[Double]): DenseMatrix[Double] =
  val n = covMatrix.rows
  val corrMatrix = DenseMatrix.zeros[Double](n, n)

  for
    i <- 0 until n
    j <- 0 until n
  do
    // Correlation formula: cov(i,j) / (stddev(i) * stddev(j)) where stddev(i) = sqrt(cov(i,i))
    corrMatrix(i, j) = covMatrix(i, j) / math.sqrt(covMatrix(i, i) * covMatrix(j, j))

  corrMatrix