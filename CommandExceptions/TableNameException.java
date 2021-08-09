package CommandExceptions;

public class TableNameException extends CommandException{
    public TableNameException(int current){
        super();
    }

    public String toString(){
        return this.getClass().getName() + "Problem at: " + super.getCurrent();
    }
}
