package pers.lizechao.android_lib.support.webview;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

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
 * Date: 2018-09-10
 * Time: 14:24
 */
public class WebViewModel extends ViewModel {
    private final MutableLiveData<String> url = new MutableLiveData<>();
    private final MutableLiveData<LocalHtml> htmlLiveData = new MutableLiveData<>();



    public MutableLiveData<LocalHtml> getHtmlLiveData() {
        return htmlLiveData;
    }

    public MutableLiveData<String> getUrl() {
        return url;
    }

    protected static class LocalHtml {
        protected final String baseUrl;
        protected final String html;

        protected LocalHtml(String baseUrl, String html) {
            this.baseUrl = baseUrl;
            this.html = html;
        }
    }
}
