package com.guiping.laixidemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import android.util.Log;

import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.guiping.laixidemo.adapter.ProgressListAdapter;
import com.guiping.laixidemo.entity.ProgressEntity;
import com.guiping.laixidemo.utils.GsonUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by guiping on 2020/10/19
 * <p>
 * Describe:
 */
public class MainActivity extends AppCompatActivity {
    private RecyclerView rviewProgressList;
    private ProgressListAdapter mProgressListAdapter;
    private List<ProgressEntity> mList;
    private TextView tv_killprocess;
    private SharedPreferences mSharedPreferences;

    private final String SAVADATA_NAME = "progressData";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList = new ArrayList<>();
        mSharedPreferences = getSharedPreferences("progressData", Context.MODE_PRIVATE);
        initRview();
        List<ProgressEntity> tempDataList = getSaveDataList(SAVADATA_NAME);
        if (tempDataList != null && tempDataList.size() > 0) {
            mList.addAll(tempDataList);
            mProgressListAdapter.notifyDataSetChanged();
        }

    }

    private void initRview() {
        rviewProgressList = findViewById(R.id.rviewProgresslist);
        rviewProgressList.setLayoutManager(new LinearLayoutManager(this));
        rviewProgressList.setHasFixedSize(true);   //设定具有固定大小的
        mProgressListAdapter = new ProgressListAdapter(this, mList);
        mProgressListAdapter.setOnItemClickListener(new ProgressListAdapter.OnItemClickListener() {
            @Override
            public void onAddItem() {
                ProgressEntity progressEntity = new ProgressEntity();
                progressEntity.id = mList.size() + 1;
                if ((mList.size() + 1) % 2 == 0) {
                    progressEntity.progressTime = 20;
                } else {
                    progressEntity.progressTime = 10;
                }

                if (mProgressListAdapter != null) {
                    mProgressListAdapter.addItem(progressEntity);
                }
            }
        });
        rviewProgressList.setAdapter(mProgressListAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("ping", " ----- onPause() ");
        //失去焦点的时候就去做数据缓存， 避免缓存数据失败
        saveProgressDate(mList);
    }

    private void saveProgressDate(List<ProgressEntity> dataList) {
        if (dataList != null) {
            Editor editor = mSharedPreferences.edit();
            editor.putString(SAVADATA_NAME, GsonUtils.getGson().toJson(dataList));
            editor.commit();
        }
    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    private List<ProgressEntity> getSaveDataList(String tag) {
        List<ProgressEntity> datalist;
        String Json = mSharedPreferences.getString(tag, null);
        if (null == Json) {
            return null;
        }
        datalist = GsonUtils.getGson().fromJson(Json, new TypeToken<List<ProgressEntity>>() {
        }.getType());
        return datalist;
    }
}
