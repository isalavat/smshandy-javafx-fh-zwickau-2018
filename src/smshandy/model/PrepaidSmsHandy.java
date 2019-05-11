package smshandy.model;

import smshandy.exceptions.InvalidNumberException;
import smshandy.exceptions.InvalidProviderException;

public class PrepaidSmsHandy extends SmsHandy{
	private final int COST_PER_SMS = 10;
	/**
	 * Constructor for creating a new PrepaidSmsHandy 
	 * @param number
	 * @param provider
	 * @throws InvalidProviderException 
	 */
	public PrepaidSmsHandy(String number, Provider provider) throws InvalidNumberException, InvalidProviderException{
		super(number, provider);
//		super.getProvider().deposit(super.getNumber(), 100);
	}

	@Override
	public boolean canSendSms() {
		return super.getProvider().getCreditForSmsHandy(super.getNumber()) >= COST_PER_SMS ;
	}

	@Override
	public void payForSms() {
		if(canSendSms()) {
			super.getProvider().deposit(super.getNumber(), -COST_PER_SMS);
		}
	}
	
	/**
	 * Reloads the credit for the SmsHandy object.
	 * @param amount
	 */
	public void deposit(int amount) {
		super.getProvider().deposit(super.getNumber(), amount);;
	}

}
