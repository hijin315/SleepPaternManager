package com.example.sleeppaternmanager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SolutionAdapter extends BaseAdapter {
    ArrayList<SolutionItem> items;
    Context mContext;
    LayoutInflater inflater;

    public SolutionAdapter(Context context, ArrayList<SolutionItem> items) {
        this.items = new ArrayList<>();
        this.items.addAll(items);
        this.mContext = context;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItem(ArrayList<SolutionItem> items) {
        if(this.items != null) {
            this.items.clear();
            this.items.addAll(items);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int i) {
        return this.items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class Holder {
        ImageView img;
        TextView tvSolution, solution_type;
        TextView tvNosolution;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Holder holder;

        if(items.get(i).getContents().equals("$NO_DATA$")) {
            view = inflater.inflate(R.layout.solution_noitem, viewGroup, false);
            holder = new Holder();
            holder.img = view.findViewById(R.id.solution_noitemimg);
            view.setTag(holder);

        } else {
            if (view == null) {
                view = inflater.inflate(R.layout.solution_item, viewGroup, false);
                holder = new Holder();
                holder.img = (ImageView) view.findViewById(R.id.solution_icon);
                holder.tvSolution = (TextView) view.findViewById(R.id.solution_contents);
                holder.solution_type = (TextView) view.findViewById(R.id.solution_type);
                holder.tvNosolution = view.findViewById(R.id.solution_noexist);
                holder.tvNosolution.setVisibility(View.GONE);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            SolutionItem item = this.items.get(i);

            if (item != null) {
                holder.tvNosolution.setVisibility(View.GONE);
                holder.img.setImageResource(item.getImgRes());
                holder.tvSolution.setText(item.getContents());
                holder.solution_type.setVisibility(item.getRequiredvalue()?View.VISIBLE:View.INVISIBLE);
            }
        }
        return view;
    }



}
