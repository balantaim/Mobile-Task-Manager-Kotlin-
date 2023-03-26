package com.baubuddy.application.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import androidx.work.multiprocess.RemoteWorkManager
import androidx.work.multiprocess.RemoteWorkManager.getInstance
import com.baubuddy.application.R
import com.baubuddy.application.network.GetDataFromTheServer
import com.baubuddy.application.data.Task
import com.baubuddy.application.data.TaskDatabase
import com.baubuddy.application.tools.ThreadUtil
import com.baubuddy.application.viewmodel.MainViewModel
import com.baubuddy.application.worker.NetworkWorker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.internal.notifyAll
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private lateinit var mySwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var search : FloatingActionButton
    private lateinit var codeQR : FloatingActionButton
    private lateinit var progressBar : ProgressBar
    private var loading : Boolean = true
    private var data = ArrayList<Task>()
    private lateinit var listView: ListView
    private lateinit var loadingInformation: TextView
    private lateinit var searchField: SearchView
    private lateinit var taskDB: TaskDatabase
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        taskDB = TaskDatabase.getDatabase(this)
        init()
        periodicTimeWorker()
        updateUI()
        search.setOnClickListener {
            if(View.GONE == searchField.visibility){
                codeQR.visibility = View.VISIBLE
                searchField.visibility = View.VISIBLE
            }else if(View.VISIBLE == searchField.visibility && searchField.query == ""){
                codeQR.visibility = View.GONE
                searchField.visibility = View.GONE
            }else{
                //searchField.query = ""
            }
        }
        mySwipeRefreshLayout.setOnRefreshListener {
            Log.d("SwipeRefresh", "onRefresh called from SwipeRefreshLayout")
            oneTimeWorker()
            listView.deferNotifyDataSetChanged()
            mySwipeRefreshLayout.isRefreshing = false
        }
        codeQR.setOnClickListener{
            val intent = Intent(this, TaskInfoActivity::class.java)
            startActivity(intent)
        }
        searchField.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchField.clearFocus()
                var newList = ArrayList<Task>()
                for (i in 0 until data.size){
                    if(query?.let { data[i].task.contains(it) } == true){
                        newList.add(data[i])
                    }
                    if(query?.let { data[i].title.contains(it) } == true){
                        newList.add(data[i])
                    }
                    if(query?.let { data[i].description!!.contains(it) } == true && (!data[i].description.isNullOrBlank())){
                        newList.add(data[i])
                    }
                    if(query?.let { data[i].colorCode.contains(it) } == true){
                        newList.add(data[i])
                    }
                    if(query?.let { data[i].parentTaskID.contains(it) } == true){
                        newList.add(data[i])
                    }
                    if(query?.let { data[i].wageType.contains(it) } == true){
                        newList.add(data[i])
                    }
                }
//                data = newList
//                listView.adapter.notifyAll()

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //filterData(newText)
                return false
            }

        })

    }
    private fun periodicTimeWorker(){
        Log.d("Worker-Periodic", " <<<Main: Worker Start>>>")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val uploadWorkRequest: WorkRequest =
            PeriodicWorkRequestBuilder<NetworkWorker>(60, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInitialDelay(60, TimeUnit.MINUTES)
                .build()
        WorkManager.getInstance(applicationContext).enqueue(uploadWorkRequest)
    }
    private fun oneTimeWorker() {
        Log.d("Worker", " <<<Main: Worker Start>>>")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<NetworkWorker>()
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(applicationContext).enqueue(uploadWorkRequest)

    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun updateUI(){
        ThreadUtil.startThread{

            data = GetDataFromTheServer().run()
            loading = false
            ThreadUtil.startUIThread(0){
                if(data.isEmpty()){
                    Log.d("MainAct", "TASK: There is no data!")
                    progressBar.visibility = View.GONE
                    loadingInformation.text = getString(R.string.no_data)
                } else {
                    if(!loading){
                        loadingInformation.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        search.visibility = View.VISIBLE

                        listView.isClickable = true
                        listView.adapter = MainAdapter(this, data)
                        listView.setOnItemClickListener { parent, view, position, id ->
                            navigateToTaskInfo(position)
                        }
                        GlobalScope.launch (Dispatchers.IO){
                            removeOldData()
                            writeData()
                        }
                    }
                }
            }
        }
    }
    private fun navigateToTaskInfo(position: Int){
        val intent = Intent(this, TaskInfoActivity::class.java)
        val title = data[position].title
        val task = data[position].task
        val description = data[position].description
        val color = data[position].colorCode

        intent.putExtra("title", title)
        intent.putExtra("task", task)
        intent.putExtra("description", description)
        intent.putExtra("color", color)
        startActivity(intent)
    }
    private suspend fun writeData(){
        for (i in data.indices){
            taskDB.taskDao().addTask(
                data[i]
            )
        }
        Log.d("Database", "New records to the database!")
    }
    private suspend fun removeOldData(){
        taskDB.taskDao().dropMyStoredData()
        Log.d("Database", "Old data is removed!")
    }
    private fun filterData(text: String){
        var filteredData = ArrayList<Task>()
    }
    private fun init(){
        mySwipeRefreshLayout = findViewById(R.id.swipeRefresh)
        search = findViewById(R.id.searchBtn)
        codeQR = findViewById(R.id.codeQR)
        searchField = findViewById(R.id.searchField)
        progressBar = findViewById(R.id.progressBar)
        listView = findViewById(R.id.listView)
        loadingInformation = findViewById(R.id.loadingInformation)
    }

//    override fun onBackPressed() {
//        finish()
//        exitProcess(0)
//    }

}