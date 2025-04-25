# 📊 Portfolio Optimization Tool

A Scala-based tool for portfolio optimization using Modern Portfolio Theory (Markowitz model) with real-time market data.

This project implements a portfolio optimization system that allows users to:

- Specify assets to include in a portfolio
- Fetch real-time market data using the Alpha Vantage API
- Generate an efficient frontier and optimal portfolio allocation based on user-defined risk preferences
- The application uses the Markowitz mean-variance optimization model to balance expected returns against risk.


## Key Portfolio Metrics

1. Minimum variance portfolio
2. Maximum Sharpe ratio portfolio
3. Custom risk-preference portfolio

## Technologies Used
	
1. Scala 3.2.2	
2. Breeze	
3. Alpha Vantage API	
4. LBFGSB	

## Installation and Usage

Make sure you have JDK 11 or higher, as well as SBT, and Alpha Vantage API key.

```
git clone https://github.com/dan1lm/portfolio-optimization
cd portfolio-optimization
sbt compile
sbt run
```

## Example Workflow
```

===========================

Enter ticker symbols (comma separated, e.g., AAPL,MSFT,GOOG):
AAPL,MSFT,GOOG

You selected 3 assets: AAPL, MSFT, GOOG

Enter risk aversion parameter (1-10, where higher values prefer lower risk):
3

Analyzing portfolio with the following assets: AAPL, MSFT, GOOG
Using risk aversion parameter: 3.0

Enter your Alpha Vantage API key:
YOUR_API_KEY

Fetching market data...
Fetching data for AAPL...
Fetching data for MSFT...
Fetching data for GOOG...
Successfully retrieved data for 3 assets

Efficient Frontier:
-------------------
Risk      Return    Sharpe
10.57%    15.23%    1.2515
...

Optimal Portfolio:
------------------
Expected Return: 12.43%
Risk: 11.92%
Sharpe Ratio (Rf=0.02): 0.8747

Weights:
AAPL: 45.37%
GOOG: 32.41%
MSFT: 22.22%
```