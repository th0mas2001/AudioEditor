module com.example.audioeditor {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.audioeditor to javafx.fxml;
    exports com.example.audioeditor;
}