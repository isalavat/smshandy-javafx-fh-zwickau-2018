package smshandy.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import smshandy.exceptions.InvalidContentException;
import smshandy.exceptions.InvalidNumberException;
import smshandy.exceptions.InvalidProviderException;
import smshandy.model.Message;
import smshandy.model.PrepaidSmsHandy;
import smshandy.model.Provider;
import smshandy.model.TariffPlanSmsHandy;

public class ProviderTest {
	private static Provider o2;
	private static Provider nettoKom;
	private static Provider vadafone;
	private static PrepaidSmsHandy prepSmsHandyO2;
	private static TariffPlanSmsHandy tarSmsHandyNetKom;
	private static PrepaidSmsHandy secondPrepSmsHandyO2;
	private static TariffPlanSmsHandy secondSmsHandyNetKom;
	private static PrepaidSmsHandy thirdPrepSmsHandyO2;
	private static TariffPlanSmsHandy thirdTariffPlanSmsHandy;
	@BeforeClass
	public static void inctanceAllObjects() throws InvalidNumberException, InvalidProviderException {
		o2 = new Provider("A2");
		nettoKom = new Provider("O2");
		vadafone = new Provider("Netto");
		prepSmsHandyO2 = new PrepaidSmsHandy("123", o2);
		secondPrepSmsHandyO2 = new PrepaidSmsHandy("322", o2);
		thirdPrepSmsHandyO2 = new PrepaidSmsHandy("3212", o2);
		thirdTariffPlanSmsHandy = new TariffPlanSmsHandy("2222", vadafone);
		tarSmsHandyNetKom = new TariffPlanSmsHandy("321", vadafone);
		secondSmsHandyNetKom = new TariffPlanSmsHandy("999", vadafone);
	}
	
		
	@Test
	public void sendToAnothorProviderTest() throws InvalidContentException {
		String messageText = "Hi";
		prepSmsHandyO2.sendSms(tarSmsHandyNetKom.getNumber(), messageText);
		Assert.assertTrue(prepSmsHandyO2.getSent().get(0).getContent().equals(messageText));
		Assert.assertTrue(tarSmsHandyNetKom.getReceived().get(0).getContent().equals(messageText));
	}
	
	@Test
	public void sendWithinProviderTest() throws InvalidContentException {
		String messageText = "Hi 1";
		secondPrepSmsHandyO2.sendSms(secondSmsHandyNetKom.getNumber(), messageText);
		Assert.assertTrue(secondPrepSmsHandyO2.getSent().get(0).getContent().equals(messageText));
		Assert.assertTrue(secondSmsHandyNetKom.getReceived().get(0).getContent().equals(messageText));
	}
	
	@Test
	public void balanceTest() throws InvalidContentException {
		int balanceBefore = 
				thirdPrepSmsHandyO2.getProvider().getCreditForSmsHandy(thirdPrepSmsHandyO2.getNumber());
		thirdPrepSmsHandyO2.sendSms(thirdTariffPlanSmsHandy.getNumber(), "Hello");
		int balanceAfter = 
				thirdPrepSmsHandyO2.getProvider().getCreditForSmsHandy(thirdPrepSmsHandyO2.getNumber());
		
		Assert.assertTrue(balanceBefore-balanceAfter == 10);
		int countOfFreeSmsBefore = thirdTariffPlanSmsHandy.getRemainingFreeSms();
		thirdTariffPlanSmsHandy.sendSms(thirdPrepSmsHandyO2.getNumber(), "How are you");
		int countOfFreeSmsAfter = thirdTariffPlanSmsHandy.getRemainingFreeSms();
		Assert.assertTrue(countOfFreeSmsBefore - countOfFreeSmsAfter == 1);
	}
	
}








