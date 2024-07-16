package foerster.project.UndoRedo;

/**
 * command interface
 * Daniel Huson 6.2023
 */
public interface Command {
	void undo();

	void redo();

	String name();

	boolean canUndo();

	boolean canRedo();
}
