package com.studyinghome.doublecircle;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DoubleCircle mCircleMenuLayout;

    private String[] mItemTexts = new String[]{"安全中心 ", "特色服务", "投资理财",
            "转账汇款", "我的账户", "信用卡", "特色服务", "投资理财", "特色服务", "投资理财"};
    private int[] mItemImgs = new int[]{R.drawable.home_mbank_1_normal,
            R.drawable.home_mbank_2_normal, R.drawable.home_mbank_3_normal,
            R.drawable.home_mbank_4_normal, R.drawable.home_mbank_5_normal,
            R.drawable.home_mbank_6_normal,
            R.drawable.home_mbank_2_normal, R.drawable.home_mbank_3_normal,
            R.drawable.home_mbank_2_normal, R.drawable.home_mbank_3_normal};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //自已切换布局文件看效果
        setContentView(R.layout.activity_double);

        mCircleMenuLayout = findViewById(R.id.id_menulayout);

        mCircleMenuLayout.setFirstContentListener(choose -> {

        });
        mCircleMenuLayout.setSecondContentListener(choose -> {

        });

        //设置文本和图片
//        mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);

        //监听click事件
//        mCircleMenuLayout.setOnMenuItemClickListener(new DoubleCircle.OnMenuItemClickListener() {
//
//            @Override
//            public void itemClick(View view, int pos) {
//                Toast.makeText(MainActivity.this, mItemTexts[pos],
//                        Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void itemCenterClick(View view) {
//                Toast.makeText(MainActivity.this,
//                        "you can do something just like ccb  ",
//                        Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(MainActivity.this, Main2Activity.class));
//
//            }
//        });
    }

}
