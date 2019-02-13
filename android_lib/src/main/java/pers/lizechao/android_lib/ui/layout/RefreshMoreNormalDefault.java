package pers.lizechao.android_lib.ui.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.ui.widget.refresh.RefreshMoreNormalView;

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
 * Date: 2018-08-01
 * Time: 15:17
 */
public class RefreshMoreNormalDefault extends RefreshMoreNormalView {
    public View loading;
    public View loading_failure;
    public View loading_finish;
    public View normal;

    public RefreshMoreNormalDefault(@NonNull Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.refresh_more_view, viewGroup, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        loading = view.findViewById(R.id.loading);
        loading_failure = view.findViewById(R.id.loading_failure);
        loading_finish = view.findViewById(R.id.loading_finish);
        normal = view.findViewById(R.id.normal);
        loading_failure.setOnClickListener(v -> onRefresh(true));

        normal.setOnClickListener(v -> onRefresh(true));
    }

    @Override
    protected void onStateChange(RefreshMoreNormalView.ViewStatus newStatus) {
        switch (newStatus) {
            case ON_FAIL:
                loading.setVisibility(View.GONE);
                loading_finish.setVisibility(View.GONE);
                loading_failure.setVisibility(View.VISIBLE);
                normal.setVisibility(GONE);
                break;
            case ON_REFRESH:
                loading.setVisibility(View.VISIBLE);
                loading_finish.setVisibility(View.GONE);
                loading_failure.setVisibility(View.GONE);
                normal.setVisibility(GONE);
                break;
            case NoMoreData:
                loading.setVisibility(View.GONE);
                loading_finish.setVisibility(View.VISIBLE);
                loading_failure.setVisibility(View.GONE);
                normal.setVisibility(GONE);
                break;
            case Normal:
                loading.setVisibility(View.GONE);
                loading_finish.setVisibility(View.GONE);
                loading_failure.setVisibility(View.GONE);
                normal.setVisibility(VISIBLE);
                break;
        }
    }
}
