# ExchangeRateService
An API for accessing and converting foreign exchange rates based on the provided CSV of historical exchange rates data. 

The historical exchange rates data comes from the European Central Bank which can be accessed from this link: 
https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html.

The ExchangeRateService provides the following functionalities:
     
### getRatesByDate 

* Description:
    Retrieves the reference rates for a given date for all available currencies.
* Parameters:
  1. date [String] - date of format "YYYY-MM-DD"

### getRate

* Description:
  Retrieves the reference rate data for a given date and currency.
* Parameters:
  1. date [String] - date of format "YYYY-MM-DD"
  2. currency [String] - currency code that will be retrieved

### convertCurrency

* Description:
  Converts the given amountToConvert from the srcCurrency to the targetCurrency on the provided date.
* Parameters:
  1. date [String] - date of format "YYYY-MM-DD"
  2. srcCurrency [String] - source currency
  3. targetCurrency [String] - target currency
  4. amountToConvert [Double] - amount to convert

### getHighestRate

* Description
  Gets the highest reference exchange rate that the currency achieved from the given period.
* Parameters:
  1. currency [String] - currency code
  2. startDate [String] - start date of format "YYYY-MM-DD"
  3. endDate [String] - end date of format "YYYY-MM-DD"

### getAverageRate 

* Description
  Gets the average reference exchange rate for the currency at the given period.
* Parameters:
  1. currency [String] - currency code
  2. startDate [String] - start date of format "YYYY-MM-DD"
  3. endDate [String] - end date of format "YYYY-MM-DD"

## Sample Usage 
The ExchangeRateService accepts an Option[String] file path as input.
This filepath pertains to the local zip filepath of the CSV exchange rates data.

If the filePath is not provided (passed as None), the ExchangeRateService will automatically download the zip file from 
this website: 'https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html
as `eurofxref-hist.zip`.
```scala

val exchangeRateService = new ExchangeRateService("/path/to/localzip.zip")
exchangeRateService.getRatesByDate("2023-04-06") // returns a Map[String, Double] where the key=currency code and the value is its corresponding reference rate.
exchangeRateService.convertCurrency("2023-04-06", "USD", "PHP", 100) // returns an Option[Double] of the converted currency. It will return None if the currency is not available on the input date.
```