package git.scathiesgit.dto;

import java.math.BigDecimal;

public class ExchangeRateDto {

    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal rate;

    public ExchangeRateDto() {
    }

    public ExchangeRateDto(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        this.baseCurrencyCode = baseCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
        this.rate = rate;
    }

    public class Builder {

        public Builder baseCurrencyCode(String baseCurrencyCode) {
            ExchangeRateDto.this.baseCurrencyCode = baseCurrencyCode;
            return this;
        }

        public Builder targetCurrencyCode(String targetCurrencyCode) {
            ExchangeRateDto.this.targetCurrencyCode = targetCurrencyCode;
            return this;
        }

        public Builder rate(BigDecimal rate) {
            ExchangeRateDto.this.rate = rate;
            return this;
        }

        public ExchangeRateDto build() {
            return ExchangeRateDto.this;
        }
    }

    public static ExchangeRateDto.Builder newBuilder() {
        return new ExchangeRateDto().new Builder();
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    public void setTargetCurrencyCode(String targetCurrencyCode) {
        this.targetCurrencyCode = targetCurrencyCode;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "ExchangeRateDto{" +
                "baseCurrencyCode='" + baseCurrencyCode + '\'' +
                ", targetCurrencyCode='" + targetCurrencyCode + '\'' +
                ", rate=" + rate +
                '}';
    }
}
