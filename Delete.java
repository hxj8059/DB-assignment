import java.io.*;
import java.util.*;
import CommandExceptions.*;

public class Delete extends Command{
    List<Integer> rowToDelete = new ArrayList<>();


    public Delete(List command, BufferedWriter socketWriter){
        super(command, socketWriter);
    }

    /*Delete uses Condition class to process condition, and it finally generate rowToDelete
    * to delete rows*/
    public boolean parse() throws CommandException, IOException{
        if(DBServer.currentDatabaseName == null) throw new NotExistException();
        if(command.get(current).equals("FROM")){
            current++;
            isTableName();
            current++;
            if(command.get(current).equals("WHERE")){
                Condition newCondition = new Condition(command, socketWriter, newDatabase);
                newCondition.parse();
                current = newCondition.getCurrent();
                rowToDelete = newCondition.getRowToPrint();
                //delete first column name for updating;
                rowToDelete.remove(0);
                deleteNewDatabase();
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

    public void deleteNewDatabase() throws CommandException{
        //i for counting multiple rows to delete
        int i = 0;
        for(int row:rowToDelete){
            for(Column col:newDatabase.getTable(DBServer.currentTableName).tableContents){
                col.columnContents.remove(row-i);
            }
            i++;
        }
        return;
    }
}
