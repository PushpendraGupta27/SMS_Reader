package com.smsreader.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.smsreader.databinding.ActivityMainBinding
import com.smsreader.helperUtils.MsgHelper
import com.smsreader.model.MsgDataModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var msgAdapter: MsgAdapter
    private lateinit var msgList: ArrayList<MsgDataModel>

    private val SMS_PERMISSION_REQUEST_CODE = 101

    private val smsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val sender = intent?.getStringExtra("sender") ?: ""
            val msgBody = intent?.getStringExtra("body") ?: ""
            val msgTime = intent?.getLongExtra("time", 0) ?: 0
            val newSms = MsgDataModel(sender, msgBody, msgTime)

            // Update the dataset and notify adapter
            msgList.add(0, newSms) // Add new SMS at the top
            msgAdapter.notifyItemInserted(0) // Notify adapter about the new item

            // Scroll to the top to show new SMS
            binding.recyclerView.scrollToPosition(0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (checkSmsPermission()) {
            loadSms()
        } else {
            requestSmsPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(smsReceiver, IntentFilter("SMS_RECEIVED_ACTION"))
    }

    private fun checkSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECEIVE_SMS
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
            SMS_PERMISSION_REQUEST_CODE
        )
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSms()
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun loadSms() {
        msgList = MsgHelper.getAllSms(this)
        val originalList = ArrayList(msgList)
        msgAdapter = MsgAdapter(msgList)
        binding.recyclerView.adapter = msgAdapter

        binding.senderIdEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    msgAdapter.updateList(originalList)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchButton.setOnClickListener {
            val search = binding.senderIdEditText.text.toString()
            val filteredList = if (search.isEmpty()) msgList else msgList.filter {
                it.sender?.contains(search, ignoreCase = true) == true
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No matching messages found!", Toast.LENGTH_SHORT).show()
            } else {
                msgAdapter.updateList(ArrayList(filteredList))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver)
    }
}