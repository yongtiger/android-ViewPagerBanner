package cc.brainbook.viewpager.viewpagerbanner.app;

import android.content.Context;
import androidx.appcompat.widget.AppCompatImageView;

import cc.brainbook.viewpager.looppageradapter.ViewHolder;

/**
 * The local {@link AppCompatImageView} view holder that implements {@link ViewHolder}.
 *
 * @author Robert Han
 * @email brainbook.cc@outlook.com
 * @website www.brainbook.cc
 * @time 2016/4/9 0:15
 */
public class LocalImageViewHolder implements ViewHolder<Integer> {
    private AppCompatImageView imageView;

    @Override
    public AppCompatImageView createView(Context context) {
        imageView = new AppCompatImageView(context);
        imageView.setScaleType(AppCompatImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void onBind(Context context, int position, Integer data) {
        imageView.setBackgroundResource(data);
    }
}
