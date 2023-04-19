/**
 * Pour les GUI et l'initialisation, je me suis inspire du site web oracle et ses libreries
 */
package client_fx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import server.models.Course;
import java.io.IOException;


public class client_fx extends Application {

    private TableView<Course> tabCours;
    private TextField[] entrees;
    private ChoiceBox choisirSession;

    // @param args 

    public static void main(String[] args) {
        launch(args);
    }

    // Fenêtre en GUI gauche et droite
    
    public void start(Stage primaryStage) {

        HBox root = new HBox(5);
        root.setMaxHeight(480);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 720, 480);

        Text texteListeCours = new Text("Liste des cours");
        texteListeCours.setFont(Font.font(20));

        HBox centrerTexteLC = new HBox();
        centrerTexteLC.setPadding(new Insets(10, 0, 10, 0));
        centrerTexteLC.setAlignment(Pos.CENTER);
        centrerTexteLC.getChildren().add(texteListeCours);

        tabCours = new TableView<>();
        tabCours.setMinWidth(320);
        TableColumn colonneCode = new TableColumn("Code");
        colonneCode.setMinWidth(100);
        colonneCode.setCellValueFactory(new PropertyValueFactory<Course, String>("code"));
        TableColumn colonneNomCours = new TableColumn("Cours");
        colonneNomCours.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
        colonneNomCours.setMinWidth(220);
        tabCours.getColumns().addAll(colonneCode, colonneNomCours);

        Button charger = new Button("Charger");

        choisirSession = new ChoiceBox();
        choisirSession.setMinWidth(100);
        choisirSession.getItems().addAll("Automne", "Hiver", "Ete");

        HBox centrerBoutChargement = new HBox(60);
        centrerBoutChargement.setAlignment(Pos.CENTER);
        centrerBoutChargement.getChildren().addAll(choisirSession, charger);

        VBox zoneBoutChargement = new VBox();
        zoneBoutChargement.setAlignment(Pos.CENTER);
        zoneBoutChargement.setPadding(new Insets(20, 0, 20, 0));
        zoneBoutChargement.setBackground(new Background(new BackgroundFill(Color.rgb(235,235,225), CornerRadii.EMPTY, Insets.EMPTY)));
        zoneBoutChargement.getChildren().add(centrerBoutChargement);

        VBox zoneListeCours = new VBox();
        zoneListeCours.setPadding(new Insets(0, 15, 10, 15));
        zoneListeCours.setBackground(new Background(new BackgroundFill(Color.rgb(235,235,225), CornerRadii.EMPTY, Insets.EMPTY)));
        zoneListeCours.getChildren().addAll(centrerTexteLC, tabCours);


        VBox guiGauche = new VBox(5);
        guiGauche.setMaxHeight(480);
        guiGauche.getChildren().addAll(zoneListeCours, zoneBoutChargement);

        Text texteInscription = new Text("Formulaire d'inscription");
        texteInscription.setFont(Font.font(20));

        Text[] texteEntrees = new Text[4];
        String[] etiquetteTE = {"Prénom", "Nom", "Courriel", "Matricule"};
        entrees = new TextField[4];
        for (int i = 0; i < texteEntrees.length; i++) {
            texteEntrees[i] = new Text(etiquetteTE[i]);
            texteEntrees[i].setFont(Font.font(15));
            entrees[i] = new TextField();
            entrees[i].setMinSize(180,30);
        }

        Button envoyer = new Button("envoyer");
        envoyer.setMinWidth(65);

        HBox centrerTexteIns = new HBox();
        centrerTexteIns.setAlignment(Pos.CENTER);
        centrerTexteIns.setPadding(new Insets(8, 0, 35, 0));
        centrerTexteIns.getChildren().add(texteInscription);

        HBox centrerBoutEnv = new HBox();
        centrerBoutEnv.setAlignment(Pos.CENTER);
        centrerBoutEnv.setPadding(new Insets(15, 0, 0, 0));
        centrerBoutEnv.getChildren().add(envoyer);

