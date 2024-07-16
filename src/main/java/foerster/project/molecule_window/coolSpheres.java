package foerster.project.molecule_window;

import foerster.project.model.PdbAtom;
import javafx.scene.shape.Sphere;

/**
 * the cooler Version of Spheres with a reference to PdbAtoms
 */
public class coolSpheres extends Sphere {
    private PdbAtom atom;

    public coolSpheres(double Radius, int Quality){
        super(Radius, Quality);
    }
    public coolSpheres(){
        super();
    }

    public void setAtom(PdbAtom atom) {
        this.atom = atom;
    }
    public PdbAtom getAtom(){return this.atom;}
}
