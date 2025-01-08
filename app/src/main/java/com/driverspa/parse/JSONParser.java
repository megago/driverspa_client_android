/**
 * 
 */
package com.driverspa.parse;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * @author Yerzhan Tanatov
 * date: 09.10.2014
 * time: 13:30:11
 */
public class JSONParser {

	private static JSONParser instance;
	
	public static JSONParser getInstance(){
		if (instance == null)
			instance = new JSONParser();
		return instance;
	}
	
	public HashMap<String, String> getToken(JSONObject jsonObject){
		HashMap<String, String> map = new HashMap<String, String>();
		String status = jsonObject.optString("status", "");
		map.put("status", status);
		if(jsonObject.optString("error", "").equals(""))
		  map.put("token", jsonObject.optString("token", ""));
		else{
		  map.put("error", parseUnicod(jsonObject.optString("error", "")));
		  map.put("status", "1");
		}
		return map;
	}
	
//	public List<NewsModel> getNewsAndEvents(JSONObject jsonObject){
//		if (jsonObject == null)
//			return null;
//		List<NewsModel> listNews = new ArrayList<NewsModel>();
//		try {
//			JSONArray jsonArray = jsonObject.getJSONArray("news");
//			JSONObject news;
//			for (int i = 0; i < jsonArray.length(); i++){
//				news = jsonArray.getJSONObject(i);
//				NewsModel model = new NewsModel();
//				model.setId(news.optString("id"));
//				model.setDt(news.optString("dt"));
//				model.setTitle(news.optString("title"));
//				model.setTxt(news.optString("txt"));
//				model.setImg(news.optString("img"));
//				model.setSource_id(news.optString("source_id"));
//				model.setIsEvent(news.optString("isEvent"));				
//				model.setEvent_lat(news.optString("event_address","").equals("")||
//						news.optString("event_address","").equals("null")?news.optString("lat_new"):news.optString("event_lat"));				
//				model.setEvent_lng(news.optString("event_address","").equals("")||
//						news.optString("event_address","").equals("null")?news.optString("lng_new"):news.optString("event_lng"));				
//			    model.setEvent_address(news.optString("event_address","").equals("")||news.optString("event_address","").equals("null")?
//								      (news.optString("address_new","").equals("")||news.optString("address_new","").equals("null")?"Не указано":news.optString("address_new"))
//								      :news.optString("event_address","Не указано"));				
//				model.setEvent_dt(news.optString("event_dt"));
//				model.setViews(news.optString("views"));
//				model.setSource(news.optString("source"));
//				model.setTags_txt(news.optString("tags_txt"));
//				model.setTag0_Subscr(news.optString("tag0_subscr"));
//				model.setTag1_Subscr(news.optString("tag1_subscr"));
//				model.setSource_subscr(news.optString("source_subscr"));
//
//				if (model.getIsEvent().equals("0")){
//					List<String[]> tags = new ArrayList<String[]>();
//					JSONArray tagsAr = news.getJSONArray("tags");
//					for (int j = 0; j < tagsAr.length(); j++){
//						String idNameTag[] = new String[2];
//						idNameTag[0] = tagsAr.getJSONObject(j).optString("id");
//						idNameTag[1] = parseUnicod(tagsAr.getJSONObject(j).optString("txt"));
//						tags.add(idNameTag);
//					}
//					model.setTags(tags);
//				}
//
//				List<String> images = new ArrayList<String>();
//				JSONArray imagesAr = news.getJSONArray("images");
//				for (int j = 0; j < imagesAr.length(); j++)
//					images.add(parseUnicod(imagesAr.getJSONObject(j).optString("img")));
//				model.setImages(images);
//				
//				listNews.add(model);
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//			Log.d(Constants.LOG_TAG, "" + getClass().getName() + " " + e.getMessage()
//					+ " " + e.getLocalizedMessage());
//		}
//		return listNews;
//	}

	
	
	public HashMap<String, String> getTokenVKLogin(JSONObject jsonObject){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("status", jsonObject.optString("status",""));
		map.put("token", jsonObject.optString("token",""));
		map.put("title", parseUnicod(jsonObject.optString("title","")));
		map.put("message", parseUnicod(jsonObject.optString("message","")));
		return map;
	}
	
	private String parseUnicod(String unicodString){
		try {
		    byte[] converttoBytes = unicodString.getBytes("UTF-8");
		    unicodString = new String(converttoBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			
		}

		return unicodString;
	}

	
}
