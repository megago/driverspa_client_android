package com.driverspa.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Converters {

	public static final String PHONE_PATTERN = "## ### ### ####";
	public static final String PIN_PATTERN = 	"##-##-##";

	private static final String[] DATE_FORMATS = new String[] {
		"yyyy-MM-dd'T'HH:mm:ss.SSSz",
		"yyyy-MM-dd'T'HH:mm:ss",
		"yyyy-MM-dd'T'HH:mm:ssZZZZZ"
	};
	
	public static Gson gsonWithDate() {
		final GsonBuilder builder = new GsonBuilder();		
//		builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
//		builder.excludeFieldsWithoutExposeAnnotation();
		builder.registerTypeAdapter(Date.class, new DateSerizlier());
		return builder.create();
	}
//	
	static class DateSerizlier implements JsonDeserializer<Date> {

		@Override
		public Date deserialize(JsonElement jsonElement, Type typeOF,
				JsonDeserializationContext context) throws JsonParseException {
			for (String format : DATE_FORMATS) {
				try {
					return new SimpleDateFormat(format, Locale.getDefault()).parse(jsonElement.getAsString());
				} catch (ParseException e) {
			  }
			}
			throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString()
					+ "\". Supported formats: " + Arrays.toString(DATE_FORMATS));
		}
	}
////	
////	public static Gson gsonAuthForget() {
////		final GsonBuilder builder = new GsonBuilder();
////		
////		builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
////		builder.registerTypeAdapter(Date.class, new DateSerizlier());
////		builder.registerTypeAdapter(AuthForgotResponseHolder.class, new AuthForgotResponseHolderSeriazlizer());
////		return builder.create();
////	}
//	
//	static class AuthForgotResponseHolderSerializer implements JsonDeserializer<AuthForgotResponseHolder> {
//
//		@Override
//		public AuthForgotResponseHolder deserialize(JsonElement jsonElement, Type typeOF,
//				JsonDeserializationContext context) throws JsonParseException {
//			AuthForgotResponseHolder result;
//			
//			Gson gson = new Gson();
//			
//			JsonObject jsonObject = jsonElement.getAsJsonObject();
//			int status = jsonObject.get("status").getAsInt();
//			
//			if( status != 0 ) {
//				result = gson.fromJson(jsonElement, AuthForgotResponseHolder.class);
//			} else {
//				String responseString = jsonObject.get("response").getAsString();
//				result = new AuthForgotResponseHolder();
//				result.setResponseString(responseString);
//				result.setResponse(null);
//			}
//			return result;
//		}
//	}
//	
//	static class StatusGetResponseHolderSerializer implements JsonDeserializer<StatusGetResponseHolder> {
//
//		@Override
//		public StatusGetResponseHolder deserialize(JsonElement jsonElement, Type typeOF,
//				JsonDeserializationContext context) throws JsonParseException {
//			
//			StatusGetResponseHolder result = new StatusGetResponseHolder();
//			
//			Gson gson = new Gson();
//			
//			JsonObject jsonObject = jsonElement.getAsJsonObject();
//			int status = jsonObject.get("status").getAsInt();
//			
//			if( status != 0 ) {
//				BaseErrorResponse errorResponse = gson.fromJson(jsonObject.get("response"), BaseErrorResponse.class);
//				result.setErrorResponse(errorResponse);
//			} else {
//				result = gson.fromJson(jsonElement, StatusGetResponseHolder.class);
//			}
//			
//			return result;
//		}
//		
//	}
//	
	/**
	 * This method converts dp unit to equivalent pixels, depending on device density.
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float convertDpToPixel(float dp, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}



}
