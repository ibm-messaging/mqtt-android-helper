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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by Allan Marube on 7/8/2014.
 */
public class Publish extends Activity {


    private MqttAndroidClient client = null; //client handle
    public static String currTopic; //publish topic
    public static String currMessage; //publish payload

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish);
        client = MyActivity.client;
    }

    //publishes a message on topic on button(View) click with default Qos 0
    public void publish(View view) {
        MqttMessage message = new MqttMessage();
        EditText topicWidget = (EditText) findViewById(R.id.topic);
        String topic  = topicWidget.getText().toString();

        EditText sentMessage = (EditText) findViewById(R.id.message);
        String messageString = sentMessage.getText().toString();

        currTopic = topic;
        currMessage = messageString;
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

        System.out.println("Qos is:" + qos);

        message.setPayload(messageString.getBytes());
        message.setQos(qos);

        Switch retainedSwitch = (Switch) findViewById(R.id.switch1);

        boolean retained  = false;

        if (retainedSwitch.isChecked())
            retained = true;
        else
            retained = false;


        try {
            client.publish(topic, messageString.getBytes(), qos, retained);
        } catch (MqttException e) {
            e.printStackTrace();
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