package com.baubuddy.application.network

import android.util.Log
import com.baubuddy.application.model.Task
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.util.*
import kotlin.collections.ArrayList


class NetworkRequest {
    private var accessToken = ""
    private var refreshToken = ""
    private var allData = ArrayList<Task>()
    private val client = OkHttpClient()

    private fun getLoginToken() : String{
        val mediaType = "application/json".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, "{\n        \"username\":\"365\",\n        \"password\":\"1\"\n}")
        val request = Request.Builder()
            .url("https://api.baubuddy.de/index.php/login")
            .post(body)
            .addHeader("Authorization", "Basic QVBJX0V4cGxvcmVyOjEyMzQ1NmlzQUxhbWVQYXNz")
            .addHeader("Content-Type", "application/json")
            .build()

        var res: String? = null
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.d("MyFetch", "myData: FAILED")
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    res = response.body!!.string()
                    Log.d("MyFetch", "myData: $response")
                    //Log.d("My fetch ", "myData: ${response.body?.string()}")
                }
            }
        })
        return res ?:"NoConnection"
    }
    private fun getData() : ArrayList<Task>{
        val client = OkHttpClient()

        val mediaType = "application/json".toMediaTypeOrNull()
        //val body = RequestBody.create(mediaType, "{\r\n        \"username\":\"365\",\r\n        \"password\":\"1\"\r\n}")
        val request = Request.Builder()
            .url("https://api.baubuddy.de/dev/index.php/v1/tasks/select")
            .get()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()

        var allData = ArrayList<Task>()

        try {
            val response = client.newCall(request).execute()
            Log.d("MyFetch2", "myData 2: $response")
            //Get the array object
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val jsonArray = JSONTokener(response.body?.string()).nextValue() as JSONArray
            if (jsonArray.length() != 0){
                for (i in 0 until jsonArray.length()){
                    allData.add(
                        Task(
                            UUID.randomUUID().toString(),
                            jsonArray.getJSONObject(i).getString("task"),
                            jsonArray.getJSONObject(i).getString("title"),
                            jsonArray.getJSONObject(i).getString("description"),
                            jsonArray.getJSONObject(i).getInt("sort"),
                            jsonArray.getJSONObject(i).getString("wageType"),
                            jsonArray.getJSONObject(i).getString("BusinessUnitKey"),
                            jsonArray.getJSONObject(i).getString("businessUnit"),
                            jsonArray.getJSONObject(i).getString("parentTaskID"),
                            jsonArray.getJSONObject(i).getString("preplanningBoardQuickSelect"),
                            jsonArray.getJSONObject(i).getString("colorCode"),
                            jsonArray.getJSONObject(i).getString("workingTime"),
                            jsonArray.getJSONObject(i).getBoolean("isAvailableInTimeTrackingKioskMode")
                        )
                    )
                }
            }
        }catch (e: IOException){
            e.printStackTrace()
        }
        //Log.d("LIST", allData.joinToString(" "))
        return allData
    }
    private fun parseJSON(jsonArray: JSONArray) : ArrayList<Task>{
        for (i in 0 until jsonArray.length()){
            allData.add(
                Task(
                    UUID.randomUUID().toString(),
                    jsonArray.getJSONObject(i).getString("task"),
                    jsonArray.getJSONObject(i).getString("title"),
                    jsonArray.getJSONObject(i).getString("description"),
                    jsonArray.getJSONObject(i).getInt("sort"),
                    jsonArray.getJSONObject(i).getString("wageType"),
                    jsonArray.getJSONObject(i).getString("BusinessUnitKey"),
                    jsonArray.getJSONObject(i).getString("businessUnit"),
                    jsonArray.getJSONObject(i).getString("parentTaskID"),
                    jsonArray.getJSONObject(i).getString("preplanningBoardQuickSelect"),
                    jsonArray.getJSONObject(i).getString("colorCode"),
                    jsonArray.getJSONObject(i).getString("workingTime"),
                    jsonArray.getJSONObject(i).getBoolean("isAvailableInTimeTrackingKioskMode")
                )
            )
        }
        return allData
    }
    fun run() : ArrayList<Task>{
        val response: String = getLoginToken()
        //var allData = ArrayList<Task>()
        if(response != "NoConnection"){
            //Main Object
            val jsonObject = JSONTokener(response).nextValue() as JSONObject
            val name = jsonObject.getString("oauth")
            //Child Object
            val jsonObjectOauth = JSONTokener(name).nextValue() as JSONObject
            accessToken = jsonObjectOauth.getString("access_token")
            refreshToken = jsonObjectOauth.getString("refresh_token")
            //Log.d("My fetch ", "myData: $accessToken \n $refreshToken" )
            //Fetch the data
            if(accessToken != "" && refreshToken != ""){
                getData()
                Log.d("MyFetchEnd", "JSON Parsed")
            }else{
                Log.d("MyFetchEnd", "No connection")
                return allData
            }
        }
        return allData
    }
}