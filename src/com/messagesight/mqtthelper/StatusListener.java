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
import android.widget.Button;
import android.widget.Toast;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.List;

/**
 * Created by Allan Marube on 7/18/2014.
 */
public class StatusListener implements IMqttActionListener{

    private Context ctx; //application context
    private MqttAndroidClient client; //client handle
    private Toast toast; //connection status
    private Button connect; //connect button
    private Button disconnect; //disconnect button
    private Button pubsub; //pubsub button

    public StatusListener(Context ctx, MqttAndroidClient client, List<Button> buttons) {
        this.ctx = ctx;
        this.client = client;
        CharSequence text = "Connection Failure";  //default
        int duration = Toast.LENGTH_SHORT;
        toast = Toast.makeText(ctx, text, duration);

        //buttons to activate/deactivate
        connect = buttons.get(0);
        disconnect = buttons.get(1);
        pubsub = buttons.get(2);

    }

    public void onSuccess(IMqttToken asyncActionToken) {
        toast.setText("Connection successful!");
        toast.show();
        System.out.println("Connection is complete");
        if (client.isConnected()) {
            //enable buttons for disconnect and sub/pub
            //disable connection since its a single client application
            connect.setEnabled(false);
            disconnect.setEnabled(true);
            pubsub.setEnabled(true);


        }

    }

    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
       // System.out.println("Connection not successful!");
        toast.setText("Connection failure. Try again!");
        toast.show();
        exception.printStackTrace();
    }

}
