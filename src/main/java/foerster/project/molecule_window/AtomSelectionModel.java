package foerster.project.molecule_window;

import foerster.project.model.PdbAtom;
import foerster.project.model.PdbResidue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableSet;

import java.util.Collection;

/**
 * Selection Model for the Residues
 * @param <T>
 */
public class AtomSelectionModel implements SelectionModel{

    private ObservableSet<Object> selection;

    public AtomSelectionModel(){
        selection = FXCollections.observableSet();
    }
    @Override
    public boolean select(Object o) {
        // first check if the object is not already inside
        for (Object oldSelections: selection){
            if(o == oldSelections)
                return false;
        }
        selection.add(o);
        return true;
    }

    @Override
    public boolean setSelected(Object o, boolean select) {
        if (select){
            return this.select(o);
        }else {
            return selection.remove(o);
        }
    }


    @Override
    public boolean selectAll(Collection list) {
        return selection.addAll(list);
    }

    @Override
    public void clearSelection() {
        selection.clear();
    }

    @Override
    public boolean clearSelection(Object o) {
        return selection.remove(o);
    }

    @Override
    public boolean clearSelection(Collection list) {
        return selection.removeAll(list);
    }

    @Override
    public ObservableSet getSelectedItems() {
        return selection;
    }

    @Override
    public boolean isSelected(Object o){
        return selection.contains(o);
    }
}
