package foerster.project.window;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.util.Objects;

public class WindowView {
    // Class to organize the user interface

    // Attributes
    private final Parent root;
    private final WindowController controller;

    // Constructor
    public WindowView() throws IOException {
        /**
         * Window View Class to load FXML file
         */
        try(var ins= Objects.requireNonNull(getClass().getResource("Window.fxml")).openStream()) {
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

    public WindowController getController() {
        return controller;
    }
}