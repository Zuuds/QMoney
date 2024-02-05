
package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Read the json file provided in the argument[0]. The file will be avilable in
  // the classpath.
  // 1. Use #resolveFileFromResources to get actual file from classpath.
  // 2. parse the json file using ObjectMapper provided with #getObjectMapper,
  // and extract symbols provided in every trade.
  // return the list of all symbols in the same order as provided in json.
  // Test the function using gradle commands below
  // ./gradlew run --args="trades.json"
  // Make sure that it prints below String on the console -
  // ["AAPL","MSFT","GOOGL"]
  // Now, run
  // ./gradlew build and make sure that the build passes successfully
  // There can be few unused imports, you will need to fix them to make the build
  // pass.

  private static List<TotalReturnsDto> totalReturnsDtoList = new ArrayList<>();
  private static List<TotalReturnsDto> totalReturnsDtoList2 = totalReturnsDtoList;

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    ObjectMapper map = getObjectMapper();
    PortfolioTrade[] port = map.readValue(resolveFileFromResources(args[0]), 
    PortfolioTrade[].class);
    ArrayList<String> list1 = new ArrayList<String>();
    int len = port.length;

    for (int i = 0; i <= len - 1; i++) {
      list1.add(port[i].toString());
    }
    return list1;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader()
    .getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the
  // correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in
  // PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the
  // output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your
  // reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the
  // function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the
  // stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {
    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = 
        "/home/crio-user/workspace/yashyerolkar-ME_QMONEY/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@362045c0";
    String functionNameFromTestFileInStackTrace = "mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "22";
    return Arrays.asList(new String[] { valueOfArgument0, resultOfResolveFilePathArgs0, 
      toStringOfObjectMapper, functionNameFromTestFileInStackTrace, 
      lineNumberFromTestFileInStackTrace });
  }

  // TODO: CRIO_TASK_MODULE_REST_API
  // Copy the relavent code from #mainReadFile to parse the Json into
  // PortfolioTrade list.
  // Now That you have the list of PortfolioTrade already populated in module#1
  // For each stock symbol in the portfolio trades,
  // Call Tiingo api
  // (https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=&endDate=&token=)
  // with
  // 1. ticker = symbol in portfolio_trade
  // 2. startDate = purchaseDate in portfolio_trade.
  // 3. endDate = args[1]
  // Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>
  // Note - You may have to register on Tiingo to get the api_token.
  // Please refer the the module documentation for the steps.
  // Find out the closing price of the stock on the end_date and
  // return the list of all symbols in ascending order by its close value on
  // endDate
  // Test the function using gradle commands below
  // ./gradlew run --args="trades.json 2020-01-01"
  // ./gradlew run --args="trades.json 2019-07-01"
  // ./gradlew run --args="trades.json 2019-12-03"
  // And make sure that its printing correct results.

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    LocalDate endDate = LocalDate.parse(args[1]);
    byte[] byteArray = Files.readAllBytes(file.toPath());
    String content = new String(byteArray, "UTF-8");
    ObjectMapper map = getObjectMapper();
    PortfolioTrade[] portfolioTrades = map.readValue(content, PortfolioTrade[].class);
    String token = "df6bdd915147c61660eea633ecfb891181e2eec1";
    String uri = 
        "https://api.tiingo.com/tiingo/daily/$TICK/prices?startDate=$STARTD&endDate=$ENDD&token=$KEY";

    for (PortfolioTrade portfolioTrade : portfolioTrades) {
      String url = uri.replace("$KEY", token).replace("$TICK", portfolioTrade.getSymbol())
          .replace("$STARTD", portfolioTrade.getPurchaseDate().toString())
          .replace("$ENDD", endDate.toString());
      System.out.println(url);
      TiingoCandle[] tiingoCandles = new RestTemplate().getForObject(url, TiingoCandle[].class);
      TotalReturnsDto totalReturn;
      try {
        totalReturn = new TotalReturnsDto(portfolioTrade.getSymbol(),
          Stream.of(tiingoCandles).filter(candle -> candle.getDate().equals(endDate))
          .findFirst().get().getClose());
      } catch (NoSuchElementException e) {
        totalReturn = new TotalReturnsDto(portfolioTrade.getSymbol(),
          Stream.of(tiingoCandles).filter(candle -> candle.getDate().equals(endDate.minusDays(1)))
          .findFirst().get().getClose());
      }
      totalReturnsDtoList2.add(totalReturn);
    }
    totalReturnsDtoList.sort(Comparator.comparing(TotalReturnsDto::getClosingPrice));

    return Arrays.stream(portfolioTrades).map(trade -> {
      String url = uri.replace("$KEY", token)
          .replace("$TICK", trade.getSymbol())
          .replace("$STARTD", trade.getPurchaseDate().toString())
          .replace("$ENDD", endDate.toString());
      TiingoCandle[] tiingoCandles = new RestTemplate().getForObject(url, TiingoCandle[].class);
      try {
        return new TotalReturnsDto(trade.getSymbol(),
          Stream.of(tiingoCandles).filter(candle -> candle.getDate().equals(endDate))
          .findFirst().get().getClose());
      } catch (NoSuchElementException e) {
        return new TotalReturnsDto(trade.getSymbol(),
          Stream.of(tiingoCandles).filter(candle -> candle.getDate().equals(endDate.minusDays(1)))
          .findFirst().get().getClose());
      }
    }).sorted(Comparator.comparing(TotalReturnsDto::getClosingPrice))
        .map(TotalReturnsDto::getSymbol)
        .collect(Collectors.toList());
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // Copy the relevant code from #mainReadQuotes to parse the Json into
  // PortfolioTrade list and
  // Get the latest quotes from TIingo.
  // Now That you have the list of PortfolioTrade And their data,
  // With this data, Calculate annualized returns for the stocks provided in the
  // Json
  // Below are the values to be considered for calculations.
  // buy_price = open_price on purchase_date and sell_value = close_price on
  // end_date
  // startDate and endDate are already calculated in module2
  // using the function you just wrote #calculateAnnualizedReturns
  // Return the list of AnnualizedReturns sorted by annualizedReturns in
  // descending order.
  // use gralde command like below to test your code
  // ./gradlew run --args="trades.json 2020-01-01"
  // ./gradlew run --args="trades.json 2019-07-01"
  // ./gradlew run --args="trades.json 2019-12-03"
  // where trades.json is your json file

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args) 
      throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    //LocalDate endDate = LocalDate.parse(args[1]);
    byte[] byteArray = Files.readAllBytes(file.toPath());
    String content = new String(byteArray, "UTF-8");
    ObjectMapper objectmapper = getObjectMapper();
    PortfolioTrade[] portfolioTrades = objectmapper.readValue(content,PortfolioTrade[].class);
    List<String> s =
        Stream.of(portfolioTrades).map(PortfolioTrade::getSymbol)
        .collect(Collectors.toList());
    List<LocalDate> s1 =
        Stream.of(portfolioTrades).map(PortfolioTrade::getPurchaseDate)
        .collect(Collectors.toList());
    //HashMap<String, Double> map = new HashMap<>();
    RestTemplate rs = new RestTemplate();
    LocalDate d1 = LocalDate.parse(args[1]);
    List<AnnualizedReturn> ar = new ArrayList<AnnualizedReturn>();
    for (int i = 0;i < s.size();i++)  {
      if (d1.compareTo(s1.get(i)) >= 0) {
        String uri = "https://api.tiingo.com/tiingo/daily/" + s.get(i) + "/prices?startDate=" + s1.get(i).toString() + "&endDate=" + args[1] + "&token=7ccf88cc2def193d48743cdf57e06994c8bd9075";
        URI u = new URI(uri);
        ResponseEntity<String> res = rs.getForEntity(u,String.class);
        objectmapper.registerModule(new JavaTimeModule());
        TiingoCandle[] t = objectmapper.readValue(res.getBody(),TiingoCandle[].class);
        List<Double> s2 =
            Stream.of(t).map(TiingoCandle::getOpen)
            .collect(Collectors.toList());
        List<Double> s3 =
            Stream.of(t).map(TiingoCandle::getClose)
            .collect(Collectors.toList());
        Double op = s2.get(0);
        Double ce = s3.get((s3.size()) - 1);
        ar.add(calculateAnnualizedReturns(d1, portfolioTrades[i], op, ce));
      } else {
        throw new RuntimeException();
      }
    }
    ar.sort(Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed());
    return ar;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // annualized returns should be calculated in two steps -
  // 1. Calculate totalReturn = (sell_value - buy_value) / buy_value
  // Store the same as totalReturns
  // 2. calculate extrapolated annualized returns by scaling the same in years
  // span. The formula is
  // annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  // Store the same as annualized_returns
  // return the populated list of AnnualizedReturn for all stocks,
  // Test the same using below specified command. The build should be successful
  // ./gradlew test --tests
  // PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, 
      PortfolioTrade trade, Double buyPrice,
      Double sellPrice) {
    LocalDate startDate = trade.getPurchaseDate();
    LocalDate enDate;
    if (endDate == null) {
      enDate = LocalDate.now();
    } else {
      enDate = endDate;
    }
    Double totalReturn = (sellPrice - buyPrice) / buyPrice;
    Long numDays = ChronoUnit.DAYS.between(startDate,enDate);
    Double days = (double) numDays;
    Double numYrs = days / 365.24;
    Double a = 1.0 + totalReturn;
    Double b = 1.0 / numYrs;  
    Double annualizedReturn = Math.pow(a,b) - 1;
    AnnualizedReturn ann1 = 
        new AnnualizedReturn(trade.getSymbol(), annualizedReturn, totalReturn);
    return ann1;
    //  double totalReturn = ((sellPrice - buyPrice) / buyPrice);
    //  double years = trade.getPurchaseDate().until(endDate, ChronoUnit.DAYS) / 365.24;
    //  double annualizedReturns = Math.pow((1 + (totalReturn)) , (1/years)) - 1;
    //  return new AnnualizedReturn(trade.getSymbol(),annualizedReturns, totalReturn);
  }

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    printJsonObject(mainReadFile(args));
    printJsonObject(mainReadQuotes(args));
    printJsonObject(mainCalculateSingleReturn(args));

  }
}
