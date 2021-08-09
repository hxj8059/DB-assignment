import java.io.*;
import java.util.*;
import CommandExceptions.*;

public class Alter extends Command {

    public Alter(List command, BufferedWriter socketWriter){
        super(command,socketWriter);
    }

    /*Alter parse contains two situation, one is ADD and another is DROP*/
    public boolean parse() throws CommandException, IOException{
        //Alter add or drop table contents
        if(command.get(current).equals("TABLE")){
            current++;
            isTableName(command.get(current));
            current++;
            if(command.get(current).equals("ADD")){
                current++;
                isAddAttribute(command.get(current));
                return true;
            }
            else if(command.get(current).equals("DROP")){
                current++;
                isDropAttribute(command.get(current));
                return true;
            }
        }
        throw new CommandTypeException(current);
    }

    public boolean isTableName(String cmd) throws CommandException, IOException{
        DBServer.currentTableName = cmd;
        if(DBServer.currentDatabaseName == null){
            throw new NotExistException();
        }
        DBServer.currentPath = DBServer.currentDatabaseName + File.separator + cmd;
        newDatabase = new Database(DBServer.currentDatabaseName);
        newDatabase.loadDatabase(DBServer.currentDatabaseName, DBServer.currentTableName);
        return true;
    }

    public boolean isAddAttribute(String cmd) throws CommandException, IOException{
        newDatabase.getTable(DBServer.currentTableName).addColumn(cmd);
        //Add
        for(int i=0; i < newDatabase.getTable(DBServer.currentTableName).getRowNumber(); i++){
            newDatabase.getTable(DBServer.currentTableName).getColumnByName(cmd).addColumnContent(" ");
        }
        return true;
    }

    public boolean isDropAttribute(String cmd) throws CommandException, IOException{
        //Drop
        if(newDatabase.getTable(DBServer.currentTableName).deleteColumnByName(cmd)){
            return true;
        }
        throw new NotExistException();
    }
}
