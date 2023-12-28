package com.example.bluetoothconnectivity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ipsec.ike.IkeSessionParams.IkeAuthDigitalSignLocalConfig
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.example.bluetoothconnectivity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var binding : ActivityMainBinding
    lateinit var receiver : BluetoothReceiver
    lateinit var receiver2 : Discoverability
    var permission : Boolean = false
    val REQUEST_ACCESS_COARSE_LOCATION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        enableDisableBlueTooth()
        enableDisableDiscoverability()
        receiver = BluetoothReceiver()
        receiver2 = Discoverability()

        binding.getPairedDevices.setOnClickListener {
            getPairedDevices()
        }

        binding.getDiscoverDevices.setOnClickListener {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                when(ContextCompat.checkSelfPermission(
                    baseContext,Manifest.permission.ACCESS_COARSE_LOCATION
                )){
                    PackageManager.PERMISSION_DENIED -> androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("RunTime Permission")
                        .setMessage("Give Permission")
                        .setNeutralButton("Okay", DialogInterface.OnClickListener{ dialog, which ->
                            if(ContextCompat.checkSelfPermission(baseContext,Manifest.permission.ACCESS_COARSE_LOCATION)!=
                                PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),REQUEST_ACCESS_COARSE_LOCATION)
                            }

                        }).show()
                        .findViewById<TextView>(androidx.appcompat.R.id.message)!!.movementMethod = LinkMovementMethod.getInstance()


                        PackageManager.PERMISSION_GRANTED -> {
                            Log.d("discoverable Devices", " Permission Granted")
                        }

                }
            }
            discoverDevices()
        }
    }

    @SuppressLint("MissingPermission")
    private fun discoverDevices() {
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
//        registerReceiver(discoverDeviceReceiver, filter)
        bluetoothAdapter.startDiscovery()
    }

//    val discoverDeviceReceiver = object : BroadcastReceiver(){
//        @SuppressLint("MissingPermission")
//        override fun onReceive(context: Context?, intent: Intent?) {
//            var action = ""
//            if(intent!=null){
//                action = intent.action.toString()
//            }
//            when(action){
//                BluetoothAdapter.ACTION_STATE_CHANGED -> {
//                    Log.d("discoverDevices1", "STATE CHANGED")
//                }
//                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
//                    Log.d("discoverDevices2", "Discovery Started")
//                }
//                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
//                    Log.d("discoverDevices3", "Discovery Finished")
//                }
//                BluetoothDevice.ACTION_FOUND ->{
//                    val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//                    if(device != null){
//                        Log.d("discoverDevices4", "${device.name} ${device.address}")
//                        var it = 0
//                        it++
//                        when(it){
//                            1->{
//                                binding.device1.text = device.name
//                                binding.device1.setOnClickListener {
//                                    device.createBond()
//                                }
//                            }
//                        }
//                        when(device.bondState){
//                            BluetoothDevice.BOND_NONE ->{
//                                Log.d("Bluetooth Bond Status", "${device.name} bond name")
//                            }
//                            BluetoothDevice.BOND_BONDING ->{
//                                Log.d("Bluetooth Bond Status", "${device.name} bond bonding")
//                            }
//                            BluetoothDevice.BOND_BONDED ->{
//                                Log.d("Bluetooth Bond Status", "${device.name} bonded")
//                            }
//
//                        }
//
//                    }
//
//                }
//            }
//        }
//
//    }

    @SuppressLint("MissingPermission")
    private fun getPairedDevices() {
        val arr =  bluetoothAdapter.bondedDevices
        Log.d("bondedDevices", arr.size.toString())
        Log.d("bondedDevices", arr.toString())
        for(device in arr){
            Log.d("bondedDevices", device.name + " " + device.address + " " + device.bondState)
        }

    }

    private fun enableDisableDiscoverability() {
        when{
            ContextCompat.checkSelfPermission(this,android.Manifest.permission.BLUETOOTH_ADVERTISE)==PackageManager.PERMISSION_GRANTED ->{
                // if permission there no need to show anything
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.BLUETOOTH_ADVERTISE)->{
                // else need to show
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BLUETOOTH_ADVERTISE),101)
            }
        }
        binding.btnDiscover.setOnClickListener {
            val discoverIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,20)
            startActivity(discoverIntent)

            val intentFilter = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
            registerReceiver(receiver2,intentFilter)
        }

    }

    private fun enableDisableBlueTooth() {
        when{
            ContextCompat.checkSelfPermission(this,android.Manifest.permission.BLUETOOTH_CONNECT)==PackageManager.PERMISSION_GRANTED ->{
                // if permission there no need to show anything
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.BLUETOOTH_CONNECT)->{
                // else need to show
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT),101)
            }
        }

        binding.btnOnOff.setOnClickListener{
            // checking if bluetooth adaptor is on or not
            if(!bluetoothAdapter.isEnabled){
                // Enable
                bluetoothAdapter.enable()
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivity(intent)

                // For Bluettoth Access
                val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                registerReceiver(receiver,intentFilter)
            }
            if(bluetoothAdapter.isEnabled){
                    bluetoothAdapter.disable()

                    val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                    registerReceiver(receiver, intentFilter)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        unregisterReceiver(receiver2)
    }
}
