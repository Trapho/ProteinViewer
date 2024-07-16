package foerster.project.molecule_window;

import foerster.project.UndoRedo.PropertyCommand;
import foerster.project.UndoRedo.UndoRedoManager;
import foerster.project.model.PdbAtom;
import foerster.project.model.PdbMonomer;
import foerster.project.model.PdbPolymer;
import foerster.project.model.PdbResidue;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

import java.util.*;


/**
 * This class computes the molecule figure consisting of balls, sticks, and ribbons.
 */
public class MoleculeFigure {

    /**
     * Computes the molecule figure for the given PdbPolymer model and adds the elements to the respective groups.
     *
     * @param model            The PdbPolymer model.
     * @param balls            The Group for ball representations.
     * @param sticks           The Group for stick representations.
     * @param ribbons          The Group for ribbon representations.
     * @param residueSelection The selection model for residues.
     * @param res2Sphere       The map that associates each residue with its corresponding list of coolSpheres.
     * @param controller       The molecule_WindowController.
     * @param undoManager      The UndoRedoManager for handling undo and redo functionality.
     * @param Quality          The quality parameter for creating cylinders and spheres.
     */
    public static void compute(PdbPolymer model, Group balls, Group sticks, Group ribbons, SelectionModel<PdbResidue> residueSelection,
                               Map<PdbResidue, List<coolSpheres>> res2Sphere, molecule_WindowController controller, UndoRedoManager undoManager, int Quality){
        // Set up the current map for the current polymer
        controller.getMenuButtonChains().getItems().clear();
        for (PdbResidue res: model.getAllResidues()){
            res2Sphere.put(res, new ArrayList<>());
        }
        // atom2sphere mapper (clear if already exists)
        Map<PdbAtom, coolSpheres> atom2sphere = new HashMap<>();


        var atoms = model.getAllAtoms();
        var bonds = model.getAllBonds();

        // compute average x, y and z coordinates of all atoms in order to center at origin
        int sum_x = 0;
        int sum_y = 0;
        int sum_z = 0;
        for(PdbAtom atom : atoms){
            sum_x += atom.getX();
            sum_y += atom.getY();
            sum_z += atom.getZ();
        }
        double avg_x = sum_x/atoms.size();
        double avg_y = sum_y/atoms.size();
        double avg_z = sum_z/atoms.size();

        //list of all backbone atoms needed for backbone coloring
        ArrayList<String> backboneatoms = new ArrayList<>();
        backboneatoms.add("C");
        backboneatoms.add("N");
        backboneatoms.add("CA");
        // iterate over all monomers of  the model: for each monomer calculate:
        // 1. Atoms 2. MenuItems(to hide monomers) 3. Bonds 4. Ribbons
        for (PdbMonomer monomer: model.getMonomers()) {
            // subGroups belong to different monomers
            Group subBalls = new Group();
            for (PdbAtom atom : monomer.getAtoms()) {
                // for each atom, add a sphere to the Group balls
                coolSpheres atom_sphere = new coolSpheres(1.0, Quality);
                atom_sphere.setAtom(atom);
                int radius;
                radius = atom.getAtomicSpecies().getRadiusPM() / 10;
                atom_sphere.setRadius(radius);
                var material = new PhongMaterial();
                material.setDiffuseColor(atom.getAtomicSpecies().getColor());

                material.setSpecularColor(Color.WHITE);
                atom_sphere.setMaterial(material);
                atom_sphere.setTranslateX((atom.getX() - avg_x) * 100);
                atom_sphere.setTranslateY((atom.getY() - avg_y) * 100);
                atom_sphere.setTranslateZ((atom.getZ() - avg_z) * 100); //*100 + 300
                atom.setShape(atom_sphere);
                subBalls.getChildren().add(atom_sphere);
                // put the new Spheres into Hashmap, so the can be maped to atoms and aas
                res2Sphere.get(atom.getParentResidue()).add(atom_sphere);
                atom2sphere.put(atom, atom_sphere);
                // initialize that spheres can be clicked
                selectAtomByClick(atom_sphere, residueSelection, res2Sphere);
            }

            balls.getChildren().add(subBalls);
            //each chain has a MenuButtonItem
            //1.) MenuButtonItem
            CheckBox addChain = new CheckBox(monomer.getChain());
            addChain.setSelected(true);
            CustomMenuItem addChainItem = new CustomMenuItem(addChain);
            addChainItem.setHideOnClick(false);
            controller.getMenuButtonChains().getItems().add(addChainItem);
            addChain.selectedProperty().bindBidirectional(subBalls.visibleProperty());

            // compute all bonds as Cylinders
            Group subSticks = new Group();
            for(PdbMonomer.Bond bond : monomer.getBonds()){

                // for each bond, add a stick to the Group sticks
                var atom_a = bond.getAtom1();
                var atom_b = bond.getAtom2();
                boolean isBackbone = backboneatoms.contains(atom_a.getType()) && backboneatoms.contains(atom_b.getType());
                Point3D a = new Point3D(atom_a.getShape().getTranslateX(),
                        atom_a.getShape().getTranslateY(),
                        atom_a.getShape().getTranslateZ());
                Point3D b = new Point3D(atom_b.getShape().getTranslateX(),
                        atom_b.getShape().getTranslateY(),
                        atom_b.getShape().getTranslateZ());
                Cylinder bond_cylinder = Stick.create(a, b, atom_a, atom_b, atom2sphere, Quality, isBackbone, controller.getCheckHighlight());
                subSticks.getChildren().add(bond_cylinder);

            }
            sticks.getChildren().add(subSticks);
            addChain.selectedProperty().bindBidirectional(subSticks.visibleProperty());

            // Compute all ribbons
            Group subRibbons = RibbonFigure.compute(monomer, res2Sphere);
            ribbons.getChildren().add(subRibbons);
            addChain.selectedProperty().bindBidirectional(subRibbons.visibleProperty());

            // Each MenuItem has a undo Property
            addChain.selectedProperty().addListener((v, o, n) -> {
                undoManager.add(new PropertyCommand<>(monomer.getChain(), (BooleanProperty) v, o, n));
            });
        }

    }


