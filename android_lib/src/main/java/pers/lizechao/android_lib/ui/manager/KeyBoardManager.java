package pers.lizechao.android_lib.ui.manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * 监测键盘弹出
 * 控制键盘弹出
 */

public class KeyBoardManager {
    //xml activity root 布局
    private View mChildOfContent;
    private int usableHeightPrevious;
    private OnSoftChange onSoftChange;
    private boolean haveShow = false;


    public void init(Activity activity) {
        FrameLayout content = activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(
          this::possiblyResizeChildOfContent);
    }

    /**
     * 隐藏键盘
     */
    public static void hideSoftInput(EditText editText, Context context) {
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (editText.getWindowToken() != null && inputManager != null)
            inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    /**
     * 显示键盘
     */
    public static void showSoftInput(EditText editText, Context context) {
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null)
            inputManager.showSoftInput(editText, 0);
    }


    private void possiblyResizeChildOfContent() {
        //实际显示高度
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            //屏幕高度
            int usableHeightSansKeyboard = mChildOfContent.getRootView()
              .getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            //差值大于屏幕高度的1/4
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                haveShow = true;
                onSoftChange.show();
            } else {
                if (haveShow) {
                    haveShow = false;
                    onSoftChange.hide();
                }
            }
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    public interface OnSoftChange {
        void show();

        void hide();
    }

    public OnSoftChange getOnSoftChange() {
        return onSoftChange;
    }

    public void setOnSoftChange(OnSoftChange onSoftChange) {
        this.onSoftChange = onSoftChange;
    }
}