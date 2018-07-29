package com.example.demofrontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


@Theme("VaadinTest")
public class UI_Farshad extends UI {

	private static final String PYTHON_CLASSIFIER_LOCATION = "/home/farshad/Python_Classifier/mainTestForJava.py";
	private static final String VERIOSN = "1.6";
	private static final long serialVersionUID = 5924433731101343240L;
	@SuppressWarnings("unused")
//	private static Logger LOG = Logger.getLogger(UI_Farshad.class);
//	private final TagPositions tagPositions = new TagPositions();

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

		final CheckBox enableTaggedText = new CheckBox("Show Annotated Text");
		enableTaggedText.setValue(false);

		final CheckBox enableClassifier = new CheckBox("Machine Learning Classifier");
		enableClassifier.setValue(false);

		final CheckBox enableChart = new CheckBox("Show Frequency Chart");
		enableChart.setValue(false);

		final CheckBox enableNER = new CheckBox("Use NER");
		enableNER.setValue(false);
		
		final CheckBox enableCustomeNER = new CheckBox("Use Custome NER");
		enableCustomeNER.setValue(false);

		final CheckBox selectWikipedia = new CheckBox("Use Wikipedia");
		selectWikipedia.setValue(true);

		final CheckBox selectWikidata = new CheckBox("Use Wikidata");
		selectWikidata.setValue(false);

		final Notification notif = new Notification(
				"At least one data source should be seletced",
				"",
				Notification.Type.HUMANIZED_MESSAGE);
		notif.setDelayMsec(3000);

		selectWikipedia.addValueChangeListener(event -> {
			if(!selectWikipedia.getValue()&&!selectWikidata.getValue()){
				selectWikipedia.setValue(true);
				notif.show(Page.getCurrent());
			}
		});

		selectWikidata.addValueChangeListener(event -> {
			if(!selectWikipedia.getValue()&&!selectWikidata.getValue()){
				selectWikidata.setValue(true);
				notif.show(Page.getCurrent());
			}
		});

		buttomLayout.addComponent(annotateButton);
		buttomLayout.addComponent(generateSampleText);
		buttomLayout.addComponent(enableChart);
		buttomLayout.addComponent(enableTaggedText);
		buttomLayout.addComponent(enableNER);		
		buttomLayout.addComponent(enableCustomeNER);		
		buttomLayout.addComponent(selectWikipedia);
		buttomLayout.addComponent(selectWikidata);
		buttomLayout.addComponent(enableClassifier);


		
		enableTaggedText.addValueChangeListener(event -> {
			//annotatedResult.setVisible(enableTaggedText.getValue());
		});

		enableChart.addValueChangeListener(event -> {
			
		});

		annotateButton.addClickListener(event -> {
			if(textArea.getValue().equals(null) || textArea.getValue()==""){
				final Notification notifi = new Notification(
					    "Please enter some text first",
					    "",
					    Notification.Type.HUMANIZED_MESSAGE);
				notifi.show(Page.getCurrent());
				return;
			}
			
			
			/**
			 * Using machine learning classifier
			 * By using it, we will just get positive or negative as output
			 * So no tagging
			 */
			if(enableClassifier.getValue()) {
				try {
					String result = null;
					String printResult = "";
					Process p = Runtime.getRuntime().exec("python2 "+PYTHON_CLASSIFIER_LOCATION+" "+textArea.getValue());					
					BufferedReader stdInput = new BufferedReader(new 
							InputStreamReader(p.getInputStream()));
					BufferedReader stdError = new BufferedReader(new 
							InputStreamReader(p.getErrorStream()));
					System.out.println("Here is the standard output of the command:\n");
					while ((result = stdInput.readLine()) != null) {
						System.out.println(result);
						printResult = result;
					}
					final Notification notification = new Notification(
							printResult,
							"",
							Notification.Type.HUMANIZED_MESSAGE);
					notification.setDelayMsec(3000);
					notification.show(Page.getCurrent());
					String s = null;
					// read any errors from the attempted command
					System.out.println("Here is the standard error of the command (if any):\n");
					while ((s = stdError.readLine()) != null) {
						System.out.println(s);
					}
					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				if(enableNER.getValue()){
					
				}	
				else if(enableCustomeNER.getValue()) {
					
				}
				else{
					
				}
			}
		});

		mainLayout.addComponent(
				new Label("<h1><Strong>Role Tagger Version " + VERIOSN + "</Strong></h1>", ContentMode.HTML));
		mainLayout.addComponent(textArea);
		mainLayout.addComponent(buttomLayout);
		mainLayout.addComponent(new Label("<Strong>Frequency Chart:</Strong>", ContentMode.HTML));

		HorizontalLayout chartsLayout = new HorizontalLayout();


		chartsLayout.setSpacing(true);
		chartsLayout.setMargin(true);
		mainLayout.addComponent(chartsLayout);
		mainLayout.addComponent(new Label("<hr />", ContentMode.HTML));
		mainLayout.addComponent(new Label("<Strong>Annotated Text:</Strong>", ContentMode.HTML));
	}

	private String annotateTextWihtCustomeNER(String text) {
		return null;
	}

	private Button createSampleTextButton(TextArea textArea) {
		final Button run = new Button("Sample Sentense");
		final List<String> sampleSentence = Arrays.asList("The Government of Barbados (GoB), is headed by the monarch, Queen Elizabeth II as Head of State.",
				"King Edward VII oversaw a partial redecoration in a \"Belle Époque\" cream and gold colour scheme.",
				"From 1993 until 2012, the President of the Czech Republic was selected by a joint session of the parliament for a five-year term, with no more than two consecutive terms (2x Václav Havel, 2x Václav Klaus).",
				"Simón Bolívar became the first President of Colombia, and Francisco de Paula Santander was made Vice President.");
		run.addClickListener(event -> {
			int randomNum = ThreadLocalRandom.current().nextInt(0, sampleSentence.size());
			textArea.clear();
			textArea.setValue(sampleSentence.get(randomNum));
		});
		return run;
	}


	
	private Button createButton() {
		final Button run = new Button("Annotate Roles");
		return run;
	}

	private TextArea createTextArea() {
		final TextArea textArea = new TextArea();
		//textArea.setImmediate(true);
		textArea.setSizeFull();
		return textArea;
	}
}
