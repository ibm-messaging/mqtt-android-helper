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

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Button;
import android.widget.Toast;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

import java.text.DateFormat;
import java.util.*;

/**
 * Created by Allan Marube on 7/18/2014.
 */
public class MqttHandler implements MqttCallback {

    private MqttAndroidClient client; //QTTClient
    private Context ctx;  //application context


    //payload being received are stored here
    HashMap<String, List<String>> payload = new HashMap<String, List<String>>();
    List<String> topicsReceived = new ArrayList<String>(); //list of payload topics

    private static MqttHandler ourInstance = new MqttHandler();  //MQTTHandler Instance

    //returns instance of MQTTHandler
    public static MqttHandler getInstance() {
        return ourInstance;
    }

    //constructor not used
    private MqttHandler() {
    }

    //creates an MQTTAndroid Client using  ctx, broker and clientID and returns client handle.
    public MqttAndroidClient getClient(Context ctx, String broker, String clientID) {
        if (client == null) {
            client = new MqttAndroidClient(ctx, broker, clientID);
            client.setCallback(ourInstance);
        }
        return client;
    }

    //returns client handle for current connection
    public MqttAndroidClient getClientHandle() {
        return client;
    }

    //Creates an Mqtt Client connection to the broker
    public void connect(MqttConnectOptions conOpts,Context ctx, List<Button> buttons) {
        this.ctx = ctx;
        try {
            client.connect(conOpts, ctx, new StatusListener(ctx, client, buttons));
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    //Checks if a client handle exists for a connection
    public boolean clientHandleExists() {
        if (client == null)
            return false;
        else
            return true;
    }

    //Callback method is called when a connection is lost
    public void connectionLost(Throwable cause) {
        Toast lost = Toast.makeText(ctx, "Connection lost", Toast.LENGTH_SHORT);
        lost.show();
        MyActivity mainActivity = (MyActivity)ctx;
        mainActivity.onDisconnect(); //dynamically modify buttons

    }
    //Called when delivery of message is complete
    public void deliveryComplete(IMqttDeliveryToken token) {
        String time = "<" + DateFormat.getDateTimeInstance().format(new Date()) + ">";
        String publish = time + ":" + Publish.currTopic+"<P>: " + Publish.currMessage + "\n";
        MyActivity.logScreen.append(publish);
        Toast toast = Toast.makeText(ctx, publish, Toast.LENGTH_SHORT);
        toast.show();
    }
    //Called when a message Arrives from the broker
    public void messageArrived(String topic, MqttMessage message) {

        //send notification
        int duration = Toast.LENGTH_SHORT;
        String text = "message Received:" + topic;
        Toast toast = Toast.makeText(ctx, text, duration);
       // toast.show();

        //save payload in an arrayList
        List messageRecvd = new ArrayList<String>();
        messageRecvd.add(message.toString());

        String uniqueTopic = topic+":"+ UUID.randomUUID().toString();

        payload.put(uniqueTopic, messageRecvd);
        topicsReceived.add(uniqueTopic);

        //broadcast message received to payload class
        Intent intent = new Intent("payload");
        intent.putExtra("topic", topic.toString());
        intent.putExtra("message", message.toString());
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);


        //print to the log screen
        String time = "<" + DateFormat.getDateTimeInstance().format(new Date()) + ">";
        String messageRecv = time + ":" + topic +"<S>: " + message+"\n";
        MyActivity.logScreen.append(messageRecv);
    }

}
