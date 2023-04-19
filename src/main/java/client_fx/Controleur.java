package client_fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.models.Course;
import server.models.RegistrationForm;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class Controleur {

    private Socket client;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String session;
    private ArrayList<Course> listeCoursEntree;
    private Course coursChoisi;
    private RegistrationForm formulaireInscription;

    // veritification des donnees rentrees comme le matricule ou la forme du courriel. Si erreur - alerte au etudiant que c'est invalide

    public void inscrire(String prenom, String nom, String courriel, String matricule) throws IOException, NullPointerException, IllegalArgumentException{

        boolean erreurMatricule = (matricule.length() != 6);
        boolean erreurCourriel = (courriel.indexOf("@") <= 0) || (courriel.indexOf("@") > courriel.lastIndexOf("."));

        if (coursChoisi == null) {
            deconnecter();
            throw new NullPointerException("Aucun cours choisi");
        } else if (erreurCourriel & erreurMatricule) {
            deconnecter();
            throw new IllegalArgumentException("Matricule et courriel invalide");
        }
        else if (erreurMatricule) {
            deconnecter();
            throw new IllegalArgumentException("Matricule invalide");
        }
        else if (erreurCourriel) {
            deconnecter();
            throw new IllegalArgumentException("Courriel invalide");
        }
        else {
            formulaireInscription = new RegistrationForm(prenom, nom, courriel, matricule, coursChoisi);
            objectOutputStream.writeObject("INSCRIRE" + " " + session);
            objectOutputStream.flush();
            objectOutputStream.writeObject(formulaireInscription);
            objectOutputStream.flush();
        }
    }

    public void connecter() throws IOException {
        client = new Socket("127.0.0.1", 1337);
        objectOutputStream = new ObjectOutputStream(client.getOutputStream());
        objectInputStream = new ObjectInputStream(client.getInputStream());
    }
}
