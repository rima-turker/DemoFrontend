package com.example.demofrontend;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONObject;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import model.FullTextNewsResult;
import model.PartialTextNewsResult;
import requests.NewsRequest;
import requests.Request_KBTC_category;
import requests.WikipeidaRequest;
import util.SentenceSegmenter;

@Theme("DemoFrontend")
public class DemoFrontendUI
        extends
        UI
{
    private static final long serialVersionUID = -5624854082186941128L;
    final TextArea textArea = createTextArea();
    String textAreaContent = "";
    final Label numberOfSentencesValue = new Label("0");
    private final RichTextArea highlightedResult = new RichTextArea();
    private VerticalLayout resultCategoryLayout;


    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
     */
    @Override
    protected void init(
            VaadinRequest request)
    {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        setContent(mainLayout);

        final GridLayout buttonLayout = new GridLayout(10,1);
        buttonLayout.setSpacing(true);
        buttonLayout.setSizeFull();
        final Button annotateButton = createButton();
        final Button generateSampleText = createSampleTextButton();
        final Notification notif = new Notification(
                "At least one data source should be selected",
                "",
                Notification.Type.HUMANIZED_MESSAGE);
        notif.setDelayMsec(3000);
        
        buttonLayout.addComponent(generateSampleText,0,0);
        buttonLayout.setComponentAlignment(generateSampleText, Alignment.MIDDLE_LEFT);
        
        HorizontalLayout createNumberOfSentenceComponents = createNumberOfSentenceComponents();
		buttonLayout.addComponent(createNumberOfSentenceComponents,1,0);
        buttonLayout.setComponentAlignment(createNumberOfSentenceComponents, Alignment.MIDDLE_LEFT);
        
        buttonLayout.addComponent(annotateButton,9,0);
        buttonLayout.setComponentAlignment(annotateButton, Alignment.MIDDLE_RIGHT);

        mainLayout.addComponent(
                new Label(
                        "<h1><Strong>Knowledge Based Text Classification</Strong></h1>",
                        ContentMode.HTML));
        mainLayout.addComponent(textArea);
        mainLayout.addComponent(buttonLayout);

        final Component bottomLayer = createResultComponent();
        bottomLayer.setVisible(false);

        mainLayout.addComponent(createNewComponents());
        mainLayout.addComponent(bottomLayer);

        annotateButton.addClickListener(event -> {
            if (textArea.getValue().equals(null) || textArea.getValue().isEmpty()) {
                final Notification notifi = new Notification(
                        "Please enter some text first",
                        "",
                        Notification.Type.HUMANIZED_MESSAGE);
                notifi.show(Page.getCurrent());
                return;
            } else {
                bottomLayer.setVisible(true);

                String[] mentions = Request_KBTC_category.sendHttpGetMentions(textArea.getValue()).split(",");
                
                String text = textArea.getValue();
                for(String mention:mentions) {
                	text = text.replace(mention.trim(), "<mark>"+mention.trim()+"</mark>");
                }
                
                highlightedResult.setValue(text);
                
                final Map<String, Double> resultList = Request_KBTC_category.sendHttpGetCategoryList(textArea.getValue());
                
                resultCategoryLayout.removeAllComponents();
                
                final DecimalFormat formatter = new DecimalFormat("#.##");
                for(Entry<String, Double> e:resultList.entrySet()) {
                	final HorizontalLayout catLayout = new HorizontalLayout();
                    final Label categoryName = new Label(e.getKey()+":");
                    final Label categoryProbability = new Label(String.valueOf(formatter.format(e.getValue())));
                    catLayout.addComponent(categoryName);
                    catLayout.addComponent(categoryProbability);
                    resultCategoryLayout.addComponent(catLayout);
                }
            }

        });

    }

    private Component createResultComponent() {
        Panel panel = new Panel("Result:");
        panel.setSizeFull();

        final GridLayout bottomLayer = new GridLayout(10, 1);
        
        highlightedResult.setEnabled(false);
        highlightedResult.setReadOnly(true);
        highlightedResult.setSizeFull();
        highlightedResult.setHeight("250px");
        
        resultCategoryLayout = new VerticalLayout();
        bottomLayer.addComponent(highlightedResult,0,0,7,0);
        bottomLayer.addComponent(resultCategoryLayout,8,0,9,0);
        
        bottomLayer.setSizeFull();
        bottomLayer.setSpacing(true);
        bottomLayer.setMargin(true);

        panel.setContent(bottomLayer);
        return panel;
    }

    private Button createSampleTextButton() {
        final Button run = new Button("Sample Sentences");
        final List<String> sampleSentence = Arrays.asList(
                "Albert Einstein was a German-born theoretical physicist who developed the theory of relativity, one of the two pillars of modern physics (alongside quantum mechanics).",
                "FC Bayern München, FCB, Bayern Munich, or FC Bayern, is a German sports club based in Munich, Bavaria (Bayern).",
                "Berlin is the capital and the largest city of Germany, as well as one of its 16 constituent states.",
                "Dollar rises Vs Euro on asset flows data.");
        run.addClickListener(event -> {
            int randomNum = ThreadLocalRandom.current().nextInt(0,
                    sampleSentence.size());
            textAreaContent = sampleSentence.get(randomNum);
            textArea.setValue(textAreaContent);
            numberOfSentencesValue.setValue(String.valueOf(
                    SentenceSegmenter.getNumberOfSentence(textAreaContent)));
        });
        return run;
    }

    private Button createButton() {
        final Button run = new Button("Classify Text");
        run.setStyleName("contrast primary");
        return run;
    }

    private TextArea createTextArea() {
        final TextArea textArea = new TextArea();
        textArea.setSizeFull();
        textArea.setRows(10);
        textArea.addValueChangeListener(event->{
        	textAreaContent = event.getValue();
        });
        return textArea;
    }

    private Component createNewComponents() {
        final VerticalLayout mainLayout = new VerticalLayout();

        final Component wikipediaLayout = createWikipeidaComponents();
        final Component newsFullTextLayout = createFullTextNewsComponent();
        final Component newsShortTextLayout = createTitleAndDescriptionnewsComponents();

        mainLayout.addComponent(wikipediaLayout);
        mainLayout.addComponent(newsFullTextLayout);
        mainLayout.addComponent(newsShortTextLayout);

        return mainLayout;
    }

    private Component createTitleAndDescriptionnewsComponents() {
        Panel panel = new Panel("Random Short Text News:");
        panel.setSizeFull();

        final HorizontalLayout newsLayout = new HorizontalLayout();
        newsLayout.setMargin(true);
        newsLayout.setSpacing(true);

        final Link url = new Link();
        url.setVisible(false);

        final Label sourceOfNewsLabel = new Label("Source of the News: ");
        sourceOfNewsLabel.setVisible(false);

        final Label sourceOfNews = new Label();
        sourceOfNews.setVisible(false);

        final Button newsBtn = new Button("Get a random news");

        newsBtn.addClickListener(event -> {
            try {
                    final PartialTextNewsResult result = NewsRequest
                            .requestNewsApi();
                    if (result != null) {
                        textAreaContent = result.getTitle() + "\n"
                                + result.getDescription();
                        textArea.setValue(textAreaContent);
                        url.setCaption(
                                "Want to see the news page? Click here...");
                        url.setResource(new ExternalResource(result.getLink()));
                        url.setVisible(true);
                        sourceOfNews.setVisible(true);
                        sourceOfNewsLabel.setVisible(true);
                        sourceOfNews.setValue(result.getSource());

                        numberOfSentencesValue
                                .setValue(String.valueOf(SentenceSegmenter
                                        .getNumberOfSentence(textAreaContent)));

                    } else {
                        final Notification notifi = new Notification(
                                "Can not fecth any news.",
                                Notification.Type.ERROR_MESSAGE);
                        notifi.show(Page.getCurrent());
                        notifi.setDelayMsec(5000);
                    }
            } catch (Exception e) {
            	final Notification notifi = new Notification(
                        "Can not fecth any news.",
                        Notification.Type.ERROR_MESSAGE);
                notifi.show(Page.getCurrent());
                notifi.setDelayMsec(5000);
            }

        });

        newsLayout.addComponent(newsBtn);
        newsLayout.addComponent(sourceOfNewsLabel);
        newsLayout.addComponent(sourceOfNews);
        newsLayout.addComponent(url);
        panel.setContent(newsLayout);

        return panel;
    }

    private Component createFullTextNewsComponent() {
        Panel panel = new Panel("Random Full Text News:");
        panel.setSizeFull();

        final HorizontalLayout newsLayout = new HorizontalLayout();
        newsLayout.setMargin(true);
        newsLayout.setSpacing(true);

        final Link url = new Link();
        url.setVisible(false);

        final Label sourceOfNewsLabel = new Label("Source of the News: ");
        sourceOfNewsLabel.setVisible(false);

        final Label sourceOfNews = new Label();
        sourceOfNews.setVisible(false);

        final Button newsBtn = new Button("Get a random news");

        newsBtn.addClickListener(event -> {
            try {
                    final FullTextNewsResult result = NewsRequest
                            .requestNewsWebhose();
                    if (result != null) {
                    	textAreaContent = result.getFullText();
                        textArea.setValue(textAreaContent);
                        url.setCaption(
                                "Want to see the news page? Click here...");
                        url.setResource(new ExternalResource(result.getLink()));
                        url.setVisible(true);
                        sourceOfNews.setVisible(true);
                        sourceOfNewsLabel.setVisible(true);
                        sourceOfNews.setValue(result.getSource());

                        numberOfSentencesValue
                                .setValue(String.valueOf(SentenceSegmenter
                                        .getNumberOfSentence(textAreaContent)));
                    } else {
                        final Notification notifi = new Notification(
                                "Can not fecth any news.",
                                Notification.Type.ERROR_MESSAGE);
                        notifi.show(Page.getCurrent());
                        notifi.setDelayMsec(5000);
                    }
            } catch (Exception exception) {
            	final Notification notifi = new Notification(
                        "Can not fecth any news.",
                        Notification.Type.ERROR_MESSAGE);
                notifi.show(Page.getCurrent());
                notifi.setDelayMsec(5000);
            }

        });

        newsLayout.addComponent(newsBtn);
        newsLayout.addComponent(sourceOfNewsLabel);
        newsLayout.addComponent(sourceOfNews);
        newsLayout.addComponent(url);
        panel.setContent(newsLayout);
        return panel;
    }

    private Component createWikipeidaComponents() {
        Panel panel = new Panel("Wikipedia Entity:");
        panel.setSizeFull();

        final HorizontalLayout wikipediaLayout = new HorizontalLayout();
        wikipediaLayout.setMargin(true);
        wikipediaLayout.setSpacing(true);

        final Label wikipeidaLabel = new Label("Enter Wikipeida Entity:");
        final TextField wikipeidatextField = new TextField();
        final Button wikipeidatextGetBtn = new Button("Get Wikipedia Abstract");
        final Link link = new Link();
        link.setTargetName("_blank");
        link.setVisible(false);

        wikipeidatextGetBtn.addClickListener(event -> {
            String wikipeidaEntity = wikipeidatextField.getValue();
            wikipeidaEntity = wikipeidaEntity.replace(" ", "_");
            final String wikipeidaJson = WikipeidaRequest
                    .requestWikipeida(wikipeidaEntity);
            String content = extractAbstractFromJson(wikipeidaJson);
            if (content == null || content.isEmpty()
                    || content.endsWith("may refer to:"))
            {
                final Notification notifi = new Notification(
                        "Can not find the entity "
                                + wikipeidatextField.getValue(),
                        Notification.Type.ERROR_MESSAGE);
                notifi.show(Page.getCurrent());
                notifi.setDelayMsec(5000);
            } else {
                textAreaContent = content;
                textArea.setValue(textAreaContent);
                numberOfSentencesValue.setValue(String.valueOf(SentenceSegmenter
                        .getNumberOfSentence(textAreaContent)));
                final String pageLink = extractLinkFromJson(wikipeidaJson);
                link.setCaption(pageLink);
                link.setResource(new ExternalResource(pageLink));
                link.setVisible(true);
            }
        });

        wikipediaLayout.addComponent(wikipeidaLabel);
        wikipediaLayout.addComponent(wikipeidatextField);
        wikipediaLayout.addComponent(wikipeidatextGetBtn);
        wikipediaLayout.addComponent(link);

        panel.setContent(wikipediaLayout);
        return panel;
    }

    private String extractLinkFromJson(
            String wikipeidaJson)
    {
        final JSONObject obj = new JSONObject(wikipeidaJson);
        try {
            final JSONObject jsonObject1 = obj.getJSONObject("query");
            final JSONObject jsonObject2 = jsonObject1.getJSONObject("pages");
            final String pageId = jsonObject2.keys().next();
            final JSONObject jsonObject3 = jsonObject2.getJSONObject(pageId);

            return "https://en.wikipedia.org/wiki/"
                    + jsonObject3.getString("title").replace(" ", "_");
        } catch (Exception excpetion) {
            return null;
        }
    }

    private HorizontalLayout createNumberOfSentenceComponents() {
        final HorizontalLayout numberOfSentenceLayout = new HorizontalLayout();

        final Label numberOfSentencesLabel = new Label("Number Of Sentence:");

        final Button numberOfSentenceMinuesBtn = new Button("-");
        final Button numberOfSentencePlusBtn = new Button("+");

        numberOfSentenceMinuesBtn.addClickListener(event -> {
            int value = Integer.parseInt(numberOfSentencesValue.getValue());
            if (value > 1) {
                value--;
                numberOfSentencesValue.setValue(String.valueOf(value));
                final String sentences = SentenceSegmenter.getSentences(
                        Integer.parseInt(numberOfSentencesValue.getValue()),
                        textAreaContent);
                textArea.setValue(sentences);
            }
        });

        numberOfSentencePlusBtn.addClickListener(event -> {
            int value = Integer.parseInt(numberOfSentencesValue.getValue());
            if (value <= SentenceSegmenter
                    .getNumberOfSentence(textAreaContent))
            {
                value++;
                numberOfSentencesValue.setValue(String.valueOf(value));
                final String sentences = SentenceSegmenter.getSentences(
                        Integer.parseInt(numberOfSentencesValue.getValue()),
                        textAreaContent);
                textArea.setValue(sentences);
            }
        });

        numberOfSentenceLayout.addComponent(numberOfSentenceMinuesBtn);
        numberOfSentenceLayout.addComponent(numberOfSentencesLabel);
        numberOfSentenceLayout.addComponent(numberOfSentencesValue);
        numberOfSentenceLayout.addComponent(numberOfSentencePlusBtn);
        return numberOfSentenceLayout;
    }

    private String extractAbstractFromJson(
            String wikipeidaJson)
    {
        final JSONObject obj = new JSONObject(wikipeidaJson);
        try {
            final JSONObject jsonObject1 = obj.getJSONObject("query");
            final JSONObject jsonObject2 = jsonObject1.getJSONObject("pages");
            final String pageId = jsonObject2.keys().next();
            final JSONObject jsonObject3 = jsonObject2.getJSONObject(pageId);

            return jsonObject3.getString("title")+"\n"+jsonObject3.getString("extract");
        } catch (Exception excpetion) {
            return null;
        }
    }
}
