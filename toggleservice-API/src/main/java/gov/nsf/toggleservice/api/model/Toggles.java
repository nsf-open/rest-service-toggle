package gov.nsf.toggleservice.api.model;

import java.util.HashMap;

/**
 * Created by gareytaylor on 3/4/16.
 */
public class Toggles {

    HashMap<String,Boolean> features = new HashMap<String, Boolean>();

    public HashMap<String, Boolean> getFeatures() {
        return features;
    }
}
