package smshandy.model;

import smshandy.exceptions.InvalidContentException;
import smshandy.exceptions.InvalidNumberException;
import smshandy.exceptions.InvalidProviderException;

public class Test {
	
	public static void main(String args[]) throws InvalidNumberException, InvalidContentException, InvalidProviderException{
		
		Provider p1 = new Provider("");
		Provider p2 = new Provider("");
		
		PrepaidSmsHandy h1 = new PrepaidSmsHandy("12443", p1);
		TariffPlanSmsHandy h2 = new TariffPlanSmsHandy("321", p1);
		PrepaidSmsHandy h3 = new PrepaidSmsHandy("543", p2);
		
//		h1.sendSms(h2.getNumber(), "Hello");
//		System.out.println(h2.getProvider().getCreditForSmsHandy(h1.getNumber()));
		
//		h2.sendSms(h1.getNumber(), "Zdarova");
//		System.out.println(h2.getRemainingFreeSms());
		
		h3.sendSms(h1.getNumber(), "Hi there different provider!");
//		h3.sendSms(h2.getNumber(), "Hi again different provider!");
		
//		h1.sendSms("*101#", "What is my balance?");
//		h2.sendSms("*101#", "I would like to know that too.");
//		h3.sendSms("*101#", "Tell me my balance.");

		
//		System.out.println("heh");
		h1.listReceived();
		System.out.println("heh");
//		h2.listReceived();
//		System.out.println("heh");
//		h3.listReceived();
	}
}
