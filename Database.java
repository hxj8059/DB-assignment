import CommandExceptions.*;

import java.io.*;
import java.util.*;
public class Database {
    String databaseName;
    List<Table> databaseContents = new ArrayList<Table>();

    public Database() { }

    public Database(String databaseName){
        this.databaseName = databaseName;
    }

    public boolean addTable(String tableName){
        for(Table table:databaseContents){
            if(tableName.equals(table.getTableName())){
                return false;
            }
        }
        Table newTable = new Table(tableName);
        databaseContents.add(newTable);
        return true;
    }

    public Table getTable(String tableName) throws CommandException {
        for(Table i:databaseContents){
            if(i.getTableName().equals(tableName)){
                return i;
            }
        }
        throw new NotExistException();
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void syncDatabaseToFile() throws IOException, CommandException{
        //Delete database if name is null namely database is dropped
        if(databaseName == null) {
            if(DBServer.currentDatabaseName == null) throw new NotExistException();
            File databasePath = new File(DBServer.currentDatabaseName);
            if(databasePath.isDirectory()){
                if (databasePath.delete()) {
                } else {
                    File[] folderContents = databasePath.listFiles();
                    for(File table:folderContents){
                        table.delete();
                    }
                    if(databasePath.delete()){
                        System.out.println("database delete successfully");
                    }
                }
            }
            return;
        }
        File databasePath = new File(databaseName);
        if(!databasePath.isDirectory()){
            databasePath.mkdir();
        }
        //Delete table if database contents are cleared
        if(databaseContents == null && DBServer.currentTableName != null) {
            File currentTable = new File(DBServer.currentPath);
            if(currentTable.isFile()){
                if(currentTable.delete()){
                    System.out.println("table delete successfully");
                }
                else{
                    System.out.println("table delete failed");
                }
            }
            return;
        }
        for(Table table:databaseContents){
             String tablePath = databaseName + File.separator + table.getTableName();
             File currentTable = new File(tablePath);
             if(!currentTable.isFile()){
                 currentTable.createNewFile();
             }
             //rewriter not required
             writeTable(table);
        }
    }

    public void writeTable(Table table) throws IOException{
        String filePath = databaseName+File.separator+table.getTableName();
        File fileToWrite = new File(filePath);
        FileWriter writer = new FileWriter(fileToWrite);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        for(int i=0; i < table.tableContents.get(0).columnContents.size(); i++){
            for(Column column:table.tableContents){
                bufferedWriter.write(column.getColumnContent(i)+"\t");
            }
            bufferedWriter.write("\n");
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public void loadDatabase(String loadDatabaseName, String loadTableName) throws IOException{
        String tablePath = loadDatabaseName+File.separator+loadTableName;
        this.setDatabaseName(loadDatabaseName);
        this.databaseContents.add(readTable(tablePath, loadTableName));
    }


    public Table readTable(String tablePath, String tableName) throws IOException{
        Table newTable = new Table(tableName);
        File tableToRead = new File(tablePath);
        FileReader reader = new FileReader(tableToRead);
        BufferedReader buffReader = new BufferedReader(reader);
        String columnNameLine = buffReader.readLine();
        //first line
        String[] columnNames = columnNameLine.split("\t");
        for(String columnName:columnNames){
            newTable.addColumn(columnName);
        }
        String newLine;
        while((newLine = buffReader.readLine()) != null){
            String[] contents = newLine.split("\t");
            for(int i=0; i<contents.length; i++){
                newTable.tableContents.get(i).addColumnContent(contents[i]);
            }
        }
        buffReader.close();
        return newTable;
    }
}
