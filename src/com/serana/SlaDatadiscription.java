package com.serana;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class SlaDatadiscription extends Fragment {
	private String heading, desciption;

	public SlaDatadiscription(String head, String discrip) {
		this.heading = head;
		this.desciption = discrip;
		System.out.println("data is"+ head+ discrip);
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
		View view = inflater.inflate(R.layout.sla_data, container, false);
		TextView head = (TextView) view.findViewById(R.id.sla_data);
		TextView dis = (TextView) view.findViewById(R.id.sladescrip);
		try {
			if (heading.length() == 0 && desciption.length() == 0) {
				head.setText("");
				System.out.println("description length is 0");
				head.setGravity(Gravity.CENTER_HORIZONTAL
						| Gravity.CENTER_HORIZONTAL);
				dis.setText("No sla data");

			} else {
				System.out.println("description length is notmm 0");
				head.setText(heading);
				dis.setText(desciption);
			}
		} catch (Exception e) {
			head.setGravity(Gravity.CENTER_HORIZONTAL
					| Gravity.CENTER_HORIZONTAL);
			dis.setText("No sla data");

		}

		return view;
	}
}