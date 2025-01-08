package com.driverspa.util.otto;

/**
 * Created by Yerzhan Tanatov on 16/07/16.
 */
public class UseGPSOptionRequestEvent {
    boolean useNoGPSOption = false;

    public UseGPSOptionRequestEvent(boolean useNoGPSOption) {
        this.useNoGPSOption = useNoGPSOption;
    }

    public boolean isUseNoGPSOption() {
        return useNoGPSOption;
    }
}
