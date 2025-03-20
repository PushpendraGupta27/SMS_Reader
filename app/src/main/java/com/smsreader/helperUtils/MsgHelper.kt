package com.smsreader.helperUtils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import com.smsreader.model.MsgDataModel

object MsgHelper {
    fun getAllSms(context: Context): ArrayList<MsgDataModel> {
        val smsList = ArrayList<MsgDataModel>()
        val uri: Uri = Telephony.Sms.Inbox.CONTENT_URI
        val projection = arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE)
        val cursor: Cursor? = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            Telephony.Sms.DEFAULT_SORT_ORDER
        )

        cursor?.use {
            val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)

            while (it.moveToNext()) {
                val sender = it.getString(addressIndex)
                val body = it.getString(bodyIndex)
                val time = it.getLong(dateIndex)
                smsList.add(MsgDataModel(sender, body, time))
            }
        }
        return smsList
    }
}