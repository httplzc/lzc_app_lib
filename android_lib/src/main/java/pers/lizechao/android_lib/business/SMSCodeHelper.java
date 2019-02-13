package pers.lizechao.android_lib.business;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pers.lizechao.android_lib.utils.SharedPreferencesUtil;

/**
 * Created by Lzc on 2018/5/17 0017.
 */
public class SMSCodeHelper {
    //周期豪秒
    private final long intervalTime;
    //回调UI状态
    private CodeStatusCallBack codeStatusCallBack;
    //唯一标识
    private final String tag;

    private final static String SMS_CODE_SAVE = "SMS_CODE_SAVE";
    private final static String START_TIME = "START_TIME";

    private Disposable disposable;
    private boolean haveStart = false;
    private Context context;

    public SMSCodeHelper(String tag, int intervalTime, TimeUnit timeUnit, CodeStatusCallBack codeStatusCallBack) {
        this.tag = tag;
        this.intervalTime = timeUnit.toMillis(intervalTime);
        this.codeStatusCallBack = codeStatusCallBack;

    }

    public void init(Context context) {
        this.context = context;
        long lastTime = SharedPreferencesUtil.get(context, START_TIME, 0L, SMS_CODE_SAVE + tag);
        long spendTime = System.currentTimeMillis() - lastTime;
        if (spendTime > intervalTime) {
            if (codeStatusCallBack != null)
                codeStatusCallBack.enable();
            haveStart = false;
        } else {
            long leftTime = intervalTime - spendTime;
            codeStatusCallBack.disEnable((int) leftTime / 1000);
            startThread(leftTime);
        }
    }

    public void onDestroyDo() {
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
        context = null;

    }

    private void startThread(final long time) {
        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .take(time / 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                        if (codeStatusCallBack != null)
                            codeStatusCallBack.disEnable((int) (time / 1000 - value));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        haveStart = false;
                        if (codeStatusCallBack != null)
                            codeStatusCallBack.enable();
                    }
                });
    }


    public void start() {
        if (haveStart)
            return;
        haveStart = true;
        codeStatusCallBack.disEnable((int) (intervalTime / 1000));
        long startTime = System.currentTimeMillis();
        SharedPreferencesUtil.put(context, START_TIME, startTime, SMS_CODE_SAVE + tag);
        startThread(intervalTime);
    }

    public interface CodeStatusCallBack {
        void enable();

        void disEnable(int lastTime);
    }

    public void setCodeStatusCallBack(CodeStatusCallBack codeStatusCallBack) {
        this.codeStatusCallBack = codeStatusCallBack;
    }
}
