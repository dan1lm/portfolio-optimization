package input

import scala.io.StdIn

/**
 * This is where user input handling happens
 */
object UserInputHandler:
  /**
   * Prompt the user to enter assets to include in the portfolio
   * @return List of ticker symbols
   */
  def getAssetList(): List[String] =
    println("Enter ticker symbols (comma separated, e.g., AAPL,MSFT,GOOG):")
    val input = StdIn.readLine()
    val tickers = input.split(",").map(_.trim.toUpperCase).filter(_.nonEmpty).toList

    if tickers.isEmpty then
      println("No valid tickers entered. Please try again.")
      getAssetList() // Recursively call until the input is valid
    else
      println(s"You selected ${tickers.size} assets: ${tickers.mkString(", ")}")
      tickers

  /**
   * Prompt the user to enter risk aversion parameter
   * @return Risk aversion value
   */
  def getRiskAversion(): Double =
    println("Enter risk aversion parameter (1-10, where higher values prefer lower risk):")
    try
      val input = StdIn.readLine().trim
      val value = input.toDouble
      if value <= 0 then
        println("Risk aversion must be positive. Please try again.")
        getRiskAversion() // Recursively call until valid
      else
        value
    catch
      case _: NumberFormatException =>
        println("Invalid input. Please enter a numeric value.")
        getRiskAversion() // Recursively call until valid