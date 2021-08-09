import java.io.*;
import java.util.*;
import CommandExceptions.*;

public class Command {
    List<String> command;
    BufferedWriter socketWriter;
    int current = 0;
    Database newDatabase;

    public Command(List command, BufferedWriter socketWriter){
        this.command = command;
        this.socketWriter = socketWriter;
        this.current = 1;
    }

    public int getCurrent(){ return current; }

    public Database getNewDatabase(){
        return newDatabase;
    }
}
