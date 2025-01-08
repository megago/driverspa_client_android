package com.driverspa.util.otto;

import java.util.HashMap;

/**
 * Created by Yerzhan Tanatov on 18/01/18.
 */
public class WantedWashersResponseEvent {
    HashMap<String, String> wantedWashers;

    public WantedWashersResponseEvent(HashMap<String, String> wantedWashers) {
        this.wantedWashers = wantedWashers;
    }

    public HashMap<String, String> getWantedWashers() {
        return wantedWashers;
    }

    public void setWantedWashers(HashMap<String, String> wantedWashers) {
        this.wantedWashers = wantedWashers;
    }
}
