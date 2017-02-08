package fr.sebaoun.android.mnemosyne;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by sebaou_d on 17/01/17.
 */

public class Task extends RealmObject {
    private int     id;
    private String      name;
    private String      comment;
    private String      content;
    private Date        start;
    private Date        end;
    private boolean     state;


    /*
    * Getters
    */

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getContent() {
        return content;
    }

    public Calendar getStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        return calendar;
    }

    public String getDisplayableStart() {
        return dateToString(getStart());
    }

    public Calendar getEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(end);
        return calendar;
    }

    public String getDisplayableEnd() {
        return dateToString(getEnd());
    }

    public boolean getState() {
        return state;
    }

    /*
    * Setters
    */

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStart(Calendar start) {
        this.start = start.getTime();
    }

    public void setEnd(Calendar end) {
        this.end = end.getTime();
    }

    public void setState(boolean state) {
        this.state = state;
    }

    /*
    *
    */

    private String dateToString(Calendar calendar) {
        String date = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)) + "/" +
                Integer.toString(calendar.get(Calendar.MONTH) + 1) + "/" +
                Integer.toString(calendar.get(Calendar.YEAR)) + " " +
                Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                Integer.toString(calendar.get(Calendar.MINUTE));
        return date;
    }
}
