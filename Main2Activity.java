package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Main2Activity extends AppCompatActivity {
    static final int REQ_CODE_SPEECH_INPUT = 100;
    EditText editTexttitle, editTextContent;
    ImageButton imageButtonsave, imageButtoncancel;
    Button buttonnotes;
    SharedPreferences sharedPreferences;
    String create = "http://13.233.64.181:4000/api/createnote";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        editTexttitle = findViewById(R.id.et_title);
        editTextContent = findViewById(R.id.et_content);
        imageButtonsave = findViewById(R.id.save);
        imageButtoncancel = findViewById(R.id.cancel);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Main2Activity.this);


        imageButtonsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTexttitle.getText().toString();
                String text = editTextContent.getText().toString();
                String userId = sharedPreferences.getString("userId", "");
                try {
                    myApi(create, title, text, userId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


        imageButtoncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelClicked();
            }
        });


    }
    private void myApi(String count, String title, String text, String userId) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);
        jsonObject.put("title", title);
        jsonObject.put("description", text);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, count, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("@@@@@@@@@@@@", String.valueOf(response));

                try {
                    JSONObject dataobj = response.getJSONObject("data");
                    String userId = dataobj.getString("user_id");
                    String title = dataobj.getString("title");
                    String description = dataobj.getString("description");
                    String id = dataobj.getString("_id");

                    Log.e("notedatataa", userId + "\n" + title + "\n" + description + "\n" + id);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Toast.makeText(Main2Activity.this, "Saved", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", String.valueOf(error));


            }
        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    public void onCancelClicked() {
        this.finish();
    }
}







