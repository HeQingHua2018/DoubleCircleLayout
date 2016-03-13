##概述
双圆圈菜单，内外环均可以转动，内圈小白点对准外圈选项时，内圈切换图片。    

![doublecircle](http://img.blog.csdn.net/20160313000216269)  

##使用方法

    DoubleCircleLayout fragmentCircle;

    //设置点击事件
    fragmentCircle.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void itemClick(View view, int pos) {
                showToast(String.valueOf(pos), Toast.LENGTH_SHORT);
            }

            @Override
            public void itemCenterClick(View view) {
                showToast("centre", Toast.LENGTH_SHORT);
            }
        });

    //载入数据
    List<Integer> mResIds = new ArrayList<>();
	mResIds.add(R.mipmap.test);
	mResIds.add(R.mipmap.test2);
	mResIds.add(R.mipmap.test3);
	mResIds.add(R.mipmap.test4);
	mResIds.add(R.mipmap.test5);
	mResIds.add(R.mipmap.test6);
    fragmentCircle.setMenuItemIconsAndTexts(mResIds);
   
 布局文件：
 	
 	<com.loommo.circlelayout.ui.widget.DoubleCircleLayout
        android:id="@+id/fragment_circle"
        android:layout_width="match_parent"
        android:layout_height="450dp"
        android:layout_gravity="center|left">

        <ImageView
            android:id="@id/id_circle_menu_item_focus"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@mipmap/inx_03" />

        <RelativeLayout
            android:id="@id/id_circle_menu_item_center"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content">

            <ImageView
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:alpha="0.12"
                android:src="@mipmap/inx_12" />

            <com.loommo.circlelayout.ui.widget.CircleImageView
                android:id="@id/id_circle_menu_center_image"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                app:isHasFrame="false"
                android:src="@mipmap/test"
                android:layout_margin="40dp"/>

        </RelativeLayout>

    </com.loommo.circlelayout.ui.widget.DoubleCircleLayout>
