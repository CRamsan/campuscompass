package com.cesarandres.campuscompass;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cesarandres.campuscompass.dummy.ContentNDSU;

public class PlaceDetailFragment extends Fragment {

	public static final String ARG_ITEM_ID = "item_id";
	private int index = -1;

	public PlaceDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			index = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_place_detail,
				container, false);
		if (index != -1) {
			((TextView) rootView.findViewById(R.id.place_detail))
					.setText("Building name: "
							+ ContentNDSU.places_names[index]);
			((TextView) rootView.findViewById(R.id.place_extra))
					.setText("What can you find here: "
							+ ContentNDSU.places_descriptions[index]);
			((TextView) rootView.findViewById(R.id.place_address))
					.setText("Address: " + ContentNDSU.places_address[index]);

			((ImageView) rootView.findViewById(R.id.imageViewBackground))
					.setImageResource(ContentNDSU.getDrawableResourceHD(index));
			
			((ImageView) rootView.findViewById(R.id.imageViewForeground))
			.setImageResource(ContentNDSU.getDrawableResourceHD(index));
		}
		return rootView;
	}
}