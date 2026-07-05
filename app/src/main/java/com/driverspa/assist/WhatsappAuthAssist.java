package com.driverspa.assist;

import android.text.TextUtils;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import com.driverspa.BA;
import com.driverspa.model.User;
import com.driverspa.model.api.response.AuthClientVerificationResponseHolder;
import com.driverspa.model.api.response.WhatsappRequestResponseHolder;
import com.driverspa.model.api.response.WhatsappStatusResponseHolder;
import com.driverspa.util.RetrofitClient.WashmeApi;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.AuthClientVerificationResponseEvent;
import com.driverspa.util.otto.ws.WhatsappRequestEvent;
import com.driverspa.util.otto.ws.WhatsappRequestResponseEvent;
import com.driverspa.util.otto.ws.WhatsappStatusRequestEvent;
import com.driverspa.util.otto.ws.WhatsappStatusResponseEvent;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Drives the two-call WhatsApp registration flow for the client (user) app:
 *
 *  - whatsapp_request: ask the backend for a short token + wa_link; the screen
 *    deep-links the user into WhatsApp with the token pre-filled.
 *  - whatsapp_status: poll with that SHORT token. While pending the backend
 *    returns {verified:false}; once the WhatsApp message (sent from the same
 *    number) is matched it returns the full account + password_required + token,
 *    where token is the api_key.
 *
 * On verification this signs the user in (stores the api_key, writes the user to
 * the db) and reuses the existing client verification response
 * ({@link AuthClientVerificationResponseEvent}) so the UI routes to the client
 * home exactly like the OTP path — this is the plain "user" behaviour (no admin /
 * carwash bootstrap).
 */
public class WhatsappAuthAssist extends BaseAssist {

    // Guards against the polling loop firing the sign-in twice when a verified
    // status is returned by more than one in-flight poll. Reset on each fresh
    // whatsapp_request (incl. Retry).
    private volatile boolean verifiedHandled;

    public WhatsappAuthAssist(WashmeApi api, Bus eventBus) {
        super(api, eventBus);
    }

    @Subscribe
    public void onWhatsappRequested(final WhatsappRequestEvent event) {
        logD("onWhatsappRequested");
        verifiedHandled = false;
        getApi().whatsappRequest(event.getRequest(), new Callback<WhatsappRequestResponseHolder>() {
            @Override
            public void success(WhatsappRequestResponseHolder data, Response response) {
                if (isSuccess(data) && data.getResponse() != null) {
                    getEventsBus().post(new WhatsappRequestResponseEvent(data));
                } else {
                    onDataError(data);
                    getEventsBus().post(new WhatsappRequestResponseEvent(data));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                onRetrofitError(error);
                getEventsBus().post(new WhatsappRequestResponseEvent(new WhatsappRequestResponseHolder()));
            }
        });
    }

    @Subscribe
    public void onWhatsappStatusRequested(final WhatsappStatusRequestEvent event) {
        logD("onWhatsappStatusRequested");
        getApi().whatsappStatus(event.getRequest(), new Callback<WhatsappStatusResponseHolder>() {
            @Override
            public void success(WhatsappStatusResponseHolder data, Response response) {
                User user = data.getResponse();
                // The presence of an api_key token is the unambiguous "done"
                // signal; while pending the backend returns {verified:false}.
                if (isSuccess(data) && user != null && !TextUtils.isEmpty(user.getToken())) {
                    if (verifiedHandled) {
                        // Another in-flight poll already signed us in — ignore.
                        return;
                    }
                    verifiedHandled = true;
                    UserPreferences.onUserLogin(BA.getContext(), user.getId(), user.getPhone(), user.getToken());
                    writeSelfToDb(user);
                    // Reuse the client verification response so the screen routes
                    // to the client home exactly like the OTP path.
                    getEventsBus().post(new AuthClientVerificationResponseEvent(buildVerificationHolder(user)));
                } else {
                    // Still pending (or a soft error) — let the screen keep polling
                    // until its timeout fires.
                    getEventsBus().post(new WhatsappStatusResponseEvent(data));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                // Network hiccup mid-poll: don't surface a hard error, the screen
                // will retry on its next tick.
                onRetrofitError(error, false);
                getEventsBus().post(new WhatsappStatusResponseEvent(null));
            }
        });
    }

    /**
     * Wraps the signed-in {@link User} in an {@link AuthClientVerificationResponseHolder}
     * flagged success, mirroring the activate (OTP) response so the registration
     * screen routes to the client home (and honours password_required).
     */
    private AuthClientVerificationResponseHolder buildVerificationHolder(User user) {
        AuthClientVerificationResponseHolder holder = new AuthClientVerificationResponseHolder();
        holder.setStatus("success");
        holder.setResponse(user);
        return holder;
    }
}
