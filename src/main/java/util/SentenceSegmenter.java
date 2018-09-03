package util;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.DocumentPreprocessor;

public class SentenceSegmenter {

    public static String getSentences(
            int numberOfSentence,
            String text)
    {
        final Reader reader = new StringReader(text);
        final DocumentPreprocessor dp = new DocumentPreprocessor(reader);
        final List<String> sentenceList = new ArrayList<String>();

        for (List<HasWord> sentence: dp) {
            String sentenceString = SentenceUtils.listToString(sentence);
            sentenceList.add(sentenceString);
        }

        final StringBuilder result = new StringBuilder();
        for (int i=0;i<Math.min(numberOfSentence, sentenceList.size());i++) {
            result.append(sentenceList.get(i)).append(" ");
        }
        
        return result.toString().replace("-LRB-", "(").replace("-RRB-", ")").replace("-LSB-", "[").replace("-RSB-","]").trim();
    }

    public static int getNumberOfSentence(
            String text)
    {
        final Reader reader = new StringReader(text);
        final DocumentPreprocessor dp = new DocumentPreprocessor(reader);
        final List<String> sentenceList = new ArrayList<String>();

        for (List<HasWord> sentence: dp) {
            String sentenceString = SentenceUtils.listToString(sentence);
            sentenceList.add(sentenceString);
        }
        return sentenceList.size();
    }

}
