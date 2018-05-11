package com.example.mnkrs.anote.NoteClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Surface on 2017/4/27 0027.
 */
public class HWNote extends Note{
    private String HWNoteTitle;
    private String HWNoteURL;
    public void setHWNoteTitle(String T){
        HWNoteTitle = T;
    }
    public String getHWNoteTitle(){
        return HWNoteTitle;
    }
    public void setHWNoteURL(String url){
        HWNoteURL = url;
    }
    public String getHWNoteURL(){
        return HWNoteURL;
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(HWNoteTitle);
        dest.writeString(HWNoteURL);
    }
    public static final Parcelable.Creator<HWNote> CREATOR = new Creator<HWNote>() {
        @Override
        public HWNote createFromParcel(Parcel source) {
            HWNote hwsenote = new HWNote();
            hwsenote.HWNoteTitle = source.readString();
            hwsenote.HWNoteURL = source.readString();
            return hwsenote;
        }

        @Override
        public HWNote[] newArray(int size) {
            return new HWNote[size];
        }
    };
}
