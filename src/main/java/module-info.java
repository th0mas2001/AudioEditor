module at.jku.audioeditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires static lombok;
    requires slf4j.api;

    opens at.jku.audioeditor to javafx.fxml;
    exports at.jku.audioeditor;
}