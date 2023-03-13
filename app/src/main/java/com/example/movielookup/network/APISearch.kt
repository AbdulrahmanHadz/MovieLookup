package com.example.movielookup.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class APISearch {
    val urlRoot = "https://www.omdbapi.com"
    val apiKey = "c7df471e"

    fun addParams(params: Map<String, String>): String {
        // Always add the apikey to the parameters
        var returnstring = "apikey=$apiKey&"

        if (params.isNotEmpty()) {
            // Join the parameters as a string separating the key and value with a =
            // and each entry with a &
            for (p in params.entries) {
                returnstring += "${p.key.trim()}=${URLEncoder.encode(p.value.trim(), "utf-8")}&"
            }
        }

        Log.d("DEV_DEBUG", "Params: $returnstring")
        return returnstring
    }

    // Initialise a HTTPUrlConnection using the url and parameters
    fun openConnection(url: String, params: Map<String, String>): HttpURLConnection {
        // Format the params map as a string
        var parsedParams = addParams(params)
        var urlToUse = "$url?$parsedParams"

        var urlObj = URL(urlToUse)
        return urlObj.openConnection() as HttpURLConnection
    }

    // Read the connection and return as string
    fun readConnection(connection: HttpURLConnection): String {
        return connection.inputStream.bufferedReader().use { it.readText() }
    }

    // Convert the string into a JSON Object
    fun convertJsonObj(data: String): JSONObject {
        return JSONObject(data)
    }

    // Convert the string into a JSON Array object
    fun convertJsonArray(data: String): JSONArray {
        return JSONArray(data)
    }

    fun search(params: Map<String, String>): JSONObject? {
        var rawData = ""
        var responseCode = 0
        var connection: HttpURLConnection? = null

        runBlocking { launch {
            withContext(Dispatchers.IO) {
                connection = openConnection(urlRoot, params)
                Log.d("DEV_DEBUG", "Connection URL: ${connection?.url}")
                rawData = readConnection(connection!!)
                Log.d("DEV_DEBUG", "rawData response: ${rawData.toString()}")
            }
        } }

        // Read the response code
        responseCode = connection?.responseCode ?: 500

        // Response codes 200-300 generally mean ok
        if (responseCode in 200 until 300) {
            var data = convertJsonObj(rawData)
            Log.d("DEV_DEBUG", "data response: ${data.toString()}")
            return data
        }
        return null
    }

    // Loop over the JSON Object and check if the "Error" key is available
    fun checkError(json: JSONObject): Boolean {
        var found = false
        for (key in json.keys()) {
            if (key == "Error") {
                found = true
            }
        }
        return found
    }
}