package com.yioks.lzclib.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.yioks.lzclib.Data.BigImgShowData;
import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;
import com.yioks.lzclib.View.BigImgImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.facebook.imagepipeline.common.RotationOptions.NO_ROTATION;

/**
 * Created by ${User} on 2017/2/23 0023.
 */

public class ShowBigImgViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private Context context;
    private BigImgShowData bigImgShowData = new BigImgShowData();
    private List<View> viewList = new ArrayList<>();
    private HashMap<Uri, Bitmap> data = new HashMap<>();
    private static final int maxView = 5;
    private int position = 0;


    public ShowBigImgViewPagerAdapter(Context context, BigImgShowData bigImgShowData) {
        this.context = context;
        this.bigImgShowData = bigImgShowData;
        for (int i = 0; i < maxView; i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.big_img_view, null, false);
            viewList.add(view);
        }

    }

    @Override
    public int getCount() {
        return bigImgShowData.getCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = viewList.get(position % maxView);
        BigImgImageView imageView = (BigImgImageView) view.findViewById(R.id.img);
        imageView.setNeedToLoadRealBigImg(bigImgShowData.isNeedShowReal());
        Uri uri = bigImgShowData.getData(position);
        imageView.setMessageUri(bigImgShowData.getMessageUri(position));
        // imageView.setImageURI((Uri) bigImgShowData.getData(position));
        Bitmap bitmap = data.get(uri);
        if (bitmap == null || bitmap.isRecycled()) {
            imageView.setCanMove(false);
            imageView.setImageResource(R.drawable.holder);
            Integer rotate = bigImgShowData.getRotateHashMap().get(position);
            getData(uri, rotate == null ? 0 : rotate);
        } else {
            imageView.setCanMove(true);
            imageView.setImageBitmap(bitmap);
        }

        container.addView(view);
        return view;
    }


    //刷新当前正在显示的view
    private void refreshCurrentView(Uri uri) {

        Uri current = bigImgShowData.getData(position);
        if (current == uri) {
            BigImgImageView imageView = (BigImgImageView) viewList.get(position % maxView).findViewById(R.id.img);
            Bitmap bitmap = data.get(uri);
            if (bitmap != null) {
                imageView.setUri(uri);
                imageView.setCanMove(true);
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setUri(null);
                imageView.setCanMove(false);
                imageView.setImageResource(R.drawable.holder);
            }

        }
    }


    //获取图片的bitmap
    private void getData(final Uri uri, int rotate) {
        RotationOptions rotationOptions = RotationOptions.forceRotation(NO_ROTATION);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)

                .setResizeOptions(new ResizeOptions(ScreenData.widthPX, ScreenData.heightPX))
                .build();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(request, "");
        dataSource.subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() {
            @Override
            protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (!dataSource.isFinished()) {
                    return;
                }
                CloseableReference<CloseableImage> ref = dataSource.getResult();
                if (ref != null) {
                    try {
                        CloseableImage result = ref.get();
                        if (result instanceof CloseableBitmap) {
                            Bitmap bitmap = ((CloseableBitmap) result).getUnderlyingBitmap();
                            Log.i("lzc", "bigBitmap  " + bitmap.getWidth() + "----" + bitmap.getHeight());
                            data.put(uri, bitmap);
                            refreshCurrentView(uri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        CloseableReference.closeSafely(ref);
                    }

                }
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }
        }, UiThreadImmediateExecutorService.getInstance());
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position % maxView));
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public BigImgShowData getBigImgShowData() {
        return bigImgShowData;
    }

    public void setBigImgShowData(BigImgShowData bigImgShowData) {
        this.bigImgShowData = bigImgShowData;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        BigImgImageView imageView = (BigImgImageView) viewList.get(this.position % maxView).findViewById(R.id.img);
        Log.i("lzc", "initPosition" + this.position);
        imageView.initSet();
        this.position = position;
        refreshCurrentView(bigImgShowData.getData(position));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public boolean back() {
        View view = viewList.get(position % maxView);
        BigImgImageView imageView = (BigImgImageView) view.findViewById(R.id.img);
        return imageView.backImageAnim();
    }

    public void setIsanim(boolean isAnim) {
        BigImgImageView imageView = (BigImgImageView) viewList.get(position % maxView).findViewById(R.id.img);
        imageView.setAnim(isAnim);
    }
}
