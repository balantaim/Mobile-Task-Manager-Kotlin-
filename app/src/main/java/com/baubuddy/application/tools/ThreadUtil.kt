package com.baubuddy.application.tools

import android.os.Looper
import java.util.concurrent.Executors
import android.os.Handler

class ThreadUtil {

    companion object{
        private var executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        private var handler = Handler(Looper.getMainLooper())

        //Start task with Thread
        fun startThread(runnable: Runnable){
            executorService.submit(runnable)
        }
        //Back to main thread for update
        fun startUIThread(delayMillis: Int, runnable: Runnable){
            handler.postDelayed(runnable, delayMillis.toLong())
        }
    }
}