package foerster.project.molecule_window;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

public class molecule_WindowView {
    // Class to organize the user interface

    // Attributes
    private final Parent root;
    private final foerster.project.molecule_window.molecule_WindowController controller;

    // Constructor
    public molecule_WindowView() throws IOException {
        /**
         * Window View Class to load FXML file
         */
        try(var ins= Objects.requireNonNull(getClass().getResource("Molecule_Window.fxml")).openStream()) {
            // load FXML file
            var fxmlLoader=new FXMLLoader();

            fxmlLoader.load(ins);

            controller=fxmlLoader.getController();

            root=fxmlLoader.getRoot();

        }
    }

    // Methods
    public Parent getRoot() {
        return root;
    }

    public molecule_WindowController getController() {
        return controller;
    }
}