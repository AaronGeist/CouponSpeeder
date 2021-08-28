package com.shakazxx.couponspeeder.core.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    public static String getRootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private static final String DELIMITER = "\n";

    public static void writeLine(String filePath, String data, boolean isAppend) {
        FileWriter writer = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(filePath, isAppend);
            writer.write(data + DELIMITER);
        } catch (IOException e) {
            Log.d(FileUtil.class.getSimpleName(), e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static String readAll(String filePath) {

        FileReader r = null;
        BufferedReader br = null;
        StringBuffer msg = new StringBuffer();
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }

            r = new FileReader(file);
            br = new BufferedReader(r);
            //由于每次只能读一行，就让其不断地读
            String s;
            while ((s = br.readLine()) != null) {
                msg = msg.append(s).append(DELIMITER); //必须要加\n 否则全部数据变成一行
            }
        } catch (Exception e) {
            Log.d(FileUtil.class.getSimpleName(), e.getMessage());
        } finally {
            try {
                if (r != null && br != null) {
                    r.close();
                    br.close();
                }
            } catch (IOException e) {
            }
        }

        return msg.toString();
    }

    public static void remove(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
