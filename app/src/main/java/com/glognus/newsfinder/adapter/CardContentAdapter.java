package com.glognus.newsfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.glognus.newsapi.article.ArticleItem;
import com.glognus.newsfinder.DetailActivity;
import com.glognus.newsfinder.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
     * Adapter to display recycler view.
     */
    public class CardContentAdapter extends RecyclerView.Adapter<CardContentAdapter.ViewHolder> {
        // Set numbers of Card in RecyclerView.

        public static List<ArticleItem> list;

        public CardContentAdapter(Context context) {
            list = new ArrayList<>();
        }

        public void setList(List<ArticleItem> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.name.setText(list.get(position).getTitle());
            holder.description.setText(list.get(position).getDescription());
            Picasso.with(holder.picture.getContext())
                    .load(list.get(position).getUrlToImage())
                    .into(holder.picture);
        }

        @Override
        public int getItemCount() {
            if(list == null)
            {
                return 0;
            }
            return list.size();
        }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView picture;
        public TextView name;
        public TextView description;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_card, parent, false));
            picture = (ImageView) itemView.findViewById(R.id.card_image);
            name = (TextView) itemView.findViewById(R.id.card_title);
            description = (TextView) itemView.findViewById(R.id.card_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("article", list.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

            Button buttonReadme = (Button)itemView.findViewById(R.id.action_button);
            buttonReadme.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("article", list.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

            ImageButton shareImageButton = (ImageButton) itemView.findViewById(R.id.share_button);
            shareImageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey look at this ! \n"
                            + list.get(getAdapterPosition()).getTitle() + "\n"
                            + list.get(getAdapterPosition()).getUrl() + "\n\n");
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
                }
            });

            ImageButton externalUrlButton = (ImageButton) itemView.findViewById(R.id.externalurl_button);
            externalUrlButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    String url = list.get(getAdapterPosition()).getUrl();

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                }
            });
        }
    }
    }