package smshandy.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import smshandy.NetworkApp;

public class RootOverviewController {
	
	@FXML
	private Button network;
	
	@FXML
	private Button message;
	
	NetworkApp networkApp;
	
	@FXML
	private void changeToNetwork() { 
		networkApp.changeViewToNetwork();
	}
	
	@FXML
	private void changeToMessage() {
		networkApp.changeViewToMessage();
	}
	
	public void setNetworkApp(NetworkApp networkApp) {
		this.networkApp = networkApp;
	}
	
}
