import java.io.*;
import java.util.*;
import CommandExceptions.*;

public class Insert extends Command{
    List<String> newContents;

    public Insert(List command, BufferedWriter socketWriter){
        super(command, socketWriter);
    }

    /*Parse the INSERT command by firstly generate a String list of new contents
    and then add the contents to the new database*/
    public boolean parse() throws IOException, CommandException{
        if(command.get(current).equals("INTO")){
            current++;
            isTableName(command.get(current));
            if(command.get(current).equals("VALUES")){
                current++;
                if(command.get(current).equals("(")){
                    current++;
                    newContents = new ArrayList<String>();
                    //Add id
                    newContents.add(Integer.toString(newDatabase.getTable(DBServer.currentTableName).getRowNumber()));
                    if(isValueList(command.get(current))){
                        updateContents();
                        return true;
                    }
                }
            }
        }
        throw new CommandTypeException(current);
    }

    public boolean isTableName(String cmd) throws IOException, CommandException{
        if(!cmd.matches("\\W")){
            DBServer.currentTableName = cmd;
            newDatabase = new Database(DBServer.currentDatabaseName);
            newDatabase.loadDatabase(DBServer.currentDatabaseName, DBServer.currentTableName);
            current++;
            return true;
        }
        throw new TableNameException(current);

    }

    public boolean isValueList(String cmd) throws IOException, CommandException{
        isValue(cmd);
        current++;
        if(command.get(current).equals(")")) return true;
        if(command.get(current).equals(",")){
            current++;
            isValueList(command.get(current));
            return true;
        }
        throw new ValueListException(current);
    }

    public boolean isValue(String cmd) throws IOException, CommandException{
        //For String
        if(cmd.equals("'")){
            String temp;
            current++;
            temp = command.get(current);
            current++;
            while(!command.get(current).equals("'")){
                temp = temp +" "+ command.get(current);
                current++;
            }
            newContents.add(temp);
            return true;

        }
        else if(cmd.equals("true") || cmd.equals("false")){
            newContents.add(cmd);
            return true;
        }
        else if(!cmd.matches("[^\\d\\.]")){
            newContents.add(cmd);
            return true;
        }
        //Not supported value
        throw new CommandTypeException(current);
    }

    public void updateContents() throws IOException, CommandException{
        //Invalid value number
        if(newContents.size() != newDatabase.getTable(DBServer.currentTableName).getColumnNumber()){
            throw new CommandTypeException(current);
        }
        for(int i=0; i<newContents.size(); i++){
            newDatabase.getTable(DBServer.currentTableName).tableContents.get(i).addColumnContent(newContents.get(i));
        }
    }
}
