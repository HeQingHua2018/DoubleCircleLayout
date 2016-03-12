package com.loommo.circlelayout.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.loommo.circlelayout.R;
import com.loommo.circlelayout.listener.OnMenuItemClickListener;
import com.loommo.circlelayout.ui.widget.DoubleCircleLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CircleActivity extends AppCompatActivity {

    @Bind(R.id.fragment_circle)
    DoubleCircleLayout fragmentCircle;

    private List<Integer> mResIds;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);
        ButterKnife.bind(this);
        initEvent();
        initData();
    }

    private void initEvent() {
        fragmentCircle.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void itemClick(View view, int pos) {//点击事件
                showToast(String.valueOf(pos), Toast.LENGTH_SHORT);
            }

            @Override
            public void itemCenterClick(View view) {
                showToast("centre", Toast.LENGTH_SHORT);
            }
        });
    }

    private void initData() {
        //载入相册数据
        mResIds = new ArrayList<>();
        mResIds.add(R.mipmap.test);
        mResIds.add(R.mipmap.test2);
        mResIds.add(R.mipmap.test3);
        mResIds.add(R.mipmap.test4);
        mResIds.add(R.mipmap.test5);
        mResIds.add(R.mipmap.test6);
        fragmentCircle.setMenuItemIconsAndTexts(mResIds);
    }

    protected void showToast(CharSequence text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(this, text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }
}
