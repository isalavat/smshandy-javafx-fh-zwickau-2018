package smshandy.model;

//import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import smshandy.exceptions.InvalidContentException;

public class Message {
	
	private String content;
	private Date date;
	private String from;
	private String to;
	
	public Message() {
	}
	
	/**
	 * 
	 * @param content - message body
	 * @param to - receiver
	 * @param from - sender
	 * @param date - current
	 * @throws InvalidContentException
	 */
	public Message(String content, String to, String from, Date date) throws InvalidContentException{
		if(content.isEmpty()) {
			throw new InvalidContentException("Message content is empty.");
		}
		if(content.length()>70) {
			throw new InvalidContentException("Message content supposed to be no longer than 70 charakters.");
		}
		this.content = content;
		this.to = to;
		this.from = from;
		this.date = date;
	}
	

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}
	
	
	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}
	
	public void setTo(String to) {
		this.to = to;
	}
	
	public StringProperty getRecievedMessageInfoProperty() {
		return new SimpleStringProperty("from "+from+": "+content);
	}
	
	public StringProperty getSentMessageInfoProperty() {
		return new SimpleStringProperty("to "+to+": "+content);
	}
	
	public StringProperty getMessageDetailsProperty() {
		return new SimpleStringProperty("from: "+from+"\nto: "+to+" \nDate: "+date.toString()+"\nContent: "+content);
	}
	
	@Override
	public String toString() {
		
		String message = "Sender: " + from + ", Receiver: " + to + 
				", Date: " + date.toString() + '\n' + "Message: " + content + '\n';
		return message;
	}


}
