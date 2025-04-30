package AMBTokenPKG;

public class Symbols extends AMBTokens {

    private final String type; // Type of symbol

    public Symbols(String type) {
        this.type = type; //  symbol type (e.g., "semi", "colon")
    }

    @Override
    public String getType() {
        return type;
    }

}
