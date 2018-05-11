package com.example.mnkrs.anote.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mnkrs.anote.Activity.EditBaseNoteActivity;
import com.example.mnkrs.anote.NoteClass.baseNote;
import com.example.mnkrs.anote.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mnkrs on 2017/4/21 0021.
 */

public class baseNoteFragment extends Fragment{

    private Context MainActivityContext;
    Bundle bundle = new Bundle();
    public baseNoteFragment() {
        Bundle bundle=getArguments();
    }
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public void onAttach(){

    }
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        bundle = getArguments();
    }
    public static baseNoteFragment newInstance(Bundle sectionNumber) {
        baseNoteFragment fragment = new baseNoteFragment();
        //??
        Bundle args = sectionNumber;
        fragment.setArguments(args);
        return fragment;
    }

    String ServletUrl = "http://10.0.2.2:8080/ANoteServlet/Servlet/UpdateNote";

    private RecyclerView baseNoteRecyclerView;
    private String UserId;
    private ArrayList<baseNote> bList = new ArrayList<baseNote>();
    List<String> bNoteList = new ArrayList<String>();
    ArrayAdapter adapter;
    private ListView baseNoteListView;
    Gson gson = new Gson();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle baseNoteBundle) {
        bList.clear();
        UserId = bundle.getString("UserId");
        bList = bundle.getParcelableArrayList("BaseNoteList");
        Log.v("bList内Size值",bList.size()+"");
        View rootView = inflater.inflate(R.layout.fragment_basenote, container, false);
        //Fragment内不能直接使用findViewById必须通过rootView调用绑定控件
        baseNoteListView = (ListView) rootView.findViewById(R.id.baseNoteListView);
        if(!gson.toJson(bList).equals("[]")){
            bNoteList.clear();
            Log.v("bFrag内获得值：",bList.get(0).getNoteId()+" "+bList.get(0).getbaseNoteTitle()+" "+bList.get(0).getbaseNoteContent());
            for(int i = 0;i<bList.size();i++){
                bNoteList.add(bList.get(i).getbaseNoteTitle());
            }
            adapter = new ArrayAdapter<String>(rootView.getContext(),android.R.layout.simple_list_item_1,bNoteList);
            baseNoteListView.setAdapter(adapter);
        }else if(gson.toJson(bList) == "[]"){
        }
        baseNoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("表项被按下","是");
                //创建Bundle对象用来存储传值
                bundle.putString("Flag","打开");
                bundle.putString("UserId",UserId);
                bundle.putString("NoteId",bList.get(position).getNoteId());
                bundle.putString("Title",bList.get(position).getbaseNoteTitle());
                bundle.putString("Content",bList.get(position).getbaseNoteContent());
                Intent intent = new Intent(getContext(),EditBaseNoteActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        baseNoteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
                new AlertDialog.Builder(getContext())
                        .setTitle("删除")
                        .setIcon(R.drawable.a_notes_main_icon)
                        .setMessage("是否删除笔记？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //调用联网模块
                                RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                                Log.v("EB内请求队列被创建","是");
                                StringRequest stringRequest = new StringRequest(
                                        Request.Method.POST, ServletUrl,
                                        new Response.Listener<String>(){
                                            public void onResponse(String response){
                                                Log.v("长按删除返回的操作信息",response);
                                            }
                                        },
                                        new Response.ErrorListener(){
                                            public void onErrorResponse(VolleyError error){
                                                Log.e("TAG",error.getMessage(),error);
                                            }
                                        }
                                )
                                {
                                    protected Map<String,String> getParams(){
                                        Map<String,String> map = new HashMap<>();
                                        map.put("Flag","deleteBase");
                                        map.put("UserId",UserId);
                                        map.put("NoteId",bList.get(position).getNoteId());
                                        map.put("NoteTitle", bList.get(position).getbaseNoteTitle());
                                        map.put("NoteContent",bList.get(position).getbaseNoteContent());
                                        return map;
                                    }
                                };
                                requestQueue.add(stringRequest);
                                bNoteList.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                return true;
            }
        });
        Log.v("BFrag结束","是");
        return rootView;
        /*尝试用RecyclerView代替ListView
        baseNoteRecyclerView = (RecyclerView) rootView.findViewById(R.id.baseNoteRecycleView);
        //固定RecyclerView大小
        baseNoteRecyclerView.setHasFixedSize(true);
        titles = setData();
        baseNoteRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        baseNoteRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //getActivity存疑
        baseNoteRecyclerViewAdapter adapter = new baseNoteRecyclerViewAdapter(rootView,getActivity(),titles);
        baseNoteRecyclerView.setAdapter(adapter);
        */
    }
}

