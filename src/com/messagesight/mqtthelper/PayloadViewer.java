/*******************************************************************************
 * Copyright (c) 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: Allan Marube
 *
 *******************************************************************************/
package com.messagesight.mqtthelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Allan Marube on 8/14/2014.
 */
public class PayloadViewer extends Activity {
    ExpandableListView expListView; //list View
    PayloadAdapter payloadAdapter;  //ArrayAdapter for payload
    List<String> headers;  //Stores topics for each
    HashMap<String, List<String>> listChildren; //holds payload information
    List<String> tempHeaders; //temporary store of headres for JSONObject keys
    HashMap<String, List<String>> tempListChildren; //temporary store for JSONObject values.

    private boolean isJsonView = false;  //is a JsonObject payload?
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.payload);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("payload"));
        expListView =(ExpandableListView)findViewById(R.id.expandableListView);


        Intent intent = getIntent();
        String jsonString = "";
        jsonString = intent.getStringExtra("json");

       // System.out.println("JSON Present: "+jsonString);
        headers = MqttHandler.getInstance().topicsReceived;
        listChildren = MqttHandler.getInstance().payload;
        payloadAdapter  = new PayloadAdapter(this,headers , listChildren);
        expListView.setAdapter(payloadAdapter); //set adapter


        // populate the listView for JSONObject Payloads
        if (jsonString != null) {

            try {
                JSONObject json = new JSONObject(jsonString);

                tempHeaders = new ArrayList<String>();
                tempListChildren = new HashMap<String, List<String>>();
               // headers.clear();
               // listChildren.clear();

                Iterator<?> keys = json.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();

                    tempHeaders.add(key);

                    List<String> val = new ArrayList<String>();
                    val.add(json.get(key).toString());
                    tempListChildren.put(key, val);
                }

                payloadAdapter  = new PayloadAdapter(this,tempHeaders , tempListChildren);
                expListView.setAdapter(payloadAdapter);
                isJsonView = true;

            } catch (JSONException e) {

            }
        }



        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                List<String> tempHeadersOnClick;
                HashMap<String, List<String>> tempListChildrenOnClick;
                if (isJsonView) {
                    tempHeadersOnClick = tempHeaders;
                    tempListChildrenOnClick = tempListChildren;
                } else {
                    tempHeadersOnClick = headers;
                    tempListChildrenOnClick = listChildren;

                }


                JSONObject json = null;
                try {
                    json = new JSONObject(tempListChildrenOnClick.get(tempHeadersOnClick.get(groupPosition)).get(childPosition));
                } catch (JSONException e) {

                }

                if (json != null) {
                    Intent intent = new Intent(getApplicationContext(), PayloadViewer.class);
                    intent.putExtra("json",json.toString());
                    startActivity(intent);
                }
                return false;
            }
        });

    }

    //receives messages form MqttHandler when a new message arrives
    //and updates the screen
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            String topicKey = intent.getStringExtra("topic");
            payloadAdapter.notifyDataSetChanged();

            //Button button = new Button(context);
            String [] topicKeyParts = topicKey.split(":");
            String topic = topicKeyParts[0];
            //System.out.println("This is the topic:"+topic);
            //createButton(topic);

           // Log.d("receiver", "Got message: " + message);


        }
    };

    @Override
    protected void onDestroy() {
        // Unregister receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.payload_menu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
            case R.id.clear:

                if (isJsonView) {
                    tempHeaders.clear();
                    tempListChildren.clear();
                } else {
                    headers.clear();
                    listChildren.clear();
                }
                payloadAdapter.notifyDataSetChanged();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}