    /**
     * This inner class provides methods for creating sticks between atoms.
     */
    public class Stick{
        // code from assignment sheet
        public static final double DEFAULT_RADIUS = 10;

        /**
         * Each of the following functions take the same 6 parameters
         * 1. ) calculates the middle point of two spheres
         *
         * @param x1 The x-coordinate of the first point.
         * @param y1 The y-coordinate of the first point.
         * @param z1 The z-coordinate of the first point.
         * @param x2 The x-coordinate of the second point.
         * @param y2 The y-coordinate of the second point.
         * @param z2 The z-coordinate of the second point.
         * @return The midpoint as a Point3D object.
         */
        public static Point3D calculateMidPoint(double x1, double y1, double z1, double x2, double y2, double z2){
            return (new Point3D(x1, y1, z1)).midpoint(new Point3D(x2, y2, z2));
        }
        /**
         * Compute Direction of one atom to the other
         */
        public static Point3D calculateDirection(double x1, double y1, double z1, double x2, double y2, double z2){
            return (new Point3D(x2, y2, z2)).subtract(new Point3D(x1, y1, z1));
        }
        /**
         * Compute YAXIS and direction of two atoms
         */
        public static Point3D calculatePerAxis(double x1, double y1, double z1, double x2, double y2, double z2){
            var YAXIS = new Point3D(0,100,0);
            return YAXIS.crossProduct(calculateDirection(x1, y1, z1, x2, y2, z2));
        }
        /**
         * Compute angle between YAXIS and direction of two points
         */
        public static Double calculateAngle(double x1, double y1, double z1, double x2, double y2, double z2){
            var YAXIS = new Point3D(0,100,0);
            return YAXIS.angle(calculateDirection(x1, y1, z1, x2, y2, z2));
        }
        /**
         * Computes Distance between two points
         */
        public static Double calculateDisance(double x1, double y1, double z1, double x2, double y2, double z2, double height){
            return (new Point3D(x1, y1, z1)).distance(new Point3D(x2, y2, z2)) / height;
        }

