package com.example.demofrontend;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("DemoFrontend")
public class DemoFrontendUI extends UI{
	private static final String VERIOSN = "1.6";

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
	 */
	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		setContent(mainLayout);

		final TextArea textArea = createTextArea();
		final HorizontalLayout buttomLayout = new HorizontalLayout();
		buttomLayout.setSpacing(true);
		final Button annotateButton = createButton();
		final Button generateSampleText = createSampleTextButton(textArea);
		final Notification notif = new Notification(
				"At least one data source should be selected",
				"",
				Notification.Type.HUMANIZED_MESSAGE);
		notif.setDelayMsec(3000);
		buttomLayout.addComponent(annotateButton);
		buttomLayout.addComponent(generateSampleText);
		
		mainLayout.addComponent(
				new Label("<h1><Strong>Knowledge Based Text Classification</Strong></h1>", ContentMode.HTML));
		mainLayout.addComponent(textArea);
		mainLayout.addComponent(buttomLayout);
		
		
		
		final HorizontalLayout bottomLayer = new HorizontalLayout();
		final TextArea listOfMentions = new TextArea();
		final Image kbtc = new Image(null,new ClassResource("smiley.jpg"));
		
		
		final VerticalLayout categoryLayout = new VerticalLayout();	
		
		final HorizontalLayout worldLayout = new HorizontalLayout();
		final Label worldCategoryName = new Label("World");
		final Label worldCategoryProbability  = new Label();
		worldLayout.addComponent(worldCategoryName);
		worldLayout.addComponent(worldCategoryProbability);	
		categoryLayout.addComponent(worldLayout);
		
		
		final HorizontalLayout businessLayout = new HorizontalLayout();
		final Label businessCategoryName = new Label("Business");
		final Label businessCategoryProbability  = new Label();
		worldLayout.addComponent(businessCategoryName);
		worldLayout.addComponent(businessCategoryProbability);	
		categoryLayout.addComponent(businessLayout);
		
		final HorizontalLayout sportsLayout = new HorizontalLayout();
		final Label sportsCategoryName = new Label("Sports");
		final Label sportsCategoryProbability  = new Label();
		worldLayout.addComponent(sportsCategoryName);
		worldLayout.addComponent(sportsCategoryProbability);	
		categoryLayout.addComponent(sportsLayout);
		
		final HorizontalLayout scienceLayout = new HorizontalLayout();
		final Label scienceCategoryName = new Label("Science");
		final Label scienceCategoryProbability  = new Label();
		worldLayout.addComponent(scienceCategoryName);
		worldLayout.addComponent(scienceCategoryProbability);	
		categoryLayout.addComponent(scienceLayout);
		
		
		bottomLayer.addComponent(listOfMentions);
		bottomLayer.addComponent(kbtc);
		bottomLayer.addComponent(categoryLayout);
//		HorizontalLayout chartsLayout = new HorizontalLayout();
//
//
//		
//		chartsLayout.setSpacing(true);
//		chartsLayout.setMargin(true);
//		mainLayout.addComponent(chartsLayout);
//		mainLayout.addComponent(new Label("<hr />", ContentMode.HTML));
		mainLayout.addComponent(bottomLayer);
		bottomLayer.setVisible(false);
		
		annotateButton.addClickListener(event -> {
			bottomLayer.setVisible(true);
			if(textArea.getValue().equals(null) || textArea.getValue()==""){
				final Notification notifi = new Notification(
					    "Please enter some text first",
					    "",
					    Notification.Type.HUMANIZED_MESSAGE);
				notifi.show(Page.getCurrent());
				return;
			}
			
			else {
				String text = textArea.getValue().toString(); 
				final Notification notifi = new Notification(
						Request_KBTC_category.sendHttpGet(text),
					    Notification.Type.HUMANIZED_MESSAGE);
				notifi.show(Page.getCurrent());
				notifi.setDelayMsec(5000);
			}
				
	
		});

	}
	private String annotateTextWihtCustomeNER(String text) {
		return null;
	}
	private Button createSampleTextButton(TextArea textArea) {
		final Button run = new Button("Sample Sentences");
		final List<String> sampleSentence = Arrays.asList("Albert Einstein was a German-born theoretical physicist who developed the theory of relativity, one of the two pillars of modern physics (alongside quantum mechanics)..",
				"FC Bayern München, FCB, Bayern Munich, or FC Bayern, is a German sports club based in Munich, Bavaria (Bayern).",
				"Berlin is the capital and the largest city of Germany, as well as one of its 16 constituent states.",
				"Dollar rises Vs Euro on asset flows data");
		run.addClickListener(event -> {
			int randomNum = ThreadLocalRandom.current().nextInt(0, sampleSentence.size());
			textArea.clear();
			textArea.setValue(sampleSentence.get(randomNum));
		});
		return run;
	}
	private Button createButton() {
		final Button run = new Button("Classify Text");
		return run;
	}

	private TextArea createTextArea() {
		final TextArea textArea = new TextArea();
		//textArea.setImmediate(true);
		textArea.setSizeFull();
		return textArea;
	}
//	
//	@Override
//	protected void init(VaadinRequest request){
//		
//		 VerticalLayout vlayout = new VerticalLayout();
//
//		    GridLayout mainLayout = new GridLayout(3, 9);
//		    mainLayout.setSpacing(true);
//		    mainLayout.setMargin(true);
//
//		    final TextField textArea = new TextField("Name of Employer: ");
//		    textArea.setWidth("100%");
//		    mainLayout.addComponent(textArea, 0, 0, 2, 0);
//
//		
//		
////		HorizontalLayout mainLayout = new HorizontalLayout();
////		
////		TextArea textArea = new TextArea();
////		textArea.setWidth("100%");
//		Button btn = new Button("Classify");
//		
////		mainLayout.addComponent(textArea);
//		mainLayout.addComponent(btn);
//		
//		btn.addClickListener(event -> {
//			String text = textArea.getValue();
//			
//			
//			String sendHttpGet = Request_KBTC_category.sendHttpGet(text);
//			if (sendHttpGet!=null) {
//				Notification.show(sendHttpGet);
//			}
//			else {
//				Notification.show("null");
//				
//			}
//			        
//		});		
//		setContent(mainLayout);
//	}
}
