package sample;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable, Runnable {

    @FXML
    private ComboBox<Integer> comboHod;

    @FXML
    private ComboBox<Integer> comboMin;

    @FXML
    private ComboBox<Integer> comboSek;

    @FXML
    private AnchorPane panelOdpoctu;

    @FXML
    private AnchorPane panelNastaveni;

    @FXML
    private Label popisekHod;

    @FXML
    private Label popisekMin;

    @FXML
    private Label popisekSek;

    private Thread t;
    private boolean bezi = false;
    private int sekundy;
    private boolean pauza = false;
    Object monitor = new Object();
    MediaPlayer mp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Integer> hodinyList = FXCollections.observableArrayList();
        ObservableList<Integer> MinutyASekundyList = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i++) {
            if (i >= 0 && i <= 24) hodinyList.add(i);
            MinutyASekundyList.add(i);
        }
        comboHod.setItems(hodinyList);
        comboHod.setValue(0);
        comboMin.setItems(MinutyASekundyList);
        comboMin.setValue(0);
        comboSek.setItems(MinutyASekundyList);
        comboSek.setValue(0);
    }

    public void zapnutiCasovace(ActionEvent actionEvent) {
        animaceNahoru();
        t = new Thread(this);
        t.setDaemon(true);
        bezi = true;
        nacteniCasu();
        t.start();
    }

    private void nacteniCasu() {
        sekundy = comboHod.getValue() * 3600 + comboMin.getValue() * 60 + comboSek.getValue();

    }

    public void animaceNahoru() {
        TranslateTransition tt1 = new TranslateTransition();
        tt1.setDuration(Duration.millis(800));
        tt1.setToX(0);
        tt1.setToY(-246);
        tt1.setNode(panelNastaveni);
        tt1.play();
    }

    public void animaceDolu() {
        TranslateTransition tt1 = new TranslateTransition();
        tt1.setDuration(Duration.millis(800));
        tt1.setToX(0);
        tt1.setToY(0);
        tt1.setNode(panelNastaveni);
        tt1.play();
    }

    public void zastaveniCasovace(ActionEvent actionEvent) {
        animaceDolu();
        bezi = false;
        pauza = false;
        /*synchronized (monitor){
            monitor.notify();
            pauza = false;
        }*/
    }

    @Override
    public void run() {
        //pocitat cas ve vlakne
        long cas = System.currentTimeMillis() + 1000; //aktuální cas + 1 vterina
        while (bezi) {
            //synchronized (monitor){
                /*if(pauza) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                    }
                }*/
            if (System.currentTimeMillis() > cas && !pauza) { //ubehla 1sekundy
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        aktualizujCas();
                    }
                });
                if (sekundy == 0) {
                    Media bell = null;
                    bell = new Media(getClass().getResource("bell.mp3").toString());
                    mp = new MediaPlayer(bell);
                    mp.setVolume(1);
                    mp.play();
                    bezi = false;

                } else sekundy--;
                cas = System.currentTimeMillis() + 1000;

                // }
            }
        }
    }


            private void aktualizujCas (){
            short hod = (short) (sekundy / 3600);
            popisekHod.setText((hod < 10) ? "0" + hod : "" + hod);

            short min = (short) ((sekundy % 3600) / 60);
            popisekMin.setText((min < 10) ? "0" + min : "" + min);

            short sek = (short) (sekundy % 60);
            popisekSek.setText((sek < 10) ? "0" + sek : "" + sek);

        }

            public void pauzaCasovace(ActionEvent actionEvent){
                pauza = !pauza;
        /*if(!pauza) pauza = true;
        else{
            synchronized (monitor){
                monitor.notify();
                pauza = false;
            }
        }*/

            }

            public void resetCasovace (ActionEvent actionEvent){
                nacteniCasu();
            }

}

