package com.example.mnkrs.anote.NoteClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Surface on 2017/4/27 0027.
 */

public class Note  implements Parcelable {
    private String NoteId;
    public void setNoteId(String id){
        NoteId = id;
    }
    public String getNoteId(){
        return NoteId;
    }
    public int describeContents(){
        return 0;
    }
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(NoteId);
    }
    public static final Parcelable.Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            Note note = new Note();
            note.NoteId = source.readString();
            return note;
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}

