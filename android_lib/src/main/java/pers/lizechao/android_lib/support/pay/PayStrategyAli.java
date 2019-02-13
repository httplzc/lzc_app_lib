package pers.lizechao.android_lib.support.pay;

import android.app.Activity;
import android.content.Intent;

import com.alipay.sdk.app.PayTask;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
 * Time: 11:59
 */
public class PayStrategyAli implements IPayStrategy<AliOrder> {
    private static final HashMap<String, String> ErrorMap = new HashMap<>();

    static {
        ErrorMap.put("4000", "订单支付失败");
        ErrorMap.put("5000", "重复请求");
        ErrorMap.put("6001", "用户中途取消");
        ErrorMap.put("6002", "网络连接出错");
        ErrorMap.put("6004", "支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态");
        ErrorMap.put("其它", "其它支付错误");
    }

    @Override
    public void pay(Activity activity, AliOrder payData, PayCallBack payCallBack) {
        new Single<Map<String, String>>() {
            @Override
            protected void subscribeActual(SingleObserver<? super Map<String, String>> observer) {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(getAliSignData(payData), true);
                observer.onSuccess(result);
            }
        }.subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new SingleObserver<Map<String, String>>() {
              @Override
              public void onSubscribe(Disposable d) {

              }

              @Override
              public void onSuccess(Map<String, String> resultMap) {
                  if (payCallBack == null)
                      return;
                  String result = resultMap.get("resultStatus");
                  if (result == null) {
                      payCallBack.onFail("未知错误");
                      return;
                  }
                  switch (result) {
                      case "9000":
                      case "8000":
                          payCallBack.onSucceed();
                          break;
                      case "6001":
                          payCallBack.onCancel();
                          break;
                      default:
                          payCallBack.onFail(ErrorMap.get(result));
                          break;
                  }
              }

              @Override
              public void onError(Throwable e) {
                  if (payCallBack != null)
                      payCallBack.onFail("sdk错误！");
              }
          });
    }


    private String getAliSignData(AliOrder payData) {
        if (payData.getPayStr() != null)
            return payData.getPayStr();
        return "app_id=" +
          payData.getApp_id() +
          "&" +
          "biz_content=" +
          payData.getBiz_content() +
          "&" +
          "charset=" +
          payData.getCharset() +
          "&" +
          "format=" +
          payData.getFormat() +
          "&" +
          "method=" +
          payData.getMethod() +
          "&" +
          "notify_url=" +
          payData.getNotify_url() +
          "&" +
          "sign_type=" +
          payData.getSign_type() +
          "&" +
          "timestamp=" +
          payData.getTimestamp() +
          "&" +
          "version=" +
          payData.getVersion() +
          "&" +
          "sign=" +
          payData.getSign();
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onCreate(Activity activity) {

    }

    @Override
    public void onDestroy(Activity activity) {

    }
}
