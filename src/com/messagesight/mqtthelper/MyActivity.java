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
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    public static MqttAndroidClient client; //Mqtt Android Client
    public static ArrayList<String> dbase;  //subscriptions
    public static TextView logScreen;   //textView for log of subscription data

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        logScreen = (TextView)findViewById(R.id.logInfo);
        logScreen.setMovementMethod(new ScrollingMovementMethod());

        EditText serverID = (EditText) findViewById(R.id.server);
        serverID.setText("messagesight.demos.ibm.com");
        EditText portID = (EditText) findViewById(R.id.port);
        portID.setText("1883");

        dbase = new ArrayList<String>();
        //System.out.println("class being started");

    }

    /*
     *  Creates a client connection on connect button Click to an Mqtt broker
     *  specified by the user
     */
    public void establishConnection(View view) {

        EditText clientID = (EditText) findViewById(R.id.clientId);
        String clientName = clientID.getText().toString();

        if (clientName.compareTo("") == 0){
            Toast.makeText(this, "Invalid Client ID",Toast.LENGTH_SHORT ).show();
            return;
        }


        EditText serverID = (EditText) findViewById(R.id.server);
        String serverName = serverID.getText().toString();
        EditText portID = (EditText) findViewById(R.id.port);
        String portName = portID.getText().toString();

        String broker = "tcp://" + serverName + ":" + portName;
        // broker = "tcp://messagesight.demos.ibm.com:1883";
        client =  MqttHandler.getInstance().getClient(this, broker, clientName);
        client.setCallback(MqttHandler.getInstance());

        MqttConnectOptions connOpts = new MqttConnectOptions();

        connOpts.setConnectionTimeout(0);



        Switch cleanSession = (Switch)findViewById(R.id.switchClean);
        if (cleanSession.isChecked())
            connOpts.setCleanSession(true);
        else
            connOpts.setCleanSession(false);

        Button disconnect = (Button) findViewById(R.id.button2);
        Button pubsub = (Button) findViewById(R.id.button3);
        Button connect = (Button) findViewById(R.id.button);


        List<Button> buttons = new ArrayList<Button>();
        buttons.add(connect);
        buttons.add(disconnect);
        buttons.add(pubsub);

        //buttons are passed to the connection to dynamically enable/disable
        MqttHandler.getInstance().connect(connOpts, this, buttons);

    }


    /*
     * Dynamically enable connect button and disable
     * disconnect and pub/sub buttons
     */
    public void onDisconnect() {
        Button disconnect = (Button) findViewById(R.id.button2);
        Button connect = (Button) findViewById(R.id.button);
        Button pubsub = (Button) findViewById(R.id.button3);

        disconnect.setEnabled(false);
        connect.setEnabled(true);
        pubsub.setEnabled(false);

        //clear subscription data
        MqttHandler.getInstance().topicsReceived.clear();
        MqttHandler.getInstance().payload.clear();
        dbase.clear();

    }

    /*
     * Checks if MqttAndroidClient client is connected to the broker
     * Returns true if connected or false otherwise
     */
    public boolean isConnected() {
        return client.isConnected();

    }

    /*
     * Called when disconnect button is clicked. Disconnects client
     * from the broker
     */
    public void disconnect(View view) {
        try {
           // System.out.println("Client ID:"+ client.getClientId());
            client.disconnect();

        } catch (MqttException e){
            e.printStackTrace();
        }

        //clear subscription data
        MqttHandler.getInstance().topicsReceived.clear();
        MqttHandler.getInstance().payload.clear();
        dbase.clear();

    }


    /*
     * Called when pub/sub button is clicked
     * Starts PubSub activity
     */
    public void startPubSub(View view) {
        Intent statusIntent = new Intent(getApplicationContext(), PubSub.class);
        // statusIntent.putExtra("database",dbase);
        startActivity(statusIntent);
    }

    //clears the log screen
    public void clearLog(View view) {
        logScreen.setText("");
    }

    //returns current client handle
    public MqttAndroidClient clientHandle(){
        return client;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    /*
     *Unregister client resources (i.e intent receivers, AlarmPingSender and MqqttService)
     *before destroying the activity
     */
    @Override
    public void onDestroy() {
        if (MqttHandler.getInstance().clientHandleExists())
             client.unregisterResources();
        //clear subscription data
        MqttHandler.getInstance().topicsReceived.clear();
        MqttHandler.getInstance().payload.clear();
        dbase.clear();
        super.onDestroy();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mqttmenu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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

