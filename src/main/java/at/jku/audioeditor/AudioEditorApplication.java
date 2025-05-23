package at.jku.audioeditor;

import at.jku.audioeditor.source.FileAudioSource;
import at.jku.audioeditor.player.AudioPlayer;
import at.jku.audioeditor.ui.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioEditorApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AudioEditorApplication.class.getResource("view/hello-view.fxml"));
        init(fxmlLoader, stage);
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle("Audio-Visuelles-Multimediastudio");
        stage.setScene(scene);
        stage.show();
        List<String> list = new ArrayList<>();
    }

    public void init(FXMLLoader loader, Stage stage) {
        MainController mainController = new MainController(stage);
        loader.setControllerFactory((c) -> mainController);
    }

    public static void main(String[] args) {
        //test();
        launch();
    }

    private static void test() {

        Runnable runnable1 = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                FileAudioSource audioSource = new FileAudioSource("C:\\Sonstiges\\Ausbildung\\JKU\\Informatik\\Semester_2\\Multimediasysteme\\Audio_Editor\\AudioEditor\\src\\main\\resources\\testaudio.wav");
                AudioPlayer audioPlayer = new AudioPlayer(audioSource);
                audioPlayer.play();
                Thread.sleep(4000);
                audioPlayer.pause();
                Thread.sleep(5000);
                audioPlayer.play();
            }
        };

        Runnable runnable2 = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                FileAudioSource audioSource = new FileAudioSource("C:\\Sonstiges\\Ausbildung\\JKU\\Informatik\\Semester_2\\Multimediasysteme\\Audio_Editor\\AudioEditor\\src\\main\\resources\\testaudio2.wav");
                AudioPlayer audioPlayer = new AudioPlayer(audioSource);
                audioPlayer.play();
            }
        };

        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);

        thread1.start();
        thread2.start();
    }
}