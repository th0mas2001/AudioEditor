package at.jku.audioeditor.ui;

import at.jku.audioeditor.player.AudioPlayer;
import at.jku.audioeditor.source.FileAudioSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User    : sfeiler
 * Date    : 15/05/2025
 * Project : Audio_Editor
 */
public class MainController {
    final Stage mainStage;
    ListView<String> audioListView;
    private ObservableList<String> audioList = FXCollections.observableArrayList();
    AudioPlayer activeAudioPlayer;

    @FXML
    public Button loadAudioButton;

    @FXML
    public Button playButton;

    @FXML
    public Button pauseButton;

    @FXML
    public VBox audioItemsVBox;

    public MainController(Stage mainStage) {
        this.audioListView = new ListView<>();
        this.mainStage = mainStage;
        init();
    }

    public void init() {
        loadAudioButton = new Button("Load Audio");
        audioItemsVBox = new VBox();
        playButton = new Button("Play");
        pauseButton = new Button("Pause");
    }

    @FXML
    public void playAudio() {
        activeAudioPlayer.play();
    }

    @FXML
    public void pauseAudio() {
        activeAudioPlayer.pause();
    }

    @FXML
    public void loadAudioFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(mainStage);
        if(selectedFile != null) {
            FileAudioSource fileAudioSource = new FileAudioSource(selectedFile);
            AudioPlayer audioPlayer = new AudioPlayer(fileAudioSource);
            audioListView.getItems().add(audioPlayer.getAudioSource().audioId());
            Button button = new Button();
            button.setText(audioPlayer.getAudioSource().audioId());
            button.setAlignment(Pos.CENTER_LEFT);
            ImageView imageView = new ImageView();
            imageView.setFitHeight(25);
            imageView.setFitWidth(25);
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);
            imageView.setImage(new Image(MainController.class.getResource("/at/jku/audioeditor/icon/audio-file.png").toExternalForm()));

            button.setPadding(new Insets(7, 7, 7, 7));
            audioItemsVBox.getChildren().add(button);
            button.setOnMouseClicked((mouseEvent) -> {
                activeAudioPlayer = audioPlayer;
            });
        }
    }
}
