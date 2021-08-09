package CommandExceptions;

public class ValueListException extends CommandException{
    public ValueListException(int current){
        super();
    }

    public String toString(){
        return this.getClass().getName() + "Problem at: " + super.getCurrent();
    }
}
