package com.driverspa.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.BindView;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.model.PushData;
import com.driverspa.model.BookInfo;
import com.driverspa.model.Washer;
import com.driverspa.util.Constants;
import com.driverspa.util.Functions;
import com.driverspa.util.L;

import static com.driverspa.util.Constants.APPROVED;
import static com.driverspa.util.Constants.PENDING;
import static com.driverspa.util.Constants.QUEUED;
import static com.driverspa.util.Constants.QUEUED_APPROVED;
import static com.driverspa.util.Constants.QUEUED_FINISHED;
import static com.driverspa.util.Constants.QUEUED_REJECTED;
import static com.driverspa.util.Constants.bookStatus;

@SuppressLint({"SimpleDateFormat", "ViewHolder"})
public class BooksAdapter extends BaseDataAdapter<BookInfo> {

    public static final int ITEM_TYPE_BOOK = 0;
    public static final int ITEM_TYPE_QUEUE = 1;

    public static final int TIMER_MINUTE = 14;
    HashMap<String, PushData> bookingPushData;
    Context context;
    Washer washer;
    private List<ViewHolder> lstHolders;
    private Handler mHandler = new Handler();

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

    public BooksAdapter(Context context) {
        super(context);
        lstHolders = new ArrayList<>();
        startUpdateTimer();
    }

