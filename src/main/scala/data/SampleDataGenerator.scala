package data

import java.io.{File, PrintWriter}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random

/**
 * Sample data for testing
 */
object SampleDataGenerator:
  /**
   * I used this to create a sample CSV file with price data
   *
   * @param filepath Path where to save the CSV file
   * @param assets List of asset symbols to include
   * @param days Number of days of data to generate
   */
  def createSampleData(
                        filepath: String = "data/prices.csv",
                        assets: Seq[String] = Seq("AAPL", "MSFT", "AMZN", "JPM", "XOM"),
                        days: Int = 252
                      ): Unit =
    val file = new File(filepath)
    file.getParentFile.mkdirs()

    val writer = new PrintWriter(file)

    try
      writer.println("Date," + assets.mkString(","))

      val random = new Random(42) // This is for reproducibility purposes
      val startDate = LocalDate.now().minusDays(days)
      val formatter = DateTimeFormatter.ISO_DATE

      val prices = Array.fill(assets.length)(100.0) // Starting prices

      for i <- 0 until days do
        val date = startDate.plusDays(i)

        // Update prices with random daily returns
        for j <- prices.indices do
          val dailyReturn = random.nextGaussian() * 0.015 + 0.0005 // mean=0.05%, sd=1.5%
          prices(j) = prices(j) * (1 + dailyReturn)

        writer.println(s"${date.format(formatter)},${prices.map(p => f"$p%.2f").mkString(",")}")

      println(s"Sample data created at $filepath")
    finally
      writer.close()