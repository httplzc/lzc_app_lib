package pers.lizechao.android_lib.support.img.upload;

import android.support.annotation.Nullable;

import pers.lizechao.android_lib.support.img.compression.comm.PressConfig;

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
 * Date: 2018-08-17
 * Time: 17:08
 */
public class UploadConfig {
    private final boolean needPress;
    private final boolean needCult;
    @Nullable
    private final PressConfig pressConfig;
    private final float cultRatio;
    private final boolean cultIsCircle;
    private int pickCount=1;

    private UploadConfig(Builder builder) {
        needPress = builder.needPress;
        needCult = builder.needCult;
        pressConfig = builder.pressConfig;
        cultRatio = builder.cultRatio;
        cultIsCircle = builder.cultIsCircle;
        pickCount = builder.pickCount;
    }


    public static final class Builder {
        private boolean needPress = true;
        private boolean needCult = true;
        private PressConfig pressConfig = null;
        private float cultRatio = 1f;
        private boolean cultIsCircle = false;
        private int pickCount = 1;

        public Builder() {
        }

        public Builder needPress(boolean val) {
            needPress = val;
            return this;
        }

        public Builder needCult(boolean val) {
            needCult = val;
            return this;
        }

        public Builder pressConfig(PressConfig val) {
            pressConfig = val;
            return this;
        }

        public Builder cultRatio(float val) {
            cultRatio = val;
            return this;
        }

        public Builder cultIsCircle(boolean val) {
            cultIsCircle = val;
            return this;
        }

        public Builder pickCount(int val) {
            pickCount = val;
            return this;
        }

        public UploadConfig build() {
            return new UploadConfig(this);
        }
    }

    public boolean isNeedPress() {
        return needPress;
    }

    public boolean isNeedCult() {
        return needCult;
    }

    @Nullable
    public PressConfig getPressConfig() {
        return pressConfig;
    }

    public float getCultRatio() {
        return cultRatio;
    }

    public boolean isCultIsCircle() {
        return cultIsCircle;
    }

    public int getPickCount() {
        return pickCount;
    }
}
