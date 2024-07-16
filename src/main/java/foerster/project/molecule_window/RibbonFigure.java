package foerster.project.molecule_window;

import foerster.project.model.PdbAtom;
import foerster.project.model.PdbMonomer;
import foerster.project.model.PdbResidue;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

import java.util.*;

public class RibbonFigure {

    /**
     * Computes and returns the ribbon figure for a given PdbMonomer.
     *
     * @param monomer       The PdbMonomer for which the ribbon figure needs to be computed.
     * @param res2Spheres   A mapping of PdbResidue to List of coolSpheres containing the atoms.
     * @return              The Group containing the ribbon figure mesh.
     */
    public static Group compute(PdbMonomer monomer, Map<PdbResidue, List<coolSpheres>> res2Spheres){
        Group submeshes = new Group();

                for (int i = 0; i < monomer.getResidues().size() - 1; i++) { // first res
                    try {
                        List<coolSpheres> pointList = getPoints(monomer.getResidues().get(i), res2Spheres, true);
                        pointList.addAll(getPoints(monomer.getResidues().get(i + 1), res2Spheres, false));
                        float[] pointArray = new float[18];


                        int[] faces = {0, 0, 1, 1, 4, 4,
                                0, 0, 4, 4, 5, 5,
                                1, 1, 2, 2, 3, 3,
                                1, 1, 3, 3, 4, 4,
                                0, 0, 4, 4, 1, 1,
                                0, 0, 5, 5, 4, 4,
                                1, 1, 3, 3, 2, 2,
                                1, 1, 4, 4, 3, 3};
                        float[] texCoords = {0, 0, // t 0
                                0, 0.5f, // t1
                                0, 1, // ...
                                1, 1,
                                1, 0.5f,
                                1, 0 // t5
                        };
                        int[] smoothing = {1, 1, 1, 1, 2, 2, 2, 2};

                        var meshView = new MeshView(); //(mesh)
                        meshView.meshProperty().bind(Bindings.createObjectBinding(()->{
                                    /**
                                     * Binding that bins the meshView to its six input coolSpheres
                                     * Everytime any of the 6 spheres is updated a new mesh is calculated
                                     */
                            var mesh1 = new TriangleMesh();
                            mesh1.getTexCoords().addAll(texCoords);
                            mesh1.getFaces().addAll(faces);
                            mesh1.getFaceSmoothingGroups().addAll(smoothing);
                            float[] dynamicPointArray = new float[18];
                            int counter = 0;
                            for (coolSpheres atom_sphere: pointList){
                                dynamicPointArray[counter++] = (float) atom_sphere.getTranslateX();
                                dynamicPointArray[counter++] = (float) atom_sphere.getTranslateY();
                                dynamicPointArray[counter++] = (float) atom_sphere.getTranslateZ();
                            }
                            mesh1.getPoints().addAll(dynamicPointArray);
                            return mesh1;
                        }, pointList.get(0).translateXProperty(), pointList.get(0).translateYProperty(), pointList.get(0).translateZProperty(),
                                pointList.get(1).translateXProperty(), pointList.get(1).translateYProperty(), pointList.get(1).translateZProperty(),
                                pointList.get(2).translateXProperty(), pointList.get(2).translateYProperty(), pointList.get(2).translateZProperty(),
                                pointList.get(3).translateXProperty(), pointList.get(3).translateYProperty(), pointList.get(3).translateZProperty(),
                                pointList.get(4).translateXProperty(), pointList.get(4).translateYProperty(), pointList.get(4).translateZProperty(),
                                pointList.get(5).translateXProperty(), pointList.get(5).translateYProperty(), pointList.get(5).translateZProperty()
                                ));
                        meshView.setMaterial(new PhongMaterial(Color.YELLOW));
                        meshView.setDrawMode(DrawMode.FILL);
                        meshView.setCullFace(CullFace.BACK);
                        //Edge case: There is a hole in the monomer because of parsing errors. Then we dont want ribons to span the hole
                        if(700 > (new Point3D(pointList.get(0).getTranslateX(),pointList.get(0).getTranslateY(),pointList.get(0).getTranslateZ()).distance(
                                new Point3D(pointList.get(3).getTranslateX(),pointList.get(3).getTranslateY(),pointList.get(3).getTranslateZ()))))
                            submeshes.getChildren().add(meshView);
                    }catch (Exception e){continue;}
                }
                return submeshes;
    }

    /**
     * Retrieves the list of coolSpheres representing the points for a given PdbResidue.
     *
     * @param res               The PdbResidue for which the points are needed.
     * @param res2Spheres       A mapping of PdbResidue to List of coolSpheres containing the atoms.
     * @param first             Boolean indicating whether it is the first residue in the sequence.
     * @return                  The list of coolSpheres representing the points.
     */
    public static ArrayList<coolSpheres> getPoints(PdbResidue res, Map<PdbResidue, List<coolSpheres>> res2Spheres, boolean first){
        Map <String, coolSpheres> extraction = new HashMap<>();
        ArrayList<String> neededAtoms = new ArrayList<>();
        neededAtoms.add("CA"); neededAtoms.add("CB"); neededAtoms.add("N");
        ArrayList<coolSpheres> output = new ArrayList<>();
        //first extract all important atoms
        for (coolSpheres atom_sphere: res2Spheres.get(res)){
            if(neededAtoms.contains(atom_sphere.getAtom().getType())){
                extraction.put(atom_sphere.getAtom().getType(), atom_sphere);
            }
        }
        // if CB is not contain bc we have Gly or the parser failed we do the following
        if (!extraction.containsKey("CB")){
            extraction.put("CB", calcOpposite(extraction.get("CA"), extraction.get("N")));
        }
        // calculate the opposite Point
        extraction.put("oppo", calcOpposite(extraction.get("CA"), extraction.get("CB")));

        // define the order

        List<String> order = Arrays.asList(new String[]{"oppo", "CA", "CB"});


        for (String key: order){
            output.add(extraction.get(key));
        }
        return output;
    }

    /**
     * Calculates and returns the opposite coolSpheres for a given pair of coolSpheres.
     *
     * @param s2    The second coolSpheres.
     * @param s1    The first coolSpheres.
     * @return      The calculated opposite coolSpheres.
     */
    public static coolSpheres calcOpposite(coolSpheres s2, coolSpheres s1){
        coolSpheres output = new coolSpheres();
        output.translateXProperty().bind(Bindings.createDoubleBinding(()->2 * s2.getTranslateX() - s1.getTranslateX(), s2.translateXProperty(), s1.translateXProperty()));
        output.translateYProperty().bind(Bindings.createDoubleBinding(()->2 * s2.getTranslateY() - s1.getTranslateY(), s2.translateYProperty(), s1.translateYProperty()));
        output.translateZProperty().bind(Bindings.createDoubleBinding(()->2 * s2.getTranslateZ() - s1.getTranslateZ(), s2.translateZProperty(), s1.translateZProperty()));

        return output;
    }
}
