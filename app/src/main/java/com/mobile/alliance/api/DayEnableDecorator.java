package com.mobile.alliance.api;

import android.graphics.Typeface;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Size;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

public class DayEnableDecorator implements DayViewDecorator {
    private HashSet<CalendarDay> dates;


    public DayEnableDecorator(Collection<CalendarDay> dates){
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day){

        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view){
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.2f));
        view.setDaysDisabled(false);
    }

}
