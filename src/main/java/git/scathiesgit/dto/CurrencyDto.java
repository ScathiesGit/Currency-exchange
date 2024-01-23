package git.scathiesgit.dto;

import git.scathiesgit.entity.Currency;

public class CurrencyDto {

    private String name;
    private String code;
    private String sign;

    public class Builder {
        private Builder() {
        }

        public CurrencyDto.Builder name(String name) {
            CurrencyDto.this.name = name;
            return this;
        }

        public CurrencyDto.Builder code(String code) {
            CurrencyDto.this.code = code;
            return this;
        }

        public CurrencyDto.Builder sign(String sign) {
            CurrencyDto.this.sign = sign;
            return this;
        }

        public CurrencyDto build() {
            return CurrencyDto.this;
        }
    }

    public static CurrencyDto.Builder newBuilder() {
        return new CurrencyDto().new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "CurrencyDto{"
                +
                "name='" + name + '\''
                +
                ", code='" + code + '\''
                +
                ", sign='" + sign + '\''
                +
                '}';
    }
}
