package com.kehui.t_h200.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.kehui.t_h200.R;
import com.kehui.t_h200.activity.AssistDetailsActivity;
import com.kehui.t_h200.base.BaseActivity;
import com.kehui.t_h200.bean.AssistListBean;

/**
 * Created by jwj on 2018/4/16.
 * 协助列表适配器
 */

public class AssistListAdapter extends RecyclerView.Adapter {

    private BaseActivity context;
    List<AssistListBean.DataBean> assistList;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE = 2;

    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;

    public AssistListAdapter(BaseActivity context, List<AssistListBean.DataBean> assistList) {
        this.context = context;
        this.assistList = assistList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_assist_list, parent, false);

            return new ViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.layout_load_more_footview, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            //未回复
            if (assistList.get(position).ReplyStatus.equals("0")) {
                viewHolder.ivReplyStatus.setImageResource(R.drawable.ic_no_reply);
                viewHolder.tvReplyStatus.setTextColor(context.getResources().getColor(R.color.red));
                viewHolder.tvReplyStatus.setText(context.getString(R.string.un_reply) + ">");
            } else if (assistList.get(position).ReplyStatus.equals("1")) {//已回复
                viewHolder.ivReplyStatus.setImageResource(R.drawable.ic_replied);
                viewHolder.tvReplyStatus.setTextColor(context.getResources().getColor(R.color.main_color));
                viewHolder.tvReplyStatus.setText(context.getString(R.string.replied) + ">");
            }
            viewHolder.tvTestName.setText(assistList.get(position).InfoUName.trim());
            viewHolder.tvTestTime.setText(assistList.get(position).InfoTime.trim());
            viewHolder.rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AssistDetailsActivity.class);
                    intent.putExtra("assistId", assistList.get(position).InfoID);
                    context.startActivityForResult(intent,0);
                }
            });


        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            switch (mLoadMoreStatus) {
                case LOADING_MORE:
                    footerViewHolder.mPbLoad.setVisibility(View.VISIBLE);
                    footerViewHolder.mTvLoadText.setText(context.getString(R.string.loading_more));
                    break;
                case NO_LOAD_MORE:
                    //隐藏加载更多
                    footerViewHolder.mPbLoad.setVisibility(View.GONE);
                    footerViewHolder.mTvLoadText.setText(context.getString(R.string.no_load_more));
                    break;

            }
        }
    }

    @Override
    public int getItemCount() {
        return assistList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (position + 1 == getItemCount()) {
            //最后一个item设置为footerView
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }


    /**
     * 更新加载更多状态
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        mLoadMoreStatus = status;
        notifyDataSetChanged();
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pbLoad)
        ProgressBar mPbLoad;
        @BindView(R.id.tvLoadText)
        TextView mTvLoadText;
        @BindView(R.id.loadLayout)
        LinearLayout mLoadLayout;


        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_reply_status)
        ImageView ivReplyStatus;
        @BindView(R.id.tv_test_name)
        TextView tvTestName;
        @BindView(R.id.tv_test_time)
        TextView tvTestTime;
        @BindView(R.id.tv_reply_status)
        TextView tvReplyStatus;
        @BindView(R.id.rl_item)
        RelativeLayout rlItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
