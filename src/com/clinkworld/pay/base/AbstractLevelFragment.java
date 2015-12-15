package com.clinkworld.pay.base;

import android.os.Bundle;

/**
 * 增加控制fragment层级，配合BaseLevelFragmentActivity一起使用
 *
 * @author Swei.Jiang
 * @date 2013-8-23
 *
 */
abstract public class AbstractLevelFragment extends AbstractFragment {
	/** 上一级页面的fragment */
	private AbstractLevelFragment previousFragment;
	private int indexId;
	public static String BUNDLE_INDEXID = "INDEXID";

	public AbstractLevelFragment() {
		super();
	}

	public AbstractLevelFragment(int indexId) {
		super();
		Bundle bd = new Bundle();
		bd.putInt(BUNDLE_INDEXID, indexId);
		this.setArguments(bd);
	}

	public void setPreviousFragment(AbstractLevelFragment previousFragment) {
		this.previousFragment = previousFragment;
	}

	public AbstractLevelFragment getPrevious() {
		return previousFragment;
	}

	public AbstractLevelFragment(Bundle bd) {
		super(bd);
	}

	public int getRootId() {
		return indexId;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Bundle bd = getArguments();
			if (bd != null) {
				indexId = getArguments().getInt(BUNDLE_INDEXID);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void onRefresh() {
	}

	public void setRootId(int rootId) {
		Bundle bd = new Bundle();
		bd.putInt(BUNDLE_INDEXID, rootId);
		this.setArguments(bd);
		this.indexId = rootId;
	}
}
