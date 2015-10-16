package com.muhil.zohokart.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import com.muhil.zohokart.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by muhil-ga42 on 01/10/15.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    TextView dateOfBirth;
    Calendar calendar;
    DateFormat dateFormat, finalDateFormat;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        dateOfBirth = (TextView) getActivity().findViewById(R.id.dateOfBirth);

        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        finalDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String date = null;

        Log.d("MONTH", String.valueOf(monthOfYear));
        try
        {
            date = finalDateFormat.format(dateFormat.parse(dayOfMonth + "-" + (monthOfYear+1) + "-" + year));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        dateOfBirth.setText(date.toString());

    }
}
