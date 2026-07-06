package com.driverspa.navigation.client;

import android.app.Activity;
import android.app.AlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.activity.LoginActivity;
import com.driverspa.assist.BaseAssist;
import com.driverspa.client.activity.ClientProfileActivity;
import com.driverspa.model.User;
import com.driverspa.util.Converters;
import com.driverspa.util.Functions;
import com.driverspa.util.HttpClient;
import com.driverspa.util.RetrofitClient;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.TransformationCircle;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.AuthClientLogoutRequestEvent;
import com.driverspa.util.otto.ws.DeleteDeviceRequestEvent;
import com.driverspa.util.otto.ws.DeleteDeviceResponseEvent;
import com.driverspa.util.otto.ws.UserGetSelfResponseEvent;
import com.driverspa.view.ScrimInsetsFrameLayout;

/**
 * Created by Yerzhant on 26/10/2015.
 */
public class NavigationDrawerFragment extends Fragment implements NavigationDrawerCallbacks {
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREFERENCES_FILE = "washme_settings_menu";
    private NavigationDrawerCallbacks mCallbacks;
    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private int mCurrentSelectedPosition;

    @BindView(R.id.profile_avatar)
    ImageView profileAvatar;
    @BindView(R.id.profile_name)
    TextView profileName;
    @BindView(R.id.profile_phone)
    TextView profilePhone;
    @BindView(R.id.drawerList)
    RecyclerView mDrawerList;
    NavigationDrawerAdapter adapter;
    List<NavigationItem> navigationItems;
    @BindView(R.id.logout)
    View logoutView;
    @BindView(R.id.profile_avatar_invisible)
    ImageView avatarBackground;
    @BindView(R.id.fade_for_avatar)
    View fadeAvatar;
    @BindView(R.id.version)
    TextView version;


    User user;
    boolean loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        ButterKnife.bind(this, view);

        String versionName = "";
        try {
            versionName = "v"+getActivity().getApplicationContext().getPackageManager().getPackageInfo(getActivity().getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version.setText(versionName);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);
        mDrawerList.setHasFixedSize(true);

        navigationItems = getMenu();
        adapter = new NavigationDrawerAdapter(navigationItems);
        adapter.setNavigationDrawerCallbacks(this);
        mDrawerList.setAdapter(adapter);
//        mDrawerList.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
//        mCurrentSelectedPosition = Integer.valueOf(readSharedSetting(getActivity(),mCurrentSelectedPosition+"","0"));
        selectItem(mCurrentSelectedPosition);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
            adapter.selectPosition(mCurrentSelectedPosition);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public void setActionBarDrawerToggle(ActionBarDrawerToggle actionBarDrawerToggle) {
        mActionBarDrawerToggle = actionBarDrawerToggle;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        if(mFragmentContainerView.getParent() instanceof ScrimInsetsFrameLayout){
            mFragmentContainerView = (View) mFragmentContainerView.getParent();
        }
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.myPrimaryDarkColor));

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "true");
                }

                getActivity().invalidateOptionsMenu();
            }
        };

        // Tint the hamburger / drawer-arrow icon white so it's visible on the toolbar.
        mActionBarDrawerToggle.getDrawerArrowDrawable()
                .setColor(getResources().getColor(R.color.White));

//        if (!mUserLearnedDrawer && !mFromSavedInstanceState)
//            mDrawerLayout.openDrawer(mFragmentContainerView);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public List<NavigationItem> getMenu() {
        List<NavigationItem> items = new ArrayList<NavigationItem>();
        String city = UserPreferences.getCity(getActivity())!=null?Functions.getCityDescription(UserPreferences.getCity(getActivity())):"";
        items.add(new NavigationItem(""+(TextUtils.isEmpty(city)?"Автомойки":"Автомойки ("+city+")"), getResources().getDrawable(R.drawable.ic_business_white_24dp)));
        items.add(new NavigationItem("Мои брони", getResources().getDrawable(R.drawable.ic_timer_white_24dp)));
        items.add(new NavigationItem("Уведомления", getResources().getDrawable(R.drawable.ic_tab_notification_normal)));
        items.add(new NavigationItem("Избранные", getResources().getDrawable(R.drawable.ic_favourite)));
//        items.add(new NavigationItem("История посещений", getResources().getDrawable(R.drawable.ic_history_white_24dp)));
//        items.add(new NavigationItem("Бонусы", getResources().getDrawable(R.drawable.ic_local_offer_white_18dp)));
        items.add(new NavigationItem("О нас", getResources().getDrawable(R.drawable.ic_about_us)));
