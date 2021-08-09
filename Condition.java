import java.io.*;
import java.util.*;
import CommandExceptions.*;

public class Condition extends Command{
    static int depth = -1;
    static int col = 0;
    List<String> condition = new ArrayList<>();
    List<Integer> rowToPrint;
    List<Node>[] allCondition = new List[10];

    public Condition(List command, BufferedWriter socketWriter, Database newDatabase){
        super(command, socketWriter);
        this.newDatabase = newDatabase;
    }

    /*parse is the main method for the class, and it firstly use depth to record the recursive times
    * which can help us spot the layer of a condition, and then initialise a Node for each condition(if
    * it is condition instead of AND & OR, parse it and stored the results in Node), which is stored
    * in a List<Node>[] based on its depth. Then, generate a condition tree by connecting the nodes in the
    * List. Finally, combine those AND & OR, by getting the union and intersection of two nodes. A List of
    * rowToPrint will be generate for future process*/
    public boolean parse() throws CommandException{
        current = command.indexOf("WHERE");
        current++;
        depth=-1;
        isCondition();
        generateConditionTree();
        rowToPrint = combineCondition();
        //reorder the list
        rowToPrint.sort(Comparator.naturalOrder());
        return true;
    }

    public boolean isCondition() throws CommandException{
        depth++;
        if(command.get(current).equals("(")){
            current++;
            isCondition();
        }
        if(command.get(current).equals(")")){
            if((current+2) >= command.size()){
                return true;
            }
            current++;
            depth--;
            return true;
        }
        if(command.get(current).equals("AND") ||command.get(current).equals("OR")) {
            if(allCondition[depth] == null){
                allCondition[depth] = new ArrayList<>();
            }
            allCondition[depth].add(new Node(command.get(current)));
            current++;
            depth--;
            isCondition();
            return true;
        }
        if(isAttributeName()){
            current++;
            if(isOperator()){
                current++;
                isValue();
                //Reach end
                if((current+2) >= command.size()){
                    return true;
                }
                current++;
                if(command.get(current).equals(")")){
                    //Reach end
                    if((current+2) >= command.size()){
                        return true;
                    }
                    current++;
                    depth--;
                    return true;
                }
            }
        }
        throw new CommandTypeException(current);
    }

    public boolean isAttributeName() throws CommandException{
        if(!command.get(current).matches("\\W")){
            condition.add(command.get(current));
            return true;
        }
        throw new CommandTypeException(current);
    }

    public boolean isOperator() throws CommandException{
        if(command.get(current).equals("==")){
            condition.add(command.get(current));
            return true;

        }
        if(command.get(current).equals(">")){
            condition.add(command.get(current));
            return true;
        }
        if(command.get(current).equals("<")){
            condition.add(command.get(current));
            return true;
        }
        if(command.get(current).equals(">=")){
            condition.add(command.get(current));
            return true;
        }
        if(command.get(current).equals("<=")){
            condition.add(command.get(current));
            return true;
        }
        if(command.get(current).equals("!=")){
            condition.add(command.get(current));
            return true;
        }
        if(command.get(current).equals("LIKE")){
            condition.add(command.get(current));
            return true;
        }
        throw new CommandTypeException(current);

    }

    public boolean isValue() throws CommandException{
        //For String
        if(command.get(current).equals("'")){
            current++;
            String temp;
            temp = command.get(current);
            current++;
            while(!command.get(current).equals("'")){
                //Reach end
                if(current >= command.size()-2){
                    throw new CommandTypeException(current);
                }
                temp = temp +" "+ command.get(current);
                current++;

            }
            condition.add(temp);
            //Add to condition pool
            if(allCondition[depth] == null){
                allCondition[depth] = new ArrayList<>();
            }
            allCondition[depth].add(new Node(condition, parseCondition(condition)));
            condition.clear();
            if(command.get(current).equals("'")){
                return true;
            }
        }
        else{
            condition.add(command.get(current));
            if(allCondition[depth] == null){
                allCondition[depth] = new ArrayList<>();
            }
            allCondition[depth].add(new Node(condition, parseCondition(condition)));
            condition.clear();
            return true;
        }
        throw new CommandTypeException(current);
    }

