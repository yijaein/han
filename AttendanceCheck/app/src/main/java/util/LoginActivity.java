package util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.yijaein.attendancecheck.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import kr.co.multi.beacontest.control.AppController;
import kr.co.multi.beacontest.control.SessionManager;
import kr.co.multi.beacontest.model.AppConfig;
import kr.co.multi.beacontest.model.SQLiteHandler;

/**
 * Created by Administrator on 2015-11-10.
 */
public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText l_id,l_pwd;
    private Button login_btn,register_btn;
    private SessionManager session;
    private ArrayAdapter<CharSequence> adspin;
    private Button btnInsert, loginlink_btn;
    private RadioGroup radioGroup;
    private Spinner spinner;
    private TextView test;
    private ProgressDialog pDialog;
    private String u_id;
    private String u_pwd;
    private RadioButton gender;
    private String u_gender, u_age;
    private JSONObject jObj;
    private Dialog popup_sign;
    private EditText id,pwd;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginlayout);

        l_id = (EditText)findViewById(R.id.l_id);
        l_pwd = (EditText)findViewById(R.id.l_pwd);
        login_btn = (Button)findViewById(R.id.login_btn);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        if(session.isLoggedIn()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        popup_sign = new Dialog(this);
        popup_sign.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_sign.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup_sign.setContentView(R.layout.join_layout);
        popup_sign.setCanceledOnTouchOutside(false);

        spinner = (Spinner)popup_sign.findViewById(R.id.age_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.age_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        final TextView signup_btn;
        signup_btn=(TextView)findViewById(R.id.signup_btn);
        signup_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//				signup_btn.setSelected(true);
                popup_sign.show();
                id = (EditText)popup_sign.findViewById(R.id.idEdit);
                pwd = (EditText)popup_sign.findViewById(R.id.pwdEdit);

                InputFilter filterAlphaNum = new InputFilter() {
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
                        if (!ps.matcher(source).matches()) {
                            return "";
                        }
                        return null;
                    }
                };
                id.setFilters(new InputFilter[]{filterAlphaNum});
                pwd.setFilters(new InputFilter[]{filterAlphaNum});
            }
        });
        Button popup_closebtn=(Button)popup_sign.findViewById(R.id.sign_close_btn);
        popup_closebtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                u_id = ((EditText) popup_sign.findViewById(R.id.idEdit)).getText().toString().trim();
                u_pwd = ((EditText) popup_sign.findViewById(R.id.pwdEdit)).getText().toString().trim();
                radioGroup = (RadioGroup)popup_sign.findViewById(R.id.gender);
                gender = (RadioButton) popup_sign.findViewById(radioGroup.getCheckedRadioButtonId());
                u_gender = gender.getText().toString();
                u_age = spinner.getSelectedItem().toString();
                if (!u_id.isEmpty() && !u_pwd.isEmpty() && !u_gender.isEmpty() && !u_age.isEmpty()) {
                    registerUser(u_id, u_pwd, u_gender, u_age);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
       // btnInsert = (Button)findViewById(R.id.sign_close_btn);
        radioGroup = (RadioGroup)findViewById(R.id.gender);


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u_id = l_id.getText().toString().trim();
                String u_pwd = l_pwd.getText().toString().trim();

                if (!u_id.isEmpty() && !u_pwd.isEmpty()) {

                  checkLogin(u_id, u_pwd);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }


    private void checkLogin(final String u_id, final String u_pwd){
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        session.setLogin(true);

                        JSONObject user = jObj.getJSONObject("user");
                        String gender = user.getString("gender");
                        String age = user.getString("age");
                        String uid = user.getString("id");
                        db.addUser(gender, age, uid);
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", u_id);
                params.put("pw", u_pwd);

                return params;
            }
    };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void registerUser(final String u_id, final String u_pwd , final String u_gender, final String u_age){
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if(!error) {

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();
                        id.setText("");
                        pwd.setText("");
                        popup_sign.dismiss();
                    }else{
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", u_id);
                params.put("pw", u_pwd);
                params.put("gender", u_gender);
                params.put("age", u_age);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
