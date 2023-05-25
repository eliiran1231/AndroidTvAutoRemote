package com.example.androidtvremote.logic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidtvremote.R;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    private final App[] appList;
    private onAppClickedListener onAppClickedListener;

    public AppAdapter(App[] appList,onAppClickedListener onAppClickedListener){
        this.appList = appList;
        this.onAppClickedListener = onAppClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        App app = appList[position];
        holder.textViewName.setText(app.getName());
        holder.imageViewPicture.setImageResource(app.getImage());
        holder.itemView.setOnClickListener((view)->onAppClickedListener.onAppClicked(app));
    }

    @Override
    public int getItemCount() {
        return appList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textViewName;
        private final ImageView imageViewPicture;


        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            imageViewPicture = itemView.findViewById(R.id.imageViewPicture);
        }
    }
    interface onAppClickedListener{
        void onAppClicked(App app);
    }
}

