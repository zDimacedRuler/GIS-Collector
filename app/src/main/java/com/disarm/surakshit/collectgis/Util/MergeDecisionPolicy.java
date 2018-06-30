package com.disarm.surakshit.collectgis.Util;

import java.util.Random;

/**
 * Created by bishakh on 6/30/18.
 */

public class MergeDecisionPolicy {
    private int policy;
    public static final int RANDOM_POLICY = 1;
    public static final int DISTANCE_THRESHOLD_POLICY = 2;
    public static final int DISTANCE_OR_TFIDF_THRESHOLD_POLICY = 3;
    public static final int DISTANCE_AND_TFIDF_THRESHOLD_POLICY = 4;

    public MergeDecisionPolicy(int policy) {
        this.policy = policy;
    }

    public boolean mergeDecider(double tfidfScore, double housDroff) {
        switch (policy) {
            case RANDOM_POLICY:
                Random random = new Random();
                return random.nextBoolean();
            case DISTANCE_THRESHOLD_POLICY:
                if (housDroff <= 30)
                    return true;
                return false;
            case DISTANCE_OR_TFIDF_THRESHOLD_POLICY:
                if (housDroff <= 30 || tfidfScore >= .45)
                    return true;
                return false;
            case DISTANCE_AND_TFIDF_THRESHOLD_POLICY:
                if (housDroff <= 30 && tfidfScore >= .45)
                    return true;
                return false;
            default:
                return false;
        }
    }
}