        /**
         * Creates a cylinder representing a stick between two atoms.
         * NOTE: I did a lot of rewriting for this methods, thus some of the parameters might not be used!
         * I am sorry for any confussions
         *
         * @param a            The first atom's position as a Point3D object.
         * @param b            The second atom's position as a Point3D object.
         * @param atom_a       The first atom.
         * @param atom_b       The second atom.
         * @param atom2sphere  The map that associates each atom with its corresponding coolSpheres.
         * @param Quality      The quality parameter for creating the cylinder.
         * @param isBackbone   True if the bond belongs to the backbone
         * @param isHighlighted   Checkbox to whether we want to highlight the backbone
         * @return The created Cylinder object.
         */
        public static Cylinder create (Point3D a, Point3D b, PdbAtom atom_a, PdbAtom atom_b, Map<PdbAtom, coolSpheres> atom2sphere, int Quality, boolean isBackbone, CheckBox isHighlighted){

            var aP = atom2sphere.get(atom_a);
            var bP = atom2sphere.get(atom_b);


            var cylinder = new Cylinder(DEFAULT_RADIUS, 100, Quality);



            //bind to the backbone highlight checkbox
            cylinder.materialProperty().bind(Bindings.createObjectBinding(()->{
                var backboneMaterial = new PhongMaterial();
                backboneMaterial.setSpecularColor(Color.WHITE);
                if(isBackbone && isHighlighted.isSelected())
                    backboneMaterial.setDiffuseColor(Color.SALMON);
                else
                    backboneMaterial.setDiffuseColor(Color.LIGHTGRAY);

                return backboneMaterial;
            }, isHighlighted.selectedProperty()));

            // each property of the cylinder is bound to its two atoms. To achieve this bindings were created which
            // bind the cylinder properties to the coordinate properties of the atoms (each cylinder is bound to 6 coordinates in total)
            cylinder.translateXProperty().bind(Bindings.createDoubleBinding(() -> calculateMidPoint(aP.getTranslateX(), aP.getTranslateY(), aP.getTranslateZ(), bP.getTranslateX(), bP.getTranslateY(), bP.getTranslateZ()).getX(), aP.translateXProperty(), aP.translateYProperty(), aP.translateZProperty(), bP.translateXProperty(), bP.translateYProperty(), bP.translateZProperty()));
            cylinder.translateYProperty().bind(Bindings.createDoubleBinding(() -> calculateMidPoint(aP.getTranslateX(), aP.getTranslateY(), aP.getTranslateZ(), bP.getTranslateX(), bP.getTranslateY(), bP.getTranslateZ()).getY(), aP.translateXProperty(), aP.translateYProperty(), aP.translateZProperty(), bP.translateXProperty(), bP.translateYProperty(), bP.translateZProperty()));
            cylinder.translateZProperty().bind(Bindings.createDoubleBinding(() -> calculateMidPoint(aP.getTranslateX(), aP.getTranslateY(), aP.getTranslateZ(), bP.getTranslateX(), bP.getTranslateY(), bP.getTranslateZ()).getZ(), aP.translateXProperty(), aP.translateYProperty(), aP.translateZProperty(), bP.translateXProperty(), bP.translateYProperty(), bP.translateZProperty()));
            cylinder.rotationAxisProperty().bind(Bindings.createObjectBinding(() -> calculatePerAxis(aP.getTranslateX(), aP.getTranslateY(), aP.getTranslateZ(), bP.getTranslateX(), bP.getTranslateY(), bP.getTranslateZ()), aP.translateXProperty(), aP.translateYProperty(), aP.translateZProperty(), bP.translateXProperty(), bP.translateYProperty(), bP.translateZProperty()));
            cylinder.rotateProperty().bind(Bindings.createDoubleBinding(() -> calculateAngle(aP.getTranslateX(), aP.getTranslateY(), aP.getTranslateZ(), bP.getTranslateX(), bP.getTranslateY(), bP.getTranslateZ()), aP.translateXProperty(), aP.translateYProperty(), aP.translateZProperty(), bP.translateXProperty(), bP.translateYProperty(), bP.translateZProperty()));
            cylinder.scaleYProperty().bind(Bindings.createDoubleBinding(() -> calculateDisance(aP.getTranslateX(), aP.getTranslateY(), aP.getTranslateZ(), bP.getTranslateX(), bP.getTranslateY(), bP.getTranslateZ(), cylinder.getHeight()), aP.translateXProperty(), aP.translateYProperty(), aP.translateZProperty(), bP.translateXProperty(), bP.translateYProperty(), bP.translateZProperty(), cylinder.heightProperty()));
            return cylinder;
        }
    }
    /**
     * Selects an atom by clicking on its coolSphere representation.
     *
     * @param sphere         The coolSphere representing the atom.
     * @param selectedResidues The selection model for residues.
     * @param atom2Sphere     The map that associates each atom with its corresponding list of coolSpheres.
     */
    public static void selectAtomByClick (coolSpheres sphere, SelectionModel<PdbResidue> selectedResidues, Map<PdbResidue, List<coolSpheres>> atom2Sphere
                                          ){
        sphere.setOnMouseClicked(e -> {

            boolean isClickedAlreadySele = selectedResidues.isSelected(sphere.getAtom().getParentResidue());
            if (!e.isShiftDown())
                selectedResidues.clearSelection();
            if (isClickedAlreadySele)
                selectedResidues.clearSelection(sphere.getAtom().getParentResidue());
            else
                selectedResidues.select(sphere.getAtom().getParentResidue());
        });
    }
}
