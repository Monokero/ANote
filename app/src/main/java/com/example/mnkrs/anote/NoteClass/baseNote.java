package com.example.mnkrs.anote.NoteClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Surface on 2017/4/27 0027.
 */

public class baseNote extends Note implements Parcelable{
    private String baseNoteTitle;
    private  String baseNoteContent;
    public void setbaseNoteTitle(String T){
        baseNoteTitle = T;
    }
    public String getbaseNoteTitle(){
        return baseNoteTitle;
    }
    public void setbasenoteContent(String con){
        baseNoteContent = con;
    }
    public String getbaseNoteContent(){
        return baseNoteContent;
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(baseNoteTitle);
        dest.writeString(baseNoteContent);
    }
    public static final Parcelable.Creator<baseNote> CREATOR = new Creator<baseNote>() {
        @Override
        public baseNote createFromParcel(Parcel source) {
            baseNote basenote = new baseNote();
            basenote.baseNoteTitle = source.readString();
            basenote.baseNoteContent = source.readString();
            return basenote;
        }

        @Override
        public baseNote[] newArray(int size) {
            return new baseNote[size];
        }
    };
}
