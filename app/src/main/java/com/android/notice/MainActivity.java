package com.android.notice;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.lib.Notice;
import com.android.lib.NoticeBuilder;
import com.android.lib.NoticeManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("TAG", "Activity:" + this);
        NoticeManager.getInstance().register(this);

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoticeBuilder builder = new NoticeBuilder();
                builder.setContent("别点我了，再点我我就弹一个站内信给你看")
                        .setTitle("微信")
                        .setResidenceTime(2, TimeUnit.SECONDS)
                        .setNoticeViewListener(new Notice.NoticeViewListener() {
                            @Override
                            public void onViewCreated(View view, Notice notice) {

                            }
                        });
                NoticeManager.getInstance().send(builder.build());

            }
        });
    }

    @Override
    protected void onDestroy() {
        NoticeManager.getInstance().unregister(this);
        super.onDestroy();
    }
}
