package com.baubuddy.application.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.baubuddy.application.R

class TaskInfoActivity : AppCompatActivity() {

    private lateinit var txtTitle: TextView
    private lateinit var txtTask: TextView
    private lateinit var txtDescription: TextView
    private lateinit var viewColor: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_info)

        txtTitle = findViewById(R.id.titleInfo)
        txtTask = findViewById(R.id.taskInfo)
        txtDescription = findViewById(R.id.descriptionInfo)
        viewColor = findViewById(R.id.colorInfo)

        txtTitle.text = intent.getStringExtra("title")
        txtTask.text = intent.getStringExtra("task")
        txtDescription.text = intent.getStringExtra("description")
        var color: String? = intent.getStringExtra("color")
        if (color == "" || color == null){
            color = "#FFFFFFFF"
        }
        viewColor.setBackgroundColor(Color.parseColor(color))
    }

}