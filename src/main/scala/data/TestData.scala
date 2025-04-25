package data

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.stats.mean

@main
def testDataLoader(): Unit =
  println("Testing Data Loader")
  println("==================")

  // Generate sample data and load it
  val dataFile = "data/test_prices.csv"
  SampleDataGenerator.createSampleData(dataFile)
  
  val dataLoader = new DataLoader()
  val (assets, covMatrix) = dataLoader.loadMarketData(dataFile)

  println(s"Loaded ${assets.length} assets:")
  assets.foreach { asset =>
    println(f"${asset.symbol}: Expected Return = ${asset.expectedReturn * 100}%.2f%%")
  }

  println("\nCovariance Matrix:")
  println(covMatrix)

  // Correlation matrix
  val corrMatrix = calculateCorrelation(covMatrix)
  println("\nCorrelation Matrix:")
  println(corrMatrix)

def calculateCorrelation(covMatrix: DenseMatrix[Double]): DenseMatrix[Double] =
  val n = covMatrix.rows
  val corrMatrix = DenseMatrix.zeros[Double](n, n)

  for
    i <- 0 until n
    j <- 0 until n
  do
    corrMatrix(i, j) = covMatrix(i, j) / math.sqrt(covMatrix(i, i) * covMatrix(j, j))

  corrMatrix