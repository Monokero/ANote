package com.example.mnkrs.anote.CustomlizeTool;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.*;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mnkrs.anote.R;

import java.util.List;
import java.util.Map;

/**
 * Created by piaol on 2017/4/24 0024.
 */

public class baseNoteRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context baseNoteFragementContext;
    //
    private List<Map<String,Object>> baseNoteTitleList;
    private LayoutInflater thisLayoutInflater;
    private OnClickListener OnItemClickListener;
    private OnLongClickListener OnItemLongClickListener;
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = thisLayoutInflater.from(parent.getContext()).inflate(R.layout.item_base_note_list,parent,false);
        return new ViewHolder(v);
    }
    public baseNoteRecyclerViewAdapter(View rootView,Context mContext, List<Map<String,Object>> baseNoteTitleList){
        //调转到此Fragment时获取需要资源
        this.baseNoteFragementContext = mContext;
        this.baseNoteTitleList = baseNoteTitleList;
        thisLayoutInflater = LayoutInflater.from(mContext);
    }
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Map<String,Object> entity = baseNoteTitleList.get(position);

        for(int i=0;i<baseNoteTitleList.size();i++){
            Log.v("entity输出",baseNoteTitleList.get(i).toString());
        }
        if(null == entity){
            return;
        }
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.baseNoteTitleTextView.setText(entity.get("baseNoteTitleTextView").toString());
    }
    public int getItemCount() {
        //计算列表长度函数
        return baseNoteTitleList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView baseNoteTitleTextView;
        public ViewHolder(View itemView){
            super(itemView);
            baseNoteTitleTextView = (TextView)itemView.findViewById(R.id.baseNoteTitleTextView);
            itemView.findViewById(R.id.baseNoteTitleContainer).setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){

                }
            });
        }
    }
}