    public BooksAdapter(Context context, HashMap<String, PushData> bookingPushData) {
        super(context);
        this.context = context;
        this.bookingPushData = bookingPushData;
        lstHolders = new ArrayList<>();
        startUpdateTimer();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (isLoad && position == getCount() - 1) {
            LinearLayout ll = new LinearLayout(context);
            ll.setGravity(Gravity.CENTER_HORIZONTAL);
            ProgressBar bar = new ProgressBar(context);
            ll.addView(bar);
            return ll;
        }

        view = LayoutInflater.from(context).inflate(R.layout.list_books_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        synchronized (lstHolders) {
            lstHolders.add(holder);
        }
        view.setTag(holder);
        final BookInfo item = this.getItem(position);
        Date bookDateTime = Functions.getTZDate(item.getTime(),item.getTimeZone());
        HashMap<String, String> timeMap = Functions.formatTZDate(item.getTime(),item.getTimeZone());
        holder.parentView.setHasTransientState(true);
        if (holder.parentView.getAnimation() != null)
            holder.parentView.getAnimation().cancel();

        holder.txtBoxName.setVisibility(View.GONE);

        holder.bookTime.setText("" + timeMap.get(Functions.TIME));
        PrettyTime p = new PrettyTime(new Locale("ru"));
        Date bookTS = Functions.getTZDate(item.getTs(),item.getTimeZone());
        holder.bookTS.setVisibility(View.VISIBLE);
//        if (DateUtils.isToday(Functions.getTZDate(item.getTime(),item.getTimeZone()).getTime())) {
//            holder.bookTS.setText(p.format(bookTS));
//        } else {
        HashMap<String, String> timeTSMap = Functions.formatTZDate(item.getTs(),item.getTimeZone());
        holder.bookTS.setText(timeTSMap.get(Functions.DATE) + BA.str(R.string.nl_at) + timeTSMap.get(Functions.TIME));
//        }

        //For tomorrow or different date bookings
        if (!DateUtils.isToday(bookDateTime.getTime()) && (new Date()).getTime() < bookDateTime.getTime()) {
            holder.bookTS.setText("" + timeMap.get(Functions.TIME));
            holder.bookTime.setText(BA.str(R.string.tomorrow));
        }

        holder.bookStatus.setText("" + bookStatus.get(item.getStatus()));
        holder.bookStatus.setBackground(Functions.getBookStatusBGColor(context,item.getStatus()));
        holder.bookStatus.setTextColor(Functions.getBookStatusTextColor(context,item.getStatus()));

        if(item.getStatus().equals(PENDING) || item.getStatus().equals(APPROVED)) {
            holder.parentView.setSelected(true);
//            holder.parentView.setBackgroundResource(R.drawable.background_client_booked_black);
        }
        else {
            holder.parentView.setSelected(false);
            //make it transparent and remove this id from push data
//            holder.parentView.setBackgroundColor(Color.TRANSPARENT);
        }

        if (!item.isQueued()) {
            holder.onlineBook.setVisibility(View.VISIBLE);
            holder.remainingTimeLayout.setVisibility(View.VISIBLE);
            holder.layoutQueue.setVisibility(View.GONE);
            holder.arrowRight.setVisibility(View.VISIBLE);
            holder.bookStatus.setVisibility(View.VISIBLE);
            holder.carwashName.setText(item.getCarwashName().toLowerCase().contains("автомойка")?item.getCarwashName():BA.str(R.string.car_wash_label_sp)+item.getCarwashName());
//            holder.carwashName.setText(item.getCarwashName());
            holder.bookKey.setText(item.getClientKey().replaceAll("\\(", "").replaceAll("\\)", ""));
            if(TextUtils.isEmpty(item.getClientKey()))
                holder.bookKey.setText(BA.str(R.string.number_not_specified));

            if (!item.getStatus().equals(PENDING)) {
                holder.remainingTime.setVisibility(View.GONE);
            }

        } else {
            holder.carwashName.setText(item.getCarwashName().toLowerCase().contains("автомойка")?item.getCarwashName():BA.str(R.string.car_wash_label_sp)+item.getCarwashName());
            holder.onlineBook.setVisibility(View.GONE);
//            holder.layoutQueue.setVisibility(View.VISIBLE);
            holder.remainingTime.setVisibility(View.GONE);
            holder.remainingTimeLayout.setVisibility(View.GONE);

            holder.order.setText("" + item.getOrder());
            holder.queueCarNo.setText(item.getClientKey().replaceAll("\\(", "").replaceAll("\\)", ""));
//            holder.bookKey.setVisibility(View.GONE);

            holder.bookKey.setText(item.getClientKey().replaceAll("\\(", "").replaceAll("\\)", ""));
            if(TextUtils.isEmpty(item.getClientKey()))
                holder.bookKey.setText(BA.str(R.string.number_not_specified));
            if (item.getStatus().equals(QUEUED_APPROVED)) {
                holder.bookKey.setVisibility(View.VISIBLE);
                holder.bookKey.setText(BA.str(R.string.auto_finish_after));
            } else if (item.getStatus().equals(QUEUED_REJECTED)) {
                holder.bookKey.setText(BA.str(R.string.queue_cancelled));
            } else if (item.getStatus().equals(QUEUED_FINISHED)) {
                holder.bookKey.setText(BA.str(R.string.queue_finished));
            }
        }

        holder.setBookInfo(item);
        return view;
    }

    static class ViewHolder {


        BookInfo bookInfo;

        @BindView(R.id.txtBookTime)
        TextView bookTime;
        @BindView(R.id.txtBookTS)
        TextView bookTS;
        @BindView(R.id.txtBoxName)
        TextView txtBoxName;
        @BindView(R.id.txtBookKey)
        TextView bookKey;
        @BindView(R.id.txtCarwashName)
        TextView carwashName;

        @BindView(R.id.txtBookStatus)
        TextView bookStatus;
        @BindView(R.id.parentView)
        View parentView;
        @BindView(R.id.remainingTimeLayout)
        View remainingTimeLayout;

        @BindView(R.id.remainingTime)
        TextView remainingTime;
        @BindView(R.id.layoutQueue)
        View layoutQueue;
        @BindView(R.id.arrowRight)
        View arrowRight;
        @BindView(R.id.order)
        TextView order;
        @BindView(R.id.queue_car_no)
        TextView queueCarNo;
        @BindView(R.id.imgAvailability)
        View onlineBook;
        @BindView(R.id.notPaid)
        View notPaid;

        public void updateTimeRemaining(long currentTime) {
            if (!bookInfo.isQueued()) {
                long timeDiff = currentTime - bookInfo.getTs().getTime();

                if (timeDiff > 0) {
                    int seconds = (int) (timeDiff / 1000) % 60;
                    int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                    int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);

                    if ((TIMER_MINUTE - minutes) <= 0) {
                        remainingTime.setText("00:00");
//					remainingTime.setVisibility(View.GONE);
                    } else if (bookInfo.getStatus().equals(PENDING)) {
                        remainingTime.setVisibility(View.VISIBLE);
                        remainingTime.setText(String.format("%02d", (TIMER_MINUTE - minutes)) + ":" + String.format("%02d", (59 - seconds)));
                    }
                }

                if (bookInfo.getStatus().equals(PENDING) || (bookInfo.getStatus().equals(Constants.APPROVED))){// && bookInfo.getServerTime().getTime() > bookInfo.getEndTime().getTime())) {
                    if (parentView.getTag() == null) {
                        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(parentView, "alpha", 1f, 0f);
                        fadeOut.setDuration(700);
                        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(parentView, "alpha", 0f, 1f);
                        fadeIn.setDuration(700);
                        parentView.setHasTransientState(false);
                        final AnimatorSet mAnimationSet = new AnimatorSet();
                        //				Random rn = new Random();
                        //				int min = 0, max = 100;
                        //				int startDelay = rn.nextInt(max - min + 1) + min;
                        //				mAnimationSet.setStartDelay(startDelay);

                        mAnimationSet.play(fadeIn).after(fadeOut);
                        mAnimationSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (bookInfo.getStatus().equals(PENDING) || (bookInfo.getStatus().equals(Constants.APPROVED) )){//&& bookInfo.getServerTime().getTime() > bookInfo.getEndTime().getTime())) {
                                    mAnimationSet.start();
                                    parentView.setHasTransientState(true);
                                } else {
                                    parentView.setHasTransientState(false);
                                }
                            }
                        });
                        parentView.setTag(mAnimationSet);
//                        mAnimationSet.start();
                    }

                } else {
                    parentView.setSelected(false);
                }
            } else {
//                bookKey.setVisibility(View.GONE);
                //Calculate remaining time until end time
                Date dt = Functions.getTZDate(bookInfo.getEndTime(),bookInfo.getTimeZone());
                long timeDiff = dt.getTime() - currentTime;
                if (timeDiff > 0) {
                    int seconds = (int) (timeDiff / 1000) % 60;
                    int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                    int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
                    minutes = hours * 60 + minutes;
                    if (bookInfo.getStatus().equals(QUEUED_APPROVED) //&& minutes > 0
) {
                        bookKey.setVisibility(View.VISIBLE);
                        bookKey.setText(BA.str(R.string.auto_finish_after) + String.format("%02d", (minutes)) + ":" + String.format("%02d", (seconds)));
                        bookStatus.setText(Constants.bookStatus.get(QUEUED_APPROVED));
                    } else if(!(bookInfo.getStatus().equals(QUEUED))) {
//                        if (bookInfo.getStatus().equals(QUEUED_REJECTED))
//                            bookKey.setText("Очередь отменен");
//                        else bookKey.setText("Очередь завершен");
                    }
                } else {
//                    if (bookInfo.getStatus().equals(QUEUED_REJECTED))
//                        bookKey.setText("Очередь отменен");
//                    else if(!(bookInfo.getStatus().equals(QUEUED))) {
//                        bookKey.setText("Очередь завершен");
//                        bookStatus.setText(Constants.bookStatus.get(QUEUED_FINISHED));
//                    }
                }
            }
        }

        public void setBookInfo(BookInfo bookInfo) {
            this.bookInfo = bookInfo;
        }

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
