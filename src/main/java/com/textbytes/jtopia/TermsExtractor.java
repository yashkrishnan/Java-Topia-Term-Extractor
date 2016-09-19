package com.textbytes.jtopia;

import com.sree.textbytes.StringHelpers.string;
import com.textbytes.jtopia.cleaner.TextCleaner;
import com.textbytes.jtopia.container.TaggedTermsContainer;
import com.textbytes.jtopia.extractor.TermExtractor;
import com.textbytes.jtopia.filter.TermsFilter;
import com.textbytes.jtopia.tagger.LexiconTagger;
import com.textbytes.jtopia.tagger.OpenNLPTagger;
import com.textbytes.jtopia.tagger.StanfordTagger;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */

class TermsExtractor {
    public static Logger logger = Logger.getLogger(TermsExtractor.class.getName());

    TermDocument performTermExtraction(String text) {
        if (!string.isNullOrEmpty(text) && !string.isNullOrEmpty(JTopiaConfiguration.getModelFileLocation())) {
            logger.info("Input String and lexicon is neither null nor empty");
            TextCleaner textCleaner = new TextCleaner();
            TermDocument termDocument = new TermDocument();
            String normalizedText = textCleaner.normalizeText(text);
            termDocument.setNormalizedText(normalizedText);
            List<String> tokenizedText = textCleaner.tokenizeText(normalizedText);
            termDocument.setTerms(tokenizedText);
            //termDocument.setTerms(textCleaner.tokenizeText(text));

            if (JTopiaConfiguration.taggerType.equalsIgnoreCase("default")) {
                logger.info("\nUsing English Lexicon POS tagger");
                LexiconTagger lexiconTagger = new LexiconTagger();
                termDocument.setTagsByTerm(lexiconTagger.initialize(JTopiaConfiguration.getModelFileLocation()));
                termDocument = lexiconTagger.tag(termDocument);
            } else if (JTopiaConfiguration.taggerType.equalsIgnoreCase("openNLP")) {
                logger.info("Using openNLP POS tagger");
                OpenNLPTagger openNLPTagger = new OpenNLPTagger();
                termDocument = openNLPTagger.tag(termDocument);
            } else if (JTopiaConfiguration.taggerType.equalsIgnoreCase("stanford")) {
                logger.info("Using Stanford POS tagger");
                StanfordTagger stanfordTagger = new StanfordTagger();
                termDocument = stanfordTagger.tag(termDocument);
            }

            TermExtractor termExtractor = new TermExtractor();
            TaggedTermsContainer taggedTermsContainer = termDocument.getTaggedContainer();
            Map<String, Integer> extractedTerms = termExtractor.extractTerms(taggedTermsContainer);
            termDocument.setExtractedTerms(extractedTerms);

            int singleStrengthMinOccur = JTopiaConfiguration.getSingleStrengthMinOccur();
            int noLimitStrength = JTopiaConfiguration.getNoLimitStrength();
            TermsFilter termsFilter = new TermsFilter(singleStrengthMinOccur, noLimitStrength);
            Map<String, ArrayList<Integer>> filteredTerms = termsFilter.filterTerms(extractedTerms);
            termDocument.setFinalFilteredTerms(filteredTerms);
            return termDocument;
        } else {
            logger.info("Input text/lexicon is either null or empty");
            return null;
        }
    }

}
