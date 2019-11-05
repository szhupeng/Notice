package com.android.notice;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.lib.Notice;
import com.android.lib.NoticeBuilder;
import com.android.lib.NoticeManager;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NoticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notice);

        NoticeManager.getInstance().register(this);

        App.watch(this);

        final Random random = new Random();
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int order = random.nextInt(100) + 100;
                NoticeBuilder builder = new NoticeBuilder();
                builder.setContent("站内信第" + order + "条：请注意查收！！！")
                        .setTitle("站内信")
                        .setResidenceTime(2, TimeUnit.SECONDS)
                        .setViewBinder(new Notice.ViewBinder() {
                            @Override
                            public void bindView(View view, Notice notice) {

                            }
                        });
                NoticeManager.getInstance().send(builder.build());
                Log.d("TAG", "第:" + order);
            }
        });
    }
}
