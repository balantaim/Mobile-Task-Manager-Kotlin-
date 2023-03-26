package com.baubuddy.application.view

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.baubuddy.application.R
import com.baubuddy.application.data.Task

class MainAdapter(private val context: Activity, private val arrayList: ArrayList<Task>) : ArrayAdapter<Task>(
    context, R.layout.list_item, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.list_item, null)

        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        val txtTask: TextView = view.findViewById(R.id.txtTask)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
        val viewColor: View = view.findViewById(R.id.colorView)

        txtTitle.text = arrayList[position].title
        txtTask.text = arrayList[position].task
        txtDescription.text = arrayList[position].description
        var color: String? = arrayList[position].colorCode
        if (color == ""){
            color = "#FFFFFFFF"
        }
        viewColor.setBackgroundColor(Color.parseColor(color))

        return view
    }
}