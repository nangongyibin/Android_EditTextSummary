package com.ngyb.edittextsummary;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * 作者：南宫燚滨
 * 描述：
 * 邮箱：nangongyibin@gmail.com
 * 日期：2020/2/15 09:48
 */
public class DropEditTextAdapter extends BaseAdapter {
    private Context context;
    private List<String> data;

    public DropEditTextAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public String getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_drop_edittext, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvData.setText(getItem(i));
        //中间加横线
        viewHolder.tvData.setPaintFlags(viewHolder.tvData.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        return view;
    }

    class ViewHolder {
        private final TextView tvData;

        public ViewHolder(View view) {
            tvData = view.findViewById(R.id.tvData);
        }
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
