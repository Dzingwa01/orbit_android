package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.carefulcollections.gandanga.orbit.Adapters.CommentAdapter;
import com.carefulcollections.gandanga.orbit.EmployeesManager.EmployeeScheduleFragment;
import com.carefulcollections.gandanga.orbit.Helpers.AppHelper;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Helpers.VolleyMultipartRequest;
import com.carefulcollections.gandanga.orbit.Helpers.VolleySingleton;
import com.carefulcollections.gandanga.orbit.Models.Comment;
import com.carefulcollections.gandanga.orbit.Models.Team;
import com.carefulcollections.gandanga.orbit.Models.TeamComparator;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gandanga on 2018-03-07.
 */

public class ChatFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    ImageView img, picture_attachement;
    ImageView profile_image;
    ImageView send_button;
    EditText message_area;
    String imageString, image_string;
    ArrayList<Comment> post_list;
    public ListView mainListView;
    String content = "";
    Bitmap bitmap;
    ProgressBar comments_progress;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView recyclerView;
    View view;

    @Override
    public void onRefresh() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        view =v;
        comments_progress = v.findViewById(R.id.comments_progress);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager((getActivity()));
//        mainListView = findViewById(R.id.mainListView);
        imageString = "none";
        image_string = "none";
        if (shouldAskPermissions()) {
            askPermissions();
        }
        content = "";
        post_list = new ArrayList<Comment>();
        new GetTeams().execute();
        message_area = v.findViewById(R.id.message_area);
        send_button = v.findViewById(R.id.sendButton);
        picture_attachement = v.findViewById(R.id.picture_attachement);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = message_area.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    saveComment(content);
                } else {
                    Toast.makeText(getActivity(), "Please enter a message", Toast.LENGTH_LONG).show();
                }
            }
        });
        final FragmentActivity activity = getActivity();
        picture_attachement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
            }
        });
        PusherOptions options = new PusherOptions();
        options.setCluster("ap2");
        Pusher pusher = new Pusher("347d0dccd5f2a3e703a2", options);
        Channel channel = pusher.subscribe("my-channel");
        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                //Log.d("Message", "message");
//                Log.d("Triggered", data);
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(data);
                Gson gson = new Gson();
                Comment user_comment = gson.fromJson(element, Comment.class);
                if (!post_list.contains(user_comment)) {
                    Log.d("Contained","Yes not contained");
                    post_list.add(0, user_comment);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setupAdapter();
                        }
                    });
                }
            }
        });
        pusher.connect();
        return v;
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

    public void cameraIntent() {
        CropImage.activity()
                .setAspectRatio(4, 5)
                .setRequestedSize(600, 400, CropImageView.RequestSizeOptions.RESIZE_FIT)
                .start(getContext(),this);
    }

    public void showCaptionDialog() {
        Log.d("Caption","Hit caption");
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
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
                            saveComment(content);
                        } else {
                            Toast.makeText(getActivity(), "Please enter a caption for the picture you selected", Toast.LENGTH_LONG).show();
                            showCaptionDialog();
                        }

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public class GetTeams extends AsyncTask<Void, Void, Boolean> {
        GetTeams() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
//            post_list = new ArrayList<Post>();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
                UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
                final String url = credentials.server_url;
                String URL = url+"api/get_employee_teams/"+pref.id;

                JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray response_obj = response.getJSONArray("teams");
                            if (response_obj.length() > 0) {
                                new GetComments().execute();

                            } else {
                                Snackbar.make(view, "You can't send messages because you don't belong to any team as yet.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                message_area.setEnabled(false);
                                send_button.setEnabled(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Data error, please try again", Toast.LENGTH_LONG).show();
                            showProgress(false);
                            Intent intent = new Intent(getActivity(),MainActivity.class);
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
            } catch (Exception e) {
                showProgress(false);
//                Toast.makeText(EmployeeTeams.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(EmployeeTeams.this,MainActivity.class);
//                startActivity(intent);
            }
            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Caption Dialog","Hit");
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
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
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG);
            }
        }
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
    public void setupAdapter() {
        CommentAdapter adapter = new CommentAdapter(post_list, getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void saveComment(String content) {
        showProgress(true);
        Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);

        final String url = credentials.server_url;
        String URL = url+"api/store_chat_message";
        final String content_text = content;
        final UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    showProgress(false);
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");
                    JSONObject comment = result.getJSONObject("message");
                    JsonParser parser = new JsonParser();
                    Log.d("Posts", comment.toString());
                    JsonElement element = parser.parse(comment.toString());
                    Gson gson = new Gson();
                    Comment user_comment = gson.fromJson(element, Comment.class);

                    message_area.setText("");
                    message_area.clearFocus();
                    pushMessage(user_comment);


                } catch (JSONException e) {
                    e.printStackTrace();
                    showProgress(false);
//                    Intent intent = new Intent(QuestionComment.this, HomeActivity.class);
//                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        //Log.d("Error Result", result);
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");
                        Log.e("Error Status", status);
                        Log.e("Error Message", message);
                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
//                Log.i("Error", errorMessage);
                error.printStackTrace();
                showProgress(false);
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", pref.id);
//                params.put("first_name", pref.first_name);
//                params.put("last_name", pref.last_name);
                params.put("comment_text", content_text);
                return params;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (!image_string.equals("none")) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(image_string, options);
                    //Log.d("Image_String", image_string);
                    params.put("image", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getActivity().getBaseContext(), bitmap), "image/jpeg"));

                } else {
                    //Log.d("Image_String1", image_string);
                }
                return params;
            }

        };
        VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(multipartRequest);
 
    }
    public void pushMessage(Comment cur_comment) {
        final Comment comment = cur_comment;
        Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
        final String url = credentials.server_url+"chat_server.php";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response32", response);
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
                params.put("event_type","Comment");
                params.put("user_id", String.valueOf(comment.user_id));
                params.put("id", String.valueOf(comment.id));
                params.put("team_id", String.valueOf(comment.team_id));
                params.put("comment_text", comment.comment_text);
                params.put("first_name", comment.first_name);
                params.put("last_name", comment.last_name);
                params.put("created_at", comment.created_at);
                params.put("picture_url", comment.picture_url);
                if(comment.user_picture_url!=null){
                    params.put("user_picture_url", comment.user_picture_url);
                }
                else{
                    params.put("user_picture_url", "none");
                }

                return params;
            }
        };
        queue.add(postRequest);
    }
    public class GetComments extends AsyncTask<Void, Void, Boolean> {

        GetComments() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
            post_list = new ArrayList<Comment>();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
                UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
                final String url = credentials.server_url;
                String URL = url+"api/get_chat_messages/"+pref.id;
                JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray response_obj = response.getJSONArray("messages");
                            if (response_obj.length() > 0) {
//
                                for (int i = 0; i < response_obj.length(); i++) {
                                    JSONObject obj = response_obj.getJSONObject(i);
                                    JsonParser parser = new JsonParser();

                                    JsonElement element = parser.parse(obj.toString());
                                    Gson gson = new Gson();
                                    Comment post = gson.fromJson(element, Comment.class);
                                    post_list.add(post);
//                                    //Log.d("Subject",post.comment_text);
                                }
                                if (post_list.size() > 0) {
                                    setupAdapter();
                                } else {
//                                    Toast.makeText(getActivity(), "There are no messages as yet", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(getView(), "There are no messages as yet", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }

                            } else {
                                Snackbar.make(getView(), "There are no messages as yet", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
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
            } catch (Exception e) {
                showProgress(false);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
        }
    }
}
