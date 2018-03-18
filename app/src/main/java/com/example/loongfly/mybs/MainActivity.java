package com.example.loongfly.mybs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.Util.VibreateUtil;
import com.example.loongfly.mybs.entity.temp_data;
import com.my.design.view.MyListView;
import com.my.design.view.ReFlashListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ReFlashListView.IReFlashListener{
    private static String SERVER_URL="http://192.168.31.87:8080/MyBs/servlet/InfoServlet";

    private ArrayAdapter<String> adpter;
   // ListView listView;
    private ReFlashListView listView;
    private ListView DrawerList;
    private Button btn;
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        MyAsyncTask asyncTask=new MyAsyncTask();
        if(isNetWorkAvailable(this)){
            initDate(asyncTask);
           /*if(asyncTask!=null && asyncTask.getStatus()==AsyncTask.Status.RUNNING){
                asyncTask.cancel(true);
            }*/


        }else{
            Toast.makeText(MainActivity.this,"网络连接失败",Toast.LENGTH_SHORT).show();
        }

        //adpter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,new String[]{"sss"});

        btn.setOnClickListener(new MyButtonListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("positionsss",position+"");
            switch (position){
                case 0:
                    final LinearLayout dia=(LinearLayout)getLayoutInflater().inflate(R.layout.input_layout,null);
                    AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MainActivity.this);
                    new AlertDialog.Builder(MainActivity.this).setView(dia)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("测试能不能用","触发了确认按钮");
                                    final Spinner spinner=(Spinner)dia.findViewById(R.id.spinner_type);
                                    final String s=(String)spinner.getSelectedItem();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                int type=-1;
                                                URL url = new URL(SERVER_URL);
                                                HttpURLConnection con=(HttpURLConnection)url.openConnection();
                                                con.setRequestMethod("POST");
                                                con.setDoInput(true);
                                                con.setDoOutput(true);
                                                OutputStreamWriter osw=new OutputStreamWriter(con.getOutputStream(),"utf-8");
                                                BufferedWriter bw=new BufferedWriter(osw);
                                                Log.d("收到的s",s);
                                                if(s.equals("温度提升")){
                                                    type=1;
                                                }else if(s.equals("温度降低")){
                                                    type=2;
                                                }
                                                Log.d("改变之后的type",""+type);
                                                bw.write("method=PostOrder&type="+type);
                                                bw.flush();
                                                int code=con.getResponseCode();
                                                Log.d("连接结果",""+code);
                                                if(code!=HttpURLConnection.HTTP_OK){
                                                    Log.d("hehehehehe","连接错误");
                                                    Looper.prepare();
                                                    Toast.makeText(MainActivity.this,"发送信息失败",Toast.LENGTH_SHORT).show();
                                                    Looper.loop();
                                                }else{
                                                    Looper.prepare();
                                                    Toast.makeText(MainActivity.this,"发送信息成功",Toast.LENGTH_SHORT).show();
                                                    Looper.loop();
                                                }
                                                con.disconnect();

                                            }catch(IOException E){
                                                E.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                    break;
                case 1:
                    break;
            }
        }
    }


    private void initViews() {
        btn=findViewById(R.id.button);
        listView=findViewById(R.id.listview);
        listView.setInterface(this);
        DrawerList=findViewById(R.id.drawer_list);
        mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);

        mPlanetTitles=getResources().getStringArray(R.array.operation);
        DrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item,mPlanetTitles));
        DrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }


    public static boolean isNetWorkAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm==null){
        }else{
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null || !cm.getBackgroundDataSetting()) {
                return false;
            }else{
                return true;
            }

        }
        return false;
    }
    public class MyButtonListener implements View.OnClickListener  {

        @Override
        public void onClick(View v) {

            final LinearLayout dia=(LinearLayout)getLayoutInflater().inflate(R.layout.input_layout,null);

            AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MainActivity.this);
            new AlertDialog.Builder(MainActivity.this).setView(dia)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("测试能不能用","触发了确认按钮");
                            final Spinner spinner=(Spinner)dia.findViewById(R.id.spinner_type);
                            final String s=(String)spinner.getSelectedItem();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        int type=-1;
                                        URL url = new URL(SERVER_URL);
                                        HttpURLConnection con=(HttpURLConnection)url.openConnection();
                                        con.setRequestMethod("POST");
                                        con.setDoInput(true);
                                        con.setDoOutput(true);
                                        OutputStreamWriter osw=new OutputStreamWriter(con.getOutputStream(),"utf-8");
                                        BufferedWriter bw=new BufferedWriter(osw);
                                        Log.d("收到的s",s);
                                        if(s.equals("温度提升")){
                                            type=1;
                                        }else if(s.equals("温度降低")){
                                            type=2;
                                        }
                                        Log.d("改变之后的type",""+type);
                                        bw.write("method=PostOrder&type="+type);
                                        bw.flush();
                                        int code=con.getResponseCode();
                                        Log.d("连接结果",""+code);
                                        if(code!=HttpURLConnection.HTTP_OK){
                                            Log.d("hehehehehe","连接错误");
                                            Looper.prepare();
                                            Toast.makeText(MainActivity.this,"发送信息失败",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }else{
                                            Looper.prepare();
                                            Toast.makeText(MainActivity.this,"发送信息成功",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }
                                        con.disconnect();

                                    }catch(IOException E){
                                        E.printStackTrace();
                                    }
                                }
                            }).start();


                       /* new AsyncTask<String,Void,Void>(){
                            @Override
                            protected Void doInBackground(String... strings) {
                                try{
                                    int type=-1;
                                    URL url = new URL(strings[0]);
                                    HttpURLConnection con=(HttpURLConnection)url.openConnection();
                                    con.setRequestMethod("POST");
                                    con.setDoInput(true);
                                    con.setDoOutput(true);
                                    OutputStreamWriter osw=new OutputStreamWriter(con.getOutputStream(),"utf-8");
                                    BufferedWriter bw=new BufferedWriter(osw);
                                    Log.d("收到的s",s);
                                    if(s.equals("温度提升")){
                                        type=1;
                                    }else if(s.equals("温度降低")){
                                        type=2;
                                    }
                                    Log.d("改变之后的type",""+type);
                                    bw.write("method=PostOrder&type="+type);
                                    bw.flush();
                                    int code=con.getResponseCode();
                                    Log.d("连接结果",""+code);
                                    if(code!=HttpURLConnection.HTTP_OK){
                                        Log.d("hehehehehe","连接错误");
                                        Looper.prepare();
                                        Toast.makeText(MainActivity.this,"发送信息失败",Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }else{
                                        Looper.prepare();
                                        Toast.makeText(MainActivity.this,"发送信息成功",Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                    }catch(IOException E){
                                        E.printStackTrace();
                                    }
                                return null;
                            }
                            }.execute(SERVER_URL);*/

                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }
    public class MyAsyncTask extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... strings) {
            if(isCancelled()){
                Log.d("是否返回","开始返回");
                return null;
            }
            String data=null;
            try{
                URL url=new URL(strings[0]);
                HttpURLConnection con=(HttpURLConnection)url.openConnection();

                con.setConnectTimeout(10*1000);
                con.setReadTimeout(10*1000);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/octet-stream");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStreamWriter osw=new OutputStreamWriter(con.getOutputStream(),"utf-8");
                BufferedWriter bw=new BufferedWriter(osw);
                bw.write("method=GetData");
                bw.flush();
                int code=con.getResponseCode();
                if(code==HttpURLConnection.HTTP_OK){
                    InputStream inputStream=con.getInputStream();
                    if(inputStream!=null){
                        data=convertStreamToString(inputStream);
                        Log.d("result","result======"+data);
                    }


                }else{
                    Looper.prepare();
                    Toast.makeText(MainActivity.this,"网络错误无法连接服务器",Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
                con.disconnect();
            }catch(IOException e){
                e.printStackTrace();
            }

            return data;
        }
        @Override
        protected  void onPostExecute(String result){
            super.onPostExecute(result);
            if(result!=null){
                setData(result);
            }

            Log.d("是否设置了","设置测试");
        }




    }
    public void initDate(MyAsyncTask asyncTask){
        asyncTask.execute(SERVER_URL);

    }
    public void setData(String result){

        try {
            JSONObject object=new JSONObject(result);

            JSONArray array=object.getJSONArray("temps");
            if(array!=null){
                Log.d("NUM",":::::::::::::::"+array.length());
            }
            String s[]=new String[100];
            ArrayList<String> list=new ArrayList<>();
            for(int i=0;i<array.length();i++){
                JSONObject temp=array.getJSONObject(i);
                float tempnum=Float.parseFloat(temp.get("TEMP").toString());
                /*if(tempnum < 10){
                    VibreateUtil.Vibrate(MainActivity.this,10*1000);
                }*/
                Log.d("time222",temp.get("TIME")+"");
                Date time=new Date((Long)temp.get("TIME"));
                Log.d("testtme",time.toString());

                SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss  EE",new Locale("zh"));
                String time_str=shortDateFormat.format(time);
                int id=(int)temp.get("ID");
                temp_data tp=new temp_data();
                tp.setId(id);
                tp.setTempNum(tempnum);
                tp.setTime(time);
                String str=tempnum+"     "+time_str;
                list.add(str);

            }

            Log.d("length",s.length+"");
            adpter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,list);

            listView.setAdapter(adpter);

        } catch (JSONException e) {
            Log.d("what error","message error");
            e.printStackTrace();
        }
    }
   /* public void GetData(){
        try {
            JSONObject object=new JSONObject(data);
            //JSONObject temps=object.getJSONObject("temps");
            JSONArray array=object.getJSONArray("temps");
            if(array!=null){


                Log.d("NUM",":::::::::::::::"+array.length());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
    public static String convertStreamToString(InputStream is){
        BufferedReader reader=new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line=null;

        try{
            while((line=reader.readLine())!=null){
                sb.append(line+"\n");
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    @Override
    public void onReflash() {
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MyAsyncTask asyncreflash=new MyAsyncTask();
                asyncreflash.execute(SERVER_URL);
                listView.reflashComplete();
            }
        },2000);

    }
}
