package git.scathiesgit.entity;

public class Currency {

    private int id;
    private String code;
    private String fullName;
    private String sign;

    public Currency() {
    }

    public Currency(int id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public class Builder {
        private Builder() {
        }

        public Builder id(int id) {
            Currency.this.id = id;
            return this;
        }

        public Builder code(String code) {
            Currency.this.code = code;
            return this;
        }

        public Builder fullName(String fullName) {
            Currency.this.fullName = fullName;
            return this;
        }

        public Builder sign(String sign) {
            Currency.this.sign = sign;
            return this;
        }

        public Currency build() {
            return Currency.this;
        }
    }

    public static Builder newBuilder() {
        return new Currency().new Builder();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "Currency{id=" + id + ", code='" + code + '\''
                + ", fullName='" + fullName + '\''
                + ", sign='" + sign + '\'' + '}';
    }
}
