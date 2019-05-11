package smshandy.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import smshandy.NetworkApp;
import smshandy.exceptions.InvalidContentException;
import smshandy.model.Message;
import smshandy.model.Provider;
import smshandy.model.SmsHandy;

public class MessageOverviewController {
	@FXML
	private ComboBox<String> fromProviderBox;
	@FXML
	private ComboBox<String> toProviderBox;
	@FXML
	private ComboBox<String> fromSmsHandyBox;
	@FXML
	private ComboBox<String> toSmsHandyBox;

	@FXML
	private TableView<Message> fromSentTableView;
	@FXML
	private TableView<Message> fromRecievedTableView;
	
	@FXML
	private TableView<Message> toSentTableView;
	@FXML
	private TableView<Message> toRecievedTableView;
	
	@FXML
	private TableColumn<Message, String> fromSentTableColumn;
	@FXML
	private TableColumn<Message, String> fromRecievedTableColumn;
	
	@FXML
	private TableColumn<Message, String> toSentTableColumn;
	@FXML
	private TableColumn<Message, String> toRecievedTableColumn;
	
	@FXML
	private Button sendButton;
	@FXML
	private TextField messageTextField;
	
	private NetworkApp networkApp;
	
	private Provider changedFromProvider;
	private Provider changedToProvider;
	
	private SmsHandy changedFromSmsHandy;
	private SmsHandy changedToSmsHandy;
	
	private AnimationWindow animationWindow;
	
	private final String SEND_ERROR_TITLE = "Message sending error";
	private final String SEND_ERROR_HEADER_SAME_NUMBER = "An attempt "
			              + "to send a message to the same number";
	private final String SEND_ERROR_CONTENT_SAME_NUMBER = "Change sender or reciever !";
	
	
	private boolean from = false;
	private boolean to = false;
	
	private final int MESSAGE_CONTENT_LENGTH = 70;
	
	@FXML
	private void initialize() {
		messageTextField.setDisable(true);
		sendButton.setDisable(true);
		animationWindow = new AnimationWindow();
		fillComboBoxs();
		disableSendButtonAndTextField();
		messageTextField.textProperty().addListener((l, o, n) -> {
			if(messageTextField.textProperty().getValue().length() > 0) {
				sendButton.setDisable(false);
			}else {
				sendButton.setDisable(true);
			}
			
			if (messageTextField.textProperty().getValue().length() > MESSAGE_CONTENT_LENGTH) {
				messageTextField.textProperty().setValue(o);
			}
		});
		
		fromSentTableView.getSelectionModel().selectedItemProperty()
			.addListener((observable, oldVal, newVal) -> showMessageDetails(observable.getValue()));
		fromRecievedTableView.getSelectionModel().selectedItemProperty()
			.addListener((observable, oldVal, newVal) -> showMessageDetails(observable.getValue()));
		toSentTableView.getSelectionModel().selectedItemProperty()
			.addListener((observable, oldVal, newVal) -> showMessageDetails(observable.getValue()));
		toRecievedTableView.getSelectionModel().selectedItemProperty()
			.addListener((observable, oldVal, newVal) -> showMessageDetails(observable.getValue()));
	}
	
	
	@FXML
	private void handleFromProviderBox() {
		if(fromProviderBox.getSelectionModel().getSelectedItem() == null) { 
			return;
		}
		fromSentTableView.setItems(null);
		fromRecievedTableView.setItems(null);
		String selectedProviderName = fromProviderBox.getSelectionModel().getSelectedItem();
		changedFromProvider = Provider.findProviderByName(selectedProviderName);
		fillSmsHandyBox(selectedProviderName, fromSmsHandyBox);
		from  = false;
		disableSendButtonAndTextField();
	}
	
	@FXML
	private void handleToProviderBox() {
		if(toProviderBox.getSelectionModel().getSelectedItem() == null){
			return;
		}
		toSentTableView.setItems(null);
		toRecievedTableView.setItems(null);
		String selectedProviderName = toProviderBox.getSelectionModel().getSelectedItem();
		changedToProvider = Provider.findProviderByName(selectedProviderName);
		fillSmsHandyBox(selectedProviderName, toSmsHandyBox);
		to = false;
		disableSendButtonAndTextField();
	}
	
	@FXML
	private void handleFromSmsHandyBox() {
		if(fromSmsHandyBox.getSelectionModel().getSelectedItem() == null) {
			return;
		}
		String selectedSubscriberNumber = fromSmsHandyBox.getSelectionModel().getSelectedItem();
	    changedFromSmsHandy = changedFromProvider.getSubscriber().get(selectedSubscriberNumber); 
		fillTableView(fromSentTableView, fromRecievedTableView, fromSentTableColumn, fromRecievedTableColumn, changedFromSmsHandy);
		from = true;
	}
	
