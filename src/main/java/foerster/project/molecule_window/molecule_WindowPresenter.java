package foerster.project.molecule_window;

import foerster.project.model.PdbComplex;
import foerster.project.model.PdbMonomer;
import foerster.project.model.PdbPolymer;
import foerster.project.model.PdbResidue;
import foerster.project.window.WindowPresenter;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import foerster.project.UndoRedo.*;

/**
 * The molecule_WindowPresenter class manages the presentation and interaction logic of the molecule window.
 * It handles rendering the molecule, handling user input, implementing animations, and managing undo/redo operations.
 */
public class molecule_WindowPresenter {

    // Global parameters needed for certain functionalities
    double xPrev;   // needed for rotation
    double yPrev;   // needed for rotation
    double dx;      // Distance of the current mouse drag
    double dy;      // Distance of the current mouse drag
    boolean spinning = false;   // is the molecule currently spinning?
    Timeline endless_rotation = new Timeline();

    /**
     * Starts the endless rotation animation for the given figure.
     * The animation continuously rotates the figure based on the mouse movement.
     *
     * @param figure The JavaFX Group representing the figure to rotate
     */

    private void endlessRotation(Group figure){
        this.endless_rotation = new Timeline(new KeyFrame(Duration.millis(15), e -> {
            final var orthogonalAxis = new Point3D(dy, -dx, 0);
            var rotate = new Rotate(0.25 * orthogonalAxis.magnitude(), orthogonalAxis);
            var oldTrafo = figure.getTransforms().get(0);
            var newTrafo = rotate.createConcatenation(oldTrafo);
            figure.getTransforms().set(0, newTrafo);
        }));
    }

    /**
     * Updates the opacity of a collection of Shape3D objects to the given opacity value.
     *
     * @param list    The collection of Shape3D objects
     * @param opacity The opacity value to set (0.0 to 1.0)
     */
    public static <T> void updateOpacity(Collection<? extends Shape3D> list, double opacity) {
        for (var shape : list) {
            var color = ((PhongMaterial) shape.getMaterial()).getDiffuseColor();
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
            ((PhongMaterial) shape.getMaterial()).setDiffuseColor(color);
        }
    }

    /**
     * Initializes the text field in the molecule window's controller with the sequence and secondary structure data.
     *
     * @param controller    The molecule window controller
     * @param model         The PdbComplex model containing the sequence and secondary structure data
     * @param atomSelection The selection model for atoms
     * @param res2Label     The mapping of residues to labels
     */
    public static void initializeTextfield(molecule_WindowController controller, PdbComplex model,
                                           SelectionModel<PdbResidue> atomSelection, Map<PdbResidue, Label> res2Label){
        // implement text field containing sequence and sec struc

        controller.getSequenceList().getSelectionModel().setSelectionMode(new NoSelectionModel<Label>().getSelectionMode());
        for (Pair<PdbResidue, String> resAndString : model.getCurrentPolymer().getSequenceandSecStruc()) {
            Label a = new Label(resAndString.getValue());
            a.setFont(Font.font("System", FontWeight.BOLD,12));
            res2Label.put(resAndString.getKey(), a);
            if (resAndString.getKey() != null) {
                a.setOnMouseClicked(e -> {
                    boolean isClickedAlreadySele = atomSelection.isSelected(resAndString.getKey());
                    if (!e.isShiftDown())
                        atomSelection.clearSelection();
                    if (isClickedAlreadySele)
                        atomSelection.clearSelection(resAndString.getKey());
                    else
                        atomSelection.select(resAndString.getKey());
                });
            }else if(resAndString.getValue().startsWith("<")){  // in this case a new chain begins. So we want to implement the scrollTofunction
                //add scroll-to button
                Button addChain2 = new Button(resAndString.getValue());
                //first fill the button with live
                addChain2.setOnAction(e -> {
                    controller.getSequenceList().scrollTo(a);
                });
                CustomMenuItem addChainItem2 = new CustomMenuItem(addChain2);
                controller.getMenuButtonScrollTo().getItems().add(addChainItem2);

            }
            controller.getSequenceList().getItems().add(a);

        }
        //controller.getSequenceList().setMouseTransparent( true );
        controller.getSequenceList().setFocusTraversable( false );
    }

