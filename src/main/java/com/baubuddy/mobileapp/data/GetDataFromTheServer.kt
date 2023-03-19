package com.baubuddy.mobileapp.data

import android.util.Log
import com.baubuddy.mobileapp.model.Task
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener


class GetDataFromTheServer {
    private var accessToken = ""
    private var refreshToken = ""

    private fun getLoginToken() : String{
        val client = OkHttpClient()
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
            Log.d("My fetch ", "myData: $response")
            //Log.d("My fetch ", "myData: ${response.body?.string()}")
            return response.body?.string() ?: ""
        }catch (e: IOException){
            e.printStackTrace()
        }
        return ""
    }
    private fun getData(){
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
            //Log.d("My fetch 2 ", "myData: " + response.body?.string())
            //Get the array object
            val jsonArray = JSONTokener(response.body?.string()).nextValue() as JSONArray
            for (i in 0 until jsonArray.length()){
                allData.add(
                    Task(
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
        }catch (e: IOException){
            e.printStackTrace()
        }
        //Log.d("LIST", allData.joinToString(" "))
    }
    fun run(){
        val response: String = getLoginToken()
        if(response != ""){
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
            }
        }
    }

}