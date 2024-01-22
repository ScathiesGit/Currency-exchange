package git.scathiesgit;


import git.scathiesgit.dao.CurrencyDao;
import git.scathiesgit.dao.ExchangeRateDao;
import git.scathiesgit.dto.CurrencyDto;
import git.scathiesgit.dto.ExchangeRateDto;
import git.scathiesgit.entity.Currency;
import git.scathiesgit.entity.ExchangeRate;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) {
       // getExchangeRateByCurrencyCode();
//        insertExchangeRate();
     //   updateExchangeRate();
      //  getReverseRate();
       // getCrossRate();
        // findByCurrenciesCodes();
        //saveCurrency();

       // getAllCurrencies(); // когда ОК - проверено
        //saveCurrency(); //
        getCurrency();
    }
    public static void getCurrency() {
        var dao = CurrencyDao.getInstance();

        System.out.println(dao.getCurrencyByCode("FD"));
    }
    public static void getAllCurrencies() {
        var currencyDao = CurrencyDao.getInstance();

        currencyDao.getAllCurrencies().forEach(System.out::println);
    }
    public static void findByCurrenciesCodes() {
        var exchangeRateDao = ExchangeRateDao.getInstance();

        var exchangeRate = exchangeRateDao.findByCurrenciesCodes("EUR", "GBP");
        System.out.println(exchangeRate);
    }

    public static void saveCurrency() {
        var currencyDto = new CurrencyDto();
        currencyDto.setName("Test");
        currencyDto.setCode("USD");
        currencyDto.setSign("%$");

        var currencyDao = CurrencyDao.getInstance();
        System.out.println(currencyDao.save(currencyDto));
    }

//    public static void getCrossRate() {
//        var exchangeRateDao = ExchangeRateDao.getInstance();
//        var exchangeRate = new ExchangeRate();
//        exchangeRate.setBaseCurrencyId(2);
//        exchangeRate.setTargetCurrencyId(5);
//        System.out.println(exchangeRateDao.findExchangeRate(exchangeRate));
//    }
//
//    public static void getReverseRate() {
//        var exchangeRateDao = ExchangeRateDao.getInstance();
//        var exchangeRate = new ExchangeRate();
//        exchangeRate.setBaseCurrencyId(2);
//        exchangeRate.setTargetCurrencyId(4);
//        System.out.println(exchangeRateDao.findExchangeRate(exchangeRate));
//    }
//
    public static void updateExchangeRate() {
        var dto = new ExchangeRateDto();
        dto.setBaseCurrencyCode("USD");
        dto.setTargetCurrencyCode("JPY");
        dto.setRate(BigDecimal.valueOf(143.2));

        var exchangeRateDao = ExchangeRateDao.getInstance();
        int id = exchangeRateDao.update(dto);
        System.out.println(id + " " + dto);
    }
//
//    public static void insertExchangeRate() {
//        var exchangeRate = new ExchangeRate();
//        exchangeRate.setBaseCurrencyId(5);
//        exchangeRate.setTargetCurrencyId(3);
//        exchangeRate.setRate(BigDecimal.valueOf(113.4));
//
//        var exchangeRateDao = ExchangeRateDao.getInstance();
//        exchangeRateDao.save(exchangeRate);
//        System.out.println(exchangeRate);
//    }
//
//    public static void getExchangeRateByCurrencyCode() {
//        //USD RUB - нужно получить айди USD, далее айди RUB
//        // в параметрах есть коды валют
//        String baseCurrencyCode = "GBP";
//        String targetCurrencyCode = "JPY";
//
//        int baseCurrencyId = getCurrencyByCode(baseCurrencyCode);
//        int targetCurrencyId = getCurrencyByCode(targetCurrencyCode);
//
//        var exchangeRateDao = ExchangeRateDao.getInstance();
//        var exchangeRate = new ExchangeRate();
//        exchangeRate.setBaseCurrencyId(baseCurrencyId);
//        exchangeRate.setTargetCurrencyId(targetCurrencyId);
//        exchangeRateDao.findByBaseAndTargetCurrencyId(exchangeRate);
//        System.out.println(exchangeRate);
//    }
//
//    public static int getCurrencyByCode(String code) {
//        var currencyDao = CurrencyDao.getInstance();
//        var currency = new Currency();
//        currency.setCode(code);
//        currencyDao.getCurrencyByCode(currency);
//        return currency.getId();
//    }
}
