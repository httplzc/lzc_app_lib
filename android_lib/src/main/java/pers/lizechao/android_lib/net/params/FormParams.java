package pers.lizechao.android_lib.net.params;

import java.util.Map;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public class FormParams extends BaseFormParams<FormParams.Builder> {


    FormParams(Map<String, String> heads, Map<String, String> urlParams) {
        super(heads, urlParams);
    }

    @Override
    public Builder newBuilder() {
        return new Builder().putAll(getUrlParams());
    }


    public static class Builder extends BaseFormParams.BuilderAb<FormParams, Builder> {
        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public FormParams build() {
            return new FormParams(heads, urlParams);
        }
    }
}
