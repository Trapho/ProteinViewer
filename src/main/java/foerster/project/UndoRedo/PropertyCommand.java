package foerster.project.UndoRedo;

import javafx.beans.property.Property;

/**
 * property command
 * Daniel Huson 6.2023
 */
public class PropertyCommand<T> extends SimpleCommand {
	public PropertyCommand(String name, Property<T> v, T oldValue, T newValue) {
		super(name, () -> v.setValue(oldValue), () -> v.setValue(newValue));
	}
}
