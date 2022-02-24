package com.example.jo.eznotes;


import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class Note {

    private String noteTitel;
    private String myNote;
    private boolean expanded;

    private int currPosition;

    private String firstLineOfNote;
    private String preview;

    String id;


    private String datum;

    public Note(String myNote) {
        this.myNote = myNote;
        Date dNow = new Date();
        SimpleDateFormat fd = new SimpleDateFormat("dd.MM.yyyy");
        datum = fd.format(dNow);


        expanded = false;

        String[] t = myNote.split("\n");
        this.noteTitel = t[0];

        if(t.length >= 2){
            preview = t[1];
            preview += "...";
        }else{
            preview = "...";
        }


    }

    public String getNoteTitel() {
        return noteTitel;
    }

    public void setNoteTitel(String noteTitel) {
        this.noteTitel = noteTitel;
    }

    public String getMyNote() {
        return myNote;
    }

    public void setMyNote(String myNote) {
        this.myNote = myNote;
    }

    public String getFirstLineOfNote() {
        return firstLineOfNote;
    }

    public void setFirstLineOfNote(String firstLineOfNote) {
        this.firstLineOfNote = firstLineOfNote;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public SpannableStringBuilder noteWithBoldTitle(){
        SpannableStringBuilder str = new SpannableStringBuilder(myNote);
        str.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, myNote.indexOf("\n") == -1 ? myNote.length() :  myNote.indexOf("\n")  , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return str;
    }

    public SpannableStringBuilder boldTitle(){
        SpannableStringBuilder str = new SpannableStringBuilder(noteTitel);
        str.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, noteTitel.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return str;
    }

    public SpannableStringBuilder greyHintTitle(){
        SpannableStringBuilder str = new SpannableStringBuilder(noteTitel);
        str.setSpan(new android.text.style.StyleSpan(Typeface.NORMAL), 0, noteTitel.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new android.text.style.ForegroundColorSpan(Color.GRAY), 0, noteTitel.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return str;
    }

    public int getAnzLine(){
        String[] lines = myNote.split("\r\n|\r|\n");
        return lines.length;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public int getCurrPosition() {
        return currPosition;
    }

    public void setCurrPosition(int currPosition) {
        this.currPosition = currPosition;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }
}
