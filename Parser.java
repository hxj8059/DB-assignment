import CommandExceptions.*;
import java.io.*;
import java.util.*;

public class Parser {
    List<String> command;
    int current = 0;
    String errorMessage = null;
    Database newDatabase;
    Table tableToPrint = null;
    BufferedWriter socketWriter;

    public Parser(List command, BufferedWriter socketWriter){
        this.command = command;
        this.socketWriter = socketWriter;
    }

    public boolean parse(BufferedWriter socketWriter) throws CommandException, IOException{
        try{
            isCommand(command.get(current), socketWriter);
            if(newDatabase != null) {
                newDatabase.syncDatabaseToFile();
            }
        }catch (Exception e){
            //Error
            System.out.println("Error occurred");
            socketWriter.write("[ERROR]: ");
            socketWriter.write(e.toString());
            socketWriter.write("\n" + ((char)4) + "\n");
            socketWriter.flush();
            e.printStackTrace();
            return false;
        }
        //OK, print table if tableToPrint is not null
        socketWriter.write("[OK]");
        if(tableToPrint != null) printTable();
        socketWriter.write("\n" + ((char)4) + "\n");
        socketWriter.flush();
        return true;
    }

    public boolean isCommand(String cmd, BufferedWriter socketWriter) throws CommandException, IOException{
        if(isCommandType(cmd, socketWriter)){
            current++;
            /*Missing ;*/
            if(current<command.size() && command.get(current).equals(";")) {
                return true;
            }
            throw new MissingSemiColonException(current);
        }
        throw new CommandException(current);
    }

    public boolean isCommandType(String cmd, BufferedWriter socketWriter) throws CommandException, IOException {
        if(cmd.equals("USE")) {
            current++;
            isDatabaseName(command.get(current), socketWriter);
            return true;
        }
        if(cmd.equals("CREATE")) {
            current++;
            if(isCreateDatabase(command.get(current), socketWriter)){
                return true;
            }
            if(isCreateTable(command.get(current), socketWriter)) return true;
            return false;
        }
        if(cmd.equals("DROP")) {
            Drop newDrop = new Drop(command, socketWriter);
            newDrop.parse();
            newDatabase = newDrop.getNewDatabase();
            current = newDrop.getCurrent();
            return true;
        }
        if(cmd.equals("ALTER")) {
            Alter newAlter = new Alter(command, socketWriter);
            newAlter.parse();
            newDatabase = newAlter.getNewDatabase();
            current = newAlter.getCurrent();
            return true;
        }
        if(cmd.equals("INSERT")) {
            Insert newInsert = new Insert(command, socketWriter);
            newInsert.parse();
            newDatabase = newInsert.getNewDatabase();
            current = newInsert.getCurrent();
            return true;
        }
        if(cmd.equals("SELECT")) {
            Select newSelect = new Select(command, socketWriter);
            newSelect.parse();
            newDatabase = newSelect.getNewDatabase();
            current = newSelect.getCurrent();
            tableToPrint = newSelect.tableToPrint;
            return true;
        }
        if(cmd.equals("UPDATE")) {
            Update newUpdate = new Update(command, socketWriter);
            newUpdate.parse();
            current = newUpdate.getCurrent();
            newDatabase = newUpdate.getNewDatabase();
            return true;
        }
        if(cmd.equals("DELETE")) {
            Delete newDelete = new Delete(command, socketWriter);
            newDelete.parse();
            current = newDelete.getCurrent();
            newDatabase = newDelete.getNewDatabase();
            return true;
        }
        if(cmd.equals("JOIN")) {
            Join newJoin = new Join(command, socketWriter);
            newJoin.parse();
            current = newJoin.getCurrent();
            tableToPrint = newJoin.jointTableToPrint;
            return true;
        }
        //If not equals to above command, error
        throw new CommandTypeException(current);
    }

    public boolean isCreateDatabase(String cmd, BufferedWriter socketWriter) throws CommandException, IOException{
        if(cmd.equals("DATABASE")){
            current++;
            DBServer.currentDatabaseName = command.get(current);
            File database = new File(DBServer.currentDatabaseName);
            //Already exist
            if(database.isDirectory()) {
                throw new AlreadyExistException(current);
            }
            //database.mkdir();
            newDatabase = new Database(DBServer.currentDatabaseName);
            DBServer.currentDatabaseName = null;
            return true;
        }
        return false;
    }
    public boolean isCreateTable(String cmd, BufferedWriter socketWriter) throws CommandException, IOException{
        if(cmd.equals("TABLE")){
            current++;
            DBServer.currentTableName = command.get(current);
            DBServer.currentPath = DBServer.currentDatabaseName + File.separator + DBServer.currentTableName;
            File tableToCreate = new File(DBServer.currentPath);
            //Create table in database
            newDatabase = new Database(DBServer.currentDatabaseName);
            if(tableToCreate.isFile()){
                throw new AlreadyExistException(current);
            }
            newDatabase.addTable(DBServer.currentTableName);
            newDatabase.getTable(DBServer.currentTableName).addColumn("id");
            //CREATE with Attributes
            current++;
            if(current == command.size()-1){
                current--;
                return true;
            }
            if(command.get(current).equals("(")){
                current++;
                isAttributeList(command.get(current), socketWriter);
                return true;
            }
        }
        return false;

    }
    public boolean isAttributeList(String cmd, BufferedWriter socketWriter) throws CommandException, IOException{
        isAttribute(cmd, socketWriter);
        current++;
        if(command.get(current).equals(")")) return true;
        if(command.get(current).equals(",")){
            current++;
            isAttributeList(command.get(current), socketWriter);
        }
        return true;
    }

    public boolean isAttribute(String cmd, BufferedWriter socketWriter) throws CommandException, IOException{
        if(command.get(current).matches("\\W")){
            throw new AttributeNameException(current);
        }
        newDatabase.getTable(DBServer.currentTableName).addColumn(command.get(current));
        return true;
    }

    public boolean isDatabaseName(String cmd, BufferedWriter socketWriter) throws CommandException, IOException{
        if(cmd.matches("^[a-z0-9A-Z]+$")){
            File databaseToUse = new File(cmd);
            //Not exist database
            if(!databaseToUse.isDirectory()) {
                throw new NotExistException();
            }
            DBServer.currentDatabaseName = command.get(current);
            newDatabase = new Database(DBServer.currentDatabaseName);
            return true;
        }
        //Not only words database name
        throw new CommandTypeException(current);
    }

    public void printTable() throws IOException{
        //Print contents
        String spaceNeeded;
        for(int i=0; i<tableToPrint.tableContents.get(0).columnContents.size(); i++){
            socketWriter.write("\n");
            for(Column column:tableToPrint.tableContents){
                spaceNeeded = " ";
                //For print beauty
                for(int j = column.columnContents.get(i).length(); j < column.maxLengthOfContent(); j++){
                    spaceNeeded = spaceNeeded + " ";
                }
                socketWriter.write(column.columnContents.get(i)+spaceNeeded+"\t");
            }
        }
    }
}