    //Parse a single condition
    public List<Integer> parseCondition(List<String> condition) throws CommandException{
        List<Integer> rows = new ArrayList<>();
        Column selectedColumn = newDatabase.getTable(DBServer.currentTableName).getColumnByName(condition.get(0));
        rows.add(0);
        if(condition.get(1).equals("==")){
            for(int i=1; i<selectedColumn.columnContents.size(); i++){
                if(condition.get(2).equals(selectedColumn.columnContents.get(i))){
                    rows.add(i);
                }
            }
            return rows;
        }
        if(condition.get(1).equals("!=")){
            for(int i=1; i<selectedColumn.columnContents.size(); i++){
                if(!condition.get(2).equals(selectedColumn.columnContents.get(i))){
                    rows.add(i);
                }
            }
            return rows;
        }
        if(condition.get(1).equals("LIKE")){
            if(isNum(condition.get(2))){
                throw new CommandTypeException(current);
            }
            for(int i=1; i<selectedColumn.columnContents.size(); i++){
                if(selectedColumn.columnContents.get(i).contains(condition.get(2))){
                    rows.add(i);
                }
            }
            return rows;
        }
        if(condition.get(1).equals(">")){
            for(int i=1; i<selectedColumn.columnContents.size(); i++){
                if(Float.parseFloat(selectedColumn.columnContents.get(i)) > Float.parseFloat(condition.get(2))){
                    rows.add(i);
                }
            }
            return rows;
        }
        if(condition.get(1).equals("<")){
            for(int i=1; i<selectedColumn.columnContents.size(); i++){
                if(Float.parseFloat(selectedColumn.columnContents.get(i)) < Float.parseFloat(condition.get(2))){
                    rows.add(i);
                }
            }
            return rows;
        }
        if(condition.get(1).equals(">=")){
            for(int i=1; i<selectedColumn.columnContents.size(); i++){
                if(Float.parseFloat(selectedColumn.columnContents.get(i)) >= Float.parseFloat(condition.get(2))){
                    rows.add(i);
                }
            }
            return rows;
        }
        if(condition.get(1).equals("<=")){
            for(int i=1; i<selectedColumn.columnContents.size(); i++){
                if(Float.parseFloat(selectedColumn.columnContents.get(i)) <= Float.parseFloat(condition.get(2))){
                    rows.add(i);
                }
            }
            return rows;
        }
        throw new CommandTypeException(current);
    }

    public void generateConditionTree(){
        int row = 0;
        while(allCondition[row] != null){
            col = 0;
            for(Node node:allCondition[row]){
                if(node.isOperator()){
                    //link next level
                    node.setLeft(allCondition[row+1].get(col));
                    col++;
                    node.setRight(allCondition[row+1].get(col));
                    col++;
                }
            }
            row++;
        }
    }

    //combine and & or condition
    public List<Integer> combineCondition(){
        for(int i=getMaxDepthOfCondition(); i >=0; i--){
            for(Node node:allCondition[i]){
                if(node.isAnd()){
                    node.setRows(andCondition(node.left.rows, node.right.rows));
                }
                else if(node.isOr()){
                    node.setRows(orCondition(node.left.rows, node.right.rows));
                }
            }
        }
        return allCondition[0].get(0).rows;
    }

    //For AND condition
    public List<Integer> andCondition(List<Integer> left, List<Integer> right){
        left.retainAll(right);
        return left;
    }
    //For OR condition
    public List<Integer> orCondition(List<Integer> left, List<Integer> right){
        right.removeAll(left);
        left.addAll(right);
        return left;
    }

    public int getMaxDepthOfCondition(){
        int row = 0;
        while(allCondition[row] != null){
            row++;
        }
        row--;
        return row;
    }

    public List<Integer> getRowToPrint() {
        return rowToPrint;
    }

    //is a pure number string
    public boolean isNum(String cmd){
        for (int i=0; i < cmd.length(); i++){
            if (!Character.isDigit(cmd.charAt(i))){
                return false;
            }
        }
        return true;
    }
}
