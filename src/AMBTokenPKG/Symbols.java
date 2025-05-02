package AMBTokenPKG;

public class Symbols extends AMBTokens {

    private final String type;

    public Symbols(String type) {
        this.type = type; //   type
    }

    @Override
    public String getType() {
        return type;
    }

}
