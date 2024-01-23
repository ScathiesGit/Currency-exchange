package git.scathiesgit.entity;

import java.math.BigDecimal;

public class ExchangeRate {

    private int id;
    private int baseCurrencyId;
    private int targetCurrencyId;
    private BigDecimal rate;

    public ExchangeRate() {
    }

    public class Builder {
        private Builder() {
        }

        public Builder id(int id) {
            ExchangeRate.this.id = id;
            return this;
        }

        public Builder baseCurrencyId(int id) {
            ExchangeRate.this.baseCurrencyId = id;
            return this;
        }

        public Builder targetCurrencyId(int id) {
            ExchangeRate.this.targetCurrencyId = id;
            return this;
        }

        public Builder rate(BigDecimal rate) {
            ExchangeRate.this.rate = rate;
            return this;
        }

        public ExchangeRate build() {
            return ExchangeRate.this;
        }
    }

    public static Builder newBuilder() {
        return new ExchangeRate().new Builder();
    }

    public ExchangeRate(int id, int baseCurrencyId, int targetCurrencyId, BigDecimal rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public void setBaseCurrencyId(int baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public int getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public void setTargetCurrencyId(int targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "ExchangeRate{id=" + id + ", baseCurrencyId=" + baseCurrencyId
                + ", targetCurrencyId=" + targetCurrencyId + ", rate=" + rate + '}';
    }
}
