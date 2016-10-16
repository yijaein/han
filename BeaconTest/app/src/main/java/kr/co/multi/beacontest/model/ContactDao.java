package kr.co.multi.beacontest.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactDao {


	public String[] mPath;
	public String[] text;

	public String JSON_ARRAY = "photo";

	public String KEY_URL = "photoPath";

	public String KEY_NAME = "photoContent";

	private JSONArray photo = null;

	private String json;

	public ContactDao(String json){
		this.json = json;
	}

	public void parseJSON(){
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
			photo = jsonObject.getJSONArray(JSON_ARRAY);

			mPath = new String[photo.length()];
			text = new String[photo.length()];

			for(int i=0;i<photo.length();i++){
				JSONObject jo = photo.getJSONObject(i);
				mPath[i] = jo.getString(KEY_URL);
				text[i] = jo.getString(KEY_NAME);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
