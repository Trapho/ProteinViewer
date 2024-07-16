package foerster.project.molecule_window;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class molecule_WindowController {
    /**
     * The controller class for the Molecule window of the application.
     */

    @FXML
    private ChoiceBox<Integer> ModelChoiceBox;


    @FXML
    private Button ButtonRedo;

    @FXML
    private Button ButtonUndo;

    @FXML
    private Label LabelPDBName;

    @FXML
    private MenuButton MenuButtonScrollTo;

    @FXML
    private MenuButton MenuButtonChains;

    public MenuButton getMenuButtonChains() {
        return MenuButtonChains;
    }

    @FXML
    private CheckBox MeshesCheck;

    public ChoiceBox<Integer> getModelChoiceBox() {
        return ModelChoiceBox;
    }

    public CheckBox getMeshesCheck() {
        return MeshesCheck;
    }

    @FXML
    private ListView<Label> sequenceList;

    public ListView<Label> getSequenceList() {
        return sequenceList;
    }

    @FXML
    private TextFlow textflowtest;

    public TextFlow getTextflowtest(){
        return textflowtest;
    }
    @FXML
    private CheckBox BallsCheck;

    @FXML
    private Pane PaneGraphs;

    @FXML
    private TextArea TextFieldPdbEntry;
    @FXML
    private Slider BallsSlider;

    @FXML
    private ChoiceBox<String> ColorMenu;

    @FXML
    private Button ExplodeButton;

    @FXML
    private Pane MainPain;

    @FXML
    private TextArea SequenceTextField;


    @FXML
    private MenuItem MenuItemClose;

    @FXML
    private MenuItem MenuItemCopy;

    @FXML
    private CheckMenuItem MenuItemDarkMode;

    @FXML
    private MenuItem MenuItemRedo;

    @FXML
    private CheckMenuItem MenuItemShowBalls;

    @FXML
    private CheckMenuItem MenuItemShowRibbons;

    @FXML
    private CheckMenuItem MenuItemShowSticks;

    @FXML
    private MenuItem MenuItemUndo;

    @FXML
    private CheckBox SticksCheck;

    @FXML
    private Slider SticksSlider;

    @FXML
    private Button ZoomInButton;

    @FXML
    private Button ZoomOutButton;

    @FXML
    private Pane PaneSecStrucPie;
    @FXML
    private Button ButtonTransition;

    @FXML
    private ChoiceBox<Integer> ChoiceBoxAnimationSpeed;

    @FXML
    private ChoiceBox<Integer> ChoiceBoxExplosionSize;

    @FXML
    private MenuItem MenuFullScreen;

    @FXML
    private MenuItem MenuAbout;
    @FXML
    private Label LabelNumberAA;

    @FXML
    private Label LabelNumberAtoms;

    @FXML
    private Button ButtonCenter;
    @FXML
    private CheckBox CheckHighlight;

    @FXML
    private MenuButton menuCoolPlots;

    public MenuButton getMenuCoolPlots() {
        return menuCoolPlots;
    }

    public CheckBox getCheckHighlight() {
        return CheckHighlight;
    }

    public Button getButtonCenter() {
        return ButtonCenter;
    }

    public Label getLabelNumberAA() {
        return LabelNumberAA;
    }

    public Label getLabelNumberAtoms() {
        return LabelNumberAtoms;
    }

    public MenuItem getMenuFullScreen() {
        return MenuFullScreen;
    }

    public MenuItem getMenuAbout() {
        return MenuAbout;
    }

    public ChoiceBox<Integer> getChoiceBoxExplosionSize() {
        return ChoiceBoxExplosionSize;
    }

    public ChoiceBox<Integer> getChoiceBoxAnimationSpeed() {
        return ChoiceBoxAnimationSpeed;
    }

    public Button getButtonTransition() {
        return ButtonTransition;
    }

    public Pane getPaneSecStrucPie() {
        return PaneSecStrucPie;
    }

    public MenuItem getMenuItemClose() {
        return MenuItemClose;
    }

    public MenuItem getMenuItemCopy() {
        return MenuItemCopy;
    }

    public CheckMenuItem getMenuItemDarkMode() {
        return MenuItemDarkMode;
    }

    public MenuItem getMenuItemRedo() {
        return MenuItemRedo;
    }

    public CheckMenuItem getMenuItemShowBalls() {
        return MenuItemShowBalls;
    }

    public CheckMenuItem getMenuItemShowRibbons() {
        return MenuItemShowRibbons;
    }

    public CheckMenuItem getMenuItemShowSticks() {
        return MenuItemShowSticks;
    }

    public MenuItem getMenuItemUndo() {
        return MenuItemUndo;
    }

    public CheckBox getBallsCheck() {
        return BallsCheck;
    }

    public Pane getPaneGraphs() {
        return PaneGraphs;
    }

    public TextArea getTextFieldPdbEntry() {
        return TextFieldPdbEntry;
    }

    public Slider getBallsSlider() {
        return BallsSlider;
    }

    public ChoiceBox<String> getColorMenu() {
        return ColorMenu;
    }

    public Button getExplodeButton() {
        return ExplodeButton;
    }

    public Pane getMainPain() {
        return MainPain;
    }

    public TextArea getSequenceTextField() {
        return SequenceTextField;
    }

    public CheckBox getSticksCheck() {
        return SticksCheck;
    }

    public Slider getSticksSlider() {
        return SticksSlider;
    }

    public Button getZoomInButton() {
        return ZoomInButton;
    }

    public Button getZoomOutButton() {
        return ZoomOutButton;
    }

    public Button getButtonRedo() {
        return ButtonRedo;
    }

    public Button getButtonUndo() {
        return ButtonUndo;
    }

    public Label getLabelPDBName() {
        return LabelPDBName;
    }

    public MenuButton getMenuButtonScrollTo() {
        return MenuButtonScrollTo;
    }
}
