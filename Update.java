import java.io.*;
import java.util.*;
import CommandExceptions.*;

public class Update extends Command{
    List<Integer> columnToUpdate = new ArrayList<>();
    List<Integer> rowToUpdate = new ArrayList<>();
    List<String> contentsToUpdate = new ArrayList<>();

    public Update(List command, BufferedWriter socketWriter){
        super(command, socketWriter);
    }

    /*parse method firstly returns columnToUpdate and rowToUpdate, and then update the contents
    * of row and column*/
    public boolean parse() throws CommandException, IOException{
        if(DBServer.currentDatabaseName == null) throw new NotExistException();
        isTableName();
        current++;
        if(command.get(current).equals("SET")){
            current++;
            isNameValueList();
            if(command.get(current).equals("WHERE")){
                Condition newCondition = new Condition(command, socketWriter, newDatabase);
                newCondition.parse();
                current = newCondition.getCurrent();
                rowToUpdate = newCondition.getRowToPrint();
                //delete first column name for updating;
                rowToUpdate.remove(0);
                updateDatabase();
                return true;
            }
        }
        throw new CommandTypeException(current);
    }

    public boolean isTableName() throws IOException{
        //Initialise database based on file
        DBServer.currentTableName = command.get(current);
        newDatabase = new Database(DBServer.currentDatabaseName);
        newDatabase.loadDatabase(DBServer.currentDatabaseName, DBServer.currentTableName);
        return true;
    }

    public boolean isNameValueList() throws CommandException{
        isNameValuePair();
        current++;
        if(command.get(current).equals(",")){
            current++;
            isNameValueList();
        }
        return true;
    }

    public boolean isNameValuePair() throws CommandException{
        //AttributeName
        int col = newDatabase.getTable(DBServer.currentTableName).getColumnIndexByName(command.get(current));
        columnToUpdate.add(col);
        current++;
        if(command.get(current).equals("=")){
            current++;
            //String
            if(command.get(current).equals("'")){
                current++;
                String temp;
                temp = command.get(current);
                current++;
                while(!command.get(current).equals("'")){
                    temp = temp +" "+ command.get(current);
                    current++;
                }
                contentsToUpdate.add(temp);
                return true;
            }
            //Others
            contentsToUpdate.add(command.get(current));
            return true;
        }
        throw new CommandTypeException(current);
    }

    //update newDatabase
    public void updateDatabase() throws CommandException{
        for(int row:rowToUpdate){
            for(int col=0; col<columnToUpdate.size(); col++){
                newDatabase.getTable(DBServer.currentTableName).setContentsByIndex(row, columnToUpdate.get(col), contentsToUpdate.get(col));
            }
        }
    }
}
