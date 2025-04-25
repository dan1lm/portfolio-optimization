package data
import com.github.tototoshi.csv.defaultCSVFormat

import models.Asset
import com.github.tototoshi.csv.CSVReader
import breeze.linalg.*
import breeze.stats.mean

import java.io.File
import scala.util.{Try, Success, Failure}

/**
 * Loading and processing of financial data
 */
class DataLoader:

  def loadMarketData(filepath: String): (Seq[Asset], DenseMatrix[Double]) =
    val reader = CSVReader.open(new File(filepath))

    try
      val header = reader.readNext().getOrElse(List.empty)
      val assetSymbols = header.tail

      val rows = reader.all()
      val prices = rows.map(row => row.tail.map(p => Try(p.toDouble).getOrElse(Double.NaN)))


      val returns = Array.ofDim[Double](prices.length - 1, assetSymbols.length)

      for i <- 0 until prices.length - 1 do
        for j <- 0 until assetSymbols.length do
          returns(i)(j) = (prices(i+1)(j) / prices(i)(j)) - 1.0

      val returnMatrix = DenseMatrix.create(
        returns.length,
        assetSymbols.length,
        returns.flatten
      )

      // Calculate annualized returns
      val annualReturns = DenseVector.tabulate(assetSymbols.length) { j =>
        val colMean = mean(returnMatrix(::, j))
        math.pow(1 + colMean, 252.0) - 1.0
      }

      // Covariance matrix
      val covMatrix = cov(returnMatrix).copy
      for (i <- 0 until covMatrix.rows; j <- 0 until covMatrix.cols) do
        covMatrix(i, j) *= 252.0

      // Create Asset objects
      val assets = assetSymbols.zip(annualReturns.toArray).map { case (symbol, ret) =>
        Asset(symbol, ret, "Unknown")
      }

      (assets, covMatrix)
    finally
      reader.close()