package com.example.hpur.spragent.Logic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hpur.spragent.Logic.Queries.OnChatCardClickedCallback;

import java.util.List;

public class TeenagerAdapter extends RecyclerView.Adapter<TeenagerHolder> {

    private Context mContext;
    private List<TeenagerNameModel> mTeenagersNames;
    private int mItemResource;
    private OnChatCardClickedCallback mCallback;

    public TeenagerAdapter(Context context, int resource, List<TeenagerNameModel> teenagerNameModels, OnChatCardClickedCallback callback)
    {
        this.mContext = context;
        this.mItemResource = resource;
        this.mTeenagersNames = teenagerNameModels;
        this.mCallback = callback;
    }
    @NonNull
    @Override
    public TeenagerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(mItemResource, viewGroup, false);
        return new TeenagerHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return this.mTeenagersNames.size();
    }

    @Override
    public void onBindViewHolder(@NonNull TeenagerHolder teenagerHolder, int position) {
        TeenagerNameModel teenagerNameModel = this.mTeenagersNames.get(position);
        teenagerHolder.bindTeenagerNameModel(teenagerNameModel, mCallback);

    }
}
