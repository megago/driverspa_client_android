package com.driverspa.client.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.activity.LoginActivity;
import com.driverspa.assist.BaseAssist;
import com.driverspa.client.activity.ClientAboutUSActivity;
import com.driverspa.client.activity.ClientHomeActivity;
import com.driverspa.client.activity.ClientProfileActivity;
import com.driverspa.dialog.LanguageDialog;
import com.driverspa.model.User;
import com.driverspa.util.Converters;
import com.driverspa.util.Functions;
import com.driverspa.util.HttpClient;
import com.driverspa.util.LocaleManager;
import com.driverspa.util.RetrofitClient;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.TransformationCircle;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.AuthClientLogoutRequestEvent;
import com.driverspa.util.otto.ws.DeleteDeviceRequestEvent;
import com.driverspa.util.otto.ws.DeleteDeviceResponseEvent;
import com.driverspa.util.otto.ws.UserGetSelfResponseEvent;

/**
 * The Profile tab of the home screen: the signed-in user, their activity
 * (notifications, favorites), language, city and log out. Replaces
 * the old left menu. Bookings has its own tab.
 */
public class ClientAccountFragment extends ClientBaseFragment {

    public interface ActivityActions {
        /** Shows a page of the home pager (ClientHomeActivity.PAGE_*). */
        void openHomePage(int page);
        void showChooseCity();
    }

    @BindView(R.id.accountAvatar)
    ImageView avatar;
    @BindView(R.id.accountAvatarInitial)
    TextView avatarInitial;
    @BindView(R.id.accountName)
    TextView name;
    @BindView(R.id.accountDetails)
    TextView details;
    @BindView(R.id.accountLanguageValue)
    TextView languageValue;
    @BindView(R.id.accountCityValue)
    TextView cityValue;
    @BindView(R.id.accountLogout)
    View logout;
    @BindView(R.id.accountVersion)
    TextView version;

    private ActivityActions activityActions;
    private boolean loggedIn;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityActions = (ActivityActions) activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_account, container, false);
        ButterKnife.bind(this, view);
        try {
            version.setText("v" + requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        bindUser(null);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BA.getEventBus().register(this);
        // Both can change on other screens (city picker, language dialog).
        languageValue.setText(LocaleManager.displayName(LocaleManager.current()));
        cityValue.setText(Functions.getCityDescription(UserPreferences.getCity(BA.getContext())));
    }

    @Override
    public void onPause() {
        super.onPause();
        BA.getEventBus().unregister(this);
    }

    @Subscribe
    public void onUserReceived(UserGetSelfResponseEvent event) {
        loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
        if (loggedIn && event != null && event.getUser() == null)
            UserPreferences.onUserLogout(BA.getContext());
        bindUser(loggedIn && event != null ? event.getUser() : null);
    }

    private void bindUser(User user) {
        if (name == null) return;
        logout.setVisibility(user != null ? View.VISIBLE : View.GONE);
        if (user == null) {
            name.setText(BA.str(R.string.login_word));
            details.setText(BA.str(R.string.profile_login_hint));
            avatarInitial.setText("");
            avatar.setVisibility(View.GONE);
            return;
        }

        String firstName = user.getFirstName();
        name.setText(TextUtils.isEmpty(firstName) ? "" : firstName);
        details.setText(Functions.maskPhoneNumber(user.getPhone(), Converters.PHONE_PATTERN));
        avatarInitial.setText(TextUtils.isEmpty(firstName) ? "" : firstName.substring(0, 1).toUpperCase());
        if (!TextUtils.isEmpty(user.getAvatar())) {
            avatar.setVisibility(View.VISIBLE);
            HttpClient.getPicasso()
                    .load(RetrofitClient.API_URL_IMAGES + user.getAvatar())
                    .fit()
                    .centerCrop()
                    .transform(new TransformationCircle(getActivity()))
                    .into(avatar);
        } else {
            avatar.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.accountHeader)
    public void onHeaderClicked() {
        if (loggedIn)
            startActivity(new Intent(getActivity(), ClientProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        else
            startActivity(new Intent(getActivity(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }

    @OnClick(R.id.accountNotifications)
    public void onNotificationsClicked() {
        activityActions.openHomePage(ClientHomeActivity.PAGE_NOTIFICATIONS);
    }

    @OnClick(R.id.accountFavorites)
    public void onFavoritesClicked() {
        activityActions.openHomePage(ClientHomeActivity.PAGE_FAVORITES);
    }

    @OnClick(R.id.accountLanguage)
    public void onLanguageClicked() {
        LanguageDialog.show(getActivity());
    }

    @OnClick(R.id.accountCity)
    public void onCityClicked() {
        activityActions.showChooseCity();
    }

    @OnClick(R.id.accountAbout)
    public void onAboutClicked() {
        startActivity(new Intent(getActivity(), ClientAboutUSActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }

    @OnClick(R.id.accountLogout)
    public void onLogoutClicked() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setTitle(BA.str(R.string.confirm_logout));
        dialog.setNegativeButton(BA.str(R.string.cancel_word), null);
        dialog.setPositiveButton(BA.str(R.string.ok_word), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BA.getEventBus().post(new DeleteDeviceRequestEvent());
            }
        });
        dialog.show();
    }

    @Subscribe
    public void onDeleteDeviceRequestReceived(DeleteDeviceResponseEvent event) {
        if (event != null && event.getResponse() != null) {
            if (BaseAssist.isSuccess(event.getResponse())) {
                BA.getEventBus().post(new AuthClientLogoutRequestEvent());
                loggedIn = false;
                bindUser(null);
            } else {
                ToastUtil.display(BA.getContext(), event.getResponse().getMessage());
            }
        } else {
            ToastUtil.display(BA.getContext(), BA.str(R.string.err_logout));
        }
    }
}
