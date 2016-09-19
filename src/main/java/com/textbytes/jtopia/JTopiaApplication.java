package com.textbytes.jtopia;

import org.apache.log4j.Logger;

import java.io.*;

public class JTopiaApplication {
    public static Logger logger = Logger.getLogger(JTopiaApplication.class.getName());

    public static void main(String[] args) {
        //for default lexicon POS tags
        //JTopiaConfiguration.setTaggerType("default");
        // for openNLP POS tagger
        //JTopiaConfiguration.setTaggerType("openNLP");
        //for Stanford POS tagger
        //JTopiaConfiguration.setTaggerType("stanford");

        JTopiaConfiguration.setTaggerType(args[0]);
        JTopiaConfiguration.setSingleStrengthMinOccur(3);
        JTopiaConfiguration.setNoLimitStrength(2);

        // if tagger type is "openNLP" then give the openNLP POS tagger path
        //JTopiaConfiguration.setModelFileLocation("model/openNLP/en-pos-maxent.bin");
        // if tagger type is "default" then give the default POS lexicon file
        //JTopiaConfiguration.setModelFileLocation("model/default/english-lexicon.txt");
        // if tagger type is "stanford "
        //JTopiaConfiguration.setModelFileLocation("model/stanford/english-left3words-distsim.tagger");

        JTopiaConfiguration.setModelFileLocation(args[1]);

        String line = "";
        try {
            //FileInputStream fileInputStream = fileInputStream = new FileInputStream("example.txt");
            FileInputStream fileInputStream = new FileInputStream(args[2]);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            line = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TermsExtractor termExtractor = new TermsExtractor();
        TermDocument topiaDoc = termExtractor.performTermExtraction(line);

        logger.info("Extracted terms : " + topiaDoc.getExtractedTerms());
        logger.info("Final Filtered Terms : " + topiaDoc.getFinalFilteredTerms());
    }
}
