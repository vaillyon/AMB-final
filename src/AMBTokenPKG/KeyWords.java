package AMBTokenPKG;

public class KeyWords extends AMBTokens {
    public String keyword;

    public KeyWords(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String getType() {
        return "KEYWORD";
    }

    public String getKeyword() {
        return keyword;
    }
}

