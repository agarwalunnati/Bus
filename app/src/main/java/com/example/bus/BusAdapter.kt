package com.example.bus

import BusDetails
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class BusAdapter(private val context: Context, private val busList: List<BusDetails>) :
    RecyclerView.Adapter<BusAdapter.BusViewHolder>() {

    class BusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val busNumber: TextView = itemView.findViewById(R.id.busNumber)
        val studentName: TextView = itemView.findViewById(R.id.studentName)
        val pickupLocation: TextView = itemView.findViewById(R.id.pickupLocation)
        val dropLocation: TextView = itemView.findViewById(R.id.dropLocation)
        val editButton: Button = itemView.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bus_info, parent, false)
        return BusViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        val bus = busList[position]

        holder.busNumber.text = bus.busNumber ?: "Unknown"
        holder.studentName.text = bus.studentName ?: "No Name"
        holder.pickupLocation.text = bus.pickupLocation ?: "N/A"
        holder.dropLocation.text = bus.dropLocation ?: "N/A"

        holder.editButton.setOnClickListener {
            showEditDialog(bus)
        }
    }

    override fun getItemCount(): Int = busList.size

    private fun showEditDialog(bus: BusDetails) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_bus, null)

        val busNumberEditText = dialogView.findViewById<EditText>(R.id.editBusNumber)
        val studentNameEditText = dialogView.findViewById<EditText>(R.id.editStudentName)
        val pickupEditText = dialogView.findViewById<EditText>(R.id.editPickupLocation)
        val dropEditText = dialogView.findViewById<EditText>(R.id.editDropLocation)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        // Set existing values in fields
        busNumberEditText.setText(bus.busNumber ?: "")
        studentNameEditText.setText(bus.studentName ?: "")
        pickupEditText.setText(bus.pickupLocation ?: "")
        dropEditText.setText(bus.dropLocation ?: "")

        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Edit Bus Info")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .create()

        saveButton.setOnClickListener {
            val newStudentName = studentNameEditText.text.toString()
            val newPickup = pickupEditText.text.toString()
            val newDrop = dropEditText.text.toString()

            updateBusInfo(bus.busNumber ?: "", newStudentName, newPickup, newDrop)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun updateBusInfo(busNumber: String, newStudentName: String, newPickup: String, newDrop: String) {
        if (busNumber.isEmpty()) return

        val databaseRef = FirebaseDatabase.getInstance().getReference("Buses").child(busNumber)

        val updates = mapOf(
            "studentName" to newStudentName,
            "pickupLocation" to newPickup,
            "dropLocation" to newDrop
        )

        databaseRef.updateChildren(updates)
            .addOnSuccessListener {
                notifyDataSetChanged()
            }
    }
}
