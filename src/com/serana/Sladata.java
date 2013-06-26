package com.serana;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView.BufferType;

@SuppressLint("ValidFragment")
public class Sladata extends Fragment {
	String description;

	public Sladata(String dis) {
		System.out.println("dis value is" + dis);
		description = dis;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("Test", "hello");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sla_descri, container, false);
		TextView textView = (TextView) view.findViewById(R.id.discrip);
		System.out.println("view of sladata");
		try {
			if (description.length() == 0) {
				System.out.println("description length is 0");
				textView.setGravity(Gravity.CENTER_HORIZONTAL
						| Gravity.CENTER_HORIZONTAL);

				textView.setText("No description");
			} else {
				System.out.println("description length is not 0"+ description.length());
			
				Spanned spannedContent = Html.fromHtml(description);
				textView.setText(spannedContent, BufferType.SPANNABLE);
			}
		} catch (Exception e) {
			textView.setGravity(Gravity.CENTER_HORIZONTAL
					| Gravity.CENTER_HORIZONTAL);
			textView.setText("No description");
		}
		return view;
	}
}