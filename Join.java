import java.io.*;
import java.util.*;
import CommandExceptions.*;

public class Join extends Command{
    Table firstTable = new Table("firstTable");
    int firstIndex;
    Table secondTable = new Table("secondTable");
    int secondIndex;
    Table jointTableToPrint = new Table("jointTable");
    List<Integer> firstRowToJoin = new ArrayList<>();
    List<Integer> secondRowToJoin = new ArrayList<>();

    public Join(List command, BufferedWriter socketWriter) {
        super(command, socketWriter);
    }

    /*parse is the main method for the class, it generates two RowToJoins for the new Table
    and then generate a joint table for the Parse class to print for client*/
    public boolean parse() throws CommandException, IOException{
        if(DBServer.currentDatabaseName == null) throw new NotExistException();
        isFirstTableName();
        current++;
        if(command.get(current).equals("AND")){
            current++;
            isSecondTableName();
            current++;
            if(command.get(current).equals("ON")){
                current++;
                isFirstAttributeName();
                current++;
                if(command.get(current).equals("AND")){
                    current++;
                    isSecondAttributeName();
                    generateRowToJoin();

                }
                generateJoinTable();
                return true;
            }
        }
        throw new CommandTypeException(current);
    }

    public boolean isFirstTableName() throws IOException{
        String currentTablePath = DBServer.currentDatabaseName+File.separator+command.get(current);
        firstTable = firstTable.readTable(currentTablePath, command.get(current));
        return true;
    }

    public boolean isSecondTableName() throws IOException{
        String currentTablePath = DBServer.currentDatabaseName+File.separator+command.get(current);
        secondTable = secondTable.readTable(currentTablePath, command.get(current));
        return true;
    }

    public void isFirstAttributeName() throws CommandException{
        firstIndex = firstTable.getColumnIndexByName(command.get(current));
    }

    public void isSecondAttributeName() throws CommandException{
        secondIndex = secondTable.getColumnIndexByName(command.get(current));
    }

    public void generateRowToJoin(){
        Column firstColumn = firstTable.tableContents.get(firstIndex);
        Column secondColumn = secondTable.tableContents.get(secondIndex);
        //If same add to two arraylists for two tables
        for(int i=0; i<firstColumn.columnContents.size(); i++){
            for(int j=0; j<secondColumn.columnContents.size(); j++){
                if(firstColumn.columnContents.get(i).equals(secondColumn.columnContents.get(j))){
                    firstRowToJoin.add(i);
                    secondRowToJoin.add(j);
                }
            }
        }
        return;
    }

    public void generateJoinTable(){
        int col;
        jointTableToPrint.addColumn("id");
        //Column name for joint table
        String table1Name = firstTable.getTableName()+".";
        String table2Name = secondTable.getTableName()+".";
        for(col =1; col<firstTable.tableContents.size(); col++){
            jointTableToPrint.addColumn(table1Name+firstTable.tableContents.get(col).getColumnName());
        }
        for(col =1; col<secondTable.tableContents.size(); col++){
            jointTableToPrint.addColumn(table2Name+secondTable.tableContents.get(col).getColumnName());
        }
        //Contents
        for(int i=0; i < firstRowToJoin.size(); i++){
            //id contents
            jointTableToPrint.getColumnByIndex(0).addColumnContent(Integer.toString(i+1));
            for(col =1; col<firstTable.tableContents.size(); col++){
                String content = firstTable.getColumnByIndex(col).getColumnContent(firstRowToJoin.get(i));
                jointTableToPrint.getColumnByIndex(col).addColumnContent(content);
            }
            for(col =1; col<secondTable.tableContents.size(); col++){
                String content = secondTable.getColumnByIndex(col).getColumnContent(secondRowToJoin.get(i));
                //Second table contents - add first table size
                jointTableToPrint.getColumnByIndex(col+firstTable.tableContents.size()-1).addColumnContent(content);
            }
        }
    }

}
