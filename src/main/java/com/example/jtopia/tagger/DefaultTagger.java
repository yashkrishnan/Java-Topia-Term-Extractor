package com.example.jtopia.tagger;

import com.example.jtopia.container.TaggedTermsContainer;
import com.sree.textbytes.StringHelpers.string;
import com.example.jtopia.models.TermDocument;
import com.example.jtopia.container.TaggedTerms;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Default tagger which uses the simple english POS tagger lexicon
 */

public class DefaultTagger {
    public static Logger logger = Logger.getLogger(DefaultTagger.class.getName());
    List<String> tags = Arrays.asList("NNS", "NNPS");

    /**
     * A set of post tag process taken place here
     *
     * @param termDocument
     * @return {@link TermDocument}
     */
    public TermDocument postTagProcess(TermDocument termDocument) {
        termDocument.setTaggedContainer(correctDefaultNounTag(termDocument));
        termDocument.setTaggedContainer(verifyProperNounAtSentenceStart(termDocument));
        termDocument.setTaggedContainer(determineVerbAfterModal(termDocument));
        termDocument.setTaggedContainer(normalizePluralForm(termDocument));
        return termDocument;
    }

    /**
     * Determine whether a default noun is plural or singular.
     *
     * @param termDoc
     * @return {@link TaggedTermsContainer}
     */

    private TaggedTermsContainer correctDefaultNounTag(TermDocument termDoc) {
        TaggedTermsContainer taggedContainer = new TaggedTermsContainer();
        taggedContainer = termDoc.getTaggedContainer();
        for (int i = 0; i < taggedContainer.taggedTerms.size(); i++) {
            TaggedTerms taggedTerms = taggedContainer.taggedTerms.get(i);
            String term = taggedTerms.getTerm();
            if (taggedTerms.getTag().equals("NND")) {
                logger.info("Term : " + term + " has tag : NND");
                if (term.endsWith("s")) {
                    taggedTerms.setTag("NNS");
                    String norm = term.substring(0, term.length() - 1);
                    taggedTerms.setNorm(norm);
                    logger.info("Term : " + term + " ends with 's' setting tag to NNS and Norm : " + norm);
                } else {
                    logger.info("Term is NND but its not end with 's'");
                    taggedTerms.setTag("NN");
                }
            }
        }
        return taggedContainer;
    }

    /**
     * Verify that noun at sentence start is truly proper.
     *
     * @param termDoc
     * @return {@link TaggedTermsContainer}
     */
    private TaggedTermsContainer verifyProperNounAtSentenceStart(TermDocument termDoc) {
        TaggedTermsContainer taggedContainer = termDoc.getTaggedContainer();
        for (int i = 0; i < taggedContainer.taggedTerms.size(); i++) {
            TaggedTerms taggedTerms = taggedContainer.taggedTerms.get(i);
            String term = taggedTerms.getTerm();
            String tag = taggedTerms.getTag();
            String norm = taggedTerms.getNorm();
            String prevTerm = null;
            if (i != 0) {
                prevTerm = taggedContainer.taggedTerms.get(i - 1).term;
            }
            if (tags.contains(tag) && (i == 0 || prevTerm.equals("."))) {
                logger.info("Tags {NNS,NNPS} contain tag : " + tag + " or prevTerm equals .");
                String lowerTerm = term.toLowerCase();
                String lowerTag = termDoc.getTagsByTerm().get(lowerTerm);
                if (!string.isNullOrEmpty(lowerTag)) {
                    if (lowerTag.equals("NN") || lowerTag.equals("NNS")) {
                        logger.info("Lower tag is " + lowerTag + " for term : " + lowerTerm);
                        taggedTerms.setTerm(lowerTerm);
                        taggedTerms.setTag(lowerTag);
                        taggedTerms.setNorm(lowerTerm);
                    }
                }
            }
        }
        return taggedContainer;
    }


    /**
     * Determine the verb after a modal verb to avoid accidental noun detection.
     *
     * @param termDoc
     * @return {@link TaggedTermsContainer}
     */
    private TaggedTermsContainer determineVerbAfterModal(TermDocument termDoc) {
        TaggedTermsContainer taggedContainer = new TaggedTermsContainer();
        taggedContainer = termDoc.getTaggedContainer();
        for (int i = 0; i < taggedContainer.taggedTerms.size(); i++) {
            TaggedTerms taggedTerms = taggedContainer.taggedTerms.get(i);
            String term = taggedTerms.getTerm();
            String tag = taggedTerms.getTag();
            if (!tag.equals("MD") || tag.equals("RB")) {
                logger.info("Tag is MD or RB , skipping");
                continue;
            }
            if (tag.equals("NN")) {
                taggedTerms.setTag("VB");
            }
        }
        return taggedContainer;
    }

    /**
     * Normalises the plural forms
     *
     * @param termDoc
     * @return {@link TaggedTermsContainer}
     */
    private TaggedTermsContainer normalizePluralForm(TermDocument termDoc) {
        TaggedTermsContainer taggedContainer = termDoc.getTaggedContainer();
        LinkedHashMap<String, String> tagsByTerm = termDoc.getTagsByTerm();
        for (int i = 0; i < taggedContainer.taggedTerms.size(); i++) {
            TaggedTerms taggedTerms = taggedContainer.taggedTerms.get(i);
            String term = taggedTerms.getTerm();
            String tag = taggedTerms.getTag();
            String norm = taggedTerms.getNorm();
            String singular = "";
            if (tags.contains(tag) && term.equals(norm)) {
                if (term.length() > 1) {
                    singular = term.substring(0, term.length() - 1);
                    if (term.endsWith("s") && tagsByTerm.containsKey(singular)) {
                        logger.info("Term ends with 's' setting norm to " + singular);
                        taggedTerms.setNorm(singular);
                        continue;
                    }
                }
                if (term.length() > 2) {
                    singular = term.substring(0, term.length() - 2);
                    if (term.endsWith("es") && tagsByTerm.containsKey(singular)) {
                        logger.info("Term ends with 'es' setting norm to " + singular);
                        taggedTerms.setNorm(singular);
                        continue;
                    }
                }
                if (term.length() > 3) {
                    singular = term.substring(0, term.length() - 3) + "y";
                    if (term.endsWith("ies") && tagsByTerm.containsKey(singular)) {
                        logger.info("Term ends with 'ies' setting norm to " + singular);
                        taggedTerms.setNorm(singular);
                    }
                }
            }
        }

        return taggedContainer;
    }
}
