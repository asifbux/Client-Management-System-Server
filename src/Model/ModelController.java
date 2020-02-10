package Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ModelController implements Runnable {

    private ClientHelper clientHelper;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public ModelController(ObjectOutputStream outputStream, ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        this.outputStream = outputStream;
        this.inputStream = inputStream;

        try {
            clientHelper = (ClientHelper) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        setupTheDB();
    }

    public void setupTheDB() {

    }


    @Override
    public void run() {

    }
}
