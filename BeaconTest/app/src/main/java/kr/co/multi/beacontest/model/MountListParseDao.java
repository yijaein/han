package kr.co.multi.beacontest.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MountListParseDao {

    public String[] mPath;
    public String[] mName;

    private JSONArray mountain = null;

    private String json;

    public MountListParseDao(String json){
        this.json = json;
    }

    public void parseJSON(String _JSON_ARRAY, String _KEY_URL, String _KEY_NAME){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            mountain = jsonObject.getJSONArray(_JSON_ARRAY);


            mPath = new String[mountain.length()];
            mName = new String[mountain.length()];

            for(int i=0;i<mountain.length();i++){
                JSONObject jo = mountain.getJSONObject(i);
                mPath[i] = jo.getString(_KEY_URL);
                mName[i] = jo.getString(_KEY_NAME);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
