package com.example.demofrontend;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONObject;

import com.byteowls.vaadin.chartjs.config.ChartConfig;
import com.byteowls.vaadin.chartjs.config.PieChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.vaadin.annotations.Theme;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import model.Category;
import model.FullTextNewsResult;
import model.PartialTextNewsResult;
import requests.NewsRequest;
import requests.Request_KBTC_category;
import requests.WikipeidaRequest;
import util.ColorUtil;
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
    private TextArea listOfMentions;
    private VerticalLayout resultCategoryLayout;

    // final ChartJs pieChart = new ChartJs();
    // {
    // pieChart.setJsLoggingEnabled(true);
    // }

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

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        final Button annotateButton = createButton();
        final Button generateSampleText = createSampleTextButton();
        final Notification notif = new Notification(
                "At least one data source should be selected",
                "",
                Notification.Type.HUMANIZED_MESSAGE);
        notif.setDelayMsec(3000);
        buttonLayout.addComponent(annotateButton);
        buttonLayout.addComponent(generateSampleText);
        buttonLayout.addComponent(createNumberOfSentenceComponents());

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
                // pieChart.setVisible(true);
                
                listOfMentions.setValue(Request_KBTC_category.sendHttpGetMentions(textArea.getValue()).replaceAll(", ","\n"));
                String[] mentions = Request_KBTC_category.sendHttpGetMentions(textArea.getValue()).split(",");
                
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
//                String result = textArea.getValue();
//                for(String mention:mentions) {
//                	result = result.replace(mention.trim(), "<mark>"+mention.trim()+"</mark>");
//                }
//                textArea.setValue(result);
                
                
              //  System.err.println(Request_KBTC_category.sendHttpGetCategoryList(textAreaContent));
//                final Notification notifi = new Notification(
//                        Request_KBTC_category.sendHttpGet(textAreaContent),
//                        Notification.Type.HUMANIZED_MESSAGE);
              
