package com.example.mnkrs.anote.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.icu.text.MessagePattern;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mnkrs.anote.CustomlizeTool.ImageUpload;
import com.example.mnkrs.anote.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditHWNoteActivity extends AppCompatActivity {
    //笔记，变量实例化、赋值参数不能在onCreat外面，必须在里面
    private Bundle bundle;
    private TextView HWNoteTitleTextView;
    private ImageView HWNoteContentImageView;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;


    File file;
    OutputStream outStream;

    private String UserId;
    private String HWNoteId;
    private String HWNoteTitle;
    private String HWNoteURL;
    private String FLAG;
    int width;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        bundle = this.getIntent().getExtras();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        UserId = bundle.getString("UserId");
        setContentView(R.layout.activity_edit_hw_note);
        HWNoteTitleTextView =(TextView) findViewById(R.id.hwnote_title);
        HWNoteContentImageView = (ImageView) findViewById(R.id.hwnote_content);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        width = HWNoteContentImageView.getWidth();
        if(bundle.getString("Flag").equals("打开")){
            Log.v("执行了打开","是");
            FLAG = "updateHW";
            HWNoteId = bundle.getString("NoteId");
            HWNoteTitle = bundle.getString("Title");
            HWNoteURL = bundle.getString("URL");
            Log.v("获取位图URL：","http://10.0.2.2:8080/"+HWNoteURL+HWNoteId+"Of"+UserId+".jpg");
            getImageAndShow(HWNoteURL);
        }
        if(bundle.getString("Flag").equals("新建")){
            Log.v("执行了新建","是");
            FLAG = "newHW";
            HWNoteId = getNow();
            HWNoteTitle = "未命名手写笔记";
            createImage(1055,1485);
            canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            showImage();
        }
        HWNoteTitleTextView.setText(HWNoteTitle);
    }
    protected void onStart(){
        super.onStart();

        checkPermission();
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
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.v("EB内请求队列被创建","是");
                saveToFile();
                UploadTask ut = new UploadTask();
                ut.execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    String folderString;
    private void saveToFile(){
        try {
            folderString = Environment.getExternalStorageDirectory().toString()+"/"+UserId;
            File filefolder = new File(folderString);
            if(!filefolder.exists()){
                filefolder.mkdirs();
                Log.v("file的mkdirs执行完成：","是");
            }
            file = new File(folderString,HWNoteId+"Of"+UserId+".jpg");
            Log.v("file对象创建完成：","是");
            if(!file.exists()){
                file.mkdirs();
            }
            if(file.exists()){
                file.delete();
            }
            outStream = new FileOutputStream(file);
            HWNoteContentImageView.setDrawingCacheEnabled(true);
            bitmap = HWNoteContentImageView.getDrawingCache();
            if(bitmap == null){Log.v("保存处bitmap为空","是");}
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outStream);
            HWNoteContentImageView.setDrawingCacheEnabled(false);
            Log.v("存储代码执行完成：","是");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private void checkPermission(){
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(this,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
    }
    private StringRequest updateURL(){
        String ServletUrl = "http://10.0.2.2:8080/ANoteServlet/Servlet/UpdateNote";
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, ServletUrl,
                new Response.Listener<String>(){
                    public void onResponse(String response){
                        Log.v("eHWNote返回按钮的服务器返回信息",response);
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
                map.put("NoteId",HWNoteId);
                map.put("NoteTitle", HWNoteTitleTextView.getText().toString());
                map.put("NoteContent",HWNoteURL);
                return map;
            }
        };
        return stringRequest;
    }
    private void back(){
        Bundle bundle = new Bundle();
        bundle.putString("Flag","EH");
        bundle.putString("UserId",UserId);
        Intent intentToMainActivity = new Intent(EditHWNoteActivity.this,MainActivity.class);
        intentToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentToMainActivity.putExtras(bundle);
        startActivity(intentToMainActivity);
        finish();
    }

    private void createImage(int w,int h){
        if(bitmap == null){Log.v("bitmap是否为空","是");}
        Log.v("执行了创建空白画布","是");
        bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
    }
    private void getImageAndShow(String ImageURL){
        RequestQueue rq = Volley.newRequestQueue(this);
        Log.v("执行了取位图","是");
        ImageRequest ir = new
                ImageRequest(ImageURL,
                new Response.Listener<Bitmap>(){
                    public void onResponse(Bitmap responseBitmap){
                        if(responseBitmap == null){
                            Log.v("位图未取到","是");
                        }else{Log.v("位图取到","是");}
                        bitmap = responseBitmap.copy(Bitmap.Config.ARGB_8888,true);
                        if(bitmap == null){
                            Log.v("位图未存入bitmap","是");
                        }else{Log.v("位图存入bitmap","是");}
                        Log.v("开始显示位图","是");
                        canvas = new Canvas(bitmap);
                        showImage();
                    }
                },0,0, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        Log.e("TAG",error.getMessage(),error);
                    }
                }
                );
        rq.add(ir);
    }
    int startX,startY,endX,endY;
    private void showImage(){
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        canvas.drawBitmap(bitmap,new Matrix(),paint);
        HWNoteContentImageView.setImageBitmap(bitmap);
        HWNoteContentImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        // 获取手按下时的坐标
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 获取手移动后的坐标
                        endX = (int) event.getX();
                        endY = (int) event.getY();
                        // 在开始和结束坐标间画一条线
                        canvas.drawLine(startX, startY, endX, endY, paint);
                        // 刷新开始坐标
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        HWNoteContentImageView.setImageBitmap(bitmap);
                        break;

                }
                return true;
            }
        });
    }
    class UploadTask extends AsyncTask{
        String tempURL;
        private String uploadImage(){
            String ServletUrl = "http://10.0.2.2:8080/ANoteServlet/Servlet/UpdateHWNoteContent";
            ImageUpload Upload = new ImageUpload();
            String response = Upload.uploadFile(file,ServletUrl);
            Log.v("uploadImage传图后返回的URL为",response);
            return response;
        }
        @Override
        protected String doInBackground(Object[] params) {
            tempURL = uploadImage();
            Log.v("EHW传图后返回的URL为",tempURL);
            return tempURL;
        }
        protected void onPostExecute(Object result){
            HWNoteURL = tempURL;
            Log.v("存完获取的路径为",HWNoteURL);
            requestQueue.add(updateURL());
            back();
        }
    }
}
