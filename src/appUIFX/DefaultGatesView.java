package appUIFX;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

import framework2FX.gateModels.DefaultGateModel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DefaultGatesView extends AbstractGateChooser {
	
	public DefaultGatesView() {
		super("Default Gates");
	}

	public void initializeGates() {
		ToggleButton tb;
		
		Image image;
		ImageView imageView;
		for(DefaultGateModel dg : DefaultGateModel.values()) {
			tb = new ToggleButton();
			image = SwingFXUtils.toFXImage((BufferedImage) GateIcon.getDefaultGateIcon(dg).getImage(), null);
			imageView = new ImageView(image);
			tb.setGraphic(imageView);
			list.getChildren().add(tb);
			tg.getToggles().add(tb);
		}
		
		
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {		
		setButtonVisible(false);
		initializeGates();
	}

	@Override
	public void buttonAction() {}


	@Override
	public void receive(Object source, String methodName, Object... args) {
		
	}
	
}
