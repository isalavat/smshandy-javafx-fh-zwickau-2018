package smshandy.model;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import smshandy.exceptions.InvalidContentException;
import smshandy.exceptions.InvalidNumberException;
import smshandy.exceptions.InvalidProviderException;

public class Provider {
	
	public static ObservableList<Provider> providerList = null;
	private String name;
	private ObservableMap<String, IntegerProperty> credits;
	private ObservableMap<String, SmsHandy> subscriber;
	/**
	 * credits HashMap for managing balance of customers
	 * subscribers HashMap for managing of customers 
	 */
	public Provider(String name) {
		credits = FXCollections.observableHashMap();		//String - number, Integer - amount
		subscriber = FXCollections.observableHashMap();		//String - number, SmsHandy - phone
		this.name = name;
		if(providerList == null) {
			providerList = FXCollections.observableArrayList();
		}
		providerList.add(this);
	}
	
	public boolean send(Message message) throws InvalidContentException {

		Provider providerFrom = findProviderFor(message.getFrom());
		SmsHandy from = providerFrom.subscriber.get(message.getFrom());
		
		if(message.getTo().equals("*101#") ) {
			Message balance;
			if(from instanceof PrepaidSmsHandy) {
				balance = new Message(credits.get(message.getFrom()).toString(), message.getFrom(), "Operator", new Date());
			} else {
				balance = new Message(String.valueOf(((TariffPlanSmsHandy)from).getRemainingFreeSms()), message.getFrom(), "Operator", new Date());
			}			
			subscriber.get(message.getFrom()).receiveSms(balance);
			return true;
		}
		
		Provider providerTo = findProviderFor(message.getTo());
		SmsHandy to = providerTo.subscriber.get(message.getTo());
		
		if (from.canSendSms()) {
			from.payForSms();
			to.receiveSms(message);
			return true;
		}
		
		return false;
	}	
	
	public void register(SmsHandy handy) {
		subscriber.put(handy.getNumber(), handy);
		if(handy instanceof PrepaidSmsHandy) {
			credits.put(handy.getNumber(), new SimpleIntegerProperty(100));
		}
	}
	
	public void unregister(SmsHandy handy) {
		subscriber.remove(handy.getNumber(), handy);
		if(handy instanceof PrepaidSmsHandy) {
			credits.remove(handy.getNumber());
		}
	}
	
	public void changeProvider(SmsHandy handy, String provider) throws InvalidNumberException, InvalidProviderException {
		Provider newProvider = findProviderByName(provider);
		this.unregister(handy);
		newProvider.register(handy);
		handy.setProvider(newProvider);
	}
	
	/**
	 * @param number - phone number
	 * @param amount - credits to put on balance
	 */
	public void deposit(String number, int amount) {
		int balance = credits.get(number).get();
		Provider depositProvider = findProviderFor(number);
		depositProvider.credits.put(number, new SimpleIntegerProperty(balance + amount));
	}
	
	public int getCreditForSmsHandy(String number) {
		return credits.get(number).get();
	}
	
	
	
	public ObservableMap<String, IntegerProperty> getCredits() {
		return credits;
	}

	public ObservableMap<String, SmsHandy> getSubscriber() {
		return subscriber;
	}
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public StringProperty getNameProperty() {
		return new SimpleStringProperty(name);
	}

	/**
	 * 
	 * @param receiver
	 * @return checks, whether receiver is a subscriber of a current provider 
	 */
	private boolean canSendTo(String receiver) {
		return this.subscriber.containsKey(receiver);
	}
	
	/**
	 * 
	 * @param receiver
	 * @return returns the provider of the receiver or if there is no such number - returns null. 
	 */
	public static Provider findProviderFor(String receiver) {
//		return providerList.stream().filter(provider -> provider.canSendTo(receiver)).findFirst().orElse(null);
		for(Provider p : providerList) {
			if(p.canSendTo(receiver)) {
				return p;
			}
		}
		return null;
	}
	
	public static boolean isThereSameNumber(String number) {
		for (Provider provider : providerList) {
			if (provider.subscriber.containsKey(number)) {
				return true;
			};
		}
		return false;
	}
	
	public static Provider findProviderByName(String name) {
		Provider searchResult = null;
		List<Provider> searchResults = providerList.stream().filter(provider -> 
			provider.getName().equals(name)).collect(Collectors.toList());
		if(searchResults.size() > 0) {
			searchResult = searchResults.get(0);
		}
		return searchResult;
	}
}
