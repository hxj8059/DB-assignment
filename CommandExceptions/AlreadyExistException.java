package CommandExceptions;

public class AlreadyExistException extends CommandException{
    int currentCMD;
    public AlreadyExistException(int current){
        currentCMD = current;
    }

    public String toString(){
        return this.getClass().getName() + "Problem at: "+currentCMD;
    }
}