    /**
     * Creates a new instance of molecule_WindowPresenter.
     *
     * @param stage The Stage for the molecule window
     * @param view  The molecule_WindowView for the molecule window
     * @param model The PdbComplex model representing the molecule
     * @param Quality The quality of the molecule rendering
     */
    public molecule_WindowPresenter(Stage stage, molecule_WindowView view, PdbComplex model, int Quality) {

        model.setCurrentPolymer(0); // initialy always show the first

        //initialize everything important
        var controller = view.getController();
        //style
        stage.getScene().getStylesheets().add(WindowPresenter.class.getResource("style.css").toExternalForm());
        // Set Label of the current PDB ID
        controller.getLabelPDBName().setText(model.getPdbID());
        // PDB Entry
        controller.getTextFieldPdbEntry().setText(model.getPdbEntry());
        // BarChart1
        controller.getPaneGraphs().getChildren().add(Charts.computeBarCharts(model.getCurrentPolymer()));
        //BarChart 2
        controller.getPaneSecStrucPie().getChildren().add(Charts.computeBarChartsSecStruc(model.getCurrentPolymer()));
        //update Number of AA and atoms
        controller.getLabelNumberAtoms().setText(Integer.toString(model.getCurrentPolymer().getAllAtoms().size()));
        controller.getLabelNumberAA().setText(Integer.toString(model.getCurrentPolymer().getAllResidues().size()));

        // setup the Undo Manager
        UndoRedoManager undoManager = new UndoRedoManager();
        controller.getButtonUndo().setOnAction(e-> { undoManager.undo();});
        controller.getButtonUndo().textProperty().bind(undoManager.undoLabelProperty());
        controller.getButtonUndo().disableProperty().bind(undoManager.canUndoProperty().not());

        controller.getButtonRedo().setOnAction(e->{undoManager.redo();});
        controller.getButtonRedo().textProperty().bind(undoManager.redoLabelProperty());
        controller.getButtonRedo().disableProperty().bind(undoManager.canRedoProperty().not());

        // initialize the maps which map atoms to spheres and residues to spheres
        Map<PdbResidue, List<coolSpheres>> atom2Sphere = new HashMap<>();
        Map<PdbResidue, Label> res2Label = new HashMap<>(); // we need this for the listener

        // implement selection model
        SelectionModel<PdbResidue> atomSelection = new AtomSelectionModel();

        // Setup Textfield containing the AA-sequence and the SecStruc -Seq
        initializeTextfield(controller, model, atomSelection, res2Label);
        //Setup Groups for balls, sticks and meshes
        Group balls = new Group();
        Group sticks = new Group();
        Group meshes = new Group();
        Group figure = new Group();
        // Compute balls and sticks for the initial model
        MoleculeFigure.compute(model.getCurrentPolymer(), balls, sticks, meshes, atomSelection, atom2Sphere, controller, undoManager, Quality);
        // Show the atoms and bonds
        figure.getChildren().add(balls);
        figure.getChildren().add(sticks);
        figure.getChildren().add(meshes);

        //set up the model selection:
        int model_counter = 0;
        for(PdbPolymer polymer: model.getPolymers()){
            controller.getModelChoiceBox().getItems().add(model_counter++);
        }
        controller.getModelChoiceBox().setValue(0);
        controller.getModelChoiceBox().getSelectionModel().selectedItemProperty().addListener((observed, oldV, newV)-> {
            /**
             * When a new model is selected each sphere, cylinder mesh is calculated a new and the old ones are removed.
             * Also the SequenceList must be computed anew, so the selection model can be setup correctly
             * Set all available options to their initial value
             */
                undoManager.clear();
                model.setCurrentPolymer(newV);
                atom2Sphere.clear();
                res2Label.clear();
                atomSelection.clearSelection();
                controller.getSequenceList().getItems().clear();
                controller.getMenuButtonScrollTo().getItems().clear();
                initializeTextfield(controller, model, atomSelection, res2Label);
                balls.getChildren().clear();
                sticks.getChildren().clear();
                meshes.getChildren().clear();
                controller.getSticksSlider().setValue(0.5);
                controller.getBallsSlider().setValue(1);
                controller.getColorMenu().setValue("Atom Types"); // set default value
            controller.getLabelNumberAtoms().setText(Integer.toString(model.getCurrentPolymer().getAllAtoms().size()));
            controller.getLabelNumberAA().setText(Integer.toString(model.getCurrentPolymer().getAllResidues().size()));

                MoleculeFigure.compute(model.getCurrentPolymer(), balls, sticks, meshes, atomSelection, atom2Sphere, controller, undoManager, Quality);

        });



        atomSelection.getSelectedItems().addListener((SetChangeListener<? super PdbResidue>) c -> {
            /**
             * Listener of the selection model. object can be added by clicking on the atom (implemented in the MoleculeFigure class)
             * and by clicking on the labels of the SequenceList (implemented in the method initializeTextfield)
             * When a atom is added all others are set to a lower opacity. in the SequenceList the AA are just colored and decolored
             * when selected/deselected
             */
                        if (c.wasAdded()) {
                            Platform.runLater(() -> {
                                //first set color of sequence
                                res2Label.get(c.getElementAdded()).setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                                if (atomSelection.getSelectedItems().size() == 1) { // previously nothing selecte
                                    updateOpacity(atom2Sphere.values().stream().flatMap(Collection::stream).collect(Collectors.toList()), 0.2);
                                }
                                updateOpacity(atom2Sphere.get(c.getElementAdded()), 1.0);
                            });
                        } else if (c.wasRemoved()) {
                            Platform.runLater(() -> {
                                res2Label.get(c.getElementRemoved()).setBackground(new Background(new BackgroundFill(null, CornerRadii.EMPTY, Insets.EMPTY)));

                                if (atomSelection.getSelectedItems().size() > 0) {
                                    updateOpacity(atom2Sphere.get(c.getElementRemoved()), 0.2);
                                }
                                else
                                    updateOpacity(atom2Sphere.values().stream().flatMap(Collection::stream).collect(Collectors.toList()), 1.0);
                            });
                        }
                }
        );

        // setup camera
        var camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(100000);
        camera.setTranslateZ(-20000);
        camera.setTranslateY(500);

        // setup subscene
        controller.getMainPain().getChildren().removeAll(figure);
        var subScene = new SubScene(figure, 1100, 650, true, SceneAntialiasing.BALANCED);
        subScene.widthProperty().bind(stage.widthProperty());
        subScene.heightProperty().bind(stage.heightProperty());
        subScene.setCamera(camera);
        controller.getMainPain().getChildren().add(subScene);

        // setup zoom
        controller.getZoomInButton().setOnAction(e -> camera.setTranslateZ(1 / 1.1 * camera.getTranslateZ()));
        controller.getZoomOutButton().setOnAction(e -> camera.setTranslateZ(1.1 * camera.getTranslateZ()));
        // disable zoom buttons if no molecule is loaded
        controller.getZoomInButton().disableProperty().bind(Bindings.isEmpty(balls.getChildren()).or(balls.visibleProperty().not().and(sticks.visibleProperty().not())));
        controller.getZoomOutButton().disableProperty().bind(Bindings.isEmpty(balls.getChildren()).or(balls.visibleProperty().not().and(sticks.visibleProperty().not())));
        // setup center button
        controller.getButtonCenter().setOnAction(e-> {
            camera.setTranslateY(500);
            camera.setTranslateX(0);
        });


        // set up zoom in/out by mouse
        controller.getMainPain().setOnScroll(e -> {
            var delta = e.getDeltaY();
            if (delta < 0)
                camera.setTranslateZ(1.1 * camera.getTranslateZ());
            else if (delta > 0)
                camera.setTranslateZ(1 / 1.1 * camera.getTranslateZ());
        });



        // Setup rotate by mouse
        // a) update the x and y coordinates
        controller.getMainPain().setOnMousePressed(e -> {
            xPrev = e.getSceneX();
            yPrev = e.getSceneY();
        });
        // b)setup rotate/translate
        figure.getTransforms().add(new Rotate());
        camera.getTransforms().add(new Translate());


        // c) setup drag
        controller.getMainPain().setOnMouseDragged(e -> {
            // if we still drag after releasing shift we do not want the endless rot to run
            endless_rotation.stop();

            dx = e.getSceneX() - xPrev;
            dy = e.getSceneY() - yPrev;
            //rotate the atom
            if(e.getButton() == MouseButton.PRIMARY) {
                final var orthogonalAxis = new Point3D(dy, -dx, 0);

                var rotate = new Rotate(0.25 * orthogonalAxis.magnitude(), orthogonalAxis);
                var oldTransform = figure.getTransforms().get(0);
                var newTransform = rotate.createConcatenation(oldTransform);
                figure.getTransforms().set(0, newTransform);

                //after each iteration of the drag listener check if we press shift, in which case we want the endless rot
                if (e.isShiftDown()) {
                    endlessRotation(figure);
                    endless_rotation.setCycleCount(Animation.INDEFINITE);
                    endless_rotation.play();
                }

            }else if(e.getButton() == MouseButton.SECONDARY){  // move the atom
                /*Translate moveCamera = new Translate(dx,dy,0);
                var trafoOld = camera.getTransforms().get(0);
                var trafoNew = moveCamera.createConcatenation(trafoOld);
                camera.getTransforms().set(0, trafoNew);*/
                camera.setTranslateX(1.3*dx + camera.getTranslateX());
                camera.setTranslateY(1.3*dy + camera.getTranslateY());
            }
            xPrev = e.getSceneX();
            yPrev = e.getSceneY();

        });

        // stop loop by clicking on Scene
        subScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                endless_rotation.stop();

            }
        });
        //second stop by clicking the pane
        controller.getMainPain().setOnMouseClicked(e ->{
            if(!e.isShiftDown()){
                this.endless_rotation.stop();
                spinning = false;
            }

        });

        // disable check boxes and slider if balls and sticks are empty
        controller.getBallsSlider().disableProperty().bind(balls.visibleProperty().not());
        controller.getBallsCheck().disableProperty().bind(Bindings.isEmpty(balls.getChildren()));
        controller.getSticksCheck().disableProperty().bind(Bindings.isEmpty(sticks.getChildren()));
        controller.getSticksSlider().disableProperty().bind(Bindings.isEmpty(sticks.getChildren()).or(sticks.visibleProperty().not()));


        // implement sliders
        controller.getBallsSlider().setMin(1);
        controller.getBallsSlider().setMax(5);
        controller.getBallsSlider().valueProperty().addListener((observable, oldValue, newValue) -> {
            undoManager.add(new PropertyCommand<>("ball size", (DoubleProperty) observable, oldValue, newValue));

            for (javafx.scene.Node node : balls.getChildren()) {
                node = (Group) node;
                for(javafx.scene.Node subnode :  ((Group) node).getChildren()) {
                    Sphere sphere = (Sphere) subnode;
                    sphere.setRadius(sphere.getRadius() * (newValue.doubleValue() / oldValue.doubleValue()));
                }
            }
        });
        controller.getSticksSlider().setMin(0.5);
        controller.getSticksSlider().setMax(1.5);
        controller.getSticksSlider().valueProperty().addListener((observable, oldValue, newValue) -> {
            undoManager.add(new PropertyCommand<>("stick size", (DoubleProperty) observable, oldValue, newValue));
            for (javafx.scene.Node node : sticks.getChildren()) {
                node = (Group) node;
                for (javafx.scene.Node subnode : ((Group) node).getChildren()) {
                    Cylinder cylinder = (Cylinder) subnode;
                    cylinder.setRadius(15.0 * newValue.doubleValue());
                }
            }
        });

        // implement check boxes + add undos
        controller.getBallsCheck().selectedProperty().bindBidirectional(balls.visibleProperty());
        controller.getSticksCheck().selectedProperty().bindBidirectional(sticks.visibleProperty());
        controller.getMeshesCheck().selectedProperty().bindBidirectional(meshes.visibleProperty());
        controller.getMeshesCheck().setSelected(false);
        controller.getBallsCheck().selectedProperty().addListener((v, o, n) -> {
            undoManager.add(new PropertyCommand<>("balls", (BooleanProperty) v, o, n));
        });
        controller.getSticksCheck().selectedProperty().addListener((v, o, n) -> {
            undoManager.add(new PropertyCommand<>("stick", (BooleanProperty) v, o, n));
        });
        controller.getMeshesCheck().selectedProperty().addListener((v, o, n) -> {
            undoManager.add(new PropertyCommand<>("ribbons", (BooleanProperty) v, o, n));
        });


        // Animations:
        //a) set up animation speed option:
        controller.getChoiceBoxAnimationSpeed().getItems().addAll(1,2,3,4,5);
        controller.getChoiceBoxAnimationSpeed().setValue(3);
        //b) set up explosion size option:
        controller.getChoiceBoxExplosionSize().getItems().addAll(1,2,3,4,5);
        controller.getChoiceBoxExplosionSize().setValue(3);

        // c) implement Explode Button
        controller.getExplodeButton().setOnAction(e->{
            Animations.explode(balls, sticks, meshes, controller.getChoiceBoxExplosionSize().getValue(), controller);
        });
        //d) implement Transition Button
        controller.getButtonTransition().setOnAction(e -> {
            Animations.modelTransition(balls, sticks, meshes, model, controller);
        });


        // implement color menu
        controller.getColorMenu().getItems().addAll("Atom Types", "Secondary structures", "Chains", "Residues");
        controller.getColorMenu().setValue("Atom Types"); // set default value
        controller.getColorMenu().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldC, String newC) {
                AminoAcidColorScheme aminoAcidColorScheme = new AminoAcidColorScheme();
                Map<String, Color> chainColor = aminoAcidColorScheme.generateColorsForChains(model.getCurrentPolymer());
                for (Node upperNode : balls.getChildren()) {
                    upperNode = (Group) upperNode;
                    for(Node node: ((Group) upperNode).getChildren()) {
                        if (node instanceof coolSpheres) {// taken from stackoverflow

                            coolSpheres spheres = (coolSpheres) node;
                            var material = new PhongMaterial();
                            if (newC == "Atom Types") {
                                material.setDiffuseColor(spheres.getAtom().getAtomicSpecies().getColor());
                                material.setSpecularColor(Color.WHITE);
                            }
                            if (newC == "Secondary structures") {
                                if (spheres.getAtom().getSecStruc() == "H")
                                    material.setDiffuseColor(Color.RED);
                                if (spheres.getAtom().getSecStruc() == "S") {
                                    material.setDiffuseColor(Color.BLUE);
                                }
                                if (spheres.getAtom().getSecStruc() == null) {
                                    material.setDiffuseColor(Color.GRAY);
                                }
                            }
                            if (newC == "Residues")
                                material.setDiffuseColor(aminoAcidColorScheme.getColorForAminoAcid(spheres.getAtom().getParentResidue().getOneLetter()));
                            if (newC == "Chains")
                                material.setDiffuseColor(chainColor.get(spheres.getAtom().getChain()));

                            //also change color of label
                            res2Label.get(spheres.getAtom().getParentResidue()).setTextFill(material.getDiffuseColor());
                            // if there is a selection the colors must be adapted
                            if (!atomSelection.isSelected(spheres.getAtom().getParentResidue()) && atomSelection.getSelectedItems().size() != 0) {
                                var color = ((PhongMaterial) material).getDiffuseColor();
                                color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.2);
                                ((PhongMaterial) material).setDiffuseColor(color);
                            }
                            spheres.setMaterial(material);

                        }
                    }
                }
            }
        });


        //Menu Items
        //a) close
        controller.getMenuItemClose().setOnAction(e -> {
            stage.close();
        });
        // b) copy
        controller.getMenuItemCopy().setOnAction(e -> {
            var content = new ClipboardContent();
            content.putImage(controller.getMainPain().snapshot(null, null));
            Clipboard.getSystemClipboard().setContent(content);
        });
        // c) dark mode
        controller.getMenuItemDarkMode().setSelected(false);
        var styleURL = WindowPresenter.class.getResource("modena_dark.css");
        controller.getMenuItemDarkMode().selectedProperty().addListener((v, o, n) -> {
            if (controller.getMenuItemDarkMode().isSelected()){
                stage.getScene().getStylesheets().add(styleURL.toExternalForm());
            }else{
                stage.getScene().getStylesheets().remove(styleURL.toExternalForm());
            }
        });
        // d) all undo/redos
        controller.getMenuItemRedo().setOnAction(e->{undoManager.redo();});
        controller.getMenuItemRedo().textProperty().bind(undoManager.redoLabelProperty());
        controller.getMenuItemRedo().disableProperty().bind(undoManager.canRedoProperty().not());
        controller.getMenuItemUndo().setOnAction(e->{undoManager.undo();});
        controller.getMenuItemUndo().textProperty().bind(undoManager.undoLabelProperty());
        controller.getMenuItemUndo().disableProperty().bind(undoManager.canUndoProperty().not());

        // e) copy of each checkbox
        controller.getMenuItemShowBalls().selectedProperty().bindBidirectional(controller.getBallsCheck().selectedProperty());
        controller.getMenuItemShowSticks().selectedProperty().bindBidirectional(controller.getSticksCheck().selectedProperty());
        controller.getMenuItemShowRibbons().selectedProperty().bindBidirectional(controller.getMeshesCheck().selectedProperty());
        //f) full screen
        controller.getMenuFullScreen().setOnAction(e -> {stage.setFullScreen(true);});
        //g) helpmenu
        controller.getMenuAbout().setOnAction(e -> {
            Text AboutMessage = new Text("This is a Project from Raphael Förster. For a brief documentation check the readme" +
                    "in the GitHub Repo");

            StackPane helpLayout = new StackPane();
            helpLayout.getChildren().add(AboutMessage);
            Scene helpScene = new Scene(helpLayout, 600, 100);
            Stage helpWindow = new Stage();
            helpWindow.setTitle("About");
            helpWindow.setScene(helpScene);
            helpWindow.show();
        });
    }
}
