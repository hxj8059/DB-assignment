package CommandExceptions;

public class AttributeNameException extends CommandException{
    public AttributeNameException(int current){
        super(current);
    }

    public String toString(){
        return this.getClass().getName() + "Problem at: " + super.getCurrent();
    }
}
