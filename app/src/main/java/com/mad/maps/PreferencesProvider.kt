package com.mad.maps

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class PreferencesProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val sp = context?.getSharedPreferences("MarkerDataCache", android.content.Context.MODE_PRIVATE)
        val cursor = android.database.MatrixCursor(arrayOf("markerDataList"))
        cursor.addRow(arrayOf(sp?.getString("markerDataList", "[]")))
        return cursor
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}