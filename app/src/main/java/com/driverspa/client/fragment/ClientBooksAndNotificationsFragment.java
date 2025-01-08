package com.driverspa.client.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;

import static com.driverspa.util.Constants.PUSH_TYPE;

public class ClientBooksAndNotificationsFragment extends ClientBaseHomeFragment {

    public interface ActivityActions {
        public void hideShowFilterButton(int tab);
    }

    @InjectView(R.id.pager)
    ViewPager viewPager;
    PagerAdapter adapter;

    int selectedTab = 0;
    @InjectView(R.id.books)
    View books;
    @InjectView(R.id.notifications)
    View notifications;

    Toolbar toolbar;
    TextView titleView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_books_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.inject(this, view);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
        titleView = (TextView) toolbar.findViewById(R.id.action_bar_title);

        String pushType = getActivity().getIntent().getStringExtra(PUSH_TYPE);
        if(!TextUtils.isEmpty(pushType)){
            if(pushType.equals("info") || pushType.equals("info_with_update")){
                selectedTab = 1;
                setTabButtons(selectedTab);
                viewPager.setCurrentItem(selectedTab);
            }
        }

        books.setSelected(true);
        adapter = new PagerAdapter(getChildFragmentManager());
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                selectedTab = position;
                setTabButtons(position);
            }
        });

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(selectedTab);
    }


    @OnClick(R.id.books)
    public void onBooksButtonClick(){
        viewPager.setCurrentItem(0);
    }

    @OnClick(R.id.notifications)
    public void onNotificationButtonClick(){
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        BA.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BA.getEventBus().unregister(this);
    }

    private void setTabButtons(int pos){
        switch (pos){
            case 0:
                books.setSelected(true);
                notifications.setSelected(false);
                break;
            case 1:
                books.setSelected(false);
                notifications.setSelected(true);
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int getTitleResourceId() {
        return 0;
    }

    @Override
    public void load() {

    }

    public class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    //Books Fragment
                    return new ClientBooksFragment();
                case 1:
                    //Notification fragment
                    return new ClientNotificationFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2; //No of Tabs
        }
    }

    private ActivityActions activityActions;

    boolean fragmentVisible;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        fragmentVisible = isVisibleToUser;
        if(activityActions != null){
            activityActions.hideShowFilterButton(1);
        }
        if(fragmentVisible){
            if(titleView != null) {
                if(selectedTab == 0)
                titleView.setText("Брони/мойки");
                else
                    titleView.setText("Уведомления");
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityActions = (ActivityActions) activity;
        if(fragmentVisible)
            activityActions.hideShowFilterButton(1);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activityActions = null;
    }

}



