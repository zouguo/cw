package com.clinkworld.pay.imagepicker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.clinkworld.pay.R;
import com.clinkworld.pay.activity.CImagePickActivity;
import com.clinkworld.pay.loaders.HotfixCursorLoader;
import com.clinkworld.pay.util.UiUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Created by ime on 14-1-14.
 */
public class CImageBucketFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int QUERY_IMAGE_BUCKET_LIST = -1;

    private LoaderManager loaderManager;
    private int bucketCoverSide;
    private CImageBucketAdapter adapter;
    private HashMap<Integer, Holder> loadingTask = new HashMap<Integer, Holder>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = getLoaderManager();
        bucketCoverSide = UiUtils.dp2px(getActivity(), 48);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("照片");
        ((CImagePickActivity) getActivity()).showBackButton(true);
        loadBucketList();
    }

    private void loadBucketList() {
        loaderManager.initLoader(QUERY_IMAGE_BUCKET_LIST, null, this);
    }

    private void loadBucketInfo(int id, Bundle args, Holder holder) {
        loadingTask.put(id, holder);
        loaderManager.initLoader(id, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == QUERY_IMAGE_BUCKET_LIST) {
            Uri bucketUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter("distinct", "true").build();
            String[] projection = new String[]{ImageColumns.BUCKET_ID + " AS " + ImageColumns._ID, ImageColumns.BUCKET_DISPLAY_NAME};
            return new HotfixCursorLoader(getActivity(), bucketUri, projection, null, null, null);
        } else if (args.containsKey("bucketId")) {
            String[] projection = new String[]{ImageColumns._ID, ImageColumns.DATA};
            return new HotfixCursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, ImageColumns.BUCKET_ID + "=? AND " + ImageColumns.DATA + ">'/0'", new String[]{args.getString("bucketId")}, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        if (loaderId == QUERY_IMAGE_BUCKET_LIST) {
            // 加载出图片集列表
            if (adapter == null) {
                adapter = new CImageBucketAdapter(getActivity(), data);
                setListAdapter(adapter);
            } else {
                adapter.swapCursor(data);
            }
        } else {
            Holder holder = loadingTask.remove(loaderId);
            // 加载出图片集的详细信息（图片列表）
            if (holder != null && data.moveToLast()) {
                // 设置Cover的缩略图和图片张数
                File imageFile = new File(data.getString(data.getColumnIndex(ImageColumns.DATA)));
                if (imageFile != null && imageFile.exists()) {
                    holder.bitmapBindTask = BitmapLoadManager.load(holder.cover, Uri.fromFile(imageFile), bucketCoverSide, bucketCoverSide);
                    holder.count.setText("(" + data.getCount() + ")");
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int loaderId = loader.getId();
        if (loaderId == QUERY_IMAGE_BUCKET_LIST) {
            if (getListAdapter() != null) {
                ((CursorAdapter) getListAdapter()).swapCursor(null);
            }
        } else {
            loadingTask.remove(loaderId);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Holder holder = (Holder) v.getTag();
        Bundle args = new Bundle();
        args.putString("bucketId", holder.id);
        args.putString("bucketName", holder.name.getText().toString());
        ((CImagePickActivity) getActivity()).openBucket(args);
    }

    private class CImageBucketAdapter extends CursorAdapter {

        public CImageBucketAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.imagebucket_listitem, null);
            Holder holder = new Holder();
            holder.cover = (ImageView) view.findViewById(R.id.bucket_cover);
            holder.name = (TextView) view.findViewById(R.id.bucket_name);
            holder.count = (TextView) view.findViewById(R.id.image_count);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Holder holder = (Holder) view.getTag();
            holder.id = cursor.getString(cursor.getColumnIndex(ImageColumns._ID));
            holder.cover.setImageResource(R.color.black);
            holder.name.setText(cursor.getString(cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME)));
            holder.count.setText(null);

            holder.cover.setImageResource(R.color.top_dialog_bg_color);
            if (holder.bitmapBindTask != null) {
                holder.bitmapBindTask.cancel(true);
            }

            Bundle bundle = new Bundle();
            bundle.putString("bucketId", holder.id);
            loadBucketInfo(cursor.getPosition(), bundle, holder);
        }
    }

    private static final class Holder {
        String id;
        ImageView cover;
        TextView name;
        TextView count;
        AsyncTask bitmapBindTask;
    }
}
