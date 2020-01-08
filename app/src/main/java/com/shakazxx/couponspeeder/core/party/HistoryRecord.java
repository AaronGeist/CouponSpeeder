package com.shakazxx.couponspeeder.core.party;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class HistoryRecord {

    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/partyStudyRecords.txt";

    private static final String DELIMITER = "\n";

    public void writeData(String data) {
        FileWriter writer = null;
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }

            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(FILE_PATH, true);
            writer.write(data + DELIMITER);
        } catch (IOException e) {
            Log.d(this.getClass().getSimpleName(), e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                Log.d(this.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    public List<String> readData() {
        FileReader r = null;
        BufferedReader br = null;
        StringBuffer msg = new StringBuffer();
        try {
            File file = new File(FILE_PATH);
            r = new FileReader(file);
            br = new BufferedReader(r);
            //由于每次只能读一行，就让其不断地读

            String s;
            while ((s = br.readLine()) != null) {
                msg = msg.append(s).append(DELIMITER); //必须要加\n 否则全部数据变成一行
            }
        } catch (Exception e) {
            Log.d(this.getClass().getSimpleName(), e.getMessage());
        } finally {
            try {
                if (r != null) {
                    r.close();
                    br.close();
                }
            } catch (IOException e) {
            }
        }

        Log.d("History", msg.toString());
        return Arrays.asList(msg.toString().split(DELIMITER));
    }
}
