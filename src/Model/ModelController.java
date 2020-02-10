package Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ModelController implements Runnable {

    private ClientHelper clientHelper;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    public DBModel dbModel;

    public ModelController(ObjectOutputStream outputStream, ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        setupTheDB();
    }

    public void setupTheDB() {
        dbModel = new DBModel();

        // Save - make a call for update and add
        //1. Requestnumber from client = 1 , if ( clientID == null) create and save
        // else update
        // serveReponse == 1 refresh list for all clients
        // Delete - make a call to delete
        // requestNumber = 2, delete and serverResponse == 1 refresh all clients
        // search - make a call for search by client id name and type
        // requestNumber 31 for CLient ID - serverResponse 1
        // requestNumber 32 for Last Name - serverResponse 1
        // requestNumber 33 for Client TYpe - serverResponse 1

    }

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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

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
