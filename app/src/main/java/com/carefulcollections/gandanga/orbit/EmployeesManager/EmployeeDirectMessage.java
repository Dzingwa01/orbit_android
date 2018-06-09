package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.orbit.Helpers.AppHelper;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Helpers.VolleyMultipartRequest;
import com.carefulcollections.gandanga.orbit.Helpers.VolleySingleton;
import com.carefulcollections.gandanga.orbit.Models.Comment;
import com.carefulcollections.gandanga.orbit.Models.Message;
import com.carefulcollections.gandanga.orbit.Models.Team;
import com.carefulcollections.gandanga.orbit.Models.TeamComparator;
import com.carefulcollections.gandanga.orbit.Models.User;
import com.carefulcollections.gandanga.orbit.Models.UserComparator;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EmployeeDirectMessage extends AppCompatActivity {
    private ArrayList<User> users_list;
    private ArrayList<Team> teams_list;
    Spinner employees_to;
    private ArrayList<String> employee_names;
    private int receiver_id;
    ImageView img, picture_attachement;
    ImageView profile_image;
    ImageView send_button;
    EditText message_area;
    String image_string;
    String content = "";
    Bitmap bitmap;
    ProgressBar comments_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_direct_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        users_list = new ArrayList<>();
        teams_list =  new ArrayList<>();
        employee_names = new ArrayList<>();
        image_string = "none";
        content = "";
        employees_to = findViewById(R.id.employees_to);
        message_area = findViewById(R.id.message_area);
        send_button = findViewById(R.id.sendButton);
        picture_attachement = findViewById(R.id.picture_attachement);
        comments_progress = findViewById(R.id.comments_progress);

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = message_area.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    sendMessage(content);
                } else {
                    Toast.makeText(EmployeeDirectMessage.this, "Please enter a message", Toast.LENGTH_LONG).show();
                }
            }
        });

        picture_attachement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
            }
        });
        employees_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                User user = users_list.get(i);
                receiver_id = user.id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if (shouldAskPermissions()) {
            askPermissions();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getTeamMembers();
    }
    public void cameraIntent() {
        CropImage.activity()
                .setAspectRatio(4, 5)
                .setRequestedSize(600, 400, CropImageView.RequestSizeOptions.RESIZE_FIT)
                .start(this);
    }

    public void showCaptionDialog() {
        Log.d("Caption","Hit caption");
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EmployeeDirectMessage.this);
        LayoutInflater inflater = LayoutInflater.from(EmployeeDirectMessage.this);
        final View view = inflater.inflate(R.layout.caption_popup_dialog, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle("Image Caption");
        final EditText captionText = view.findViewById(R.id.caption_text);
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.cancel();
                    }
                });
            }
        });
        alertDialogBuilder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String content = captionText.getText().toString();
                        if (!TextUtils.isEmpty(content)) {
                            sendMessage(content);
                        } else {
                            Toast.makeText(EmployeeDirectMessage.this, "Please enter a caption for the picture you selected", Toast.LENGTH_LONG).show();
                            showCaptionDialog();
                        }

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Caption Dialog","Hit");
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            UserPref pref = EasyPreference.with(EmployeeDirectMessage.this).getObject("user_pref", UserPref.class);
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == EmployeeDirectMessage.this.RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(EmployeeDirectMessage.this.getContentResolver(), resultUri);
//                    comment_image.setImageBitmap(bitmap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    String imageString = "";
                    String filename = "orbit_chat_image.jpg";
                    File sd = Environment.getExternalStorageDirectory();
                    File dest = new File(sd, filename);
                    try {
                        FileOutputStream out = new FileOutputStream(dest);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        imageString = dest.getAbsolutePath();
                        image_string = imageString;
                        Log.d("Check me",imageString);
                        showCaptionDialog();
                    } catch (Exception e) {
                        image_string = "none";
                        Log.d("Erroroccured",e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Error occured",e.getMessage());

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("resultcodeerr",error.getMessage());
                Toast.makeText(EmployeeDirectMessage.this, error.toString(), Toast.LENGTH_LONG);
            }
        }
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            comments_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            comments_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    comments_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            comments_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            comments_progress.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void sendMessage(final String content) {
        showProgress(true);
        Credentials credentials = EasyPreference.with(EmployeeDirectMessage.this).getObject("server_details", Credentials.class);

        final String url = credentials.server_url;
        String URL = url+"api/store_user_message";
        final String content_text = content;
        final UserPref pref = EasyPreference.with(EmployeeDirectMessage.this).getObject("user_pref", UserPref.class);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String message = result.getString("message");
                    JSONObject push_msg = result.getJSONObject("push_msg");

                    JsonParser parser = new JsonParser();
                    JsonElement msg = parser.parse(push_msg.toString());
                    Gson gson = new Gson();
                    Message push_message = gson.fromJson(msg,Message.class);
                    pushMessage(push_message);
                    message_area.setText("");
                    message_area.clearFocus();
                    showProgress(false);
                    Intent intent = new Intent(EmployeeDirectMessage.this,EmployeeMessages.class);
                    startActivity(intent);
                    Toast.makeText(EmployeeDirectMessage.this,message,Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "An error occured please try again later";
                error.printStackTrace();
                Intent intent = new Intent(EmployeeDirectMessage.this,EmployeeMessages.class);
                startActivity(intent);
                showProgress(false);
                Toast.makeText(EmployeeDirectMessage.this, errorMessage, Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("from", String.valueOf(pref.id));
                params.put("to", String.valueOf(receiver_id));
                params.put("message_text",content_text);

                return params;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (!image_string.equals("none")) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(image_string, options);
                    Log.d("Image_String", image_string);
                    params.put("image", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(EmployeeDirectMessage.this, bitmap), "image/jpeg"));

                } else {
                    //Log.d("Image_String1", image_string);
                }
                return params;
            }

        };
        VolleySingleton.getInstance(EmployeeDirectMessage.this).addToRequestQueue(multipartRequest);

    }
    private void pushMessage(Message message){
        final Message push_message = message;
        Credentials credentials = EasyPreference.with(EmployeeDirectMessage.this).getObject("server_details", Credentials.class);
        final String url = credentials.server_url+"chat_server.php";
        RequestQueue queue = Volley.newRequestQueue(EmployeeDirectMessage.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
//                        Log.d("Error.Response", error.getMessage());
//                        Toast.makeText(getActivity(), "An error occured while trying to send your message, please try again", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("event_type","Send_Message");
                params.put("id",String.valueOf(push_message.id));
                params.put("first_name", push_message.first_name);
                params.put("last_name", push_message.last_name);
                params.put("user_picture_url",push_message.user_picture_url);
                params.put("message_picture_url",push_message.message_picture_url);
                params.put("message_text",push_message.message_text);

                return params;
            }
        };
        queue.add(postRequest);
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }
    public void getTeamMembers(){
        showProgress(true);
        RequestQueue requestQueue = Volley.newRequestQueue(EmployeeDirectMessage.this);
        Credentials credentials = EasyPreference.with(getApplicationContext()).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(getApplicationContext()).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/get_employee_teams/"+pref.id;

        JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray response_obj = response.getJSONArray("teams");
                    if (response_obj.length() > 0) {
//
                        for (int i = 0; i < response_obj.length(); i++) {
                            JSONObject obj = response_obj.getJSONObject(i);
                            JsonParser parser = new JsonParser();

                            JsonElement element = parser.parse(obj.toString());
                            Gson gson = new Gson();
                            Team team = gson.fromJson(element, Team.class);
                            teams_list.add(team);
                            getTeamEmployees(team.id);
                        }

                    } else {
                        Toast.makeText(EmployeeDirectMessage.this, "There are no teams available as yet", Toast.LENGTH_LONG).show();
                    }
                    showProgress(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(EmployeeDirectMessage.this, "Data error, please try again", Toast.LENGTH_LONG).show();
//                    showProgress(false);
                    showProgress(false);
                    Intent intent = new Intent(EmployeeDirectMessage.this,MainActivity.class);
                    startActivity(intent);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("error", error.toString());

            }
        });
        requestQueue.add(provinceRequest);
    }
    public void getTeamEmployees(int team_id){
//        progressBar.setVisibility(View.VISIBLE);
        try{

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Credentials credentials = EasyPreference.with(this).getObject("server_details", Credentials.class);
           final UserPref pref = EasyPreference.with(this).getObject("user_pref", UserPref.class);
            final String url = credentials.server_url;
            String URL = url+"api/get_team_employees/"+team_id;
            Log.d("Url",URL);
            JSONObject jsonBody = new JSONObject();
            JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray response_obj = response.getJSONArray("employees");
                        Log.d("employees",response_obj.toString());
                        if (response_obj.length() > 0) {
//
                            for (int i = 0; i < response_obj.length(); i++) {
                                JSONObject obj = response_obj.getJSONObject(i);
                                JsonParser parser = new JsonParser();
                                JsonElement element = parser.parse(obj.toString());
                                Gson gson = new Gson();
                                User user = gson.fromJson(element, User.class);
                                if(user.id!=Integer.valueOf(pref.id)&&!users_list.contains(user) && !employee_names.contains(user.name + " " +user.surname)){
                                    users_list.add(user);
                                    employee_names.add(user.name + " " +user.surname);
                                }

                            }
                            employees_to.setAdapter(new ArrayAdapter<String>(EmployeeDirectMessage.this,android.R.layout.simple_spinner_dropdown_item,employee_names));

                        } else {
                            Toast.makeText(EmployeeDirectMessage.this, "There are no team members in this team", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.d("error", error.toString());

                }
            });
            requestQueue.add(provinceRequest);
        }
        catch (Exception e){

        }
    }
}
