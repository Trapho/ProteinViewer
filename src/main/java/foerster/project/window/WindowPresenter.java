package foerster.project.window;

import foerster.project.model.PdbComplex;
import foerster.project.model.PdbContacter;
import foerster.project.model.PdbParser;
import foerster.project.model.PdbPolymer;
import foerster.project.molecule_window.molecule_WindowPresenter;
import foerster.project.molecule_window.molecule_WindowView;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
// org.json.JSONArray;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The presenter class for the main window of the application.
 */
public class WindowPresenter {

    /**
     * Constructs a new WindowPresenter object.
     *
     * @param stage The main stage of the application.
     * @param view  The view associated with this presenter.
     * @param model The model of the application.
     * @throws IOException If an I/O error occurs.
     */
    public WindowPresenter(Stage stage, WindowView view, PdbComplex model) throws IOException {

        // initilize the program

        var controller=view.getController();
        // css style
        stage.getScene().getStylesheets().add(WindowPresenter.class.getResource("style.css").toExternalForm());

        //1.) setup the Resolution Option
        controller.getChoiceBoxResolution().getItems().addAll("Low", "Mid", "High");
        controller.getChoiceBoxResolution().setValue("High");
        //2.) Setup the List
        controller.getLabelPdb().setText("");
        ArrayList<String> allPdbEntries;
        try {
            allPdbEntries = PdbContacter.contact(new String[0]);
        }catch (Exception e){
            allPdbEntries = new ArrayList<>();
        }
        ObservableList<String> allPdbEntriesObs = FXCollections.observableArrayList();
        for ( int i = 0; i< allPdbEntries.size(); i++) {
            allPdbEntriesObs.add(allPdbEntries.get(i).toString());
            //controller.getPdbList().getItems().add(allPdbEntries.get(i).toString());
        }
        //3.) Implement the Pdb LIst
        FilteredList<String> allPdbEntriesFilter = new FilteredList<>(allPdbEntriesObs, s-> true);
        controller.getSearchField().textProperty().addListener( obs -> {
            String filter = controller.getSearchField().getText().toUpperCase();
            if(filter == null || filter.length() == 0) {
                allPdbEntriesFilter.setPredicate(s -> true);
            }
            else {
                allPdbEntriesFilter.setPredicate(s -> s.contains(filter));
            }
        });
        controller.getPdbList().setItems(allPdbEntriesFilter);

        //Implement the menuItems
        //1.) Open

        var service_openPdbLocal = new Service<String>(){
            private File file;
            public void setFile(File file){this.file = file;}
            @Override
            protected Task<String> createTask(){
                return new PdbContacter.OpenEntry(this.file);
            }
        };
        controller.getMenuOpen().setOnAction(e -> {
            try {
                var chooser = new FileChooser();
                var file = chooser.showOpenDialog(stage);
                if (file != null & (file.toString().endsWith(".pdb") || file.toString().endsWith("pdb.txt"))) {
                    controller.getPdbEntry().clear();
                    controller.getLabelPdb().setText(file.toString());
                    controller.getPdbEntry().setText("Loading ...");
                    service_openPdbLocal.setFile(file);
                    service_openPdbLocal.restart();
                    controller.getPdbList().setDisable(true);
                }
            }catch (Exception t){

            }
        });
        service_openPdbLocal.setOnSucceeded(e -> {
            controller.getPdbEntry().clear();
            controller.getPdbList().setDisable(false);
            controller.getPdbEntry().setText(service_openPdbLocal.getValue());
        });
        service_openPdbLocal.setOnFailed(e -> {
            controller.getPdbEntry().clear();
            controller.getPdbEntry().setDisable(false);
            controller.getPdbEntry().setText(service_openPdbLocal.getMessage());
        });

        //2.) Close Menu Button
        controller.getMenuClose().setOnAction(e -> {
            Platform.exit();
        });

        //3.) export menu button
        controller.getMenuExport().setOnAction(e-> {
            FileChooser export = new FileChooser();
            export.setInitialDirectory(new File(System.getProperty("user.home")));

            export.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDB Files (*.pdb)","*.pdb"));
            File file = export.showSaveDialog(stage);
            if (file != null){
                try{
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(controller.getPdbEntry().getText());
                    fileWriter.close();
                }catch (IOException e2){
                    e2.printStackTrace();
                }
            }
        });
        //4.) dark mode
        controller.getMenuDarkMode().setSelected(false);
        var styleURL = WindowPresenter.class.getResource("modena_dark.css");
        controller.getMenuDarkMode().selectedProperty().addListener((v, o, n) -> {
            if (controller.getMenuDarkMode().isSelected()){
                stage.getScene().getStylesheets().add(styleURL.toExternalForm());
            }else{
                stage.getScene().getStylesheets().remove(styleURL.toExternalForm());
            }
        });

        //5.) ClearMenu
        controller.getMenuItemClear().setOnAction(e -> {
            controller.getPdbEntry().clear();
            controller.getLabelPdb().setText("");
        });

        //6.) AboutButton
        controller.getMenuItemAbout().setOnAction(e -> {
            Text AboutMessage = new Text("To run the program. Select a PDB file on the left panel or open a \n " +
                    "local panel with the MenuItem open. As soon as the Pdb file is \n" +
                    "visible in the text field press show 3D, what opens a new window");

            StackPane secondaryLayout = new StackPane();
            secondaryLayout.getChildren().add(AboutMessage);

            Scene secondScene = new Scene(secondaryLayout, 600, 100);

            Stage newWindow = new Stage();
            newWindow.setTitle("About");
            newWindow.setScene(secondScene);

            newWindow.show();
        });

        // implement clear button
        controller.getSearchButton().setOnAction(e -> {
            controller.getPdbEntry().clear();
            controller.getLabelPdb().setText("");
        });

        // implement the text field showing the pdb file in text format
        // contacting the server takes a while, so here we will work with service
        var service_showPdbEntry = new Service<String>(){
            private String pdb;
            public void setPdb(String pdb){this.pdb = pdb;}
            @Override
            protected Task<String> createTask(){
                return new PdbContacter.GetEntry(this.pdb);
            }
        };


        controller.getPdbList().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null) {
                    controller.getLabelPdb().setText(newValue);
                    controller.getPdbEntry().clear();
                    service_showPdbEntry.setPdb(newValue);
                    service_showPdbEntry.restart();
                    controller.getPdbList().setDisable(true);
                }

            }
        });
        service_showPdbEntry.setOnSucceeded(v->{
            controller.getPdbEntry().setText(service_showPdbEntry.getValue());
            controller.getPdbList().setDisable(false);
        });
        service_showPdbEntry.setOnFailed(v ->{
            controller.getPdbList().setDisable(false);
            controller.getPdbEntry().setText("Ups! Something went wrong. Error: \n" + service_showPdbEntry.getException());
        });

        controller.getSearchField().disableProperty().bindBidirectional(controller.getPdbList().disableProperty());



        // implement the show 3D button. we want to open a new window, this should be
        // similar to assignemnt 05

        controller.getViewButton().disableProperty().bind(Bindings.createBooleanBinding(
                () -> controller.getPdbEntry().getText().isEmpty() || controller.getPdbEntry().getText().startsWith("Ups!"),
                controller.getPdbEntry().textProperty()
        ));

        controller.getViewButton().setOnAction(e -> {
            // set Quality value for the divisions in the next window
            int Quality = 64;
            String QualityString = controller.getChoiceBoxResolution().getValue();
            if (QualityString == "Low") Quality = 10;
            if (QualityString == "Mid") Quality = 30;
            if (QualityString == "High") Quality = 64;


            try {
                var molecule_view = new molecule_WindowView();
                Stage newWindow = new Stage();
                newWindow.setScene(new Scene(molecule_view.getRoot(),1100,750));
                newWindow.setTitle("Project Foerster");
                // first reset the old model then fill it anew
                model.resetModel();

                PdbParser.parse(controller.getLabelPdb().getText(),controller.getPdbEntry().getText(), model);

                // First check if we have a Protein structure at all
                if (model.getPolymers().get(0).getAllAtoms().size() != 0){
                    // if the model contains more polymers we want to choose one
                    if (model.getPolymers().size() > 1) {
                        // new GridPane
                        GridPane grid = new GridPane();
                        grid.setPadding(new Insets(10, 10, 10, 10));
                        grid.setVgap(1);
                        grid.setHgap(1);
                        // add label to gridpane
                        Label modelselectionLabel = new Label("This PDB file contains several model. \n Which one do you want to select? \n" +
                                "Enter a number between 1 and " + String.valueOf(model.getPolymers().size()) + "\n (For multiple models x1-x2)");
                        GridPane.setConstraints(modelselectionLabel, 0, 0);
                        grid.getChildren().add(modelselectionLabel);
                        final TextField label = new TextField();
                        label.setPromptText("Which model?");

                        GridPane.setConstraints(label, 0, 1);
                        grid.getChildren().add(label);
                        // add buttons
                        Button submit = new Button("OK");
                        GridPane.setConstraints(submit, 0, 2);
                        grid.getChildren().add(submit);
                        Button cancel = new Button("Cancel");
                        GridPane.setConstraints(cancel, 1, 2);
                        grid.getChildren().add(cancel);
                        Scene secondScene = new Scene(grid, 300, 200);
                        // New window (Stage) to show the new dialog
                        Stage modelSelectionWindow = new Stage();
                        modelSelectionWindow.setTitle("Choose model");
                        modelSelectionWindow.setScene(secondScene);
                        // Set position of second window, related to primary window.
                        modelSelectionWindow.setX(stage.getX() + 200);
                        modelSelectionWindow.setY(stage.getY() + 100);
                        modelSelectionWindow.show();
                        // Set methods for buttons
                        // cancel --> closes window
                        cancel.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                modelSelectionWindow.close();
                            }
                        });
                        int finalQuality = Quality;

                        //implement the submit button: this scenario only happens if we have multiple model
                        // Two cases are possible: we select only one model. or multiple models (1-30)
                        submit.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                try {

                                    if (label.getText().contains("-")) {
                                        String[] label_split = label.getText().strip().split("-");
                                        int from = Integer.parseInt(label_split[0]);
                                        int to = Integer.parseInt(label_split[1]);
                                        model.createSubModel(from, to);
                                        var molecule_presenter = new molecule_WindowPresenter(newWindow, molecule_view, model, finalQuality);
                                        newWindow.show();
                                        modelSelectionWindow.close();
                                    } else {
                                        int labelOutput = Integer.parseInt(label.getText());
                                        model.createSubModel(labelOutput, labelOutput);
                                        var molecule_presenter = new molecule_WindowPresenter(newWindow, molecule_view, model, finalQuality);
                                        newWindow.show();
                                        modelSelectionWindow.close();
                                    }
                                } catch (Exception e) {
                                    label.clear();
                                    label.setPromptText("invalid model");
                                }
                            }
                        });

                    } else {
                        var molecule_presenter = new molecule_WindowPresenter(newWindow, molecule_view, model, Quality);
                        newWindow.show();
                    }

                    // show a info box about removed atoms
                    Label infoAboutRemovedAtomsLabel = new Label(Integer.toString(model.getPolymers().get(0).getNumberOfRemovedAtoms()) +
                            " Atoms where removed because they did not belong to Amino Acids. \n" +
                            Integer.toString(model.getPolymers().get(0).getNumberOfErrors()) + " Atoms could not be parsed (for the first model).\n ");
                    GridPane.setConstraints(infoAboutRemovedAtomsLabel, 0, 0);
                    Button infoCloseButton = new Button("Close");
                    GridPane.setConstraints(infoCloseButton, 0, 2);
                    GridPane infoAboutRemovedAtomsLayout = new GridPane();

                    infoAboutRemovedAtomsLayout.getChildren().add(infoAboutRemovedAtomsLabel);
                    infoAboutRemovedAtomsLayout.getChildren().add(infoCloseButton);
                    Scene infoAboutRemovedAtomsScene = new Scene(infoAboutRemovedAtomsLayout, 400, 100);
                    Stage infoWindow = new Stage();
                    infoWindow.setTitle("Removed Atoms for " + model.getPdbID());
                    infoWindow.setScene(infoAboutRemovedAtomsScene);
                    infoCloseButton.setOnAction(ex -> {
                        infoWindow.close();
                    });

                    infoWindow.show();
                }else{
                    controller.getPdbEntry().clear();
                    controller.getPdbEntry().setText("UPS! The loaded PDB file did not contain any Amino Acids." +
                            "\n (Or not a single line was parsed correctly. Check the PDB file for potential errors.)");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }


        });

    }
}
