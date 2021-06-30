package cc.brainbook.viewpager.viewpagerbanner.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;

import cc.brainbook.viewpager.looppageradapter.LoopPagerAdapter;
import cc.brainbook.viewpager.looppageradapter.ViewHolderCreator;
import cc.brainbook.viewpager.transformer.CommonTransformer;
import cc.brainbook.viewpager.viewpagerbanner.ViewPagerBanner;

public class MainActivity extends AppCompatActivity {

    private ViewPagerBanner viewPagerBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ///test: local images and LocalImageHolderView
        ArrayList<Integer> localImages = new ArrayList<>();
        for (int position = 0; position < 5; position++) {  ///test: page numbers is 0, 1, 2,...5 or more
//            localImages.add(getResId("ic_test_" + position, R.drawable.class));
            localImages.add(getResources().getIdentifier("ic_test_" + position, "drawable", getPackageName()));
        }
        LoopPagerAdapter adapter = new LoopPagerAdapter(new ViewHolderCreator() {
            @Override
            public LocalImageViewHolder createViewHolder() {
                return new LocalImageViewHolder();
            }
        }, localImages);

        ///test: OnStartUpdateListener
        adapter.setOnStartUpdateListener(new LoopPagerAdapter.OnStartUpdateListener() {
            @Override
            public void onStartUpdate(@NonNull ViewGroup container, int updatePosition, int updateCount) {
                Log.d("TAG", "LoopPagerAdapter.OnStartUpdateListener#onStartUpdate()# updatePosition: " + updatePosition);
                Log.d("TAG", "LoopPagerAdapter.OnStartUpdateListener#onStartUpdate()# updateCount: " + updateCount);
            }
        });

        ///test: setCanLoop
//        adapter.setCanLoop(false);


        viewPagerBanner = findViewById(R.id.viewpagerbanner);
        ViewPager viewPager = viewPagerBanner.getViewPager();
        viewPager.setAdapter(adapter);


        ///test: setCurrentItem
//        viewPager.setCurrentItem(3);


        ///test: one view page may show multiple pages
        viewPager.setPadding(50, 150,100, 150);
        viewPager.setClipToPadding(false);

        ///--------- Comment out for RotateDownTransformer ---------
        final String[][] params = {
                {"PivotX", "-1", "1", "0.5", "0.5", "-1", "1"},
                {"PivotY", "-1", "1", "1", "1", "-1", "1"},

                {"Rotation", "-1", "0", "-0.05", "0", "-1", "0"},
                {"Rotation", "0", "0", "0", "0"},
                {"Rotation", "0", "1", "0", "0.05", "0", "1"},
        };

        final CommonTransformer transformer = new CommonTransformer(params);
        viewPager.setPageTransformer(false, transformer);

        viewPager.setBackgroundColor(getResources().getColor(R.color.colorAccent));


        ///test: setAutoPlayStep
//        viewPagerBanner.setAutoPlayStep(-1);  // forward: 1, backward: -1


        ///test: startAutoPlay
//        viewPagerBanner.startAutoPlay();


        ///test: OnAutoPlayTimeUpListener
        viewPagerBanner.setOnAutoPlayTimeUpListener(new ViewPagerBanner.OnAutoPlayTimeUpListener() {
            @Override
            public void onAutoPlayTimeUp(ViewPagerBanner container, int position) {
                Log.d("TAG", "ViewPagerBanner.OnAutoPlayTimeUpListener#onAutoPlayTimeUp()# position: " + position);
                ///注意：由于Loop时，position范围为[getRealCount(), getRealCount()+getRealCount())，所以真实的position要用getRealCount()取余！
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG", "onPause()#");
        viewPagerBanner.stopAutoPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG", "onResume#");
        viewPagerBanner.startAutoPlay();
    }

    public void onclick_button_start(View view){
        Log.d("TAG", "onclick_button_start: ");
        viewPagerBanner.startAutoPlay();
    }
    public void onclick_button_stop(View view){
        Log.d("TAG", "onclick_button_stop: ");
        viewPagerBanner.stopAutoPlay();
    }
    public void onclick_button_forward(View view){
        Log.d("TAG", "onclick_button_forward: ");
        viewPagerBanner.setAutoPlayStep(1);
    }
    public void onclick_button_backward(View view){
        Log.d("TAG", "onclick_button_backward: ");
        viewPagerBanner.setAutoPlayStep(-1);
    }

    /**
     * Get resource ID by file name.
     *
     * @param variableName The file name.
     * @param c The class.
     * @return The resource ID.
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
