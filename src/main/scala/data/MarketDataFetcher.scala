package data

import models.Asset
import breeze.linalg.*
import breeze.stats.mean
import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.time.Duration

/**
 * Fetches real-time market data from Alpha Vantage API
 *
 * @param apiKey Alpha Vantage API key
 */
class MarketDataFetcher(apiKey: String):
  private val baseUrl = "https://www.alphavantage.co/query"
  private val client = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(10))
    .build()

  /**
   * Fetches market data for a list of ticker symbols
   *
   * @param tickers List of ticker symbols
   * @param lookbackDays Number of trading days to look back
   * @return Tuple of (assets, covariance matrix)
   */
  def fetchMarketData(tickers: List[String], lookbackDays: Int = 252): (Seq[Asset], DenseMatrix[Double]) =
    println(s"Fetching market data for ${tickers.size} assets...")

    // Fetch data for each ticker
    val dataMap = tickers.map { ticker =>
      println(s"Fetching data for $ticker...")
      val prices = fetchHistoricalPrices(ticker, lookbackDays)
      (ticker, prices)
    }.toMap.filter(_._2.nonEmpty)

    // Process the data
    processMarketData(dataMap, tickers, lookbackDays)

  /**
   * Fetches historical daily prices for a single ticker
   */
  private def fetchHistoricalPrices(ticker: String, lookbackDays: Int): Seq[Double] =
    try
      val url = s"$baseUrl?function=TIME_SERIES_DAILY&symbol=$ticker&outputsize=compact&apikey=$apiKey"
      val request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .GET()
        .build()

      val response = client.send(request, HttpResponse.BodyHandlers.ofString())

      if response.statusCode() == 200 then
        try
          val responseBody = response.body()
          val json = ujson.read(responseBody)

          if json.obj.contains("Error Message") then
            println(s"API Error for $ticker: ${json("Error Message").str}")
            Seq.empty
          else if !json.obj.contains("Time Series (Daily)") then
            println(s"Unexpected response format for $ticker")
            Seq.empty
          else
            val timeSeries = json("Time Series (Daily)")

            // Sort dates in descending order and take requested number of days
            val dates = timeSeries.obj.keys.toSeq.sorted.reverse.take(lookbackDays + 1)

            // Extract closing prices
            dates.map { date =>
              timeSeries(date)("4. close").str.toDouble
            }
        catch
          case e: Exception =>
            println(s"Error parsing data for $ticker: ${e.getMessage}")
            Seq.empty
      else
        println(s"HTTP Error for $ticker: Status ${response.statusCode()}")
        Seq.empty
    catch
      case e: Exception =>
        println(s"Request Error for $ticker: ${e.getMessage}")
        Seq.empty

  /**
   * Process market data to calculate returns and covariance
   */
  private def processMarketData(
                                 dataMap: Map[String, Seq[Double]],
                                 tickers: List[String],
                                 lookbackDays: Int
                               ): (Seq[Asset], DenseMatrix[Double]) =
    // Filter out tickers with insufficient data
    val validTickers = tickers.filter(t =>
      dataMap.contains(t) && dataMap(t).length > 1
    )

    println(s"Successfully retrieved data for ${validTickers.size} out of ${tickers.size} assets")

    if validTickers.isEmpty then
      throw new RuntimeException("No valid market data retrieved for any ticker")

    // Calculate daily returns
    val minDataLength = validTickers.map(t => dataMap(t).length).min - 1
    val actualLookback = math.min(lookbackDays, minDataLength)

    val returns = Array.ofDim[Double](actualLookback, validTickers.size)

    for (i <- 0 until validTickers.size) do
      val ticker = validTickers(i)
      val prices = dataMap(ticker)

      for (j <- 0 until actualLookback) do
        // Daily return calculation: (today/yesterday) - 1
        returns(j)(i) = (prices(j) / prices(j+1)) - 1.0

    val returnMatrix = DenseMatrix.create(
      returns.length,
      validTickers.size,
      returns.flatten
    )

    // Calculate annualized returns
    val annualReturns = DenseVector.tabulate(validTickers.size) { j =>
      val colMean = mean(returnMatrix(::, j))
      // Annualize: (1 + daily mean)^252 - 1
      math.pow(1 + colMean, 252.0) - 1.0
    }
    
    val covMatrix = cov(returnMatrix).copy
    for (i <- 0 until covMatrix.rows; j <- 0 until covMatrix.cols) do
      covMatrix(i, j) *= 252.0

    // Create Asset objects
    val assets = validTickers.zip(annualReturns.toArray).map { case (symbol, ret) =>
      val sector = getSectorForTicker(symbol)
      Asset(symbol, ret, sector)
    }

    (assets, covMatrix)

  /**
   * Gets sector information for a ticker
   */
  private def getSectorForTicker(ticker: String): String =
    ticker match
      case t if Seq("AAPL", "MSFT", "GOOG", "GOOGL", "META", "NVDA").contains(t) => "Technology"
      case t if Seq("JPM", "BAC", "WFC", "C", "GS", "MS").contains(t) => "Financial"
      case t if Seq("XOM", "CVX", "COP", "BP", "SLB").contains(t) => "Energy"
      case t if Seq("JNJ", "PFE", "MRK", "ABT", "LLY").contains(t) => "Healthcare"
      case t if Seq("PG", "KO", "PEP", "WMT", "COST").contains(t) => "Consumer"
      case _ => "Unknown"