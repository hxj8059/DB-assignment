import java.io.*;
import java.util.*;
import CommandExceptions.*;

public class Select extends Command{
    Table tableToPrint = new Table("Select");
    List<Integer> columnToPrint = new ArrayList<>();
    List<Integer> rowToPrint = new ArrayList<>();

    public Select(List command, BufferedWriter socketWriter){
        super(command, socketWriter);
    }

    /*parse for the class firstly generate columnToPrint, then it use Condition class
    * to process conditions which can generate rowToPrint. Finally combine columnToPrint
    * and rowToPrint, we can generate the tableToPrint*/
    public boolean parse() throws IOException, CommandException{
        //Find table name of the command
        if(DBServer.currentDatabaseName == null) throw new NotExistException();
        if(!command.get((command.indexOf("FROM")+1)).matches("\\W")){
            DBServer.currentTableName = command.get((command.indexOf("FROM")+1));
        }
        newDatabase = new Database(DBServer.currentDatabaseName);
        newDatabase.loadDatabase(DBServer.currentDatabaseName, DBServer.currentTableName);
        if(command.get(current).equals("*")){
            for(int i=0; i<newDatabase.databaseContents.get(0).getColumnNumber(); i++){
                columnToPrint.add(i);
            }
            current++;
        }
        else {
            isAttributeList(command.get(current));
        }
        if(command.get(current).equals("FROM")){
            current++;
            //Reach the end of the command
            if((current+2) >= command.size()) {
                //Add first row for column name;
                for(int i=0; i<newDatabase.getTable(DBServer.currentTableName).getRowNumber(); i++){
                    rowToPrint.add(i);
                }
                generateTableToPrint();
                return true;
            }
            current++;
            if(command.get(current).equals("WHERE")){
                //Conditions
                Condition newCondition = new Condition(command, socketWriter, newDatabase);
                newCondition.parse();
                current = newCondition.getCurrent();
                rowToPrint = newCondition.getRowToPrint();
                generateTableToPrint();
                return true;
            }
            throw new CommandTypeException(current);
        }
        throw new CommandTypeException(current);
    }

    public void generateTableToPrint() throws IOException, CommandException{
        int i=0;
        //Add row and column
        for(int col:columnToPrint){
            tableToPrint.addColumn();
            for(int row:rowToPrint){
                String content = newDatabase.getTable(DBServer.currentTableName).tableContents.get(col).getColumnContent(row);
                tableToPrint.tableContents.get(i).addColumnContent(content);
            }
            i++;
        }
    }

    public boolean isAttributeList(String cmd) throws IOException, CommandException{
        isAttributeName(command.get(current));
        current++;
        if(command.get(current).equals(",")){
            current++;
            isAttributeList(command.get(current));
        }
        return true;
    }

    public boolean isAttributeName(String cmd) throws IOException, CommandException{
        columnToPrint.add(newDatabase.getTable(DBServer.currentTableName).getColumnIndexByName(cmd));
        System.out.println(columnToPrint.toString());
        return true;
    }
}
