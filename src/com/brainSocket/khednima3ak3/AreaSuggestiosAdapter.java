package com.brainSocket.khednima3ak3;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

public class AreaSuggestiosAdapter extends ArrayAdapter<String> {
	ArrayList<String> originalSuggestions;
	List<String> displayedSuggestions;

	public AreaSuggestiosAdapter(Context context, int rowRes, List<String> data) {
		super(context, rowRes, data);
		displayedSuggestions = data;
		originalSuggestions = new ArrayList<String>(data);
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {

				if (results.values != null) {
					ArrayList<String> res = (ArrayList<String>) results.values;
					displayedSuggestions.clear();
					displayedSuggestions.addAll(res);
					// tracks = (ArrayList<TrackInfo>) results.values;
					notifyDataSetChanged();
				}
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				FilterResults results = new FilterResults();
				ArrayList<String> FilteredArrayNames = new ArrayList<String>();
				String constraint1 = constraint.toString().toLowerCase(Locale.ENGLISH);

				for (int i = 0; i < originalSuggestions.size(); i++) {
					String trackInf = originalSuggestions.get(i);
					if (trackInf.toLowerCase().contains(constraint1)) {
						FilteredArrayNames.add(trackInf);
					}
				}
				results.count = FilteredArrayNames.size();
				results.values = FilteredArrayNames;
				return results;
			}
		};
		return filter;
	}
}
