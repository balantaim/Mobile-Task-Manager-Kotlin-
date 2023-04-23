package com.baubuddy.application.view.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baubuddy.application.R
import com.baubuddy.application.model.Task
import com.baubuddy.application.view.info.TaskInfoActivity
import java.util.Locale
import kotlin.CharSequence
import kotlin.Int


class TaskAdapter(activity: Activity, context: Context, userModel: ArrayList<Task>) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>(), Filterable {
    private val context: Context
    private var userModelList: List<Task>
    private val userModelListFiltered: List<Task>
    private val activity: Activity

    init {
        userModelList = userModel
        userModelListFiltered = userModel
        this.context = context
        this.activity = activity
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val userEvent: Task = userModelList[position]
        holder.txtTitle.text = userModelList[position].title
        holder.txtTask.text = userModelList[position].task
        holder.txtDescription.text = userModelList[position].description
        var color: kotlin.String? = userModelList[position].colorCode
        if (color == ""){
            color = "#FFFFFFFF"
        }
        holder.viewColor.setBackgroundColor(Color.parseColor(color))
        holder.mainLayout?.setOnClickListener {
            val intent = Intent(context, TaskInfoActivity::class.java)
            intent.putExtra("title", userModelList[position].title)
            intent.putExtra("task", userModelList[position].task)
            intent.putExtra("description", userModelList[position].description)
            intent.putExtra("color", color)

            activity.startActivityForResult(intent, 1)
        }
    }
    override fun getItemCount(): Int {
        return userModelList.size
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint == null || constraint.length == 0) {
                    filterResults.values = userModelListFiltered
                    filterResults.count = userModelListFiltered.size
                } else {
                    val search = constraint.toString().lowercase(Locale.getDefault())
                    val userFilteredList: MutableList<Task> = ArrayList<Task>()
                    for (Task in userModelListFiltered) {
                        if (Task.title.lowercase(Locale.getDefault()).contains(search) ||
                            Task.task.lowercase(Locale.getDefault()).contains(search) ||
                            Task.description!!.lowercase(Locale.getDefault())
                                .contains(search)
                        ) {
                            userFilteredList.add(Task)
                        }
                    }
                    filterResults.values = userFilteredList
                    filterResults.count = userFilteredList.size
                }
                return filterResults
            }
            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                userModelList = results.values as ArrayList<Task>
                notifyDataSetChanged()
            }
        }
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTitle: TextView
        var txtTask: TextView
        var txtDescription: TextView
        var viewColor: View
        var mainLayout: LinearLayout? = null

        init {
            txtTitle = itemView.findViewById<TextView>(R.id.txtTitle)
            txtTask = itemView.findViewById<TextView>(R.id.txtTask)
            txtDescription = itemView.findViewById<TextView>(R.id.txtDescription)
            viewColor = itemView.findViewById(R.id.colorView)
            mainLayout=itemView.findViewById(R.id.mainLayout)
        }
    }
}