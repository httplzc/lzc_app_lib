package pers.lizechao.android_lib.support.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

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
public class PayStrategyWx implements IPayStrategy<WeiXinOrder> {
    private static final String PayWxCallBackData = "pay_wx_callback_data";
    private PayCallBack payCallBack;

    @Override
    public void pay(Activity activity, WeiXinOrder payData, PayCallBack payCallBack) {
        this.payCallBack = payCallBack;
        IWXAPI api = WXAPIFactory.createWXAPI(activity, PayExtraData.wxPayId);
        api.registerApp(PayExtraData.wxPayId);
        PayReq request = new PayReq();
        request.appId = payData.getAppId();
        request.partnerId = payData.getPartnerId();
        request.prepayId = payData.getPrepayId();
        request.packageValue = payData.getPackageValue();
        request.nonceStr = payData.getNonceStr();
        request.timeStamp = payData.getTimeStamp();
        request.sign = payData.getSign();
        api.sendReq(request);
    }


    public static void onWXPayCallbackDo(Context context, BaseResp baseResp) {
        if (baseResp == null)
            return;
        Bundle bundle = new Bundle();
        baseResp.toBundle(bundle);
        Intent intent = new Intent();
        intent.setClass(context, PayActivity.class);
        intent.putExtra(PayWxCallBackData, bundle);
        context.startActivity(intent);
    }


    @Override
    public void onNewIntent(Intent intent) {
        if (intent != null && payCallBack != null) {
            Bundle bundle = intent.getBundleExtra(PayWxCallBackData);
            if (bundle != null) {
                ShowMessageFromWX.Resp resp = new ShowMessageFromWX.Resp(bundle);
                switch (resp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        payCallBack.onSucceed();
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        payCallBack.onCancel();
                        break;
                    default:
                        payCallBack.onFail(resp.errStr);
                        break;
                }
            }

        }
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
