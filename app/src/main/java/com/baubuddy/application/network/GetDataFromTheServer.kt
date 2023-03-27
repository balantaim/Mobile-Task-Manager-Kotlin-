package com.baubuddy.application.network

import android.util.Log
import com.baubuddy.application.data.Task
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.util.UUID


class GetDataFromTheServer {
    private var accessToken = ""
    private var refreshToken = ""
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
        try {
            val response = client.newCall(request).execute()
            Log.d("MyFetch", "myData: $response")
            //Log.d("My fetch ", "myData: ${response.body?.string()}")
            if(!response.isSuccessful){
                return "NoConnection"
            }
            return response.body!!.string()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return "NoConnection"
    }
    private fun getData() : ArrayList<Task>{
        var allData = ArrayList<Task>()
        val request = Request.Builder()
            .url("https://api.baubuddy.de/dev/index.php/v1/tasks/select")
            .get()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()
        try {
            val response = client.newCall(request).execute()
            Log.d("MyFetch2", "myData 2: $response")
            //Get the array object
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val jsonArray = JSONTokener(response.body?.string()).nextValue() as JSONArray

            if (jsonArray.length() != 0){
                allData = convertJSONDataToTask(jsonArray)
            }
        }catch (e: IOException){
            e.printStackTrace()
        }
        return allData
    }
    private fun convertJSONDataToTask(jsonArray: JSONArray): ArrayList<Task> {
        var allData = ArrayList<Task>()
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
        return  allData
    }
    private fun convertTokenToString(jsonObject: JSONObject) {
        val name = jsonObject.getString("oauth")
        //Child Object
        val jsonObjectOauth = JSONTokener(name).nextValue() as JSONObject
        accessToken = jsonObjectOauth.getString("access_token")
        refreshToken = jsonObjectOauth.getString("refresh_token")
        //Log.d("My fetch ", "token: $accessToken \n refreshToken: $refreshToken" )
    }
    fun run() : ArrayList<Task>{
        val response: String = getLoginToken()
        var allData = ArrayList<Task>()
        if(response != "NoConnection"){
            //Convert Main Object
            val jsonObject = JSONTokener(response).nextValue() as JSONObject
            convertTokenToString(jsonObject)
            //Fetch the data
            if(accessToken != "" && refreshToken != ""){
                allData = getData()
            }else{
                Log.d("My fetch", "Error: No Token!" )
            }
        }
        return allData
    }

}