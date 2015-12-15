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
 * ��ͨfragment��һ�η�װ��
 * view ����Ҫ��γ�ʼ�����ؼ�ע�빦��
 * fragment����һ��ͨ��Bundle�����캯����
 * ��oncreateʱ��ʼ��,��������ʱ,ϵͳ�ᷴ�䲻���������캯������ʱ�����쳣
 */
abstract public class AbstractFragment extends Fragment  {
	protected View view;
	protected boolean inited;
	/** ע�⣬��fragment ���滻ʱcontext�������٣�getActivity()==null */
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
			ViewUtils.inject(this,view); // ע��view���¼�
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
