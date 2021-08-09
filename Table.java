import CommandExceptions.*;
import java.io.*;
import java.util.*;

public class Table {
    String tableName;
    List<Column> tableContents = new ArrayList<Column>();

    public Table(String tableName){
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void addColumn(String columnName){
        Column newColumn = new Column(columnName);
        tableContents.add(newColumn);
    }

    public void addColumn(){
        Column newColumn = new Column();
        tableContents.add(newColumn);
    }

    public Column getColumnByName(String columnName) throws CommandException {
        for(Column column:tableContents) {
            if(column.getColumnName().equals(columnName)){
                return column;
            }
        }
        throw new NotExistException();
    }

    public Column getColumnByIndex(int index){
        return tableContents.get(index);
    }


    public int getColumnIndexByName(String columnName) throws CommandException {
        for(Column column:tableContents) {
            if(column.getColumnName().equals(columnName)){
                return tableContents.indexOf(column);
            }
        }
        throw new NotExistException();
    }

    public boolean deleteColumnByName(String columnName){
        for(Column column:tableContents){
            if(column.getColumnName().equals(columnName)){
                tableContents.remove(column);
                return true;
            }
        }
        return false;
    }

    public int getColumnNumber(){
        return tableContents.size();
    }

    public int getRowNumber(){
        return tableContents.get(0).columnContents.size();
    }

    public void printTable(){
        //Print contents
        for(int i=0; i<tableContents.get(0).columnContents.size(); i++){
            for(Column column:tableContents){
                System.out.print(column.columnContents.get(i)+"\t");
            }
            System.out.print("\n");
        }
    }

    public void setContentsByIndex(int row, int col, String contents){
        tableContents.get(col).setColumnContent(row, contents);
    }

    public Table readTable(String tablePath, String tableName) throws IOException {
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
