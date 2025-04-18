package com.driverspa.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;


import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

import com.driverspa.listener.DatePickerDateSetListener;
import com.driverspa.util.L;

/**
 * Created by Yerzhan Tanatov on 26/04/16.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    DatePickerDateSetListener listener;
    String type;
    int year,day,month;

    public static DatePickerFragment newInstance(String type, int year){
        DatePickerFragment picker = new DatePickerFragment();
        Bundle b = new Bundle();
        b.putString("TYPE",type);
        b.putInt("YEAR", year);
        picker.setArguments(b);
        return picker;
    }

    public DatePickerFragment(){

    }
    public void setDatePickerDateSetListener(DatePickerDateSetListener listener){
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        type = getArguments().getString("TYPE");
        year = getArguments().getInt("YEAR");
        final Calendar c = Calendar.getInstance();

        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int yearTmp = c.get(Calendar.YEAR);
        if(year == 0) year = yearTmp;

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -3);
//        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
//        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
//        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
//        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());

        cal.add(Calendar.YEAR, 3);
//        cal.set((Calendar.DATE, cal.get(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());


        return datePickerDialog;

    }

    @Override
    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
        year  = selectedYear;
        month = selectedMonth + 1;
        day   = selectedDay;

        Date date = new Date();
        date.setMonth(selectedMonth);
        date.setDate(selectedDay);
        date.setYear(selectedYear-1900);
        listener.onDatePickerDataset(date.getTime(),this.type);
    }

}