package AMBTokenPKG;

public class CODE extends AMBTokens {

    private final String code; // <-- You need to store the label!

    public CODE() {
        this.code = "";  // Empty default
    }

    public CODE(String code) {
        this.code = code;
    }

    @Override
    public String getType() {
        return "CODE";
    }

    public String getCode() {
        return code;
    }
}
