package AMBPTNodes;

import java.util.ArrayList;


//NEW COMBINE
public class AMBNodes {
    private final ArrayList<Object> children;

    public AMBNodes() {
        this.children = new ArrayList<>();
    }

    public void addChild(Object obj) {
        children.add(obj);
    }

    public ArrayList<Object> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Node:\n");
        for (Object child : children) {
            sb.append(" - ").append(child.toString()).append("\n");
        }
        return sb.toString();
    }
}
