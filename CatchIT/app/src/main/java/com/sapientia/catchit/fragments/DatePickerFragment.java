package com.sapientia.catchit.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
{
    private DatePickerDialog.OnDateSetListener dateSetListener;


    public DatePickerFragment(DatePickerDialog.OnDateSetListener dateSetListener)
    {
        this.dateSetListener = dateSetListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
    }

}