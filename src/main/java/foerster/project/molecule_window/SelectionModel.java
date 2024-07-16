package foerster.project.molecule_window;

import javafx.collections.ObservableSet;

import java.util.Collection;


public interface SelectionModel <T>{

    boolean isSelected(T t);
    boolean select(T t);

    boolean setSelected(T t, boolean select);

    boolean selectAll(Collection <T> list);

    void clearSelection();

    boolean clearSelection(T t);

    boolean clearSelection(Collection<T> list);

    ObservableSet<T> getSelectedItems();


}
