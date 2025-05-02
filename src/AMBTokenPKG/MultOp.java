package AMBTokenPKG;

public class MultOp extends Symbols {

    public enum Operand { mult, divide }

    private final Operand op;

    public MultOp(Operand op) {

        super("MULT_OP");
        this.op = op;
    }

    public Operand getOp() {
        return op;
    }

    @Override
    public String getType() {
        return "MULT_OP";
    }
}
