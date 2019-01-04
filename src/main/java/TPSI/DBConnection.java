package TPSI;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class DBConnection {


    private static Datastore datastore;
    private static String DATABASE_NAME = "gradeManagerDB";
    private static String HOSTNAME = "localhost";
    private static int PORT = 8004;
    private static DBConnection ourInstance;

    private DBConnection() {
        MongoClient mongoClient = new MongoClient(HOSTNAME, PORT);
        Morphia morphia = new Morphia();
        morphia.mapPackage("TPSI.model");
        datastore = morphia.createDatastore(mongoClient, DATABASE_NAME);
    }

    public static DBConnection getInstance() {
        if(ourInstance == null)
            ourInstance = new DBConnection();

        return ourInstance;
    }

    public Datastore getDatastore() {
        return datastore;
    }

}
