package com.silptech.kdf;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.silptech.kdf.Utils.CacheNotification;
import com.silptech.kdf.Utils.InternetCheck;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Amrit on 2/4/2016.
 */
public class NoticesFragment extends android.support.v4.app.Fragment {

    private static final String KEY_AUTHOR = "Silptech";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    LinearLayout progressBar;
    public final static String TAG = "NoticesFragment";
    Context context;
    private ArrayList<NoticesModule> noticesArray;
    String filename, message_string, author_string, date_string;
    String xfileName = "cache_file";
    String notification_cache;
    File folder;
    int i;

    static final String KEY_ITEM = "item"; // parent node
    static final String KEY_TITLE = "title";
    static final String KEY_PUBDATE = "pubDate";
    ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map = new HashMap<String, String>();
    String url = "http://kdfpost.blogspot.com/feeds/posts/default?alt=rss";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notices, container, false);
        // initialising recyclerview and layoutmanager for adding list to cards
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        progressBar = (LinearLayout) v.findViewById(R.id.headerProgress);
        mLayoutManager = new LinearLayoutManager(context);
        folder = new File(Environment.getExternalStorageDirectory().toString() + "/KDF/Notices");
        folder.mkdirs();
        //executing xml parser on Asyntask method
        new NoticesParser().execute(url);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private class NoticesParser extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            //checking if internet is available and only parsing when avialable
            if (InternetCheck.hasInternet(getActivity())) {
                int savedNodeLength = 0;
                XMLParser parser = new XMLParser();
                String xml = parser.getXmlFromUrl(url);
                Document doc = parser.getDomElement(xml); // getting DOM element
                NodeList nl = doc.getElementsByTagName(KEY_ITEM);
                if (folder.list().length > 0) {
                    SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    savedNodeLength = sharedPreferences.getInt("last_xml_node", 0);
                }
                // looping through all item nodes <item>
                Log.i(TAG, "nl ko length:" + nl.getLength());
                Log.i(TAG, "sharePref ko length:" + savedNodeLength);
                //the new input data will always have the tag from 0 i.e. ascending order
                for (int i = 0; i < (nl.getLength() - savedNodeLength); i++) {
                    int y = folder.list().length;
                    filename = xfileName + y;
                    //save value on shared Preference and only execute y > nl.getLength() after 1st execution
                    Element e = (Element) nl.item(i);
                    // adding each child node to HashMap key => value
                    map.put(KEY_ITEM, parser.getValue(e, KEY_ITEM));
                    map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
                    map.put(KEY_PUBDATE, parser.getValue(e, KEY_PUBDATE));
                    // adding HashList to ArrayList
                    menuItems.add(map);
//                String a = menuItems.get(i).get(KEY_ITEM);
                    String b = menuItems.get(i).get(KEY_TITLE);
                    String c = menuItems.get(i).get(KEY_PUBDATE);

                    // adding HashList to ArrayList
                    folder = new File(Environment.getExternalStorageDirectory().toString() + "/KDF/Notices");
                    folder.mkdirs();
                    Log.i(TAG, "aayeko kuro : " + b);
                    Log.i(TAG, "aayeko kuro : " + c);
                    String toFile = ("#" + b + "##" + KEY_AUTHOR + "###" + c + "####");
                    Log.i(TAG, "toFile length : " + toFile.length());
                    CacheNotification.writeFile(filename, toFile, folder);
                    SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                    editor.putInt("last_xml_node", nl.getLength());
                    editor.apply();
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Internet Connection is required to check for new notice", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "Loading cached notices", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void success) {
            progressBar.setVisibility(View.GONE);
            try {
                mAdapter = new MyRecyclerViewAdapter(getDataSet());
                mRecyclerView.setAdapter(mAdapter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<NoticesModule> getDataSet() throws IOException {
        noticesArray = new ArrayList();

        for (int i = 0; i < folder.list().length; i++) {
            notification_cache = CacheNotification.readFile((xfileName + i), folder);
            if (notification_cache != "") {
                int a = notification_cache.indexOf("#");
                int b = notification_cache.indexOf("##");
                int c = notification_cache.indexOf("###");
                int d = notification_cache.indexOf("####");
                Log.i(TAG, "here :" + notification_cache.substring(a + 1, b));
                message_string = notification_cache.substring(a + 1, b);
                author_string = notification_cache.substring(b + 2, c);
                date_string = notification_cache.substring(c + 3, d);
                NoticesModule noticesModule = new NoticesModule();
                noticesModule.setMessage(message_string);
                noticesModule.setAuthor(author_string);
                noticesModule.setDate(date_string);
                noticesArray.add(noticesModule);
            }
        }
        Log.i(TAG, "folder length :" + folder.list().length);
        return noticesArray;
    }

}