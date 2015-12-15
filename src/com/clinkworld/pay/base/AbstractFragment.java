package com.clinkworld.pay.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lidroid.xutils.ViewUtils;

/**
 * 普通fragment的一次封装，
 * view 不需要多次初始化，控件注入功能
 * fragment传参一般通过Bundle给构造函数，
 * 在oncreate时初始化,否则销毁时,系统会反射不带参数构造函数，这时出现异常
 */
abstract public class AbstractFragment extends Fragment  {
	protected View view;
	protected boolean inited;
	/** 注意，当fragment 被替换时context进行销毁，getActivity()==null */
	protected Context context;

	abstract protected View initView(LayoutInflater inflater);
	abstract protected void initView(View view);
	abstract protected void initData(Bundle savedInstanceState);
	abstract protected void release();

	public AbstractFragment() {
		super();
	}

	public AbstractFragment(Bundle bd){
		super();
		this.setArguments(bd);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}



	@SuppressWarnings("unchecked")
	protected <T> T findViewById(int id) {
		return (T) view.findViewById(id);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (!inited) {
			initData(savedInstanceState);
			inited = true;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view =initView(inflater);
			ViewUtils.inject(this,view); // 注入view和事件
			initView(view);
		} else {
			ViewGroup root = (ViewGroup) view.getParent();
			root.removeView(view);
		}
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		release();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}
