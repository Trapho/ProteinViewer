package foerster.project.molecule_window;

import foerster.project.model.PdbAtom;
import foerster.project.model.PdbComplex;
import foerster.project.model.PdbMonomer;
import foerster.project.model.PdbPolymer;
import javafx.animation.*;
import javafx.beans.property.Property;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Animations {

    /**
     * Creates a TranslateTransition to move an atom from its current position to a target position.
     *
     * @param a     The starting atom.
     * @param b     The target atom.
     * @param speed The speed of the transition.
     * @return      The TranslateTransition animation.
     */
    public static TranslateTransition moveAtomOnce(PdbAtom a, PdbAtom b, int speed){
        double dx = (b.getX() - a.getX()) *100;
        double dy = (b.getY() - a.getY()) *100;
        double dz = (b.getZ() - a.getZ()) *100;
        var translate = new TranslateTransition();
        translate.setDuration(Duration.seconds(speed * 0.5));
        translate.setByX(dx);
        translate.setByY(dy);
        translate.setByZ(dz);
        return translate;
    }


    /**
     * Performs a model transition animation using the provided model and controller.
     * Only transitions for the atoms are computed. For the sticks and Ribbons it is not needed, because those
     * are bound to the atoms. So they are updated automatically when the atoms are moved
     *
     * @param balls         The group containing ball representations.
     * @param sticks        The group containing stick representations.
     * @param ribbons       The group containing ribbon representations.
     * @param model         The PdbComplex model to animate.
     * @param controller    The molecule_WindowController.
     */
    public static void modelTransition(Group balls, Group sticks, Group ribbons, PdbComplex model, molecule_WindowController controller){
        // First create a sequence of animations for each atom
        ArrayList<SequentialTransition> atom2animation = new ArrayList<>();
        for (PdbAtom atom: model.getCurrentPolymer().getAllAtoms()){
            atom2animation.add(new SequentialTransition());
        }

        // next bind each animation to a node (the animations are still empty)
        int nodeCounter = 0;
        for (Node upperNode: balls.getChildren()){
            for (Node subNode: ((Group) upperNode).getChildren()){
                atom2animation.get(nodeCounter).setNode(subNode);
                nodeCounter++;
            }
        }
        //fill the animations
        PdbPolymer currentAnimationStep = model.getCurrentPolymer();
        for (PdbPolymer polymer : model.getPolymers()){
            if (polymer.getAllAtoms().size() == atom2animation.size()){
                for (int iAt = 0; iAt < polymer.getAllAtoms().size(); iAt++){
                    PdbAtom a = currentAnimationStep.getAllAtoms().get(iAt);
                    PdbAtom b = polymer.getAllAtoms().get(iAt);
                    atom2animation.get(iAt).getChildren().add(moveAtomOnce(a, b, controller.getChoiceBoxAnimationSpeed().getValue()));
                }

                currentAnimationStep = polymer;
            }
        }
        // go back to init state:
        for (int iAt = 0; iAt < model.getCurrentPolymer().getAllAtoms().size(); iAt++){
            PdbAtom a = currentAnimationStep.getAllAtoms().get(iAt);
            PdbAtom b = model.getCurrentPolymer().getAllAtoms().get(iAt);
            atom2animation.get(iAt).getChildren().add(moveAtomOnce(a, b, controller.getChoiceBoxAnimationSpeed().getValue()));
        }

        //disable transition button while animation is running

        controller.getButtonTransition().setDisable(true);
        //play the animations
        for (SequentialTransition animations: atom2animation){
            animations.play();
        }
        atom2animation.get(0).setOnFinished(e -> controller.getButtonTransition().setDisable(false));
    }

    /**
     * Performs an explosion animation on the provided groups with the specified explosion size.
     *
     * @param balls             The group containing ball representations.
     * @param sticks            The group containing stick representations.
     * @param ribbons           The group containing ribbon representations.
     * @param explosionSize     The size of the explosion.
     * @param controller        The molecule_WindowController.
     */
    public static void explode(Group balls, Group sticks, Group ribbons, int explosionSize, molecule_WindowController controller){
        // first loop over balls to:
        //1.) identify all chains
        //2.) For each chain then determine the center of mass
        ArrayList<Point3D> meanCoords = new ArrayList<>();

        for (Node node: balls.getChildren()){
            node = (Group) node;
            int numberNodes = 0;
            double X_sum = 0;
            double Y_sum = 0;
            double Z_sum = 0;

            for (Node ball: ((Group) node).getChildren()){
                ball = (coolSpheres) ball;
                numberNodes++;
                X_sum += ball.getTranslateX();
                Y_sum += ball.getTranslateY();
                Z_sum += ball.getTranslateZ();
            }
            meanCoords.add(new Point3D(X_sum/numberNodes, Y_sum/numberNodes, Z_sum/numberNodes));
        }

        //next calculate the center of mass of theses coords
        double centerX_sum = 0;
        double centerY_sum = 0;
        double centerZ_sum = 0;
        for (Point3D p: meanCoords){
            centerX_sum += p.getX();
            centerY_sum += p.getY();
            centerZ_sum += p.getZ();
        }
        Point3D centerOfMass = new Point3D(centerX_sum/meanCoords.size(), centerY_sum/ meanCoords.size(), centerZ_sum/ meanCoords.size());

        // next calculate a transition based on this.
        int toChainMapper = 0;
        for (Point3D p: meanCoords){
            for(int i = 0; i<3; i++) {   // super ugly way to iterate over balls, stick and ribbons, but i found no other way
                var t = new TranslateTransition();
                t.setDuration(Duration.seconds(controller.getChoiceBoxAnimationSpeed().getValue()));

                t.setToX((2 * p.getX() - centerOfMass.getX()) * 0.5*explosionSize);

                t.setToY((2 * p.getY() - centerOfMass.getY()) * 0.5*explosionSize);

                t.setToZ((2 * p.getZ() - centerOfMass.getZ()) * 0.5*explosionSize);

                PauseTransition pause = new PauseTransition(Duration.seconds(controller.getChoiceBoxAnimationSpeed().getValue()));
                var sequential = new SequentialTransition(t, pause);
                sequential.setAutoReverse(true);
                sequential.setCycleCount(2);
                if(i == 0)
                    sequential.setNode(balls.getChildren().get(toChainMapper));
                if(i == 1)
                    sequential.setNode(sticks.getChildren().get(toChainMapper));
                if (i == 2)
                    sequential.setNode(ribbons.getChildren().get(toChainMapper));
                controller.getExplodeButton().setDisable(true);
                sequential.play();
                sequential.setOnFinished(e -> controller.getExplodeButton().setDisable(false));
            }
            toChainMapper++;
        }
    }

}
