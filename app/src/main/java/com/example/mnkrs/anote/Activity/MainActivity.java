package com.example.mnkrs.anote.Activity;

import android.content.Intent;
import android.util.Log;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mnkrs.anote.Fragment.baseNoteFragment;
import com.example.mnkrs.anote.Fragment.hwNoteFragment;
import com.example.mnkrs.anote.NoteClass.HWNote;
import com.example.mnkrs.anote.NoteClass.baseNote;
import com.example.mnkrs.anote.R;
import com.example.mnkrs.anote.Fragment.otherFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    baseNoteFragment b = new baseNoteFragment();
    hwNoteFragment h = new hwNoteFragment();
    private ViewPager mViewPager;
    private FloatingActionButton fab;
    private TabLayout tabLayout;

    private Bundle bundle;
    Bundle forH;
    private String UserId;
    private String tempStr;
    Gson gson = new Gson();
    String ServletUrl = "http://10.0.2.2:8080/ANoteServlet/Servlet/GetNoteList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //serArguments必须在Fragement与Activity绑定之前完成，否则不会赋值
        bundle = this.getIntent().getExtras();
        forH = new Bundle();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    //读取ListView内容放入onStart，如此返回此Activity时自动刷新
    protected  void onStart(){
        super.onStart();
        Bundle forB = new Bundle();
        UserId = bundle.getString("UserId");
        Log.v("Main从Login里获得的UserId为",UserId);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        Log.v("MainActivity内请求队列被创建","1");
        requestQueue.add(getList());
    }
    protected void onResume(){
        super.onResume();
    }
    private StringRequest getList(){
        StringRequest thisStringRequest = new StringRequest(Request.Method.POST,ServletUrl,
                new Response.Listener<String>(){
                    public void onResponse(String response){
                        Log.v("Mainresponse内容",response);
                        tempStr = response;
                        Log.v("Main获得的response串为",tempStr);
                        String list[] = tempStr.split("&");
                        Log.v("baseNoteJSON串",list[0]);
                        ArrayList<baseNote> bList = new ArrayList<baseNote>();
                        ArrayList<HWNote> hList = new ArrayList<HWNote>();
                        //读入数据前清空
                        bList.clear();
                        hList.clear();
                        if(!list[0].equals("[]")){
                            bList = gson.fromJson(list[0],new TypeToken<List<baseNote>>(){}.getType());
                            Log.v("Main内bListSize值",bList.size()+"");
                            Log.v("baseNoteJSON对象测试",bList.get(0).getNoteId()+" "+bList.get(0).getbaseNoteTitle()+" "+bList.get(0).getbaseNoteContent());
                        }
                        Log.v("HWNoteJSON串",list[1]);
                        if(!list[1].equals("[]")){
                            hList = gson.fromJson(list[1],new TypeToken<List<HWNote>>(){}.getType());
                            Log.v("HWNoteJSON对象测试",hList.get(0).getNoteId()+" "+hList.get(0).getHWNoteTitle()+" "+hList.get(0).getHWNoteURL());
                        }
                        bundle.putParcelableArrayList("BaseNoteList",bList);
                        forH.putString("UserId",UserId);
                        forH.putString("HWNoteList",list[1]);
                        h.setArguments(forH);
                        if(forH == null){
                            Log.v("数值未输入进Bundle","！！");
                        }
                        b.setArguments(bundle);
                        // Create the adapter that will return a fragment for each of the three
                        // primary sections of the activity.
                        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                        // Set up the ViewPager with the sections adapter.
                        mViewPager.setAdapter(mSectionsPagerAdapter);
                        tabLayout.setupWithViewPager(mViewPager);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putString("Flag","新建");
                                bundle.putString("UserId",UserId);
                                Intent intentToEditHWNote = new Intent(MainActivity.this,EditHWNoteActivity.class);
                                intentToEditHWNote.putExtras(bundle);
                                startActivity(intentToEditHWNote);
                            }
                        });
                    }
                },
                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        Log.e("TAG",error.getMessage(),error);
                    }
                }
        ){
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<>();
                map.put("UserId",UserId);
                return map;
            }
        };
        return thisStringRequest;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.v("debug","设置按钮被按下");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Bundle b = new Bundle();
            b.putString("Flag","新建");
            b.putString("UserId",UserId);
            Log.v("Main放入时UserId为：",UserId);
            Intent intentToEditbaseNote = new Intent(MainActivity.this,EditBaseNoteActivity.class);
            intentToEditbaseNote.putExtras(b);
            startActivity(intentToEditbaseNote);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // 根据position返回选中的Fragment
            switch(position){

                case 0 :
                    return b;
                case 1 :
                    return h;
                case 2 :
                    return new otherFragment();
            }
            return new baseNoteFragment();
        }

        @Override
        public int getCount() {
            // 显示三个选项卡
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "普通笔记";
                case 1:
                    return "手写笔记";
                case 2:
                    return "其他";
            }
            return null;
        }
    }
}
/**
public static class PlaceholderFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PlaceholderFragment() {
    }

    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_basenote, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.basenote_title);
        return rootView;
    }
}*/
