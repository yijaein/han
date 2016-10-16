package kr.co.multi.beacontest.util;

import android.content.Context;

public class Util {

    private Context mContext;
    public final static String hostURL = "http://map.naver.com";

    public Util(){

    }

    public Util(Context con){
        mContext = con;
    }

    public static String getHostURL() {
        return hostURL;
    }

    public void clearApplicationCache(java.io.File dir){
        if(dir==null)
            dir = mContext.getCacheDir();
        else;
        if(dir==null)
            return;
        else;
        java.io.File[] children = dir.listFiles();
        try{
            for(int i=0;i<children.length;i++)
                if(children[i].isDirectory())
                    clearApplicationCache(children[i]);
                else children[i].delete();
        }
        catch(Exception e){}
    }

    public String ReplaceString(String Expression, String Pattern, String Rep)
    {
        if (Expression==null || Expression.equals("")) return "";

        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();

        while ((e = Expression.indexOf(Pattern, s)) >= 0) {
            result.append(Expression.substring(s, e));
            result.append(Rep);
            s = e + Pattern.length();
        }
        result.append(Expression.substring(s));
        return result.toString();
    }


    public String ReplaceTag(String Expression, String type){
        String result = "";
        if (Expression==null || Expression.equals("")) return "";

        if (type == "encode") {
            result = ReplaceString(Expression, "&", "&amp;");
            result = ReplaceString(result, "\"", "&quot;");

            result = ReplaceString(result, "'", "&apos;");
            result = ReplaceString(result, "<", "&lt;");
            result = ReplaceString(result, ">", "&gt;");
            result = ReplaceString(result, "\r", "<br>");
            result = ReplaceString(result, "\n", "<p>");
        }
        else if (type == "decode") {
            result = ReplaceString(Expression, "&amp;", "&");
            result = ReplaceString(result, "&quot;", "\"");

            result = ReplaceString(result, "&apos;", "'");
            result = ReplaceString(result, "&lt;", "<");
            result = ReplaceString(result, "&gt;", ">");
            result = ReplaceString(result, "<br>", "\r");
            result = ReplaceString(result, "<p>", "\n");
        }

        return result;
    }

    public static String toTag(String str) {

        String result = str;

        if(result != null) {
            result = result.replace("<", "&lt;").replace(">", "&gt;");
            result = result.replace("[", "&#91;").replace("]", "&#93;");
            result = result.replace("{", "&#123;").replace("}", "&#125;");
            result = result.replace("!", "&#33;");
            result = result.replace("\"", "&quot;");
            result = result.replace("#", "&#35;");
            result = result.replace("$", "&#36;");
            result = result.replace("%", "&#37;");
            result = result.replace("&", "&amp;");
            result = result.replace("'", "&#39;");
            result = result.replace("(", "&#40;").replace(")", "&#41;");
            result = result.replace("/", "&#47;");
            result = result.replace("?", "&#63;");
            result = result.replace("\\", "&#92");
        }
        return result;
    }

    public String ReplaceHTMLTag(String Expression){
        String result = "";
        if (Expression==null || Expression.equals("")) return "";

        result = ReplaceString(Expression, "<a ", "<z ");
        result = ReplaceString(result, "</a>", "</z>");

        return result;
    }

    public String ReplaceJsonTag(String Expression){
        String result = "";
        if (Expression==null || Expression.equals("")) return "";

        result = ReplaceString(Expression, "&#91;", "[");
        result = ReplaceString(result, "&#93;", "]");

        return result;
    }

}