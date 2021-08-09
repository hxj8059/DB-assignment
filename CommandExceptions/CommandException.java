package CommandExceptions;

public class CommandException extends Exception{
    private int current;

    public CommandException()
    {
    }

    public CommandException(int current){
        this.current = current;
    }

    public int getCurrent() {
        return current;
    }

    public String toString(){
        return this.getClass().getName() + "Problem at: " + getCurrent();
    }
}
