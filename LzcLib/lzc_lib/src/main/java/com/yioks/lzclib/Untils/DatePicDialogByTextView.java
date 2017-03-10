package com.yioks.lzclib.Untils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 日期选择框
 * Created by ${User} on 2016/9/26 0026.
 */
public class DatePicDialogByTextView implements DatePickerDialog.OnDateSetListener {
    private Context context;
    private TextView textView;

    public DatePicDialogByTextView(Context context) {
        this.context = context;
    }

    public void ShowDataPickDialog(TextView textView) {
        this.textView = textView;
        Calendar calendar = getCalendarByInitData(textView.getText().toString());
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // 获得日历实例
        Calendar calendar = Calendar.getInstance();

        calendar.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        textView.setText(sdf.format(calendar.getTime()));
    }

    /**
     * 实现将初始日期时间2012-07-02 拆分成年 月 日,并赋值给calendar
     *
     * @param initDateTime
     * @return
     */
    private Calendar getCalendarByInitData(String initDateTime) {
        Calendar calendar = Calendar.getInstance();
        if (initDateTime.equals("") || initDateTime.equals("0000-00-00")) {
            return calendar;
        }
        //日期拆分
        try {
            String[] strDate = StringManagerUtil.stringSplit(initDateTime, "-");
            String yearStr = null;
            String monthStr = null;
            String dayStr = null;
            if (strDate != null) {
                yearStr = strDate[0];
                monthStr = strDate[1];
                dayStr = strDate[2];
            }

            int currentYear = Integer.valueOf(yearStr.trim()).intValue();
            int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
            int currentDay = Integer.valueOf(dayStr.trim()).intValue();

            calendar.set(currentYear, currentMonth, currentDay);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return calendar;
    }
}
