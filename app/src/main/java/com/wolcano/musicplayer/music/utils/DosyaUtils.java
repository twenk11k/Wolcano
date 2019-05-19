package com.wolcano.musicplayer.music.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.wolcano.musicplayer.music.R;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class DosyaUtils {

    public static boolean writeToFile(Context context, String data, String fileName) {

        String directoryPath =
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + context.getString(R.string.folder_name) + "/" + context.getString(R.string.folder_search_history)
                        + File.separator;

        File fileDirectory = new File(directoryPath);

        if (!fileDirectory.exists()) {

            if (fileDirectory.mkdirs()) {
            } else {
                return false;
            }
        }

        try {
            File fileToWrite = new File(directoryPath, fileName + ".txt");

            FileOutputStream outPutStream = new FileOutputStream(fileToWrite);
            OutputStreamWriter outPutStreamWriter = new OutputStreamWriter(outPutStream);
            outPutStreamWriter.append(data);
            outPutStreamWriter.close();
            outPutStream.flush();
            outPutStream.close();
            return true;

        } catch (IOException e) {
            Log.e("Exception", "Error: File write failed: " + e.toString());
            e.fillInStackTrace();
            return false;
        }
    }

    public static void readFileData(Context context, String path1, String path2) throws IOException {

        boolean isSuccessful = false;
        File file1 = new File(path1);
        File file2 = new File(path2);
        if (file1.exists()) {
            FileInputStream fisTargetFile = new FileInputStream(file1);

            String targetFileStr = IOUtils.toString(fisTargetFile);
            Utils.setSearchQuery(context, targetFileStr);
            isSuccessful = true;
        } else {
            isSuccessful = false;
        }
        if (file2.exists()) {
            FileInputStream fisTargetFile = new FileInputStream(file2);

            String targetFileStr = IOUtils.toString(fisTargetFile);
            Utils.setLastSearch(context, targetFileStr);
            isSuccessful = true;

        } else {
            isSuccessful = false;

        }
        if (isSuccessful) {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.search_history_import_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.cannot_found_file), Toast.LENGTH_SHORT).show();
        }
    }
}
