package CommandExceptions;

public class CommandTypeException extends CommandException{
    public CommandTypeException(int current){
        super(current);
    }

    public String toString(){
        return this.getClass().getName() + "Problem at: " + getCurrent();
    }
}
