package com.example.yijaein.becontest1;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends Activity{

    String xml;
    TextView txtResult;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtResult = (TextView)findViewById(R.id.txtResult);

        StringBuffer sBuffer = new StringBuffer();
        try {
            String urlAddr = "http://192.168.1.6/localhost/data.xml";
            //위의 사이트에는 본인의 웹호스팅 주소나 서버 주소를 적으시고 뒤에 data.xml 넣으시길 바랍니다.
            URL url = new URL(urlAddr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            if(conn != null) {
                conn.setConnectTimeout(20000);
                conn.setUseCaches(false);
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                    BufferedReader br = new BufferedReader(isr);
                    while(true) {
                        String line = br.readLine();
                        if(line == null) {
                            break;
                        }
                        sBuffer.append(line);
                    }
                    br.close();
                    conn.disconnect();
                }
            }
            xml = sBuffer.toString();
        } catch(Exception e) {
            Log.e("다운로드 중 에러 발생", e.getMessage());
        }
        parse();
    }
    public void parse() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(xml.getBytes());
            Document doc = documentBuilder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList headline_node_list = doc.getElementsByTagName("menu");
            String name = "", best = "";
            String text = "";

            for(int i = 0;i<headline_node_list.getLength(); i++) {
                Node headline_node = headline_node_list.item(i);
                if(headline_node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) headline_node;
                    NodeList node_list = element.getElementsByTagName("name");
                    Node node = node_list.item(0);
                    name = node.getTextContent();
                }
                if(headline_node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) headline_node;
                    NodeList node_list = element.getElementsByTagName("best");
                    Node node = node_list.item(0);
                    best = node.getTextContent();
                }
                text += (name + "    " + best +"\n");
            }

            txtResult.setText(text.toString());

        } catch(Exception e) {
            Log.e("파싱 중 에러 발생", e.getMessage());
        }
    }
}


}
