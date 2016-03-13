##概述
双圆圈菜单，内外环均可以转动，内圈小白点对准外圈选项时，内圈切换图片。    

![doublecircle](/Users/loommo/Desktop/android/CirclrLayout/CircleLayout3.gif)  

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