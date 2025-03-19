package models

/**
 * Represents a financial asset in the portfolio optimization process.
 *
 * @param symbol            Ticker symbol or identifier for the asset
 * @param name              Name for the asset
 * @param expectedReturn    Annualized expected return of the asset
 * @param sector            Industry sector this asset belongs to
 * @param region            Geographic region (e.g., "North America", "Europe", etc.)
 * @param assetClass        Asset class (e.g., "Equity", "Bond", "Commodity", etc.)
 */

case class Asset(
                  symbol: String,
                  name: String,
                  expectedReturn: Double,
                  sector: String,
                  region: Option[String] = None,
                  assetClass: Option[String] = Some("Equity")
                ) {
  /**
   * Validation of expected return
   *
   * @return true if the expected return is valid (not NaN or Infinite)
   */
  def isValid: Boolean = !expectedReturn.isNaN && !expectedReturn.isInfinite

  /**
   * String representation of this asset with its key properties
   *
   * @return A formatted string representing this asset
   */
  override def toString: String =
    s"Asset($symbol, expectedReturn=$expectedReturn, sector=$sector)"
}


object Asset {
  /**
   * Creates an Asset from minimal information, using the symbol as the name
   * and setting the asset class to Equity by default.
   *
   * @param symbol              Ticker symbol or identifier for the asset
   * @param expectedReturn      Annualized expected return of the asset
   * @param sector              Industry sector this asset belongs to
   * @return                    A new Asset instance
   */
  def apply(symbol: String, expectedReturn: Double, sector: String): Asset =
    Asset(symbol, symbol, expectedReturn, sector, None, Some("Equity"))

  /**
   * Converts an Asset to a tuple of (symbol, expectedReturn)
   *
   * @param asset The asset to convert
   * @return A tuple of (symbol, expectedReturn)
   */
  def unapply(asset: Asset): Option[(String, Double)] =
    Some((asset.symbol, asset.expectedReturn))
}