package Controller;

import Model.ModelController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Server controller.
 */
public class ServerController {

    private Socket aSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ServerSocket serverSocket;
    private ExecutorService pool;


    /**
     * Instantiates a new Server controller.
     */
    public ServerController() {
        try {
            serverSocket = new ServerSocket(9806);
            pool = Executors.newFixedThreadPool(10);
            runServer();
        } catch (IOException e) {
            System.err.println("Could not listen to port");
            System.exit(-1);
        }
    }

    /**
     * Run server.
     */
    public void runServer() {

        try {
            while(true) {
                aSocket = serverSocket.accept();
                outputStream = new ObjectOutputStream(aSocket.getOutputStream());
                inputStream = new ObjectInputStream(aSocket.getInputStream());

                ModelController theModel = new ModelController(outputStream, inputStream);
                pool.execute(theModel);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main.
     *
     * @param args the args
     */
    public static void main(String args[]) {

        new ServerController();
    }
}
