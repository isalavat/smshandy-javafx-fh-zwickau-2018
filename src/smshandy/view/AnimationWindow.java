package smshandy.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AnimationWindow {
	private Stage animationStage;
	private Scene scene;
	private Pane rootPane;
	private AnimationTimer timer;
	private ImageView phoneImgView1;
	private ImageView phoneImgView2;
	private ImageView envelopImgView;
	private int counter = 0;
	private final int MAX_COUNTER_VALUE = 93;
	private final int ENVELOP_MOVING_SPEED = 3;
	private final int ENVELOP_START_X_VALUE = 90;
	
	public AnimationWindow() {
		createAnimationStage();
	}
	
	/**
	 * Initialization of all gui components
	 */
	private void createAnimationStage() {
		animationStage = new Stage();
		animationStage.initModality(Modality.APPLICATION_MODAL);
		animationStage.initStyle(StageStyle.UNDECORATED);
		rootPane = new Pane();
		scene = new Scene(rootPane);
		rootPane.setPrefWidth(500);
		rootPane.setPrefHeight(150);
		 
		animationStage.setScene(scene);
		
		try(InputStream fis = new FileInputStream("res/phone.png")){
			double width = 100;
			double height = 100;
			phoneImgView1 = new ImageView(new Image(fis));
			phoneImgView1.setFitHeight(height);
			phoneImgView1.setFitWidth(width);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try(InputStream fis = new FileInputStream("res/phone.png")){
			double width = 100;
			double height = 100;
			phoneImgView2 = new ImageView(new Image(fis));
			phoneImgView2.setFitHeight(height);
			phoneImgView2.setFitWidth(width);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try(InputStream fis = new FileInputStream("res/envelop.png")){
			envelopImgView = new ImageView(new Image(fis));
			envelopImgView.setFitHeight(40);
			envelopImgView.setFitWidth(40);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		rootPane.getChildren().add(envelopImgView);
		envelopImgView.setX(ENVELOP_START_X_VALUE);
		envelopImgView.setY(55);
		rootPane.getChildren().add(phoneImgView1);
		phoneImgView1.relocate(20, 15);
		rootPane.getChildren().add(phoneImgView2);
		phoneImgView2.relocate(380, 15);
		
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				animateMessageSending();
			}
		};
	}
	
	/**
	 * Shows animation window and starts the message sending animation 
	 */
	public void showMessageSendingAnimation() {
		animationStage.show();
		timer.start();
		delay(1000);
	}
	
	/**
	 * Animates message sending 
	 */
	private void animateMessageSending() {
		counter++;
		
		if(counter < MAX_COUNTER_VALUE) {
			envelopImgView.setX((envelopImgView.getX()+ENVELOP_MOVING_SPEED));
		}else {
			delay(1000);
			counter = 0;
			envelopImgView.setX(ENVELOP_START_X_VALUE);
			timer.stop();
			animationStage.close();
		}
		
	}
	
	/**
	 * Delays by a given millisecond
	 * @param duration
	 */
	private void delay(int duration) {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
