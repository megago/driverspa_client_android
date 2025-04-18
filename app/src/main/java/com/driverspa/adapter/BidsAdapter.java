package com.driverspa.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.model.FareRequest;
import com.driverspa.util.Functions;
import com.driverspa.util.L;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.AcceptFareRequestBid;
import com.driverspa.util.otto.ws.FareRequestNotAcceptFareEvent;
import com.driverspa.util.otto.ws.HideBidsListLayoutEvent;

@SuppressLint({"SimpleDateFormat", "ViewHolder"})
public class BidsAdapter extends BaseDataAdapter<FareRequest.BidObject> {

    DecimalFormat formatter = new DecimalFormat("#,###.##");
    public static final long TIMER_SECOND = 10;
    Context context;
    private List<ViewHolder> lstHolders;
    private Handler mHandler = new Handler();
    List<Double> requestLonLat;

    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                long currentTime = System.currentTimeMillis();
                for (ViewHolder holder : lstHolders) {
                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };

    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);
            }
        }, 1000, 1000);
    }

    public void stopTimer() {
        if (mHandler != null && updateRemainingTimeRunnable != null)
            mHandler.removeCallbacks(updateRemainingTimeRunnable);
    }

    public BidsAdapter(Context context, List<Double> requestLonLat) {
        super(context);
        this.context = context;
        this.requestLonLat = requestLonLat;
        lstHolders = new ArrayList<>();
        startUpdateTimer();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        view = LayoutInflater.from(context).inflate(R.layout.list_bids_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        synchronized (lstHolders) {
            lstHolders.add(holder);
        }
        view.setTag(holder);
        final FareRequest.BidObject item = this.getItem(position);
        holder.review.setRating((float)item.getCarwashRating().doubleValue());
        holder.name.setText(item.getCarwashName().toLowerCase().contains("автомойка")?item.getCarwashName():"Автомойка "+item.getCarwashName());
        if(!TextUtils.isEmpty(item.getCarwashAddress())){
            holder.address.setText(item.getCarwashAddress());
        }
        if(TextUtils.isEmpty(item.getCarwashAddress()))
            holder.address.setText(Functions.getCityDescription("адрес не указан"));

        try{
            Location washerLocation = new Location("A");
            washerLocation.setLatitude(Double.parseDouble(item.getCarwashLonLat().get(1).toString()));
            washerLocation.setLongitude(Double.parseDouble(item.getCarwashLonLat().get(0).toString()));
            Location requestLocationB = new Location("B");
            requestLocationB.setLatitude(Double.parseDouble(requestLonLat.get(1).toString()));
            requestLocationB.setLongitude(Double.parseDouble(requestLonLat.get(0).toString()));

            float dist = requestLocationB.distanceTo(washerLocation);
            holder.distance.setText(String.format("%s км", formatter.format(dist/1000).replaceAll(",", " ")));
        }
        catch(Exception e){
            holder.distance.setText("- км");
        }

        holder.buttonAcceptBid.setText(item.getPrice()+" ₸. Принять");
        holder.buttonAcceptBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FareRequest fr = FareRequest.deserialize(UserPreferences.getActiveFareRequest(BA.getContext()));
                if(fr.getActgiveBids() != null){
                    Iterator<FareRequest.BidObject> iter = fr.getActgiveBids().iterator();
                    while (iter.hasNext()) {
                        FareRequest.BidObject b = iter.next();
                        if (b.getId().equals(item.getId())) {
                            iter.remove();
                            FareRequest frTemp = new FareRequest();
                            frTemp.setId(fr.getId());
                            frTemp.setAcceptedBid(item.getId());
                            UserPreferences.putActiveFareRequestBooking(BA.getContext(), fr.serialize());
                            BidsAdapter.this.set(fr.getActgiveBids());
                            BidsAdapter.this.notifyDataSetChanged();
                            if(BidsAdapter.this.getList().size() == 0) {BA.getEventBus().post(new HideBidsListLayoutEvent());
                                BidsAdapter.this.stopTimer();
                            }
                        }
                    }

                }
                BidsAdapter.this.notifyDataSetChanged();
                BA.getEventBus().post(new AcceptFareRequestBid(item));
            }
        });
        holder.cancelBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FareRequest fr = FareRequest.deserialize(UserPreferences.getActiveFareRequest(BA.getContext()));
                if(fr.getActgiveBids() != null){

                    Iterator<FareRequest.BidObject> iter = fr.getActgiveBids().iterator();
                    while (iter.hasNext()) {
                        FareRequest.BidObject b = iter.next();
                        if (b.getId().equals(item.getId())) {
                            iter.remove();

                            FareRequest frTemp = new FareRequest();
                            frTemp.setId(fr.getId());
                            frTemp.setAcceptedBid(item.getId());
                            BA.getEventBus().post(new FareRequestNotAcceptFareEvent(frTemp));
                            UserPreferences.putActiveFareRequestBooking(BA.getContext(), fr.serialize());
                            BidsAdapter.this.set(fr.getActgiveBids());
                            BidsAdapter.this.notifyDataSetChanged();
                            if(BidsAdapter.this.getList().size() == 0) {BA.getEventBus().post(new HideBidsListLayoutEvent());
                                BidsAdapter.this.stopTimer();
                            }
                        }
                    }

                }
                BidsAdapter.this.notifyDataSetChanged();
            }
        });

        holder.seekBar.setProgress(0);
        holder.seekBar.setPadding(0, 0, 0, 0);
        holder.seekBar.setMax((int)TIMER_SECOND*1000-1000);
        holder.setBidObject(item, position);
        holder.setAdapter(this);
        return view;
    }

    static class ViewHolder {
        FareRequest.BidObject bidObject;
        @BindView(R.id.txtWasherAddress)
        TextView address;
        @BindView(R.id.txtWasherName)
        TextView name;
        @BindView(R.id.txtDistance)
        TextView distance;
        @BindView(R.id.review)
        RatingBar review;
        @BindView(R.id.btnAcceptBid)
        Button buttonAcceptBid;
        @BindView(R.id.cancelBid)
        View cancelBid;
        @BindView(R.id.seekBar)
        SeekBar seekBar;
        BidsAdapter adapter;
        int position;
        long initialTime = System.currentTimeMillis();

        public void updateTimeRemaining(long currentTime) {
              long timeDiff = currentTime - bidObject.getInitialTime();
              seekBar.setProgress((int)timeDiff);

              if(timeDiff/1000 >= TIMER_SECOND){
                  seekBar.setProgress((int)TIMER_SECOND*1000);
                    if(adapter != null){
                        Iterator<FareRequest.BidObject> iterMain = adapter.getList().iterator();
                        while (iterMain.hasNext()) {
                            FareRequest.BidObject bid = iterMain.next();
                            if(bid.getId() == bidObject.getId()) {
                                FareRequest fr = FareRequest.deserialize(UserPreferences.getActiveFareRequest(BA.getContext()));
                                if(fr.getActgiveBids() != null){
                                    Iterator<FareRequest.BidObject> iter = fr.getActgiveBids().iterator();
                                    while (iter.hasNext()) {
                                        FareRequest.BidObject b = iter.next();
                                        if (b.getId().equals(bid.getId())) {
                                            iter.remove();

                                            FareRequest frTemp = new FareRequest();
                                            frTemp.setId(fr.getId());
                                            frTemp.setAcceptedBid(b.getId());
                                            BA.getEventBus().post(new FareRequestNotAcceptFareEvent(frTemp));

                                            UserPreferences.putActiveFareRequestBooking(BA.getContext(), fr.serialize());
                                            adapter.set(fr.getActgiveBids());
                                            adapter.notifyDataSetChanged();
                                            if(adapter.getList().size() == 0) {BA.getEventBus().post(new HideBidsListLayoutEvent());
                                                adapter.stopTimer();
                                            }
                                        }
                                    }
                               }
                           }
                     }
                }
            }
        }

        public void setBidObject(FareRequest.BidObject bidObject, int position) {
            this.bidObject = bidObject;
            this.position = position;
        }

        public void setAdapter(BidsAdapter adapter) {
          this.adapter = adapter;
        }

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
