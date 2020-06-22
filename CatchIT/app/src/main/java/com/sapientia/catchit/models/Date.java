package com.sapientia.catchit.models;

public class Date
{
    private int mYear;
    private int mMonth;
    private int mDay;

    public Date(int mYear, int mMonth, int mDay)
    {
        this.mYear = mYear;
        this.mMonth = mMonth;
        this.mDay = mDay;
    }

    public int getmYear()
    {
        return mYear;
    }

    public int getmMonth()
    {
        return mMonth;
    }

    public int getmDay()
    {
        return mDay;
    }
}
