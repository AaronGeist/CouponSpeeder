package com.shakazxx.couponspeeder.core.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TessUtil {

    public static TessBaseAPI lvBaseAPI = null;

    public static void init() {
        // 核心预设置代码
        lvBaseAPI = new TessBaseAPI();
        String path = Environment.getExternalStorageDirectory().getPath();
        // 可以优化，多次使用，一次释放
        lvBaseAPI.init(FileUtil.getRootPath() + "/Download/", "chi_sim");

        lvBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
    }

    /**
     * 识别按钮
     */
    public static Map<String, Rect> recognition(String filePath) {

        Map<String, Rect> result = new HashMap<>();
        try {
            Bitmap lvBitmap = BitmapFactory.decodeFile(filePath);

            // 裁剪
            int w = lvBitmap.getWidth(); // 得到图片的宽，高
            int h = lvBitmap.getHeight();
            lvBitmap = Bitmap.createBitmap(lvBitmap, 0, 500, w, h - 500, null, false);
            lvBaseAPI.setImage(lvBitmap);

            // 获取并显示识别结果
            String ocrText = lvBaseAPI.getUTF8Text();
            Log.d("TAG", "recognition: " + ocrText);

            String[] ocrTexts = ocrText.split("\n\n");
            List<Rect> rects = lvBaseAPI.getTextlines().getBoxRects();
            assert rects.size() != ocrText.length();
            int cnt = 0;
            for (String text : ocrTexts) {
                result.put(text, rects.get(cnt));
                cnt++;
            }
        } catch (Exception e) {
            Log.e("OCR", e.getMessage());
        }

        return result;
    }

    public static void close() {
        if (lvBaseAPI != null) {
            lvBaseAPI.clear();
            lvBaseAPI.end();
        }
    }
}
