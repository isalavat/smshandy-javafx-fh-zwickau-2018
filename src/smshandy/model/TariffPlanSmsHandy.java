package smshandy.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import smshandy.exceptions.InvalidNumberException;
import smshandy.exceptions.InvalidProviderException;

public class TariffPlanSmsHandy extends SmsHandy {
	
	private IntegerProperty remainingFreeSms = new SimpleIntegerProperty(100);
	private final PropertyChangeSupport propertySupport;

	public TariffPlanSmsHandy(String number, Provider provider) throws InvalidNumberException, InvalidProviderException{
		super(number, provider);
		this.propertySupport = new PropertyChangeSupport(this);
	}

	@Override
	public boolean canSendSms() {
		if(remainingFreeSms.get()>0) {
			return true;
		}
		return false;
	}

	@Override
	public void payForSms() {
		remainingFreeSms.set(remainingFreeSms.get()-1);
	}

	public IntegerProperty getRemainingFreeSmsProperty() {
		return remainingFreeSms;
	}
	
	public int getRemainingFreeSms() {
		return remainingFreeSms.get();
	}
	
	public void setPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
	
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertySupport;
    }
}
