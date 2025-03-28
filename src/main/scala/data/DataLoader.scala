package data

import breeze.linalg.*
import breeze.stats.mean
import models.Asset
import com.github.tototoshi.csv.*

import java.io.File
import scala.util.Try

/**
 *  Loading/processing of financial data
 */
class DataLoader:
  /**
   * Loads price data from a CSV file and calculates returns
   *
   * @param filepath Path to the CSV file
   * @return (Seq[Asset], DenseMatrix[Double]) with assets and covariance matrix
   */
  def loadMarketData(filepath: String): (Seq[Asset], DenseMatrix[Double]) =
    val reader = CSVReader.open(new File(filepath))

    try
      val header = reader.readNext().getOrElse(List.empty)
      val assetColumns = header.tail

      val rows = reader.all()

      val prices = rows.map { row =>
        row.tail.map(p => Try(p.toDouble).getOrElse(Double.NaN))
      }

      val returns = Array.ofDim[Double](prices.length - 1, assetColumns.length)

      for i <- 0 until prices.length - 1 do
        for j <- 0 until assetColumns.length do
          returns(i)(j) = (prices(i+1)(j) / prices(i)(j)) - 1.0

      val returnMatrix = DenseMatrix.create(
        returns.length,
        assetColumns.length,
        returns.flatten
      )

      val annualReturns = DenseVector.tabulate(assetColumns.length) { j =>
        val colMean = mean(returnMatrix(::, j))
        // Double literal
        math.pow(1 + colMean, 252.0) - 1.0
      }

      // Calculate covariance matrix (annualized)
      val covMatrix = cov(returnMatrix).copy
      for (i <- 0 until covMatrix.rows; j <- 0 until covMatrix.cols) {
        covMatrix(i, j) *= 252.0
      }


      val assets = assetColumns.zip(annualReturns.toArray).map { case (name, ret) =>
        // Parse sector from name if formatted as "TICKER|SECTOR"
        val parts = name.split("\\|")
        val symbol = parts(0)
        val sector = if parts.length > 1 then parts(1) else "Unknown"

        Asset(symbol, ret, sector)
      }

      (assets, covMatrix)
    finally
      reader.close()

  /**
   * Calculates statistics from a matrix
   *
   * @param returns Matrix of returns (rows are time periods, columns are assets)
   * @return (DenseVector[Double], DenseMatrix[Double]) of expected returns and covariance
   */
  def calculateStatistics(
                           returns: DenseMatrix[Double]
                         ): (DenseVector[Double], DenseMatrix[Double]) =

    val tradingDays = 252.0

    val expectedReturns = DenseVector.tabulate(returns.cols) { j =>
      val meanDailyReturn = mean(returns(::, j))
      math.pow(1 + meanDailyReturn, tradingDays) - 1.0
    }

    // Create copy
    val covMatrix = cov(returns).copy
    for (i <- 0 until covMatrix.rows; j <- 0 until covMatrix.cols) {
      covMatrix(i, j) *= tradingDays
    }

    (expectedReturns, covMatrix)