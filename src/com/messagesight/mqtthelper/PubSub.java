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
import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.ArrayList;

/**
 * Created by Allan Marube on 7/8/2014.
 */
public class PubSub extends Activity {

    private MqttAndroidClient client = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pubsub);
        client = MyActivity.client;
        ArrayList<String> dbase =  getIntent().getStringArrayListExtra("database");
            
        //System.out.println(dbase.get(0));
       // System.out.println("power");

    }

    public void startPublisher(View view) {
        Intent publisher = new Intent(this, Publish.class);
        startActivity(publisher);
    }

    public void startSubscriber(View view) {
        Intent subscriber = new Intent(this, Subscribe.class);
        //subscriber.putExtra("database", getIntent().getStringArrayListExtra("database") );
        startActivity(subscriber);

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