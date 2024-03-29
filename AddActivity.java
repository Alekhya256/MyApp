package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {
    ImageButton button;
    GoogleSignInClient googleSignInClient;
    ListView listView;
    List<NoteData> list;
    NoteData noteData;
    String editApi = "http://13.233.64.181:4000/api/editnote";
    String deleteApi = "http://13.233.64.181:4000/api/deletenote";
    String View = "http://13.233.64.181:4000/api/getnotes";
    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;
    Button buttonsignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        button = findViewById(R.id.add);
        buttonsignout = findViewById(R.id.signout);
        listView = findViewById(R.id.list_view);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AddActivity.this);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddClicked();

            }
        });


    }

    public void onResume() {
        super.onResume();
        list = new ArrayList<>();
        NoteAdapter noteAdapter = new NoteAdapter(AddActivity.this, list);
        this.listView.setAdapter(noteAdapter);
    }

    public void onEditClicked(final String editApi,String id,String title,String description) throws JSONException {

        final List<NoteData> list=new ArrayList<NoteData>();

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id",id);
        jsonObject.put("title", title);
        jsonObject.put("description", description);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, editApi, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("################", String.valueOf(response));

                try {
                    JSONObject dataobj = response.getJSONObject("data");
                    String id = dataobj.getString("_id");
                    String title = dataobj.getString("title");
                    String description = dataobj.getString("description");

                    Log.e("edit values",id+"\n"+title+"\n"+description);




                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i =0;i<list.size();i++){

                    NoteData noteData=list.get(i);
                    Log.e("success",noteData.getId()+ "\n" + noteData.getTitle()+ "\n" + noteData.getDescription());

                }


// Intent intent = new Intent(AddActivity.this, Main2Activity.class);
// intent.putExtra("title",);
// intent.putExtra("description", );
// startActivity(intent);


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

    public void onDeleteClicked(final String deleteApi, String id) throws JSONException {

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", id);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, deleteApi, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("data", String.valueOf(response));

                try {
                    JSONObject datobj = response.getJSONObject("data");
                    String id = datobj.getString("_id");
                    Log.e("success", id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    public void onAddClicked() {
        Intent intent = new Intent(AddActivity.this, Main2Activity.class);
        startActivity(intent);
    }

    public void viewNote(String get, String userId) throws JSONException {
        list = new ArrayList<NoteData>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, get, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("@@@@@@@@@@@@@@@@@", String.valueOf(response));

                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        NoteData noteData = new NoteData();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("title");
                        String description = jsonObject.getString("description");
                        String id =jsonObject.getString("user_id");
                        noteData.setTitle(title);
                        noteData.setDescription(description);
                        noteData.setId(id);
                        list.add(noteData);

                    }
                    for (int i = 0; i < list.size(); i++) {
                        NoteData noteData = list.get(i);
                        Log.e("data", noteData.getTitle() + "\n" + noteData.getDescription()+"\n"+noteData.getId());
                    }
                    NoteAdapter noteAdapter = new NoteAdapter(AddActivity.this, list);
                    listView.setAdapter(noteAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, android.view.View view, int i, long l) {
                            noteData=list.get(i);
                            Log.e("listview",noteData.getTitle());



                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }


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

    public class NoteAdapter extends BaseAdapter {
        Context context;
        List<NoteData> list;

        public NoteAdapter(Context context, List<NoteData> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.view_list, viewGroup, false);
            TextView title = view.findViewById(R.id.tv_Title);
            TextView description = view.findViewById(R.id.tv_content);
            ImageButton delete = view.findViewById(R.id.delete);
            ImageButton edit = view.findViewById(R.id.edit);
            title.setText(list.get(i).getTitle());
            description.setText(list.get(i).getDescription());

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    String id = sharedPreferences.getString("id", "");
                    try {
                        onDeleteClicked(deleteApi, id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    String id = sharedPreferences.getString("id","");
                    String title=sharedPreferences.getString("title","");
                    String description=sharedPreferences.getString("description","");

                    try {
                        onEditClicked(editApi,id,title,description);
                        Log.e("edited",id+"\n"+title+"\n"+description);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return view;

        }

    }


}
