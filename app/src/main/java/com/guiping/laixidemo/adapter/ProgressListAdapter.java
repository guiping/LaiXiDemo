package com.guiping.laixidemo.adapter;

import android.content.Context;
import android.icu.text.MessagePattern;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guiping.laixidemo.R;
import com.guiping.laixidemo.entity.ProgressEntity;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.LongUnaryOperator;

/**
 * Created by guiping on 2020/10/19
 * <p>
 * Describe:
 */
public class ProgressListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_HEAD_TYPE = 0;
    private final int ITEM_DETAILS_TYPE = 1;

    private LayoutInflater inflater;
    private List<ProgressEntity> mList;

    public ProgressListAdapter(Context context, List<ProgressEntity> list) {
        this.inflater = LayoutInflater.from(context);
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_HEAD_TYPE) {
            return new HeadViewHolder(inflater.inflate(R.layout.item_head_layout, parent, false));
        } else {
            return new DetailsViewHolder(inflater.inflate(R.layout.item_details_layout, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_HEAD_TYPE;
        } else {
            return ITEM_DETAILS_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeadViewHolder) {
            HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            headViewHolder.btn_addItem.setOnClickListener(v -> {  //点击添加
                if (null != itemClickListener) itemClickListener.onAddItem();
            });
        } else if (holder instanceof DetailsViewHolder) {
            DetailsViewHolder detailsViewHolder = (DetailsViewHolder) holder;
            UpdateProgressHaldler updateProgressHaldler = updateProgressBar(detailsViewHolder.pbar_details, mList.get(holder.getLayoutPosition() - 1));
            ((DetailsViewHolder) holder).tv_remove.setOnClickListener(view -> {
                if (holder.getLayoutPosition() - 1 > 0 && updateProgressHaldler != null)
                    updateProgressHaldler.removeMessages(mList.get(holder.getLayoutPosition() - 1).itemIndex);  //删除handler任务
                //删除item数据
                removeItem(holder.getLayoutPosition());
            });
            detailsViewHolder.tv_id.setText(mList.get(holder.getLayoutPosition() - 1).itemIndex + "");
            if (mList.get(holder.getLayoutPosition() - 1).curProgress == PROGRESS_MAX) {
                detailsViewHolder.pbar_details.setProgress(PROGRESS_MAX);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    private static class HeadViewHolder extends RecyclerView.ViewHolder {
        Button btn_addItem;

        public HeadViewHolder(@NonNull View itemView) {
            super(itemView);
            btn_addItem = itemView.findViewById(R.id.btn_addItem);
        }
    }

    public void addItem(ProgressEntity entity) {
        if (null == mList) return;
        mList.add(entity);
        notifyItemInserted(mList.size());
    }

    public void removeItem(int remPosition) {
        if (null == mList) return;
        mList.remove(remPosition - 1);
        notifyItemRemoved(remPosition);
    }

    private static class DetailsViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_id;
        private TextView tv_remove;
        private ProgressBar pbar_details;

        public DetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_remove = itemView.findViewById(R.id.tv_remove);
            pbar_details = itemView.findViewById(R.id.pbar_details);
        }
    }

    private UpdateProgressHaldler updateProgressBar(ProgressBar progressBar, ProgressEntity progressEntity) {
        if (progressEntity.curProgress == PROGRESS_MAX) return null;   //如果当前进度值 已经是最大， 就不用处理
        UpdateProgressHaldler updateProgressHaldler = new UpdateProgressHaldler(progressBar, progressEntity);
        Message message = Message.obtain();
        message.what = progressEntity.itemIndex;
        updateProgressHaldler.sendMessageDelayed(message, 1000);
        return updateProgressHaldler;
    }

    final int PROGRESS_MAX = 100;

    public class UpdateProgressHaldler extends Handler {
        int progressInterval;

        ProgressBar mProgressBar;
        ProgressEntity mProgressEntity;

        public UpdateProgressHaldler(ProgressBar progressBar, ProgressEntity progressEntity) {
            this.mProgressBar = progressBar;
            mProgressBar.setProgress(progressEntity.curProgress);
            this.mProgressEntity = progressEntity;
            progressInterval = PROGRESS_MAX / progressEntity.progressTime;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
//            ProgressBar progressBar =  progressBarWeakReference.get();
            if (msg.what == mProgressEntity.itemIndex) {
                if (mProgressBar.getProgress() < PROGRESS_MAX) {
                    ProgressBar progressBar = mProgressBar;
                    if (progressBar != null) {
                        progressBar.setProgress(progressBar.getProgress() + progressInterval);
                        mProgressEntity.curProgress = progressBar.getProgress();  //保存当前的进度
                        Message message = Message.obtain();
                        message.what = mProgressEntity.itemIndex;
                        sendMessageDelayed(message, 1000);
                    }
                } else {
                    removeMessages(msg.what);
                }
            }
        }
    }


    /***************************************封装Item点击相关的接口************************************************/

    public interface OnItemClickListener {
        void onAddItem();
    }

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }


}
