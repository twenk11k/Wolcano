package com.wolcano.musicplayer.music.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.utils.Utils.setLastSearch
import com.wolcano.musicplayer.music.utils.Utils.setSearchQuery
import org.apache.commons.io.IOUtils
import java.io.*

object FileUtils {

    fun writeToFile(context: Context, data: String?, fileName: String): Boolean {
        val directoryPath = (Environment.getExternalStorageDirectory()
            .toString() + File.separator
                + context.getString(R.string.folder_name) + "/" + context.getString(R.string.folder_search_history)
                + File.separator)
        val fileDirectory = File(directoryPath)
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdirs()) {
            } else {
                return false
            }
        }
        return try {
            val fileToWrite = File(directoryPath, "$fileName.txt")
            val outPutStream = FileOutputStream(fileToWrite)
            val outPutStreamWriter = OutputStreamWriter(outPutStream)
            outPutStreamWriter.append(data)
            outPutStreamWriter.close()
            outPutStream.flush()
            outPutStream.close()
            true
        } catch (e: IOException) {
            Log.e("Exception", "Error: File write failed: $e")
            e.fillInStackTrace()
            false
        }
    }

    @Throws(IOException::class)
    fun readFileData(context: Context, path1: String?, path2: String?) {
        var isSuccessful: Boolean
        val file1 = File(path1)
        val file2 = File(path2)
        isSuccessful = if (file1.exists()) {
            val fisTargetFile = FileInputStream(file1)
            val targetFileStr = IOUtils.toString(fisTargetFile)
            setSearchQuery(context, targetFileStr)
            true
        } else {
            false
        }
        isSuccessful = if (file2.exists()) {
            val fisTargetFile = FileInputStream(file2)
            val targetFileStr = IOUtils.toString(fisTargetFile)
            setLastSearch(context, targetFileStr)
            true
        } else {
            false
        }
        if (isSuccessful) {
            Toast.makeText(
                context.applicationContext,
                context.getString(R.string.search_history_import_success),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context.applicationContext,
                context.getString(R.string.cannot_found_file),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}