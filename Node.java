import java.util.*;

public class Node {
    String operator = null;
    List<String> condition = null;
    Node left;
    Node right;
    List<Integer> rows;

    public Node(String operator){
        this.operator = operator;
    }

    public Node(List<String> condition, List<Integer> rows){
        this.condition = new ArrayList<>();
        this.rows = new ArrayList<>();
        for(String cmd:condition){
            this.condition.add(cmd);
        }
        for(int row:rows){
            this.rows.add(row);
        }
    }

    public boolean isOperator(){
        if(operator != null){
            return true;
        }
        return false;
    }

    public boolean isAnd(){
        if(operator != null && operator.equals("AND")){
            return true;
        }
        return false;
    }

    public boolean isOr(){
        if(operator != null && operator.equals("OR")){
            return true;
        }
        return false;
    }

    public boolean isCondition(){
        if(condition != null){
            return true;
        }
        return false;
    }

    public void setRows(List<Integer> rows){
        this.rows = new ArrayList<>();
        for(int row:rows){
            this.rows.add(row);
        }
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }
}
