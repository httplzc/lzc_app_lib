package pers.lizechao.android_lib.ui.widget;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.DatePicker;

import pers.lizechao.android_lib.utils.StrUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


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
 * Date: 2018-07-02
 * Time: 17:33
 * 用于选择时间的TextView
 */
public class DatePickTextView extends android.support.v7.widget.AppCompatTextView implements DatePickerDialog.OnDateSetListener {
    private DatePickerDialog dialog;
    private static final String format = "yyyy-MM-dd";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);

    public DatePickTextView(Context context) {
        super(context);
        init();
    }

    public DatePickTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DatePickTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        dialog = new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR),
          calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Calendar calendar = getCalendarByText(s.toString());
                dialog.getDatePicker().updateDate(calendar.get(Calendar.YEAR),
                  calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        this.setOnClickListener(v -> dialog.show());
    }


    private Calendar getCalendarByText(String initDateTime) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        if (StrUtils.CheckNull(initDateTime)) {
            return calendar;
        }
        Date date = null;
        try {

            date = simpleDateFormat.parse(initDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return calendar;
        }
        calendar.setTime(date);
        return calendar;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        this.setText(simpleDateFormat.format(calendar.getTime()));
    }

    public void setFormat(String format) {
        this.simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
    }
}
