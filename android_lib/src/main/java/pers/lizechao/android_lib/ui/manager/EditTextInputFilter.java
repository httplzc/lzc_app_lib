package pers.lizechao.android_lib.ui.manager;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

/**
 * Created with
 * ********************************************************************************
 * #         ___                     ________                ________             *
 * #       |\  \                   |\_____  \              |\   ____\             *
 * #       \ \  \                   \|___/  /|             \ \  \___|             *
 * #        \ \  \                      /  / /              \ \  \                *
 * #         \ \  \____                /  /_/__              \ \  \____           *
 * #          \ \_______\             |\________\             \ \_______\         *
 * #           \|_______|              \|_______|              \|_______|         *
 * #                                                                              *
 * ********************************************************************************
 * Date: 2018-08-15
 * Time: 9:18
 * 用于控制EditText的输入长度
 */
public class EditTextInputFilter implements InputFilter {
    private int minCount = 0;
    private int maxCount = Integer.MAX_VALUE;
    private int maxLine = Integer.MAX_VALUE;
    private EditText editText;

    public EditTextInputFilter(int maxCount) {
        this.maxCount = maxCount;
    }

    public EditTextInputFilter(int minCount, int maxCount) {
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    public EditTextInputFilter(int minCount, int maxCount, int maxLine, EditText editText) {
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.maxLine = maxLine;
        this.editText = editText;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        //限制行数
        if (editText != null && editText.getLineCount() == maxLine && source.toString().contains("\n")) {
            return "";
        }
        CharSequence returnStr = null;
        //最终长度
        int endLen = (end - start) + dest.length() - (dend - dstart);
        if (endLen > maxCount) {
            //增加操作
            if (dstart == dend) {
                //裁剪长度
                int shouldEnd = end - (endLen - maxCount);
                returnStr = source.subSequence(start, shouldEnd);
            } else
                //粘贴操作
                return "";

        } else if (endLen < minCount) {
            return "";
        }
        return returnStr;
    }
}