//                
//                Map<String, Double> map = new HashMap<>(Request_KBTC_category.sendHttpGetCategoryList(textAreaContent));
//                for(Entry<String, Double> e : map.entrySet()) {
//                	System.err.println(e.getKey()+" "+e.getValue());
//                }
                
                
//                notifi.show(Page.getCurrent());
//                notifi.setDelayMsec(5000);
            }

        });

    }

    private Component createResultComponent() {
        Panel panel = new Panel("Result:");
        panel.setSizeFull();

        final GridLayout bottomLayer = new GridLayout(10, 1);
        listOfMentions = new TextArea("Mentions:");
        listOfMentions.setRows(13);

        final String basepath = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath();
        final FileResource resource = new FileResource(new File(basepath +
                "/WEB-INF/images/KBTC_demo.png"));
        final Image kbtc = new Image("KBTC:", resource);
        kbtc.setSizeFull();
        //kbtc.setWidth(100, Unit.PERCENTAGE);
        //kbtc.setHeight(100, Unit.PERCENTAGE);

        resultCategoryLayout = new VerticalLayout();

//        final HorizontalLayout worldLayout = new HorizontalLayout();
//        final Label worldCategoryName = new Label("World:");
//        final Label worldCategoryProbability = new Label();
//        worldLayout.addComponent(worldCategoryName);
//        worldLayout.addComponent(worldCategoryProbability);
//        categoryLayout.addComponent(worldLayout);
//
//        final HorizontalLayout businessLayout = new HorizontalLayout();
//        final Label businessCategoryName = new Label("Business:");
//        final Label businessCategoryProbability = new Label();
//        businessLayout.addComponent(businessCategoryName);
//        businessLayout.addComponent(businessCategoryProbability);
//        categoryLayout.addComponent(businessLayout);
//
//        final HorizontalLayout sportsLayout = new HorizontalLayout();
//        final Label sportsCategoryName = new Label("Sports:");
//        final Label sportsCategoryProbability = new Label();
//        sportsLayout.addComponent(sportsCategoryName);
//        sportsLayout.addComponent(sportsCategoryProbability);
//        categoryLayout.addComponent(sportsLayout);
//
//        final HorizontalLayout scienceLayout = new HorizontalLayout();
//        final Label scienceCategoryName = new Label("Science:");
//        final Label scienceCategoryProbability = new Label();
//        scienceLayout.addComponent(scienceCategoryName);
//        scienceLayout.addComponent(scienceCategoryProbability);
//        categoryLayout.addComponent(scienceLayout);
        

        // pieChart.configure(createPieChartConfiguration());
        // pieChart.update();
        // pieChart.setVisible(false);

        bottomLayer.addComponent(listOfMentions,0,0);
        bottomLayer.addComponent(kbtc,1,0,6,0);
        bottomLayer.addComponent(resultCategoryLayout,7,0,9,0);
        // bottomLayer.addComponent(pieChart);

        bottomLayer.setSizeFull();
        bottomLayer.setSpacing(true);
        bottomLayer.setMargin(true);

        panel.setContent(bottomLayer);
        return panel;
    }

    private ChartConfig createPieChartConfiguration() {
        final PieChartConfig config = new PieChartConfig();
        final Map<String, Double> statistic = getStatistic();
        config.data().labels(statistic.keySet().stream().toArray(String[]::new))
                .addDataset(new PieDataset().label("Dataset 1")).and();

        config.options().responsive(true).title().display(true)
                .text("Pie Chart").and().animation()
                .animateScale(true).animateRotate(true).and().done();

        for (final Dataset<?, ?> ds: config.data().getDatasets()) {
            PieDataset lds = (PieDataset)ds;
            List<Double> data = new ArrayList<>();
            List<String> colors = new ArrayList<>();

            for (Entry<String, Double> entry: statistic.entrySet()) {
                data.add(entry.getValue());
                colors.add(ColorUtil.colormap.get(entry.getKey()));
            }

            lds.backgroundColor(colors.toArray(new String[colors.size()]));
            lds.dataAsList(data);
        }
        return config;
    }

    private Map<String, Double> getStatistic() {
        final Map<String, Double> result = new HashMap<>();
        result.put("Science", 0.30);
        result.put("Sports", 0.25);
        result.put("Business", 0.25);
        result.put("World", 0.25);
        return result;
    }

    private Button createSampleTextButton() {
        final Button run = new Button("Sample Sentences");
        final List<String> sampleSentence = Arrays.asList(
                "Albert Einstein was a German-born theoretical physicist who developed the theory of relativity, one of the two pillars of modern physics (alongside quantum mechanics)..",
                "FC Bayern München, FCB, Bayern Munich, or FC Bayern, is a German sports club based in Munich, Bavaria (Bayern).",
                "Berlin is the capital and the largest city of Germany, as well as one of its 16 constituent states.",
                "Dollar rises Vs Euro on asset flows data");
        run.addClickListener(event -> {
            int randomNum = ThreadLocalRandom.current().nextInt(0,
                    sampleSentence.size());
            textAreaContent = sampleSentence.get(randomNum);
            textArea.clear();
            textArea.setValue(textAreaContent);
            numberOfSentencesValue.setValue(String.valueOf(
                    SentenceSegmenter.getNumberOfSentence(textAreaContent)));
        });
        return run;
    }

    private Button createButton() {
        final Button run = new Button("Classify Text");
        return run;
    }

    private TextArea createTextArea() {
        final TextArea textArea = new TextArea();
        textArea.setSizeFull();
        textArea.setRows(10);
        //textArea.setReadOnly(true);
//        textArea.addValueChangeListener(event -> textArea.re));
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

        final Label categoryLabel = new Label("Select News Category:");

        final List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Sport", "sports"));
        categories.add(new Category(2, "Business", "business"));
        categories.add(new Category(3, "World", "general"));
        categories.add(new Category(4, "Science", "science"));
        final ComboBox<Category> categoryComboBox = new ComboBox<>();
        categoryComboBox.setItems(categories);
        categoryComboBox.setItemCaptionGenerator(Category::getName);
        categoryComboBox.setValue(categories.get(0));
        categoryComboBox.setEmptySelectionAllowed(false);
        
        final Link url = new Link();
        url.setVisible(false);

        final Label sourceOfNewsLabel = new Label("Source of the News: ");
        sourceOfNewsLabel.setVisible(false);

        final Label sourceOfNews = new Label();
        sourceOfNews.setVisible(false);

        final Button newsBtn = new Button("Get a random news");

        newsBtn.addClickListener(event -> {
            try {
                final Optional<Category> selectedItem = categoryComboBox
                        .getSelectedItem();
                if (selectedItem != null && selectedItem.get() != null) {
                    final PartialTextNewsResult result = NewsRequest
                            .requestNewsApi(
                                    selectedItem.get().getNameInTheNewsSite());
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
                } else {
                    final Notification notifi = new Notification(
                            "Select the category first.",
                            Notification.Type.ERROR_MESSAGE);
                    notifi.show(Page.getCurrent());
                    notifi.setDelayMsec(5000);
                }
            } catch (Exception e) {
                final Notification notifi = new Notification(
                        "Select the category first.",
                        Notification.Type.ERROR_MESSAGE);
                notifi.show(Page.getCurrent());
                notifi.setDelayMsec(5000);
            }

        });

        newsLayout.addComponent(categoryLabel);
        newsLayout.addComponent(categoryComboBox);
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

        final Label categoryLabel = new Label("Select News Category:");

        final List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Sport", "sports"));
        categories.add(new Category(2, "Business", "business"));
        categories.add(new Category(3, "World", "travel"));
        categories.add(new Category(4, "Science", "tech"));
        final ComboBox<Category> categoryComboBox = new ComboBox<>();
        categoryComboBox.setItems(categories);
        categoryComboBox.setItemCaptionGenerator(Category::getName);
        categoryComboBox.setValue(categories.get(0));
        categoryComboBox.setEmptySelectionAllowed(false);

        final Button newsBtn = new Button("Get a random news");

        newsBtn.addClickListener(event -> {
            try {
                final Optional<Category> selectedItem = categoryComboBox
                        .getSelectedItem();
                if (selectedItem != null && selectedItem.get() != null) {
                    final FullTextNewsResult result = NewsRequest
                            .requestNewsWebhose(
                                    selectedItem.get().getNameInTheNewsSite());
                    if (result != null) {
                    	//TODO: Rima
                    	String originalResponseString = new String(result.getFullText().getBytes(), "UTF-8");
                        
                    	
                    	textAreaContent = result.getFullText();
                        
                        textArea.setValue(originalResponseString);
//                        textArea.setValue(textAreaContent);
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
                } else {
                    final Notification notifi = new Notification(
                            "Select the category first.",
                            Notification.Type.ERROR_MESSAGE);
                    notifi.show(Page.getCurrent());
                    notifi.setDelayMsec(5000);
                }
            } catch (Exception exception) {
                final Notification notifi = new Notification(
                        "Select the category first.",
                        Notification.Type.ERROR_MESSAGE);
                notifi.show(Page.getCurrent());
                notifi.setDelayMsec(5000);
            }

        });

        newsLayout.addComponent(categoryLabel);
        newsLayout.addComponent(categoryComboBox);
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

        // Create a vertical slider
        // final Slider numberOfSentencesSlider = new Slider("Number of
        // Sentences",
        // 1, 100);
        // numberOfSentencesSlider.setOrientation(SliderOrientation.HORIZONTAL);
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

        // Handle changes in slider value.
        // numberOfSentencesSlider.addValueChangeListener(event -> {
        // int value = event.getValue().intValue();
        // numberOfSentencesLabel.setValue(String.valueOf(value));
        // });

        // final Button numberOfSentenceBtn = new Button("Apply");
        // numberOfSentenceBtn.addClickListener(event -> {
        // final String sentences = SentenceSegmenter.getSentences(
        // Integer.parseInt(numberOfSentencesValue.getValue()),
        // textAreaContent);
        // textArea.setValue(sentences);
        // });

        // numberOfSentenceLayout.addComponent(numberOfSentencesSlider);
        numberOfSentenceLayout.addComponent(numberOfSentenceMinuesBtn);
        numberOfSentenceLayout.addComponent(numberOfSentencesLabel);
        numberOfSentenceLayout.addComponent(numberOfSentencesValue);
        numberOfSentenceLayout.addComponent(numberOfSentencePlusBtn);
        // numberOfSentenceLayout.addComponent(numberOfSentenceBtn);
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

            return jsonObject3.getString("extract");
        } catch (Exception excpetion) {
            return null;
        }
    }
}
