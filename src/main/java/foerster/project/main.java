package foerster.project;

import foerster.project.model.*;
import foerster.project.window.*;
import foerster.project.molecule_window.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


/*
Advanced Java SS 2023
Project
by  Raphael Förster
*/


public class main extends Application {
    // App to Display a user-defined Tree as described in the assignment task
    @Override
    public void start(Stage stage) throws IOException{
        var view = new WindowView();

        var model = new PdbComplex();


        stage.setScene(new Scene(view.getRoot(),800,700));
        var presenter = new WindowPresenter(stage, view, model);

        stage.setTitle("Project Foerster");
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}