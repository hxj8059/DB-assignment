import java.util.*;

public class Column {

    List<String> columnContents = new ArrayList<String>();

    public Column() {

    }

    public Column(String columnName){
        this.columnContents.add(columnName);
    }

    public String getColumnName() {
        return columnContents.get(0);
    }

    public void setColumnName(String columnName) {
        this.columnContents.set(0, columnName);
    }

    public void addColumnContent(String content){
        columnContents.add(content);
    }

    public void setColumnContent(int index, String content){
        columnContents.set(index, content);
    }

    public String getColumnContent(int index){
        return columnContents.get(index);
    }


    public int maxLengthOfContent(){
        int max = 0;
        for(String content:columnContents){
            if(content.length() > max){
                max = content.length();
            }
        }

        return max;
    }

}
