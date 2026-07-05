package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.WhatsappStatusResponseHolder;

/**
 * Posted for each whatsapp_status poll that has NOT yet verified (or failed).
 * The verified case is delivered downstream as an
 * {@link AuthClientVerificationResponseEvent} once the user is signed in.
 */
public class WhatsappStatusResponseEvent {

    private final WhatsappStatusResponseHolder data;

    public WhatsappStatusResponseEvent(WhatsappStatusResponseHolder data) {
        this.data = data;
    }

    public WhatsappStatusResponseHolder getData() {
        return data;
    }
}
