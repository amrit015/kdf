package com.silptech.kdf;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Amrit on 2/4/2016.
 */
public class ContactFragment extends android.support.v4.app.Fragment {
    /*
             Stores the scroll position of the ListView
    */
    private static Parcelable mListViewScrollPos = null;

    private final static String TAG = "MainActivity";
    DatabaseHelper dbHelper = null;
    Context context;
    ListView listView;
    ArrayList<MembersModule> memberList = new ArrayList<MembersModule>();
    CustomAdapter adapter;
    File folder;
    InputStream assets_path;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts_listview, container, false);
        listView = (ListView) view.findViewById(R.id.listview);
        folder = new File(Environment.getExternalStorageDirectory().toString() + "/KDF/Database");
        folder.mkdirs();

        //creating database object and defining its path
        try {
            assets_path = getActivity().getAssets().open("database.db");
            dbHelper = new DatabaseHelper(context, folder.toString(), assets_path);
            Log.i(TAG, " from contacts :" + folder.toString());
            dbHelper.prepareDatabase();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        getData();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Restore the ListView position
        if (mListViewScrollPos != null) {
            listView.onRestoreInstanceState(mListViewScrollPos);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListViewScrollPos = listView.onSaveInstanceState();
    }

    private void getData() {
        //getting data from data in asset folder and inserting in array
        ArrayList<MembersModule> list = dbHelper.getMembersModule();
        for (int i = 0; i < list.size(); i++) {

            int id = list.get(i).getId();
            String name = list.get(i).getName();
            String address = list.get(i).getAddress();
            int phone = list.get(i).getPhone();
            int amount = list.get(i).getAmount();

            MembersModule membersModule = new MembersModule();
            membersModule.setId(id);
            membersModule.setName(name);
            membersModule.setAddress(address);
            membersModule.setPhone(phone);
            membersModule.setAmount(amount);
            memberList.add(membersModule);
        }
        adapter = new CustomAdapter(getActivity(), memberList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_search_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_actionbar:
                Search();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Search() {
        Intent intent = new Intent(getActivity(), SeachQueryActivity.class);
        startActivity(intent);
    }
}