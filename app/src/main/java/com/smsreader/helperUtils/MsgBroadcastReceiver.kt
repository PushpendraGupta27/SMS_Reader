package com.smsreader.helperUtils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MsgBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val msg = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (sms in msg) {
                val sender = sms.originatingAddress
                val msgBody = sms.messageBody
                val msgTime = sms.timestampMillis

                Log.d("SmsReceiver", "SMS from: $sender, Body: $msgBody, Time: $msgTime")

                val smsData = Intent("SMS_RECEIVED_ACTION")
                smsData.putExtra("sender", sender)
                smsData.putExtra("body", msgBody)
                smsData.putExtra("time", msgTime)

                LocalBroadcastManager.getInstance(context!!).sendBroadcast(smsData)
            }
        }
    }
}
