package com.example.androidtechnote.ble

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ItemBleListBinding

class BleListAdapter(val itemClick: (Int) -> Unit) : ListAdapter<BluetoothDevice, BleListViewHolder>(DiffUtil()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleListViewHolder {
        val binding = ItemBleListBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return BleListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BleListViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.binding.connect.setOnClickListener {
            itemClick(position)
        }
    }
}

class BleListViewHolder(val binding: ItemBleListBinding): RecyclerView.ViewHolder(binding.root){

    val context : Context
        get() = itemView.context

    fun bind(device: BluetoothDevice){
        binding.apply {
            deviceName.text = device.name
            address.text = device.address
            bondState.text = when(device.bondState){
                BluetoothDevice.BOND_BONDED -> context.getString(R.string.ble_bond_bonded)
                BluetoothDevice.BOND_BONDING -> context.getString(R.string.ble_bond_bonding)
                BluetoothDevice.BOND_NONE -> context.getString(R.string.ble_bond_none)
                else -> ""
            }
        }
    }
}

class DiffUtil: androidx.recyclerview.widget.DiffUtil.ItemCallback<BluetoothDevice>(){
    override fun areItemsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
        return (oldItem.address == newItem.address)
    }

    override fun areContentsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
        return (oldItem.uuids == newItem.uuids)
    }
}