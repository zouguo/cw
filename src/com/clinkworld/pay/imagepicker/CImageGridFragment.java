package com.clinkworld.pay.imagepicker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.clinkworld.pay.R;
import com.clinkworld.pay.activity.CImagePickActivity;
import com.clinkworld.pay.base.AbstractFragment;
import com.clinkworld.pay.loaders.HotfixCursorLoader;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;


public class CImageGridFragment extends AbstractFragment implements LoaderManager.LoaderCallbacks<Cursor>, CImageGridItem.OnGridClickListener {

    private static final int QUERY_IMAGE_LIST = -2;

    @ViewInject(R.id.gridview)
    private GridView imageGridView;
    private LoaderManager loaderManager;
    private String bucketId, bucketName;
    private CImageGridAdapter adapter;
    private ArrayList<Uri> selectUris = new ArrayList<Uri>();

    @Override
    protected View initView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_cimage_grid, null);
    }

    @Override
    protected void initView(View view) {
        getActivity().setTitle(bucketName);
        ((CImagePickActivity) getActivity()).showBackButton(true);
        setContentShown(false);
        setEmptyText(R.string.image_not_found);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void release() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            bucketId = getArguments().getString("bucketId");
            bucketName = getArguments().getString("bucketName");
        } else {
            bucketId = savedInstanceState.getString("bucketId");
            bucketName = savedInstanceState.getString("bucketName");
        }
        if (TextUtils.isEmpty(bucketId) || TextUtils.isEmpty(bucketName)) {
            ((CImagePickActivity) getActivity()).cancelPick();
        } else {
            loaderManager = getLoaderManager();
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loaderManager.initLoader(QUERY_IMAGE_LIST, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == QUERY_IMAGE_LIST) {
            String[] projection = new String[]{ImageColumns._ID, ImageColumns.DATA};
            return new HotfixCursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, ImageColumns.BUCKET_ID + "=? AND " + ImageColumns.DATA + ">'/0'", new String[]{bucketId}, ImageColumns._ID + " DESC");
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        if (loaderId == QUERY_IMAGE_LIST) {
            if (adapter == null) {
                adapter = new CImageGridAdapter(getActivity(), data);
                imageGridView.setAdapter(adapter);
            } else {
                adapter.swapCursor(data);
            }
        }
        setContentShown(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (adapter != null) {
            adapter.swapCursor(null);
        }
        setContentShown(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("bucketId", bucketId);
        outState.putString("bucketName", bucketName);
    }

    public void setEmptyText(int resId) {
        TextView emptyTextView = (TextView) imageGridView.getEmptyView();
        if (emptyTextView == null) {
            emptyTextView = new TextView(getActivity());
            emptyTextView.setTextAppearance(getActivity(), android.R.attr.textAppearanceSmall);
        }
        emptyTextView.setText(resId);
    }

    public void setContentShown(boolean shown) {
        View view = getView();
        if (view != null) {
            View mProgressContainer = view.findViewById(R.id.progressContainer);
            mProgressContainer.setVisibility(shown ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void onClick(View imageView, int position, Uri imageUri) {
        Intent data = new Intent();
        data.putExtra(Intent.EXTRA_STREAM, imageUri);
        ((CImagePickActivity) getActivity()).finishPick(data);
    }

    private class CImageGridAdapter extends CursorAdapter {

        public CImageGridAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            CImageGridItem view = new CImageGridItem(context);
            view.setListener(CImageGridFragment.this);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            long imageId = cursor.getLong(cursor.getColumnIndex(ImageColumns._ID));
            String imagePath = cursor.getString(cursor.getColumnIndex(ImageColumns.DATA));
            File file = new File(imagePath);
            if (file != null && file.exists()) {
                Uri imageUri = Uri.fromFile(file);
                ((CImageGridItem) view).setData(cursor.getPosition(), imageId, imageUri);
            }
        }
    }
}
