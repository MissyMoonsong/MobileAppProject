package com.example.jewel.test_project;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

/**
 * Created by allis on 3/26/2017.
 */

public class DatabaseEvent {

    private Integer id;
    private String event_name;
    private Integer start_hour, start_min;
    private Integer end_hour, end_min;
    private Integer start_month, start_day, start_year;
    private Integer end_month, end_day, end_year;
    private Boolean r_sunday, r_monday, r_tuesday, r_wednesday, r_thursday, r_friday, r_saturday;

    public DatabaseEvent() {
      /*Blank default constructor essential for Firebase*/
    }

        public String getEventName() {
        return event_name;
    }

    public void setEventName(String evname) {
        this.event_name = evname;
    }

    public Integer getStartHour() {
        return start_hour;
    }

    public void setStartHour(Integer shour) {
        this.start_hour = shour;
    }

    public Integer getStartMin() {
        return start_min;
    }

    public void setStartMin(Integer smin) {
        this.start_min = smin;
    }

    public Integer getEndHour() {
        return end_hour;
    }

    public void setEndHour(Integer ehour) {
        this.end_hour = ehour;
    }

    public Integer getEndMin() {
        return end_min;
    }

    public void setEndMin(Integer emin) {
        this.end_min = emin;
    }

    public Integer getStartMonth() {
        return start_month;
    }

    public void setStartMonth(Integer smonth) {
        this.start_month = smonth;
    }

    public Integer getStartDay() {
        return start_day;
    }

    public void setStartDay(Integer sday) {
        this.start_day = sday;
    }

    public Integer getStartYear() {
        return start_year;
    }

    public void setStartYear(Integer syear) {
        this.start_year = syear;
    }

    public Integer getEndMonth() {
        return end_month;
    }

    public void setEndMonth(Integer emonth) {
        this.end_month = emonth;
    }

    public Integer getEndDay() {
        return end_day;
    }

    public void setEndDay(Integer eday) {
        this.end_day = eday;
    }

    public Integer getEndYear() {
        return end_year;
    }

    public void setEndYear(Integer eyear) {
        this.end_year = eyear;
    }

    public Boolean getRSunday() {
        return r_sunday;
    }

    public void setRSunday(Boolean input) {
        this.r_sunday = input;
    }

    public Boolean getRMonday() {
        return r_monday;
    }

    public void setRMonday(Boolean input) {
        this.r_monday = input;
    }

    public Boolean getRTuesday() {
        return r_tuesday;
    }

    public void setRTuesday(Boolean input) {
        this.r_tuesday = input;
    }

    public Boolean getRWednesday() {
        return r_wednesday;
    }

    public void setRWednesday(Boolean input) {
        this.r_wednesday = input;
    }

    public Boolean getRThursday() {
        return r_thursday;
    }

    public void setRThursday(Boolean input) {
        this.r_thursday = input;
    }

    public Boolean getRFriday() {
        return r_friday;
    }

    public void setRFriday(Boolean input) {
        this.r_friday = input;
    }

    public Boolean getRSaturday() {
        return r_saturday;
    }

    public void setRSaturday(Boolean input) {
        this.r_saturday = input;
    }


}