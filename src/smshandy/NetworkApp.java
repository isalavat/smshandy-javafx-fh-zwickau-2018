package smshandy;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import smshandy.exceptions.InvalidContentException;
import smshandy.exceptions.InvalidNumberException;
import smshandy.exceptions.InvalidProviderException;
import smshandy.model.PrepaidSmsHandy;
import smshandy.model.Provider;
import smshandy.model.TariffPlanSmsHandy;
import smshandy.view.MessageOverviewController;
import smshandy.view.NetworkOverviewController;
import smshandy.view.RootOverviewController;

public class NetworkApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private AnchorPane networkOverview;
    private AnchorPane messageOverview;
    private  MessageOverviewController messOverController;
    public NetworkApp() throws InvalidNumberException, InvalidProviderException, InvalidContentException {
    	
    	Provider p1 = new Provider("O2");
    	Provider p2 = new Provider("Netto");
		
		PrepaidSmsHandy h1 = new PrepaidSmsHandy("124", p1);
		TariffPlanSmsHandy h2 = new TariffPlanSmsHandy("321", p1);
		PrepaidSmsHandy h3 = new PrepaidSmsHandy("543", p2);
		
		h1.sendSms(h2.getNumber(), "WHZ");
		h2.sendSms(h1.getNumber(), "KSUCTA");
		h3.sendSms(h1.getNumber(), "Lesecafe");
		h2.sendSms(h3.getNumber(), "Mensa");
    	
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("SmsHandy Project");

        initRootLayout();
        showNetworkOverview();
        showMessageOverview();
        rootLayout.setCenter(networkOverview);
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(NetworkApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            RootOverviewController controller = loader.getController();
            controller.setNetworkApp(this);
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the network overview inside the root layout.
     */
    public void showNetworkOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(NetworkApp.class.getResource("view/NetworkOverview.fxml"));
            networkOverview = (AnchorPane) loader.load();

            // Set network overview into the center of root layout.
            
            NetworkOverviewController controller = loader.getController();
            controller.setNetworkApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Shows the message overview inside the root layout.
     */
    public void showMessageOverview() {
        try {
            // Load message overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(NetworkApp.class.getResource("view/MessageOverview.fxml"));
            messageOverview = (AnchorPane) loader.load();

            // Set message overview into the center of root layout.
            
            messOverController = loader.getController();
            messOverController.setNetworkApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void changeViewToNetwork() {
    	if(rootLayout.getCenter().equals(messageOverview)) {
    		rootLayout.setCenter(networkOverview);
    	}
    }
    public void changeViewToMessage() {
    	if(rootLayout.getCenter().equals(networkOverview)) {
    		rootLayout.setCenter(messageOverview);
    		messOverController.updateMessageOverview();
    	}
    }
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}