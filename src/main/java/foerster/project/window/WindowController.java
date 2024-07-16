package foerster.project.window;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class WindowController {
    /**
     * The controller class for the main window of the application.
     */

    @FXML
    private ListView<String> PdbList;

    @FXML
    private MenuItem MenuClose;

    @FXML
    private MenuItem MenuExport;

    @FXML
    private Label LabelPdb;

    @FXML
    private MenuItem MenuItemAbout;

    @FXML
    private MenuItem MenuItemClear;
    @FXML
    private CheckMenuItem MenuDarkMode;


    @FXML
    private MenuItem MenuOpen;

    @FXML
    private ChoiceBox<String> ChoiceBoxResolution;

    public ChoiceBox<String> getChoiceBoxResolution() {
        return ChoiceBoxResolution;
    }

    public ListView<String> getPdbList() {
        return PdbList;
    }

    public MenuItem getMenuItemAbout() {
        return MenuItemAbout;
    }

    public MenuItem getMenuItemClear() {
        return MenuItemClear;
    }

    public CheckMenuItem getMenuDarkMode() {
        return MenuDarkMode;
    }

    public Label getLabelPdb() {
        return LabelPdb;
    }

    public TextArea getPdbEntry() {
        return pdbEntry;
    }

    public MenuItem getMenuClose() {
        return MenuClose;
    }

    public MenuItem getMenuExport() {
        return MenuExport;
    }

    public MenuItem getMenuOpen() {
        return MenuOpen;
    }

    public Button getSearchButton() {
        return searchButton;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public Button getViewButton() {
        return viewButton;
    }

    @FXML
    private TextArea pdbEntry;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button viewButton;

}