	@FXML
	private void handleToSmsHandyBox() {
		if(toSmsHandyBox.getSelectionModel().getSelectedItem() == null) {
			return;
		}
		String selectedSubscriberNumber = toSmsHandyBox.getSelectionModel().getSelectedItem();
		changedToSmsHandy = changedToProvider.getSubscriber().get(selectedSubscriberNumber); 
		fillTableView(toSentTableView, toRecievedTableView, toSentTableColumn, toRecievedTableColumn, changedToSmsHandy);
		to = true;
		if(from) {
			enableTextField();
		}
	}
	
	
	@FXML
	private void handleSendButton() {
		try {
			if(checkSending()) {
				animationWindow.showMessageSendingAnimation();
				changedFromSmsHandy.sendSms(changedToSmsHandy.getNumber(), messageTextField.getText());
				messageTextField.clear();
			}
		} catch (InvalidContentException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * fills  combobox items of from and to
	 */
	private void fillComboBoxs() {
		Provider.providerList.forEach(provider ->
			fromProviderBox.getItems().add(provider.getName()));
		Provider.providerList.forEach(provider ->
			toProviderBox.getItems().add(provider.getName()));
	}
	
	/**
	 * clears  combobox items of from and to, 
	 * also deletes the contents of all table views
	 */
	private void clearComboBoxs() {
		fromProviderBox.getItems().clear();
		toProviderBox.getItems().clear();
		fromSmsHandyBox.getItems().clear();
		toSmsHandyBox.getItems().clear();
		fromRecievedTableView.setItems(null);
		toRecievedTableView.setItems(null);
		fromSentTableView.setItems(null);
		toSentTableView.setItems(null);
		messageTextField.clear();
		
	}
	
	private void disableSendButtonAndTextField() {
		messageTextField.setDisable(true);
		sendButton.setDisable(true);
	}
	
	private void enableTextField() {
		messageTextField.setDisable(false);
	}
	
	/**
	 * checks the sending on a different error. 
	 * returns true if there is no error, otherwise false
	 * @return
	 */
	private boolean checkSending() {
		if (changedFromSmsHandy.equals(changedToSmsHandy)) {
			showAlert(SEND_ERROR_TITLE, 
					SEND_ERROR_HEADER_SAME_NUMBER, SEND_ERROR_CONTENT_SAME_NUMBER);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Updates all needed components of GUI 
	 * to show correctly a currently model state
	 */
	public void updateMessageOverview() {
		clearComboBoxs();
		fillComboBoxs();
		disableSendButtonAndTextField();
	}
	

	/**
	 * Shows an error message in the modal window
	 * @param title
	 * @param headerText
	 * @param contentText
	 */
	private void showAlert(String title, String headerText, String contentText){
		
		Alert alert = new Alert(AlertType.WARNING);
        alert.initOwner(networkApp.getPrimaryStage());
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait(); 
	
	}
	
	/**
	 * Shows all message infromation
	 * @param message
	 */
	private void showMessageDetails(Message message) {
		if(message == null)
			return;
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.initOwner(networkApp.getPrimaryStage());
        alert.setTitle("Message details");
        alert.setContentText(message.getMessageDetailsProperty().get());
        alert.showAndWait();
	}
	
	/**
	 * fills the table 
	 * @param sentTableView
	 * @param recievedTableView
	 * @param sentTableColumn
	 * @param recievedTableColumn
	 * @param selectedSubscriber
	 */
	private void fillTableView(TableView<Message> sentTableView,
				           TableView<Message> recievedTableView, 
						   TableColumn<Message, String> sentTableColumn, 
						   TableColumn<Message, String> recievedTableColumn, 
						   SmsHandy selectedSubscriber) 
	{
		if(selectedSubscriber != null) {
			sentTableView.setItems(null);
			sentTableView.setItems(selectedSubscriber.getSent());
			sentTableColumn.setCellValueFactory(cellData -> cellData.getValue().getSentMessageInfoProperty());
			recievedTableView.setItems(null);
			recievedTableView.setItems(selectedSubscriber.getReceived());
			recievedTableColumn.setCellValueFactory(cellData -> cellData.getValue().getRecievedMessageInfoProperty());
		}
	}
	
	/**
	 * fills the ComboBox 
	 * @param providerName
	 * @param toOrFromBox
	 */
	private void fillSmsHandyBox(String selectedProviderName, ComboBox<String> toOrFromBox) {
		Provider providerBinAdp = Provider.findProviderByName(selectedProviderName);

		if (providerBinAdp != null) {
			toOrFromBox.getItems().clear();
			providerBinAdp.getSubscriber().entrySet().forEach(entry->
				toOrFromBox.getItems().add(entry.getKey()));
		}
	}
	
	public void setNetworkApp(NetworkApp networkApp) {
		this.networkApp = networkApp;
	}
}
