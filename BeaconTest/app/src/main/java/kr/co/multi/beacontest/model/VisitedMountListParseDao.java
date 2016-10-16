package kr.co.multi.beacontest.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VisitedMountListParseDao {

    public String[] mPath;
    public String[] mName;
    public String[] startTime;
    public String[] endTime;
    public String[] record;

    public String JSON_ARRAY = "mountain";
    public String KEY_URL = "mPath";
    public String KEY_STARTTIEM = "startTime";
    public String KEY_ENDTIME = "endTime";
    public String KEY_NAME = "mName";
    public String KEY_RECORD = "record";

    private JSONArray mountain = null;

    private String json;

    public VisitedMountListParseDao(String json){
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            mountain = jsonObject.getJSONArray(JSON_ARRAY);


            mPath = new String[mountain.length()];
            mName = new String[mountain.length()];
            startTime = new String[mountain.length()];
            endTime = new String[mountain.length()];
            record = new String[mountain.length()];

            for(int i=0;i<mountain.length();i++){
                JSONObject jo = mountain.getJSONObject(i);
                mPath[i] = jo.getString(KEY_URL);
                mName[i] = jo.getString(KEY_NAME);
                startTime[i] = jo.getString(KEY_STARTTIEM);
                endTime[i] = jo.getString(KEY_ENDTIME);
                record[i] = jo.getString(KEY_RECORD);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
