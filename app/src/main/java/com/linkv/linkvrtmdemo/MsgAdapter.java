package com.linkv.linkvrtmdemo;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.im.imlogic.IMMsg;
import com.im.imlogic.LVIMSDK;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaohong on 2020/6/1.
 * desc:
 */
public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ReceiveViewHolder> {
    private static final int VIEW_TYPE_SEND = 1;
    private static final int VIEW_TYPE_RECEIVE = 2;
    private Context context;
    private final LayoutInflater mInflater;

    public MsgAdapter(@NonNull Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);

    }

    private List<IMMsg> msgList = new ArrayList<>();

    /**
     * 添加消息并刷新界面
     *
     * @param msg
     */
    public void addMsg(IMMsg msg) {
        msgList.add(msg);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReceiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReceiveViewHolder holder;
        if (viewType == VIEW_TYPE_SEND) {
            holder = new ReceiveViewHolder(mInflater.inflate(R.layout.item_room_msg_send, parent, false));
        } else {
            holder = new ReceiveViewHolder(mInflater.inflate(R.layout.item_room_msg_receive, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiveViewHolder holder, int position) {
        IMMsg imMsg = msgList.get(position);
        String msgFromId = imMsg.fromID;
        String LoginUserID = LVIMSDK.sharedInstance().getUserID();
        if (TextUtils.isEmpty(msgFromId) || msgFromId.equals(LoginUserID)) {
            holder.tvUser.setVisibility(View.GONE);
        } else {
            holder.tvUser.setText(imMsg.fromID);
        }
        holder.tvMsg.setText(imMsg.getMsgContent());

    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String msgFromId = msgList.get(position).fromID;
        String LoginUserID = LVIMSDK.sharedInstance().getUserID();
        if (TextUtils.isEmpty(msgFromId) || msgFromId.equals(LoginUserID)) {
            return VIEW_TYPE_SEND;
        } else {
            return VIEW_TYPE_RECEIVE;
        }
    }


    static class ReceiveViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser;
        TextView tvMsg;

        public ReceiveViewHolder(View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tv_user);
            tvMsg = itemView.findViewById(R.id.tv_msg);
        }
    }


    //    @Override
//    public int getCount() {
//        return msgList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return msgList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        int viewType = VIEW_TYPE_RECEIVE;
//        String msgFromId = msgList.get(position).fromID;
//        String LoginUserID = LVIMSDK.sharedInstance().getUserID();
//        if (TextUtils.isEmpty(msgFromId) || msgFromId.equals(LoginUserID)) {
//            viewType = VIEW_TYPE_SEND;
//        }
//        return viewType;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View viewItem1 = null;
//        View viewItem2 = null;
//        IMMsg imMsg = msgList.get(position);
//        int itemViewType = getItemViewType(position);
//        System.out.println("--------viewtyoe = " + itemViewType);
//        if (itemViewType == VIEW_TYPE_SEND) {
//            SendViewHolder viewHolder;
//            if (convertView == null) {
//                viewHolder = new SendViewHolder();
//                viewItem1 = LayoutInflater.from(context).inflate(R.layout.item_room_msg_send, parent, false);
//                viewHolder.tvMsgSend = viewItem1.findViewById(R.id.tv_msg_send);
//                viewItem1.setTag(viewHolder);
//                convertView = viewItem1;
//            } else {
//                viewHolder = (SendViewHolder) convertView.getTag();
//            }
//            viewHolder.tvMsgSend.setText(imMsg.getMsgContent());
//
//        } else if (itemViewType == VIEW_TYPE_RECEIVE) {
//            ReceiveViewHolder receiveViewHolder;
//            if (convertView == null) {
//                receiveViewHolder = new ReceiveViewHolder();
//                viewItem2 = LayoutInflater.from(context).inflate(R.layout.item_room_msg_receive, parent, false);
//                receiveViewHolder.tvUser = viewItem2.findViewById(R.id.tv_user);
//                receiveViewHolder.tvMsg = viewItem2.findViewById(R.id.tv_msg);
//                viewItem2.setTag(receiveViewHolder);
//                convertView = viewItem2;
//            } else {
//                receiveViewHolder = (ReceiveViewHolder) convertView.getTag();
//            }
//            receiveViewHolder.tvUser.setText(imMsg.fromID);
//            receiveViewHolder.tvMsg.setText(imMsg.getMsgContent());
//        }
//
//
//        return convertView;
//    }
//
//    static class SendViewHolder {
//        TextView tvMsgSend;
//
//    }
//
//    static class ReceiveViewHolder {
//        TextView tvUser;
//        TextView tvMsg;
//
//    }

}
