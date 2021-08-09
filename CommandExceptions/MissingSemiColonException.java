package CommandExceptions;

public class MissingSemiColonException extends CommandException{
    public MissingSemiColonException(int current){
        super(current);
    }

    public String toString(){
        return this.getClass().getName() + "Problem at: " + "end";
    }
}
