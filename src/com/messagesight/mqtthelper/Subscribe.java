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
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;

/**
 * Created by Allan Marube on 7/8/2014.
 */
public class Subscribe extends Activity {

    private MqttAndroidClient client = null;
    private ArrayList<String> subscriptions;
    private String currentTopic;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe);
        client = MyActivity.client;
        subscriptions = MyActivity.dbase;

       // ArrayList<String> database =  getIntent().getStringArrayListExtra("database");
        //System.out.println("finally:"+ database.get(0));

        //create unsubscribe buttons if needed
        if (!subscriptions.isEmpty()) {
            for (String s:subscriptions)
                createButton(s);
        }


    }

    //subscribe to topic on Click. Default Qos 0
    public void subscribe(View view) {
        EditText topicWidget = (EditText) findViewById(R.id.subtopic);
        String topic  = topicWidget.getText().toString();
        currentTopic = topic;


        int qos = 0;   //default qos

        RadioButton rb0 = (RadioButton) findViewById(R.id.radioButton);
        RadioButton rb1 = (RadioButton) findViewById(R.id.radioButton2);
        RadioButton rb2 = (RadioButton) findViewById(R.id.radioButton3);

        if (rb2.isChecked())
            qos = 2;
        else if (rb1.isChecked())
            qos = 1;
        else if (rb0.isChecked())
            qos = 0;


            if (!contains(topic))
                try {
                client.subscribe(topic, qos, getApplicationContext(), new SubscribeCallback());

       // getIntent().getStringArrayListExtra("database").add(topic);

        } catch (MqttException e) {
            //handle exception
        }



    }

     //performs a linear search of the subscriptions ArrayList for topic
     public boolean contains(String topic) {

         for (String t: subscriptions)
             if (t.compareTo(topic) == 0)
                 return true;
         return false;

     }

    //create an unsubscribe button
    public void createButton(String topic) {
        final LinearLayout layout = (LinearLayout) findViewById(R.id.subLayout);
        final Button bt = new Button(this);
        bt.setText(topic);
        layout.addView(bt);
        bt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //System.out.println("View:"+v.toString());
                unSubscribe(v);
                //LinearLayout layout = (LinearLayout) findViewById(R.id.subLayout);
                layout.removeView(bt);

            }

        });
    }

    //Unsubscribe client from topic on click of unsubscribe button
    public void unSubscribe(View view) {

        Button button = (Button) view;


        try {
            client.unsubscribe((String)button.getText());
            subscriptions.remove((String)button.getText());
        } catch (MqttException e) {
            e.printStackTrace();
        }



    }

    private class SubscribeCallback implements IMqttActionListener {

        public void onSuccess(IMqttToken token) {
            //TextView logScreen = (TextView) findViewById(R.id.logInfo);
            //String subscribeMessage = "subscription done";
            //logScreen.append("subscription made");
            Toast toast = Toast.makeText(getApplicationContext(), "Subscription successful", Toast.LENGTH_SHORT);
            toast.show();

            if (!contains(currentTopic)) {
                subscriptions.add(currentTopic);
                final LinearLayout layout = (LinearLayout) findViewById(R.id.subLayout);
                final Button bt = new Button(Subscribe.this);
                bt.setText(currentTopic);
                layout.addView(bt);
                bt.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        unSubscribe(v);
                        layout.removeView(bt);
                    }

                });
            }
        }

        public void onFailure(IMqttToken token, Throwable cause) {
            Toast toast = Toast.makeText(getApplicationContext(), "subscription failed", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mqttmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.payload:
                Intent intent = new Intent(this, PayloadViewer.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}