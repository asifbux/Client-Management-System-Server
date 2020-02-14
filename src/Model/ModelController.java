package Model;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

/**
 * The type Model controller.
 */
public class ModelController implements Runnable {

    private ClientHelper clientHelper;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    /**
     * The Db model.
     */
    public DBModel dbModel;

    /**
     * Instantiates a new Model controller.
     *
     * @param outputStream the output stream
     * @param inputStream  the input stream
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    public ModelController(ObjectOutputStream outputStream, ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        setupTheDB();
    }

    /**
     * Sets the db.
     */
    public void setupTheDB() {
        dbModel = new DBModel();
    }

    /**
     * Run request.
     */
    public void runRequest() {
        while(true) {
            try {
                clientHelper = (ClientHelper) inputStream.readObject();
                if(clientHelper.getRequestNumber() == 31) {
                    clientHelper.setClientList(dbModel.getCliendRow("ID", clientHelper.getSearchParameter()));
                    clientHelper.setResponseNumber(1);
                    sendObject(clientHelper);
                } else if(clientHelper.getRequestNumber() == 32) {
                    clientHelper.setClientList(dbModel.getCliendRow("LastName", clientHelper.getSearchParameter()));
                    clientHelper.setResponseNumber(1);
                    sendObject(clientHelper);
                } else if(clientHelper.getRequestNumber() == 33) {
                    clientHelper.setClientList(dbModel.getCliendRow("ClientType", clientHelper.getSearchParameter()));
                    clientHelper.setResponseNumber(1);
                    sendObject(clientHelper);
                } else if(clientHelper.getRequestNumber() == 2) {
                    clientHelper.setClientList(dbModel.deleteClient(Integer.parseInt(clientHelper.getSearchParameter())));
                    clientHelper.setResponseNumber(1);
                    sendObject(clientHelper);
                }
                else if(clientHelper.getRequestNumber() == 1) {

                    if(clientHelper.getClientList().get(0).getId() != 0) {
                        boolean isUpdated = dbModel.updateClient(clientHelper.getClientList().get(0));
                        if(isUpdated) {
                            clientHelper.setClientList(dbModel.getClients());
                            clientHelper.setResponseNumber(3);
                            clientHelper.setSearchParameter("Client Information has been updated to the Database");
                            sendObject(clientHelper);
                        }
                    }
                    else {
                        boolean isAdded = dbModel.addClient(clientHelper.getClientList().get(0));
                        if(isAdded) {
                            clientHelper.setClientList(dbModel.getClients());
                            clientHelper.setResponseNumber(3);
                            clientHelper.setSearchParameter("Client has been added to the Database");
                            sendObject(clientHelper);
                        }
                    }
                }
            } catch (EOFException e) {
                closeStream();
            } catch (SocketException e){
                closeStream();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Close stream.
     */
    public void closeStream()  {
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Send object.
     *
     * @param aClientHelper the a client helper
     */
    public void sendObject(ClientHelper aClientHelper) {
        aClientHelper = new ClientHelper(aClientHelper.getResponseNumber(), aClientHelper.getRequestNumber(), aClientHelper.getSearchParameter(), aClientHelper.getClientList());
        try {
            this.outputStream.writeObject(aClientHelper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        runRequest();
    }
}
