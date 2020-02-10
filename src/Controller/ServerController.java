package Controller;

import Model.ModelController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController {

    private Socket aSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ServerSocket serverSocket;
    private ExecutorService pool;


    public ServerController() {
        try {
            serverSocket = new ServerSocket(9806);
            pool = Executors.newCachedThreadPool();
            runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runServer() {

        try {

            aSocket = serverSocket.accept();
            outputStream = new ObjectOutputStream(aSocket.getOutputStream());
            inputStream = new ObjectInputStream(aSocket.getInputStream());

            ModelController theModel = new ModelController(outputStream, inputStream);
            pool.execute(theModel); // two players for one thread

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {

        new ServerController();
    }
}
