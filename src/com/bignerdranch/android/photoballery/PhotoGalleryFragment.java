package com.bignerdranch.android.photoballery;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PhotoGalleryFragment extends Fragment {
	private static final String TAG = "PhotoGalleryFragment";
	
	GridView mGridView;
	ArrayList<GalleryItem> mItems;
	ThumbnailDownloader<ImageView> mThumbnailThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		new FetchItemsTask().execute();
		
		mThumbnailThread = new ThumbnailDownloader<ImageView>(new Handler());
		mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {

			@Override
			public void onThumbnailDownload(ImageView imageView, Bitmap thumbmail) {
				if (isVisible()) {
				    imageView.setImageBitmap(thumbmail);
				}
			}
			
		});
		mThumbnailThread.start();
		mThumbnailThread.getLooper();
		Log.i(TAG, "Background thread started");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
		mGridView = (GridView) v.findViewById(R.id.gridView);
		
		setupAdapter();
		
		return v;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mThumbnailThread.quit();
		Log.i(TAG, "Background thread destoryed");
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mThumbnailThread.clearQueue();
	}
	void setupAdapter() {
		if (getActivity() == null || mGridView == null) return;
		
		if (mItems != null) {
//			mGridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(), android.R.layout.simple_gallery_item, mItems));
			mGridView.setAdapter(new GalleryItemAdapter(mItems));
		} else {
			mGridView.setAdapter(null);
		}
			
	}

	private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {
		@Override
		protected ArrayList<GalleryItem> doInBackground(Void... params) {

//			try {
//				String result = new FlickrFetchr().getUrl("http://www.google.com");
//				Log.i(TAG, "Fetched contents of URL: " + result);
//			} catch (IOException e) {
//				Log.e(TAG, "Failed to fetch URL: ", e);
//			}
//			new FlickrFetchr().fetchItems();
//			return null;
			String query = "android"; //Just for testing
			
			if (query != null) {
				return new FlickrFetchr().search(query);
			} else {
				return new FlickrFetchr().fetchItems();
			}
		}
		
		@Override
		protected void onPostExecute(ArrayList<GalleryItem> result) {
			mItems = result;
			setupAdapter();
		}
	}
	
	private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {

		public GalleryItemAdapter(ArrayList<GalleryItem> items) {
			super(getActivity(), 0, items);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
			}
			ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
			imageView.setImageResource(R.drawable.brian_up_close);
			GalleryItem item = getItem(position);
			mThumbnailThread.queueThumbnail(imageView, item.getUrl());
			
			return convertView;
		}
		
	}
}
