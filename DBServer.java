import java.io.*;
import java.net.*;
import java.util.*;
import CommandExceptions.*;

class DBServer
{
    static String currentPath;
    static String currentTableName;
    static String currentDatabaseName;

    public DBServer(int portNumber) throws CommandException
    {
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server Listening");
            while(true) processNextConnection(serverSocket);
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void processNextConnection(ServerSocket serverSocket) throws CommandException
    {
        try {
            Socket socket = serverSocket.accept();
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Connection Established");
            while(true) processNextCommand(socketReader, socketWriter);
        } catch(IOException ioe) {
            System.err.println(ioe);
        } catch(NullPointerException npe) {
            System.out.println("Connection Lost");
            npe.printStackTrace();
        }
    }

    private void processNextCommand(BufferedReader socketReader, BufferedWriter socketWriter) throws IOException, NullPointerException, CommandException
    {
        String incomingCommand = socketReader.readLine();
        System.out.println("Received message: " + incomingCommand);
        List<String> command = tokenizer(incomingCommand);
        Parser testParser = new Parser(command, socketWriter);
        testParser.parse(socketWriter);
        /*TestWriter testWriter = new TestWriter(socketWriter);
        testWriter.writeToClient();*/
        /*socketWriter.write("[OK] Thanks for your message: " + incomingCommand);
        socketWriter.write("\n" + ((char)4) + "\n");
        socketWriter.flush();*/
    }

    public static void main(String args[]) throws CommandException
    {
        DBServer server = new DBServer(8888);
    }

    public List tokenizer(String incomingCommand){
        String regex = "==|>=|>|<=|!=|<|\\(|\\)|\\;|\\'|\\,";
        List<String> command = new ArrayList<>();
        //Replace operators with space+operator+space
        incomingCommand = incomingCommand.replaceAll(regex, " $0 ");
        StringTokenizer token = new StringTokenizer(incomingCommand, " ");
        while(token.hasMoreTokens()){
            command.add(token.nextToken());
        }
        return command;
    }
}
