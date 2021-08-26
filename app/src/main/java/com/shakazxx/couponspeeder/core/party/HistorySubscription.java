package com.shakazxx.couponspeeder.core.party;

import com.shakazxx.couponspeeder.core.util.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistorySubscription {

    private static final String FILE_PATH = FileUtil.getRootPath() + "/Download/partyStudySubHistory.txt";

    private static final String DELIMITER = "\n";


    public static void writeData(String data) {
        FileUtil.writeLine(FILE_PATH, data, true);
    }

    public static List<String> readData() {
        String data = FileUtil.readAll(FILE_PATH);
        if (data != null) {
            return Arrays.asList(data.split(DELIMITER));
        } else {
            return new ArrayList<>();
        }
    }

    public static void cleanup(int keepNum) {
        String data = FileUtil.readAll(FILE_PATH);
        if (data == null) {
            return;
        }

        List<String> records = Arrays.asList(data.split(DELIMITER));
        int size = records.size();

        if (keepNum >= size) {
            return;
        }

        List<String> newRecords = records.subList(size - keepNum - 1, size - 1);
        StringBuilder sb = new StringBuilder();
        for (String record : newRecords) {
            sb.append(record).append("\n");
        }
        FileUtil.writeLine(FILE_PATH, sb.toString(), false);
    }
}