//        items.add(new NavigationItem("Поделиться", getResources().getDrawable(R.drawable.ic_share_white_24dp)));
        return items;
    }

    /**
     * Changes the icon of the drawer to back
     */
    public void showBackButton() {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Changes the icon of the drawer to menu
     */
    public void showDrawerButton() {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        mActionBarDrawerToggle.syncState();
    }

    void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
        ((NavigationDrawerAdapter) mDrawerList.getAdapter()).selectPosition(position);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        selectItem(position);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null) {
            adapter.setSelectedPosition(mCurrentSelectedPosition);
        }
        BA.getEventBus().register(this);
        loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        if(adapter!=null) {
            saveSharedSetting(getActivity(), STATE_SELECTED_POSITION, adapter.getSelectedPosition() + "");
        }
        BA.getEventBus().unregister(this);
    }

    @Subscribe
    public void onUserReceived(UserGetSelfResponseEvent event){
       loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
       if(event != null && loggedIn && event.getUser() != null) {
           user = event.getUser();
           profileAvatar.setVisibility(View.VISIBLE);
           logoutView.setVisibility(View.VISIBLE);
           if (!TextUtils.isEmpty(user.getAvatar())) {
               HttpClient.getPicasso()
                       .load(RetrofitClient.API_URL_IMAGES + user.getAvatar())
                       .fit()
                       .centerCrop()
                       .transform(new TransformationCircle(getActivity()))
                       .placeholder(R.drawable.ic_no_image)
                       .into(profileAvatar);

               HttpClient.getPicasso()
                       .load(RetrofitClient.API_URL_IMAGES + user.getAvatar())
                       .fit()
                       .centerCrop()
                       .into(avatarBackground);
               fadeAvatar.setVisibility(View.VISIBLE);
           } else {
               avatarBackground.setVisibility(View.GONE);
               fadeAvatar.setVisibility(View.GONE);
               profileAvatar.setVisibility(View.VISIBLE);
               profileAvatar.setImageResource(R.drawable.ic_no_image);
           }

           profileName.setText(TextUtils.isEmpty(user.getFirstName()) ? "" : user.getFirstName());
           profileName.setPaintFlags(profileName.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
           profilePhone.setText(Functions.maskPhoneNumber(user.getPhone(), Converters.PHONE_PATTERN));

           if(adapter != null){
               navigationItems = getMenu();
               adapter = new NavigationDrawerAdapter(navigationItems);
               adapter.setNavigationDrawerCallbacks(this);
               mDrawerList.setAdapter(adapter);
               adapter.selectPosition(mCurrentSelectedPosition);
//               mDrawerList.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
           }
       }
        else{
           if(loggedIn && event != null && event.getUser() == null) UserPreferences.onUserLogout(BA.getContext());
           logoutView.setVisibility(View.GONE);
           profileName.setText("Войти");
           profilePhone.setText("");
           profileAvatar.setImageResource(R.drawable.ic_no_image);
           profileName.setPaintFlags(profileName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

           avatarBackground.setVisibility(View.GONE);
           fadeAvatar.setVisibility(View.GONE);
       }
    }

    @OnClick(R.id.profile_phone)
    public void onProfilePhoneClicked(){
        if(loggedIn) {
                openProfile();
        }
        else{
            loginButtonClicked();
        }
    }

    @OnClick(R.id.profile_avatar)
    public void onProfileAvatarClicked(){
        if(loggedIn) {
            openProfile();
        }
//        else{
//            loginButtonClicked();
//        }
    }

    @OnClick(R.id.profile_name)
    public void onProfileNameClicked(){
        if(loggedIn) {
            openProfile();
        }
        else{
            loginButtonClicked();
        }
    }

    @OnClick(R.id.logout)
    public void onLogoutButtonClicked(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
        dialog.setTitle("Вы действительно хотите выйти?");
        dialog.setNegativeButton("Отмена", null);
        dialog.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BA.getEventBus().post(new DeleteDeviceRequestEvent());
            }
        });
        dialog.show();
    }

    public void openProfile(){
       startActivity(new Intent(getActivity(),ClientProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
       getActivity().overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
    }

    public void loginButtonClicked(){
       startActivity(new Intent(getActivity(),LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }

    @Subscribe
    public void onDeleteDeviceRequestReceived(DeleteDeviceResponseEvent event) {
      if(event != null && event.getResponse() != null) {
          if (BaseAssist.isSuccess(event.getResponse())) {
              BA.getEventBus().post(new AuthClientLogoutRequestEvent());
              onUserReceived(null);
              loggedIn = false;
          } else {
              ToastUtil.display(BA.getContext(),event.getResponse().getMessage());
          }
      }
        else{
          ToastUtil.display(BA.getContext(),"Ошибка при выходе, проверьте интернет соединение");
      }
    }
}
