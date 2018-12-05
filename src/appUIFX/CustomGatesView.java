package appUIFX;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

import framework2FX.gateModels.PresetModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CustomGatesView extends AbstractGateChooser implements ChangeListener<Boolean>{

	public CustomGatesView() {
		super("Custom Gates");
	}
	
	public void initializeGates() {
		ToggleButton tb;
		Image image;
		ImageView imageView;
		for(PresetModel dg : PresetModel.values()) {
			tb = new ToggleButton();
			image = SwingFXUtils.toFXImage((BufferedImage) GateIcon.getDefaultGateIcon(dg).getImage(), null);
			imageView = new ImageView(image);
			tb.setGraphic(imageView);
			list.getChildren().add(tb);
			tg.getToggles().add(tb);
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		button.setVisible(true);
		button.setText("Create Custom Gate");
		button.pressedProperty().addListener(this);
		initializeGates();
	}

	@Override
	public void receive(Object source, String methodName, Object... args) {
		
	}

	@Override
	public void buttonAction() {
		
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		
	}

}
