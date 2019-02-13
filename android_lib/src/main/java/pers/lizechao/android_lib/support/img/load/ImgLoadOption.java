package pers.lizechao.android_lib.support.img.load;

/**
 * Created by Lzc on 2018/6/20 0020.
 */
public class ImgLoadOption {
    private int width = -1;
    private int height = -1;
    private float aspectRatio = -1;
    private boolean noCache = false;
    private int rotate = -1;
    private int failResId = -1;
    private int placeHolderResId = -1;

    private ImgLoadOption(Builder builder) {
        width = builder.width;
        height = builder.height;
        aspectRatio = builder.aspectRatio;
        noCache = builder.noCache;
        rotate = builder.rotate;
        failResId = builder.failResId;
        placeHolderResId = builder.placeHolderResId;
    }

    public ImgLoadOption(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public boolean isNoCache() {
        return noCache;
    }

    public int getRotate() {
        return rotate;
    }

    public int getFailResId() {
        return failResId;
    }

    public int getPlaceHolderResId() {
        return placeHolderResId;
    }

    public static final class Builder {
        private int width = -1;
        private int height = -1;
        private float aspectRatio = -1;
        private boolean noCache = false;
        private int rotate = -1;
        private int failResId = -1;
        private int placeHolderResId = -1;

        public Builder() {
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }


        public Builder aspectRatio(float aspectRatio) {
            this.aspectRatio = aspectRatio;
            return this;
        }

        public Builder noCache(boolean noCache) {
            this.noCache = noCache;
            return this;
        }

        public Builder rotate(int rotate) {
            this.rotate = rotate;
            return this;
        }

        public Builder failResId(int failResId) {
            this.failResId = failResId;
            return this;
        }

        public Builder placeHolderResId(int placeHolderResId) {
            this.placeHolderResId = placeHolderResId;
            return this;
        }

        public ImgLoadOption build() {
            if (aspectRatio != -1 && width != -1) {
                height = (int) (width / aspectRatio);
            } else if (aspectRatio != -1 & height != -1) {
                width = (int) (height * aspectRatio);
            } else if (width != -1 & height != -1) {
                aspectRatio = width / height;
            }
            return new ImgLoadOption(this);
        }
    }
}