        VBox groupeTexteEntree = new VBox(20);
        groupeTexteEntree.setPadding(new Insets(5, 0, 0, 0));
        groupeTexteEntree.getChildren().addAll(texteEntrees[0], texteEntrees[1], texteEntrees[2], texteEntrees[3]);

        VBox groupeEntree = new VBox(10);
        groupeEntree.getChildren().addAll(entrees[0], entrees[1], entrees[2], entrees[3], centrerBoutEnv);

        HBox zoneInscription = new HBox(20);
        zoneInscription.setAlignment(Pos.CENTER);
        zoneInscription.setPadding(new Insets(0, 50, 0, 50));
        zoneInscription.getChildren().addAll(groupeTexteEntree, groupeEntree);

        VBox guiDroite = new VBox();
        guiDroite.setMaxHeight(460);
        guiDroite.setBackground(new Background(new BackgroundFill(Color.rgb(164,164,157), CornerRadii.EMPTY, Insets.EMPTY)));
        guiDroite.getChildren().addAll(centrerTexteIns, zoneInscription);

        root.getChildren().addAll(guiGauche, guiDroite);

        primaryStage.setTitle("Inscription UdeM");
        primaryStage.setScene(scene);
        primaryStage.show();

        charger.setOnAction(actionEvent -> {
            try{
                reinitialiserBordure();
                tabCours.setItems(controler.charger());
            } catch (IOException e){
                alerter("Erreur", "Échec au chargement\n " +
                        "Le serveur doit être ouvert tout en possédant le ficher cours.txt pour que le chargement ait lieu!");
            } catch (ClassNotFoundException e) {
               e.printStackTrace();
            } catch (NullPointerException e) {
                bordureErreur(choisirSession);
                alerter("Erreur", "Échec au chargement\n" +
                        "La session du cours n'a pas encore été choisi");
            }
        });


        tabCours.setOnMouseClicked((mouseEvent -> {
            controler.selectionnerUnCours(mouseEvent.getPickResult().getIntersectedNode().toString());
        }));

        envoyer.setOnAction(actionEvent -> {
            String prenom = entrees[0].getText();
            String nom = entrees[1].getText();
            String courriel = entrees[2].getText();
            String matricule = entrees[3].getText();
            try {
                reinitialiserBordure();
                controler.connecter();
                controler.inscrire(prenom, nom, courriel, matricule);
                alerter("Succès", "Felicitations! " + nom + " " + prenom +
                        " est inscrit(e) avec succès pour le cours de " + controler.getCoursChoisi().getCode());
                for (int i = 0; i < entrees.length; i++){
                    entrees[i].clear();
                }
            } catch (IOException e) {
                alerter("Erreur", "Erreur: Le serveur doit être ouvert pour que le chargement ait lieu!");
            } catch (NullPointerException e) {
                bordureErreur(tabCours);
                alerter("Erreur", "Vous n'avez pas choisi de cours!");
            } catch (IllegalArgumentException e){
                switch (e.getMessage()){
                    case "Matricule et courriel invalide":
                        bordureErreur(entrees[2]);
                        bordureErreur(entrees[3]);
                        alerter("Erreur", "Le formulaire est invalide\n" +
                                "Le Champ \"Email\" est invalide\n" +
                                "Le champ \"Matricule\" est invalide");
                        break;
                    case "Courriel invalide":
                        bordureErreur(entrees[2]);
                        alerter("Erreur", "Le formulaire est invalide\n" +
                                "Le Champ \"Email\" est invalide\n");
                        break;
                    default:
                        bordureErreur(entrees[3]);
                        alerter("Erreur", "Le formulaire est invalide\n" +
                                "Le champ \"Matricule\" est invalide");
                        break;
                }
            }
        });
    }

    // les alertes pour les erreurs

    public void alerter(String type, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        switch (type) {
            case "Succès":
                alert = new Alert(Alert.AlertType.INFORMATION);
                break;
            case "Erreur":
                alert = new Alert(Alert.AlertType.ERROR);
                break;
        }
        alert.setContentText(message);
        alert.showAndWait();
    }
}