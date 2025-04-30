package AMBTokenPKG;

public class StringToken extends AMBTokens {
    private final String value;

    public StringToken(String value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return "STRING";
    }

    public String getValue() {
        return value;
    }

    public boolean getString() {
        return false;
    }
}
