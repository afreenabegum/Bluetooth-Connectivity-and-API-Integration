package com.example.bluetoothconnectivity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BluetoothReceiver : BroadcastReceiver () {
    override fun onReceive(context: Context?, intent: Intent?) {
       val action = intent?.action

        if(action == BluetoothAdapter.ACTION_STATE_CHANGED){
            when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR)){
                BluetoothAdapter.STATE_ON->{
                    Log.d("message 1","State ON")
                }
                BluetoothAdapter.STATE_OFF->{
                    Log.d("message 1","State OFF")
                }
                BluetoothAdapter.STATE_TURNING_OFF->{
                    Log.d("message 1","Turning OFF")
                }
                BluetoothAdapter.STATE_TURNING_ON->{
                    Log.d("message 1","Turning On")
                }
            }
        }
    }

}