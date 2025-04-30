package AMBTokenPKG;

public class IntToken extends AMBTokens {
    private final int value;

    public IntToken(int value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return "INT";
    }

    public int getValue() {
        return value;
    }
}

// var as final so it cant be changed or acessed outside of int token