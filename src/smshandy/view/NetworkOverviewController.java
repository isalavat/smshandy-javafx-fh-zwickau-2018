package smshandy.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import smshandy.NetworkApp;
import smshandy.exceptions.InvalidNumberException;
import smshandy.exceptions.InvalidProviderException;
import smshandy.model.PrepaidSmsHandy;
import smshandy.model.Provider;
import smshandy.model.SmsHandy;
import smshandy.model.TariffPlanSmsHandy;

public class NetworkOverviewController {

	@FXML
	private TableView<Provider> providerTable;
	@FXML
	private TableView<SmsHandy> phoneTable;
	
	@FXML
	private TableColumn<Provider, String> providerColumn;
	@FXML
	private TableColumn<SmsHandy, String> phoneColumn;

	@FXML
	private Label typeOfPhone;
	@FXML
	private Label balanceType;
	@FXML
	private Label balanceValue;
	
	@FXML
	private TextField providerTextField;
	@FXML
	private TextField phoneTextField;
	@FXML
	private TextField phoneBalance;
	
	@FXML
	private ComboBox<String> chooserProviderForPhone;
	@FXML
	private ComboBox<String> chooseAnotherProviderBox;
	@FXML
	private ComboBox<String> chooseDeleteProviderBox;
	@FXML
	private ComboBox<String> choosePhoneToDelete;
	@FXML
	private ComboBox<String> chooseArtOfPhone;
	
	@FXML
	private Button changeProviderButton;
	@FXML
	private Button addProviderButton;
	@FXML
	private Button deleteProviderButton;
	@FXML
	private Button addPhoneButton;
	@FXML
	private Button deletePhoneButton;
	@FXML
	private Button topUpButton;
	
	@SuppressWarnings("unused")
	private NetworkApp networkApp;
	
	public NetworkOverviewController() {
		
	}
	
	@FXML
	private void initialize() {
		addProviderButton.setDisable(true);
		changeProviderButton.setDisable(true);
		chooseAnotherProviderBox.setDisable(true);
		chooserProviderForPhone.setDisable(true);
		chooseArtOfPhone.setDisable(true);
		topUpButton.setDisable(true);
		phoneBalance.setDisable(true);
		chooseArtOfPhone.getItems().add("Tariff Plan");
		chooseArtOfPhone.getItems().add("Prepaid");
		addPhoneButton.setDisable(true);
		
		updateProviderBox();

		providerColumn.setCellValueFactory(
				cellData -> cellData.getValue().getNameProperty());
		
		providerTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> showSubscribers(newValue));
		
		phoneColumn.setCellValueFactory(cellData -> cellData.getValue().getNumberProperty());
		
		phoneTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> showPhoneInfo(observable.getValue()));
		
		providerTextField.textProperty().addListener((l, o, n) -> {
			if(l.getValue().length() > 0) {
				addProviderButton.setDisable(false);
			} else {
				addProviderButton.setDisable(true);
			}
		});
		phoneTextField.textProperty().addListener((l, o, n) -> {
			if(l.getValue().length() > 2 && l.getValue().length() < 5 && !Provider.isThereSameNumber(l.getValue())) {
				chooserProviderForPhone.setDisable(false);
				if(chooserProviderForPhone.getValue() != null) {
					chooseArtOfPhone.setDisable(false);
				}
				if(chooserProviderForPhone.getValue() != null) {
					addPhoneButton.setDisable(false);
				}
			} else {
				chooserProviderForPhone.setDisable(true);
				chooseArtOfPhone.setDisable(true);
				addPhoneButton.setDisable(true);
			}
			if (!n.matches("\\d*")) {
	            phoneTextField.setText(n.replaceAll("[^\\d]", ""));
	        }
		});
		phoneBalance.textProperty().addListener((l, o, n) -> {
			if(l.getValue().length() > 0 && l.getValue().length() < 4) {
				topUpButton.setDisable(false);
			} else {
				topUpButton.setDisable(true);
			}
			if (!n.matches("\\d*")) {
	            phoneBalance.setText(n.replaceAll("[^\\d]", ""));
	        }
		});
		chooserProviderForPhone.setOnAction(e -> {
			if(chooserProviderForPhone.getValue() != null) {
				chooseArtOfPhone.setDisable(false);
			} else {
				chooseArtOfPhone.setDisable(true);
			}
		});
		chooseArtOfPhone.setOnAction(e -> {
			if(chooserProviderForPhone.getValue() != null) {
				addPhoneButton.setDisable(false);
			} else {
				addPhoneButton.setDisable(true);
			}
		});
		updatePhoneBox();
	}
	
	/**
	 * Shows subscribers of this provider in phoneColumn, 
	 * called whenever the provider was changer or clicked onto.
	 * @param provider
	 */
	private void showSubscribers(Provider provider) {
		if(provider == null) { return; } //in case the method is called after deleting the last prvider.
		
		if(provider.getSubscriber().size() == 0) { 	//if provider has no subscribers, then there is nothing 
			phoneTable.setItems(null); 				//to be displayed.
			return; 
		}
		
		ObservableList<SmsHandy> subscribers = FXCollections.observableArrayList(); //ObsList for binding with ObsMap.
		
		for(String number : provider.getSubscriber().keySet()) {	//Populating the list with map values 
			subscribers.add(provider.getSubscriber().get(number));	//in order to set them into column.
		}
		phoneTable.setItems(subscribers);
		phoneTable.refresh();		//For some reason after changing the Map, List binded with it 
									//isn't automatically updated, therefore refresh().
	}
	
	/**
	 * Called whenever a phone number is clicked, displays all info about this "handy"
	 * @param handy
	 */
	private void showPhoneInfo(SmsHandy handy) {
		
		SmsHandy chosenPhone = handy;
		if(chosenPhone == null) { 						//In case the chosen phone, whose info is being displayed 
			typeOfPhone.setText("...");					//gets deleted, the whole info needs to be cleared,
			balanceType.setText("Balance");				//otherwise NullPtrExc.
			balanceValue.setText("...");
			phoneBalance.setDisable(true);
			topUpButton.setDisable(true);
			chooseAnotherProviderBox.setDisable(true);
			chooseAnotherProviderBox.setValue(null);
			changeProviderButton.setDisable(true);
			return; 
			}
		
		Provider providerOfPhone = chosenPhone.getProvider();
		
		//Different values and funtkionalities for different types of phones.
		if(chosenPhone instanceof PrepaidSmsHandy) { 
			typeOfPhone.setText("Prepaid SMS");
			balanceType.setText("Credits:");
			int balance = chosenPhone.getProvider().getCreditForSmsHandy(chosenPhone.getNumber());
			balanceValue.setText(String.valueOf(balance));
			phoneBalance.setDisable(false);
			topUpButton.setDisable(false);
		}
		
		if(chosenPhone instanceof TariffPlanSmsHandy) {
			typeOfPhone.setText("Tariff Plan");
			balanceType.setText("Remaining SMS:");
			balanceValue.setText(String.valueOf(((TariffPlanSmsHandy) chosenPhone).getRemainingFreeSms()));
			phoneBalance.setDisable(true); 			//Balance options are not needed in TariffPlan.
			topUpButton.setDisable(true);
		}
		
		updateProviderBox();
		
		chooseAnotherProviderBox.setDisable(false);
		chooseAnotherProviderBox.getSelectionModel().select(chosenPhone.getProvider().getName());
		chooseAnotherProviderBox.setOnAction((e) -> {
			if(chosenPhone.getProvider().getName()!=chooseAnotherProviderBox.getValue()) {
				changeProviderButton.setDisable(false);
			} else {
				changeProviderButton.setDisable(true);
			}
		});
		topUpButton.setOnAction((e) -> {
				providerOfPhone.deposit(chosenPhone.getNumber(), Integer.valueOf(phoneBalance.getText()));
				showPhoneInfo(handy);
		});
		
	}
	
	/**
	 * Calls method changeProvider in Provider class.
	 * @throws InvalidNumberException
	 * @throws InvalidProviderException
	 */
	@FXML
	private void changeProvider() throws InvalidNumberException, InvalidProviderException {
		SmsHandy chosenPhone = phoneTable.getSelectionModel().getSelectedItem();
		Provider oldProvider = chosenPhone.getProvider();
		chosenPhone.getProvider().changeProvider(chosenPhone, chooseAnotherProviderBox.getValue());
		showSubscribers(oldProvider);
		showPhoneInfo(null);
	}
	
	/**
	 * creates new Provider
	 */
	@FXML
	private void addNewProvider() {
		new Provider(providerTextField.getText());
		providerTextField.clear();
		updateProviderBox();
	}
	
	/**
	 * deletes Provider chosen in combobox.
	 */
	@FXML
	private void deleteProvider() {
		if(Provider.providerList.isEmpty()) {
			return;
		}
		if(!chooseDeleteProviderBox.getValue().isEmpty()) {
			ObservableList<Provider> providerToDelete = FXCollections.observableArrayList(Provider.findProviderByName(chooseDeleteProviderBox.getValue()));
			phoneTable.setItems(null);
			Provider.providerList.removeAll(providerToDelete);
			updateProviderBox();
			updatePhoneBox();
		}
		
	}
	
	/**
	 * Since the logic is fairly simple, decided to implement in controlled instead of a model.
	 * @throws InvalidNumberException
	 * @throws InvalidProviderException
	 */
	@FXML
	private void addNewPhone() throws InvalidNumberException, InvalidProviderException {
		Provider provider = Provider.findProviderByName(chooserProviderForPhone.getValue());
		@SuppressWarnings("unused")
		SmsHandy newHandy;
		if(chooseArtOfPhone.getValue() == "Tariff Plan") {
			newHandy = new TariffPlanSmsHandy(phoneTextField.getText(), provider);
		}
		if(chooseArtOfPhone.getValue() == "Prepaid") {
			newHandy = new PrepaidSmsHandy(phoneTextField.getText(), provider);
		}
		phoneTextField.clear();
		if(!providerTable.getSelectionModel().isEmpty()) {
			if(providerTable.getSelectionModel().getSelectedItem().equals(provider)) {
				showSubscribers(provider);
			}
		} else {
			showSubscribers(provider);
		}
		updatePhoneBox();
	}
	
	/**
	 * deletes phone chosen in combobox.
	 */
	@FXML
	private void deletePhone() {
		String phone = choosePhoneToDelete.getValue();
		Provider provider = Provider.findProviderFor(phone);
		provider.getSubscriber().remove(phone);
		updatePhoneBox();
		if(providerTable.getSelectionModel().getSelectedItem().equals(provider)) {
			showSubscribers(provider);
		}
	}
	
	/**
	 * updates all provider boxes in UI according to current providers
	 */
	@FXML
	private void updateProviderBox() {
		chooseAnotherProviderBox.getItems().clear();
		chooseDeleteProviderBox.getItems().clear();
		chooserProviderForPhone.getItems().clear();
		if(Provider.providerList.isEmpty()) {
			return;
		}
		Provider.providerList.forEach(provider -> {
			chooseAnotherProviderBox.getItems().add(provider.getName());
			chooseDeleteProviderBox.getItems().add(provider.getName());
			chooserProviderForPhone.getItems().add(provider.getName());
		});
	}
	
	/**
	 * connects this controller to the Application class.
	 * @param networkApp
	 */
	public void setNetworkApp(NetworkApp networkApp) {
		this.networkApp = networkApp;
		providerTable.setItems(Provider.providerList);
	}

	/**
	 * updates the phonebox after adding or deleting a phone.
	 */
	private void updatePhoneBox() {
		choosePhoneToDelete.getItems().clear();
		Provider.providerList.forEach(p -> {
			if(!p.getSubscriber().isEmpty()) {
				p.getSubscriber().forEach((k,v) -> {
					choosePhoneToDelete.getItems().add(k);
				});
			}
		});
	}
}
