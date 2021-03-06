/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.glognus.newsfinder;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glognus.newsapi.article.Article;
import com.glognus.newsapi.article.ArticleItem;
import com.glognus.newsfinder.adapter.CardContentAdapter;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Provides UI for the view with Cards.
 */
public class CardContentFragment extends Fragment {

    CardContentAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adapter = new CardContentAdapter(getContext());
        View view = inflater.inflate(R.layout.card_content_recycler_view, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swifeRefresh);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.my_recycler_view);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetListArticleItem().execute();
            }
        });
        // Get articles list item
        new GetListArticleItem().execute();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        }
        else{
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        // gridlayout manager
        return view;
    }

    private class GetListArticleItem extends AsyncTask<Void, Void, List<ArticleItem>> {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        private OkHttpClient client;
        private final Gson gson = new Gson();

        @Override
        protected List<ArticleItem> doInBackground(Void... voids) {
            Article mySources = null;

            try {
                client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url("https://newsapi.org/v1/articles?source=the-next-web&sortBy=latest&apiKey=" + getResources().getString(R.string.api_key))
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                mySources = gson.fromJson(response.body().charStream(), Article.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mySources != null) {
                return mySources.getArticles();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ArticleItem> articleItems) {
            CardContentFragment.this.adapter.setList(articleItems);
            progressDialog.hide();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}


