package com.obelieve.frame.utils.helper;

import android.graphics.Paint;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin
 * on 2020/5/22
 */
public class TextProcessorHelper {

    /**
     * 文本根据控件宽度分为几个部分
     *
     * @param text
     * @param viewWidth
     * @param paint
     * @return
     */
    public static List<String> splitText(String text, float viewWidth, Paint paint) {
        List<String> textList = new ArrayList<>();
        if (TextUtils.isEmpty(text) || paint == null || viewWidth == 0) {
            textList.add(text + "");
            return textList;
        }
        String[] rawTextLines = text.replaceAll("\r", "").split("\n");
        StringBuilder sb = new StringBuilder();
        for (String rawTextLine : rawTextLines) {
            if (paint.measureText(rawTextLine) <= viewWidth) {
                textList.add(rawTextLine);
            } else {
                float lineWidth = 0;
                for (int cnt = 0; cnt != rawTextLine.length(); ++cnt) {
                    char ch = rawTextLine.charAt(cnt);
                    lineWidth += paint.measureText(String.valueOf(ch));
                    if (lineWidth <= viewWidth) {
                        sb.append(ch);
                    } else {
                        textList.add(sb.toString());
                        sb = new StringBuilder();
                        lineWidth = 0;
                        --cnt;
                    }
                }
            }
            textList.add(sb.toString());
        }
        return textList;
    }
}
