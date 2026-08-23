package com.driverspa;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Reference {
        public static final String RU = "ru";
        public static final String EN = "en";
        public static final String KZ = "kz";
	    
	    public Reference(){
	    	langs = new HashMap<String,String>();
	    	langs.put(RU,BA.str(R.string.russian_lang));
	    	langs.put(KZ,BA.str(R.string.lang_name_kk));
	    	langs.put(EN,"English");
	    }	   
	    
		private HashMap<String,String> langs;
		
	    private LinkedHashMap<Integer,String> services;

	    private ArrayList<City> cities;

	    @SerializedName("additional_info")
	    private LinkedHashMap<String,String> additionalInfo;

	    @SerializedName("contact_type")
	    private LinkedHashMap<String,String> contactType;

	    private LinkedHashMap<String,String> payoptions;

	    @SerializedName("car_type")
	    private LinkedHashMap<Integer,String> carType;

	    public LinkedHashMap<Integer,String> getServices ()
	    {
	        return services;
	    }

	    public void setServices (LinkedHashMap<Integer,String> services)
	    {
	        this.services = services;
	    }

	    public ArrayList<City> getCities ()
	    {
	        return cities;
	    }

	    public void setCities (ArrayList<City> cities)
	    {
	        this.cities = cities;
	    }

	    public HashMap<String,String> getAdditionalInfo ()
	    {
	        return additionalInfo;
	    }

	    public void setAdditionalInfo (LinkedHashMap<String,String> additionalInfo)
	    {
	        this.additionalInfo = additionalInfo;
	    }

	    public LinkedHashMap<String,String> getContactType ()
	    {
	        return contactType;
	    }

	    public void setContactType (LinkedHashMap<String,String> contactType)
	    {
	        this.contactType = contactType;
	    }

	    public HashMap<String,String> getPayoptions ()
	    {
	        return payoptions;
	    }

	    public void setPayoptions (LinkedHashMap<String,String> payoptions)
	    {
	        this.payoptions = payoptions;
	    }

	    public LinkedHashMap<Integer,String> getCarType ()
	    {
	        return carType;
	    }

	    public void setCarType (LinkedHashMap<Integer,String> carType)
	    {
	        this.carType = carType;
	    }
	    
	    public static class City {
	    	String code;
	    	String title;

	    	@SerializedName("lonlat")
	    	ArrayList<String> lonLat;
			public String getCode() {
				return code;
			}
			public void setCode(String code) {
				this.code = code;
			}
			public String getTitle() {
				return title;
			}
			public void setTitle(String title) {
				this.title = title;
			}
			public ArrayList<String> getLonLat() {
				return lonLat;
			}
			public void setLonLat(ArrayList<String> lonLat) {
				this.lonLat = lonLat;
			}

			public float getDistance(Location currentLoc) {
				Location cityLocation = new Location("A");
				cityLocation.setLatitude(Double.parseDouble(getLonLat().get(1).toString()));
				cityLocation.setLongitude(Double.parseDouble(getLonLat().get(0).toString()));
				float dist = currentLoc.distanceTo(cityLocation);
                return dist;
			}
		}
}
