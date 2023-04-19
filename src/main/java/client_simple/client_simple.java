package client_simple;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import server.models.Course;
import server.models.RegistrationForm;

public class client_simple {
    public static void main(String[] args) {
        try {
            Socket client = new Socket("127.2.4.1", 1337); 
            boolean reconnection = false;

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());

            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");

            Scanner reader = new Scanner(System.in);

            while (true) {
                if (reconnection) { 
                    client = new Socket("127.2.4.1", 1337);
                    objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                    objectInputStream = new ObjectInputStream(client.getInputStream());
                }

                System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
                System.out.println("1. Automne");
                System.out.println("2. Hiver");
                System.out.println("3. Été");
                System.out.print("> Votre Choix: ");
                String session = "";
                switch (reader.nextInt()) {
                    case 1:
                        session += "Automne";
                        objectOutputStream.writeObject("CHARGER" + " " + session);
                        break;
                    case 2:
                        session += "Hiver";
                        objectOutputStream.writeObject("CHARGER" + " " + session);
                        break;
                    case 3:
                        session += "Été";
                        objectOutputStream.writeObject("CHARGER" + " " + session);
                        break;
                    default:
                        System.out.println("Votre choix ne correspond pas à celle d'une des sessions proposées.");
                        reconnection = false;
                        break;
                }

                if (!session.equals("")) {
                    objectOutputStream.flush();

                    System.out.println("Les cours offerts pendant la session d'" + session.toLowerCase() + " sont:");
                    ArrayList<Course> listeDesCoursOfferts = (ArrayList<Course>) objectInputStream.readObject(); 

                    for (int i = 0; i < listeDesCoursOfferts.size(); i++) { 
                        System.out.println((i + 1) + ". " + listeDesCoursOfferts.get(i).getCode() + " " + listeDesCoursOfferts.get(i).getName());
                    }

                    while (true) {
                        boolean sessionSelectionee = false;
                        System.out.println("> Choix:\n" +
                                "1. Consulter les cours offerts pour une autre session\n" +
                                "2. Inscription à un cours");
                        System.out.print("> Choix: ");
                        int choix = reader.nextInt();
                        System.out.println("");
                        switch (choix) {
                            case 1:
                                sessionSelectionee = true;
                                break;
                            case 2:
                                reader.nextLine(); 
                                System.out.print("Veuillez saisir votre prénom: ");
                                String prenom = reader.nextLine();
                                System.out.print("Veuillez saisir votre nom: ");
                                String nom = reader.nextLine();
                                System.out.print("Veuillez saisir votre email: ");
                                String email = reader.nextLine();
                                System.out.print("Veuillez saisir votre matricule: ");
                                String matricule = reader.nextLine();
                                System.out.print("Veuillez saisir le code du cours: ");
                                String code = reader.nextLine();

                                RegistrationForm infoEtudiant;
                                for (Course cours: listeDesCoursOfferts) {
                                    if (cours.getCode().equals(code)) {
                                        terminer = true;

                                        
                                        infoEtudiant = new RegistrationForm(prenom, nom, email, matricule, cours);

                                        client = new Socket("127.2.4.1", 1337); // Reconnection
                                        objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                                        objectInputStream = new ObjectInputStream(client.getInputStream());

                                        
                                        objectOutputStream.writeObject("INSCRIRE" + " " + session);
                                        objectOutputStream.flush();
                                        objectOutputStream.writeObject(infoEtudiant);
                                        objectOutputStream.flush();
                                        break;
                                    }
                                }

                                if (terminer) { 
                                    System.out.println("Félicitation! Inscription réussie de " + prenom + " au cours " + code.substring(0, 3) + "-" + code.substring(3));
                                    break;
                                } else {
                                    System.out.println("Échec à l'inscription du cours");
                                    reconnection = true; 
                                    break;
                                }

                            default:
                                System.out.println("Votre choix ne correspond pas à ceux qui ont été proposés. Veuillez ressayer");
                                reconnection = true; 
                                break;
                        }

                        if (sessionSelectionee || terminer) {
                            reconnection = true; 
                            break;
                        }
                    }
                }
                if (terminer) { 
                    break;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

