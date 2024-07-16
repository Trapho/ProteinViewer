module advJava{
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    //requires java.json;


    exports foerster.project to javafx.graphics;
    exports foerster.project.window;
    exports foerster.project.molecule_window;

    opens foerster.project.window to javafx.fxml;
    opens foerster.project.molecule_window to javafx.fxml;

}