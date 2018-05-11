package com.example.mnkrs.anote.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mnkrs.anote.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditBaseNoteActivity extends AppCompatActivity {
    Bundle bundle;
    private String FLAG;
    private String UserId;
    private String baseNoteId;
    private String baseNoteTitle;
    private String baseNoteContent;
    TextView baseNoteTitleEditText;
    TextView baseNoteContentEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_edit_base_note);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        baseNoteTitleEditText = (TextView) findViewById(R.id.basenote_title);
        baseNoteContentEditText = (TextView) findViewById(R.id.basenote_content);


        bundle = this.getIntent().getExtras();
        UserId = bundle.getString("UserId");
        Log.v("EditBase获得的UserId为",UserId);
        if(bundle.getString("Flag").equals("打开")){
            FLAG = "updateBase";
            Log.v("eBaseNoteFLAG内容",FLAG);
            baseNoteId = bundle.getString("NoteId");
            Log.v("打开的Id为",baseNoteId);
            baseNoteTitle = bundle.getString("Title");
            baseNoteContent = bundle.getString("Content");
        }
        if(bundle.getString("Flag").equals("新建")){
            FLAG = "newBase";
            Log.v("eBaseNoteFLAG内容",FLAG);
            baseNoteId = getNow();
            Log.v("新建的Id为",baseNoteId);
            baseNoteTitle = "未命名笔记";
            baseNoteContent = "未编辑内容";
        }
        baseNoteTitleEditText.setText(baseNoteTitle);
        baseNoteContentEditText.setText(baseNoteContent);
    }
    String getNow(){
        Calendar now = Calendar.getInstance();
        String NowTime;
        NowTime = Integer.toString(now.get(Calendar.YEAR))
                +Integer.toString(now.get(Calendar.MONTH))
                +Integer.toString(now.get(Calendar.DAY_OF_MONTH))
                +Integer.toString(now.get(Calendar.HOUR_OF_DAY))
                +Integer.toString(now.get(Calendar.MINUTE))
                +Integer.toString(now.get(Calendar.SECOND));
        return NowTime;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        //左上角返回按钮监听
        String ServletUrl = "http://10.0.2.2:8080/ANoteServlet/Servlet/UpdateNote";
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(baseNoteId == null){
                }
                if(baseNoteId !=null){
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    Log.v("EB内请求队列被创建","是");
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST, ServletUrl,
                            new Response.Listener<String>(){
                                public void onResponse(String response){
                                    Log.v("eBaseNote返回按钮的操作信息",response);
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
                            map.put("Flag",FLAG);
                            map.put("UserId",UserId);
                            map.put("NoteId",baseNoteId);
                            map.put("NoteTitle", baseNoteTitleEditText.getText().toString());
                            map.put("NoteContent",baseNoteContentEditText.getText().toString());
                            return map;
                        }
                    };
                    requestQueue.add(stringRequest);
                    Bundle bundle = new Bundle();
                    bundle.putString("Flag","EB");
                    bundle.putString("UserId",UserId);
                    Intent intentToMainActivity = new Intent(EditBaseNoteActivity.this,MainActivity.class);
                    intentToMainActivity.putExtras(bundle);
                    startActivity(intentToMainActivity);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
