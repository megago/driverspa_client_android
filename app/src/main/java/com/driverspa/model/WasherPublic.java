package com.driverspa.model;

import androidx.annotation.Nullable;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterItem;

public class WasherPublic extends WasherPublicBase implements ClusterItem {

	public WasherPublic() {
		super();
	}

	// Serialize the object using Gson
	public String serialize() {
		return new Gson().toJson(this);
	}

	// Deserialize the object using Gson
	public static WasherPublic deserialize(String serializedData) {
		return new Gson().fromJson(serializedData, WasherPublic.class);
	}

	@Override
	public LatLng getPosition() {
		// Assuming getLonLat() returns List<Object> like [lon, lat]
		try {
			double lat = Double.parseDouble(String.valueOf(getLonLat().get(1)));
			double lon = Double.parseDouble(String.valueOf(getLonLat().get(0)));
			return new LatLng(lat, lon);
		} catch (Exception e) {
			// Fallback to a default location or handle error gracefully
			return new LatLng(0.0, 0.0);
		}
	}

	@Nullable
	@Override
	public String getTitle() {
		// Provide actual data if needed later
		return null;
	}

	@Nullable
	@Override
	public String getSnippet() {
		// Provide actual data if needed later
		return null;
	}

	@Nullable
	@Override
	public Float getZIndex() {
		return 0f;
	}
}
