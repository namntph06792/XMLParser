package com.fox.xmlparser;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fox.xmlparser.model.News;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Button btnLoad;

    private String url = "http://docbao.vn/rss/export/home.rss";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingTask loadingTask = new LoadingTask();
                loadingTask.execute(url);
            }
        });
    }

    private void initViews(){
        btnLoad = findViewById(R.id.btnLoad);
    }

    //Create class for thread .AsyncTask
    class LoadingTask extends AsyncTask<String,Long, List<News>>{

        @Override
        protected List<News> doInBackground(String... strings) {

            List<News> arrayNews = new ArrayList<>();

            //Khong cap nhat giao dien trong ham nay
            try{
                //Khoi tao doi tuong URL de tao dia chi request
                URL url = new URL(strings[0]);

                //Mo ket noi den dia chi url
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                //Lay ra du lieu va gan bang doi tuong inputStream
                InputStream inputStream = httpURLConnection.getInputStream();

                //Thiet lap boc tach xml
                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                xmlPullParserFactory.setNamespaceAware(false);

                //Khoi tao va truyen du lieu vao doi tuong XmlPullParser
                XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();

                xmlPullParser.setInput(inputStream,"utf-8");

                int evenType = xmlPullParser.getEventType();

                News news = null;

                String text = "";
                while(evenType != xmlPullParser.END_DOCUMENT){
                    String name = xmlPullParser.getName();
                    switch (evenType){
                        case XmlPullParser.START_TAG:
                            if(name.equalsIgnoreCase("item")){
                                news = new News();
                            }
                            break;
                        case XmlPullParser.TEXT:
                            text = xmlPullParser.getText();
                            break;
                        case XmlPullParser.END_TAG:
                            if(news !=null && name.equalsIgnoreCase("title")){
                                news.title = text;
                            }else if(news !=null && name.equalsIgnoreCase("description")){
                                news.description = text;
                            }else if(news !=null && name.equalsIgnoreCase("pubDate")){
                                news.pubDate = text;
                            }else if(news !=null && name.equalsIgnoreCase("link")){
                                news.link = text;
                            }else if(name.equalsIgnoreCase("item")){
                                arrayNews.add(news);
                            }
                            break;
                    }
                    evenType = xmlPullParser.next();
                }


            }catch(MalformedURLException e){
                e.printStackTrace();
                Log.e("Exception 1", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Exception 2", e.getMessage());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                Log.e("Exception 3", e.getMessage());
            }
            return arrayNews;
        }

        @Override
        protected void onPostExecute(List<News> news) {
            super.onPostExecute(news);
            //Xu ly du lieu len man hinh tai day
            Toast.makeText(MainActivity.this,news.size() + "" , Toast.LENGTH_SHORT).show();
        }
    }

}
