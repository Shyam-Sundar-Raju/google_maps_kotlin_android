package com.mad.maps

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.mad.maps.data.MarkerData

class MarkerDataCacheHandler(context: Context){
    private val sp: SharedPreferences = context.getSharedPreferences("MarkerDataCache", Context.MODE_PRIVATE)

    fun saveMarkerData(markerData: MarkerData) {
        val markerList = getMarkerDataList().toMutableList()
        markerList.add(markerData)
        val gson = Gson()
        sp.edit().putString("markerDataList", gson.toJson(markerList)).apply()
    }

    fun deleteMarkerData(markerData: MarkerData) {
        val markerList = getMarkerDataList().toMutableList()
        markerList.removeIf { it.title == markerData.title && it.latLng == markerData.latLng }
        val gson = Gson()
        sp.edit().putString("markerDataList", gson.toJson(markerList)).apply()
    }

    fun getMarkerDataList(): List<MarkerData> {
        val gson = Gson()
        val markerDataListJson = sp.getString("markerDataList", "[]")
        return gson.fromJson(markerDataListJson, Array<MarkerData>::class.java).toList()
    }
}