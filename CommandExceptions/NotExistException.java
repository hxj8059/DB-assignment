package CommandExceptions;

public class NotExistException extends CommandException{


    public NotExistException(){
    }

    public String toString(){
        return this.getClass().getName();
    }
}
