package pers.lizechao.android_lib.support.pay;

import android.content.Context;

import java.io.Serializable;

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
 * Time: 11:11
 */
public class PayManager {

    public PayCallBack payCallBack;
    private final Context context;

    public PayManager(Context context) {
        this.context = context;
    }

    //支付宝付款
    public void payAli(String payStr) {
        AliOrder aliOrder=new AliOrder();
        aliOrder.setPayStr(payStr);
        startPay(aliOrder,PayTarget.Ali);
    }
    //支付宝付款
    public void payAli(AliOrder aliOrder) {
        startPay(aliOrder,PayTarget.Ali);
    }

    //微信付款
    public void payWX(WeiXinOrder weiXinOrder) {
        startPay(weiXinOrder,PayTarget.WX);
    }

    private void startPay(Serializable data,PayTarget payTarget) {
        PayActivity.pay(context, payTarget, data, payCallBack);
    }

    public void setPayCallBack(PayCallBack payCallBack) {
        this.payCallBack = payCallBack;
    }

}
