package smshandy.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import smshandy.exceptions.InvalidContentException;
import smshandy.exceptions.InvalidNumberException;
import smshandy.exceptions.InvalidProviderException;

public abstract class SmsHandy {
	
	private String number;
	private Provider provider;
	private ObservableList<Message> sent;
	private ObservableList<Message> received;
	private final PropertyChangeSupport propertySupport;
	/**
	 * Constructor for objects of class SmsHandy
	 * @param number
	 * @param provider - current numbers provider
	 * @throws InvalidProviderException 
	 * @throws InvalidNumberException
	 */
	public SmsHandy(String number, Provider provider) throws InvalidNumberException, InvalidProviderException{
		if(provider == null) {
			throw new InvalidProviderException("Provider is null.");
		}
		
		if(provider.isThereSameNumber(number)) {
			throw new InvalidNumberException("The same number yet exist");
		}
		
		if(number.isEmpty() || number == null || !number.matches("[0-9]{3,5}")) {
			throw new InvalidNumberException("Invalid number. The number supposed to contain only 3-5 digits charakters.");
		}
		
		sent = FXCollections.observableArrayList();
		received = FXCollections.observableArrayList();
		this.number = number;
		this.provider = provider;
		this.provider.register(this);
		this.propertySupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * Sends sms to another through the provider
	 * @param to
	 * @param content
	 */
	public void sendSms (String to, String content) throws InvalidContentException{
		Message message = new Message(content, to, number, new Date());
		provider.send(message);
		sent.add(message);
	}
	
	/**
	 * Abstract method to check if the sending 
	 * of the SMS can still be paid
	 * @return
	 */
	public abstract boolean canSendSms();
	
	/**
	 * Abstract method for paying SMS shipping.
	 */
	public abstract void payForSms();
	
	/**
	 * Sends an SMS without the provider to the recipient
	 * @param peer
	 * @param content
	 */
	public void sendSmsDirect(SmsHandy peer, String content) throws InvalidContentException{
		Message message = new Message(content, peer.getNumber(), number, new Date());
		message.toString();
		peer.receiveSms(message);
	}
	
	/**
	 * Receives an SMS and saves it in the received SMS
	 * @param message
	 */
	public void receiveSms(Message message) {
		received.add(message);
	}
	
	/**
	 * Returns a list of all SMS messages sent on the console.
	 */
	public void listSent() {
		sent.forEach(message->System.out.println(message));
	}
	
	/**
	 * Returns a list of all received 
	 * text messages on the console.
	 */
	public void listReceived() {
		received.forEach(message->System.out.println(message));
	}
	
	
	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public String getNumber() {
		return number;
	}
	
	public StringProperty getNumberProperty() {
		return new SimpleStringProperty(number);
	}

	public ObservableList<Message> getSent() {
		return sent;
	}

	public ObservableList<Message> getReceived() {
		return received;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void setPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
	
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertySupport;
    }
	
}
