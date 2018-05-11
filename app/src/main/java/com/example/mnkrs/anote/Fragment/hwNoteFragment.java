package com.example.mnkrs.anote.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.example.mnkrs.anote.Activity.EditHWNoteActivity;
import com.example.mnkrs.anote.NoteClass.HWNote;
import com.example.mnkrs.anote.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mnkrs on 2017/4/22 0022.
 */

public class hwNoteFragment extends Fragment {

    public static hwNoteFragment newInstance(Bundle sectionNumber) {
        hwNoteFragment fragment = new hwNoteFragment();
        //??
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    String ServletUrl = "http://10.0.2.2:8080/ANoteServlet/Servlet/UpdateNote";
    private View.OnClickListener baseNoteItemOnClickListener;

    Gson gson = new Gson();
    Bundle bundle;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v("onCreate执行","是");
        bundle = getArguments();
        if(bundle == null){
            Log.v("bundle为空","是");
        }
    }
    private ListView hwNoteListView;
    private ArrayList<HWNote> hList = new ArrayList<HWNote>();
    List<String> hNoteList = new ArrayList<String>();
    ArrayAdapter adapter;
    private String UserId;
    String str;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        str = "";
        if(bundle == null){Log.v("是否为空","为空");}
        UserId = bundle.getString("UserId");
        str = bundle.getString("HWNoteList");
        Log.v("hwFrag的JSON串为",str);
        View rootView = inflater.inflate(R.layout.fragment_hwnote, container, false);
        hwNoteListView =(ListView) rootView.findViewById(R.id.hwnoteListView);
        if(!str.equals("[]")){
            hList = gson.fromJson(str,new TypeToken<List<HWNote>>(){}.getType());
            Log.v("hFrag内获得值：",hList.get(0).getNoteId()+" "+hList.get(0).getHWNoteTitle()+" "+hList.get(0).getHWNoteURL());
            final String[] st = new String[hList.size()];
            for(int i = 0;i<hList.size();i++){
                hNoteList.add(hList.get(i).getHWNoteTitle());
                adapter = new ArrayAdapter<String>(rootView.getContext(),android.R.layout.simple_list_item_1,hNoteList);
                hwNoteListView.setAdapter(adapter);
            }
        }else if(str.equals("[]")){
        }
        hwNoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //创建Bundle对象用来存储传值
                Bundle bundle = new Bundle();
                bundle.putString("Flag","打开");
                bundle.putString("UserId",UserId);
                bundle.putString("NoteId",hList.get(position).getNoteId());
                bundle.putString("Title",hList.get(position).getHWNoteTitle());
                bundle.putString("URL",hList.get(position).getHWNoteURL());
                Intent intent = new Intent(getContext(),EditHWNoteActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        hwNoteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                                        map.put("Flag","deleteHW");
                                        map.put("UserId",UserId);
                                        map.put("NoteId",hList.get(position).getNoteId());
                                        map.put("NoteTitle", hList.get(position).getHWNoteTitle());
                                        map.put("NoteContent",hList.get(position).getHWNoteURL());
                                        return map;
                                    }
                                };
                                requestQueue.add(stringRequest);
                                hNoteList.remove(position);
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
        return rootView;
    }
    private class OnBaseNoteItemOnClick implements AdapterView.OnItemClickListener{
        public void onItemClick(AdapterView<?> parent, View view, int position,long id){

        }
    }
}
