package com.guiping.laixidemo.entity;

import java.io.Serializable;

/**
 * Created by guiping on 2020/10/19
 * <p>
 * Describe:
 */
public class ProgressEntity implements Serializable {
    public int itemIndex;   //添加编号
    public int progressTime;  //进度总时间
    public int curProgress;  //当前进度
}
