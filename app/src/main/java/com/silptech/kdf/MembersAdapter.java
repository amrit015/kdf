package com.silptech.kdf;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import com.silptech.kdf.Utils.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.silptech.kdf.Utils.CallDial;

import java.util.ArrayList;

/*
 * Created by Amrit on 3/30/2015.
 * This class is a custom adapter which implements BaseAdapter and returns text to the listView.
 * The call dialing functionality is also set here.
 */
public class MembersAdapter extends BaseAdapter {

    private static final String TAG = "MembersAdapter";
    ArrayList<MembersModule> newList;
    Activity context;

    public MembersAdapter(Activity activity, ArrayList<MembersModule> list) {
        newList = list;
        context = activity;
        Log.i(TAG, "new list size:  " + newList.size());
    }

    @Override
    public int getCount() {
        return newList.size();
    }

    @Override
    public Object getItem(int position) {
        return newList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return newList.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adapter_members, parent, false);
            holder.ID = (TextView) convertView.findViewById(R.id.member_id);
            holder.NAME = (TextView) convertView.findViewById(R.id.member_name);
            holder.ADDRESS = (TextView) convertView.findViewById(R.id.member_address);
            holder.PHONE = (TextView) convertView.findViewById(R.id.member_phone);
            holder.AMOUNT = (TextView) convertView.findViewById(R.id.member_amount);
            holder.CALL = (ImageView) convertView.findViewById(R.id.member_call);
            holder.phoneLayout = (LinearLayout) convertView.findViewById(R.id.layout_phone);
            holder.addressLayout = (LinearLayout) convertView.findViewById(R.id.layout_address);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ID.setText(Html.fromHtml(String.valueOf(newList.get(position).getId())).toString());
        holder.NAME.setText(Html.fromHtml(newList.get(position).getName()).toString());
        holder.ADDRESS.setText(Html.fromHtml(newList.get(position).getAddress()).toString());
        holder.PHONE.setText(Html.fromHtml(String.valueOf(newList.get(position).getPhone())).toString());
        holder.AMOUNT.setText(Html.fromHtml("Rs. " + String.valueOf(newList.get(position).getAmount())).toString());
        holder.phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phoneCall = String.valueOf((newList.get(position).getPhone()));
                CallDial.PhoneDialer(context, phoneCall);
            }
        });
        return convertView;
    }

    public class ViewHolder {
        TextView ID;
        TextView NAME;
        TextView ADDRESS;
        TextView PHONE;
        TextView AMOUNT;
        ImageView CALL;
        LinearLayout phoneLayout;
        LinearLayout addressLayout;
    }
}
