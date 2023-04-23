package com.baubuddy.application.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import com.baubuddy.application.R
import com.baubuddy.application.model.Task
import com.baubuddy.application.model.TaskDatabase
import com.baubuddy.application.network.GetDataFromTheServer
import com.baubuddy.application.tools.ThreadUtil
import com.baubuddy.application.view.scan_qr.ScanCodeActivity
import com.baubuddy.application.viewmodel.TaskViewModel
import com.baubuddy.application.worker.NetworkWorker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var mySwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var search : FloatingActionButton
    private lateinit var codeQR : FloatingActionButton
    private lateinit var progressBar : ProgressBar
    private var loading : Boolean = true
    private var data = ArrayList<Task>()
    private lateinit var loadingInformation: TextView
    private lateinit var searchField: SearchView
    private lateinit var taskDB: TaskDatabase
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    val viewModel: TaskViewModel by viewModels()

    companion object{
        const val RESULT = "RESULT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        taskDB = TaskDatabase.getDatabase(this)
        init()
        updateUI()
        periodicTimeWorker()
        onSearchBtnClicked()
        onSwipeAction()
        navigateToScanCodeActivity()
        sortFromListView()

        val stringQRCode: String? = intent.getStringExtra(RESULT)
        if (!stringQRCode.isNullOrEmpty()){
            if (View.GONE == searchField.visibility){
                codeQR.visibility = View.VISIBLE
                searchField.visibility = View.VISIBLE
            }
            searchField.isIconified = false
            searchField.setQuery(stringQRCode, false)
            searchField.clearFocus()
        }
//        viewModel.readAllData.observe(this, Observer {
//        })
    }
    private fun sortFromListView() {
        searchField.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null){
                    taskAdapter.filter.filter(newText);
                }
                return false
            }
        })
    }
    private fun navigateToScanCodeActivity() {
        codeQR.setOnClickListener{
            val intent = Intent(this, ScanCodeActivity::class.java)
            startActivity(intent)
        }
    }
    private fun onSwipeAction() {
        mySwipeRefreshLayout.setOnRefreshListener {
            Log.d("SwipeRefresh", "onRefresh called from SwipeRefreshLayout")
            oneTimeWorker()
            //listView.deferNotifyDataSetChanged()

            mySwipeRefreshLayout.isRefreshing = false
        }
    }
    private fun onSearchBtnClicked() {
        search.setOnClickListener {
            if(View.GONE == searchField.visibility){
                codeQR.visibility = View.VISIBLE
                searchField.visibility = View.VISIBLE
            }else if(View.VISIBLE == searchField.visibility && searchField.query.toString() == ""){
                codeQR.visibility = View.GONE
                searchField.visibility = View.GONE
            }
        }
    }
    private fun periodicTimeWorker(){
//        val instance = WorkManager.getInstance()
//        instance.enqueueUniquePeriodicWork("Periodic Work", ExistingPeriodicWorkPolicy.KEEP ,)

        Log.d("Worker-Periodic", " <<<MainPeriodic: Worker Start>>>")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val uploadWorkRequest: WorkRequest =
            PeriodicWorkRequestBuilder<NetworkWorker>(60, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag("Periodic Work")
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
                .addTag("OneTime Work")
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

                        setUpListAdapter()

                        GlobalScope.launch (Dispatchers.IO){
                            removeOldData()
                            writeData()
                        }
                    }
                }
            }
        }
    }

    private fun setUpListAdapter() {
        taskAdapter = TaskAdapter(this,this, data)
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this);
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
    private fun init(){
        mySwipeRefreshLayout = findViewById(R.id.swipeRefresh)
        search = findViewById(R.id.searchBtn)
        codeQR = findViewById(R.id.codeQR)
        searchField = findViewById(R.id.searchField)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.listView)
        loadingInformation = findViewById(R.id.loadingInformation)
    }

}