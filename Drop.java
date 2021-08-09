import java.io.*;
import java.util.*;
import CommandExceptions.*;

public class Drop extends Command{

    public Drop(List command, BufferedWriter socketWriter){
        super(command, socketWriter);
    }

    /*Drop database is to clear the newDatabase name and keep the DBServer.currentDatabaseName,
    * so that sync can delete the directory*/
    /*Drop table is to set newDatabase's content to null, and keep the static value in DBServer.
    * Sync is able to delete the file in the directory*/
    public boolean parse() throws CommandException{
        //Database or table
        if(command.get(current).equals("DATABASE")){
            current++;
            isDatabaseName(command.get(current));
            return true;
        }
        else if(command.get(current).equals("TABLE")){
            current++;
            isTableName(command.get(current));
            return true;
        }
        throw new CommandTypeException(current);
    }

    public boolean isDatabaseName(String cmd) throws CommandException{
        File databaseFolder = new File(cmd);
        if(databaseFolder.isDirectory()){
            DBServer.currentDatabaseName = cmd;
            DBServer.currentTableName = null;
            newDatabase = new Database();
            return true;
        }
        throw new NotExistException();
    }

    public boolean isTableName(String cmd) throws CommandException{
        DBServer.currentTableName = cmd;
        DBServer.currentPath = DBServer.currentDatabaseName + File.separator + cmd;
        File tableFile = new File(DBServer.currentPath);
        if(tableFile.isFile()){
            newDatabase = new Database(DBServer.currentDatabaseName);
            newDatabase.databaseContents = null;
            return true;
        }
        throw new NotExistException();
    }
}
