package com.disarm.surakshit.collectgis.Util;

import java.util.Random;

/**
 * Created by bishakh on 6/30/18.
 */

public class MergeDecisionPolicy {
    private int policy;
    private double thresholdDistance;
    private double thresholdtdIdfScore;
    public static final int RANDOM_POLICY = 1;
    public static final int DISTANCE_THRESHOLD_POLICY = 2;
    public static final int DISTANCE_OR_TFIDF_THRESHOLD_POLICY = 3;
    public static final int DISTANCE_AND_TFIDF_THRESHOLD_POLICY = 4;
    public static final int TFIDF_THRESHOLD_POLICY = 5;

    public MergeDecisionPolicy(int policy, double thresholdDistance, double thresholdtdIdfScore) {
        this.policy = policy;
        this.thresholdDistance = thresholdDistance;
        this.thresholdtdIdfScore = thresholdtdIdfScore;
    }

    public boolean mergeDecider(double tfidfScore, double housDroff) {
        switch (policy) {
            case RANDOM_POLICY:
                Random random = new Random();
                return random.nextBoolean();
            case DISTANCE_THRESHOLD_POLICY:
                if (housDroff <= thresholdDistance)
                    return true;
                return false;
            case DISTANCE_OR_TFIDF_THRESHOLD_POLICY:
                if (housDroff <= thresholdDistance || tfidfScore >= thresholdtdIdfScore)
                    return true;
                return false;
            case DISTANCE_AND_TFIDF_THRESHOLD_POLICY:
                if (housDroff <= thresholdDistance && tfidfScore >= thresholdtdIdfScore)
                    return true;
                return false;
            case TFIDF_THRESHOLD_POLICY:
                if (tfidfScore >= thresholdtdIdfScore)
                    return true;
                return false;
            default:
                return false;
        }
    }
}
