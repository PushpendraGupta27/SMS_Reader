package com.smsreader.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smsreader.databinding.ItemsSmsBinding
import com.smsreader.model.MsgDataModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MsgAdapter(private var arrayList: ArrayList<MsgDataModel>,) :
    RecyclerView.Adapter<MsgAdapter.SmsViewHolder>() {

    inner class SmsViewHolder(private val binding: ItemsSmsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun innerBind(data: MsgDataModel) {
            binding.senderIdTextView.text = "Sender: ${data.sender}"
            binding.bodyTextView.text = data.body
            binding.sentTimeTextView.text = "Time: ${convertMillisToDateTime(data.msgTime)}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgAdapter.SmsViewHolder {
        val binding = ItemsSmsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SmsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        holder.innerBind(arrayList[position])
    }

    // Function to update dataset manually
    fun updateList(newList: ArrayList<MsgDataModel>) {
        arrayList.clear()
        arrayList.addAll(newList)
        notifyDataSetChanged() // Notify RecyclerView that data has changed
    }

    private fun convertMillisToDateTime(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())  // Adjust format as needed
        return sdf.format(Date(millis))
    }
}