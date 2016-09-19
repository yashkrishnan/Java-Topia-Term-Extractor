package com.example.jtopia.configurations;

/**
 * <p>
 * Configurations for different POS taggers, filter values
 */
public class JTopiaConfiguration {

    public static String taggerType = "";
    private static int singleStrengthMinOccur;
    private static int noLimitStrength;
    private static String modelFileLocation = "";

    public static String getTaggerType() {
        return taggerType;
    }

    public static void setTaggerType(String taggerType) {
        JTopiaConfiguration.taggerType = taggerType;
    }

    public static int getSingleStrengthMinOccur() {
        return singleStrengthMinOccur;
    }

    public static void setSingleStrengthMinOccur(int singleStrengthMinOccur) {
        JTopiaConfiguration.singleStrengthMinOccur = singleStrengthMinOccur;
    }

    public static int getNoLimitStrength() {
        return noLimitStrength;
    }

    public static void setNoLimitStrength(int noLimitStrength) {
        JTopiaConfiguration.noLimitStrength = noLimitStrength;
    }

    public static String getModelFileLocation() {
        return modelFileLocation;
    }

    public static void setModelFileLocation(String modelFileLocation) {
        JTopiaConfiguration.modelFileLocation = modelFileLocation;
    }
}
