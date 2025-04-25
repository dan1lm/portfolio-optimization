package models

/**
 * Financial asset representation
 *
 * @param symbol Ticker symbol
 * @param expectedReturn Annualized expected return
 * @param sector Industry sector
 */
case class Asset(
                  symbol: String,
                  expectedReturn: Double,
                  sector: String
                ):
  override def toString: String =
    f"$symbol (Return: ${expectedReturn * 100}%.2f%%, Sector: $sector)"