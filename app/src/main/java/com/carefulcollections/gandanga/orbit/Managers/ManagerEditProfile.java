package com.carefulcollections.gandanga.orbit.Managers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.carefulcollections.gandanga.orbit.EmployeesManager.EditProfile;
import com.carefulcollections.gandanga.orbit.EmployeesManager.MyProfile;
import com.carefulcollections.gandanga.orbit.Helpers.AppHelper;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Helpers.VolleyMultipartRequest;
import com.carefulcollections.gandanga.orbit.Helpers.VolleySingleton;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.iamhabib.easy_preference.EasyPreference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManagerEditProfile extends AppCompatActivity {
    public TextView first_name,last_name,email,contact_number;
    Spinner gender;
    CircleImageView user_profile_picture;
    FloatingActionButton save_fab;
    FloatingActionButton change_profile;
    Bitmap bitmap;
    String image_string;
    UserPref pref;
    ProgressBar updating_progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pref = EasyPreference.with(getApplicationContext()).getObject("user_pref", UserPref.class);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        updating_progress = findViewById(R.id.updating_progress);

        gender = findViewById(R.id.gender);
        save_fab = (FloatingActionButton) findViewById(R.id.save_fab);
        save_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOrbiter();
            }
        });
        change_profile = (FloatingActionButton) findViewById(R.id.upload_new_image);
        image_string = pref.picture_url;
        contact_number = findViewById(R.id.phone_number);
        user_profile_picture = findViewById(R.id.edit_profile_picture);
        first_name.setText(pref.first_name);
        last_name.setText(pref.last_name);
//        email.setText(pref.email);
        contact_number.setText(pref.phone_number);
        if (pref.gender.equals("male")) {
            gender.setSelection(0);
        } else {
            gender.setSelection(1);
        }
        change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
            }
        });
        Credentials credentials = EasyPreference.with(ManagerEditProfile.this).getObject("server_details", Credentials.class);
        final String url = credentials.server_url;
        Picasso.with(getApplicationContext()).load(url+pref.picture_url).placeholder(R.drawable.placeholder).into(user_profile_picture);
        firstnameTextListner();
        lastnameTextListner();
        phonenumberTextListner();
//        idnumberTextListner();
        genderSelect();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (shouldAskPermissions()) {
            askPermissions();
        }
    }
    public void genderSelect() {
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pref.gender = gender.getSelectedItem().toString();
                EasyPreference.with(ManagerEditProfile.this)
                        .addObject("user_pref", pref)
                        .save();
                Log.d("GenderNow", pref.gender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    public void firstnameTextListner() {
        first_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pref.first_name = s.toString();
                EasyPreference.with(ManagerEditProfile.this)
                        .addObject("user_pref", pref)
                        .save();
            }
        });

    }

    public void lastnameTextListner() {
        last_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pref.last_name = s.toString();
                EasyPreference.with(ManagerEditProfile.this)
                        .addObject("user_pref", pref)
                        .save();
            }
        });
    }

    public void phonenumberTextListner() {
        contact_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pref.phone_number = s.toString();
                EasyPreference.with(ManagerEditProfile.this)
                        .addObject("user_pref", pref)
                        .save();
            }
        });
    }

    public void updateOrbiter() {
        showProgress(true);

        Credentials credentials = EasyPreference.with(ManagerEditProfile.this).getObject("server_details", Credentials.class);
//        UserPref local_pref = EasyPreference.with(EditProfile.this).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/update_user/"+pref.id;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    showProgress(false);
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");
                    String user = result.getString("user");
                    Log.d("This_User", user);
                    JSONObject response_1 = result.getJSONObject("user");
//                    JsonParser parser = new JsonParser();

                    int role_id = response_1.optInt("role_id");
                    UserPref prefs = new UserPref(response_1.optString("id"),role_id, response_1.optString("name"), response_1.optString("surname"), response_1.optString("email"), response_1.optString("contact_number"),response_1.optString("gender"), response_1.optString("picture_url"),response_1.optString("city"),response_1.optString("package_name"),response_1.optString("company_name"));
                    Toast.makeText(ManagerEditProfile.this, message, Toast.LENGTH_LONG).show();
                    EasyPreference.with(getApplicationContext())
                            .addObject("user_pref", prefs)
                            .save();

                    Intent intent = new Intent(ManagerEditProfile.this, MyProfile.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout, Please try again later";
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
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(ManagerEditProfile.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                UserPref pref = EasyPreference.with(getApplicationContext()).getObject("user_pref", UserPref.class);
                Map<String, String> params = new HashMap<>();
                params.put("name", pref.first_name);
                params.put("surname", pref.last_name);
                params.put("email", pref.email);
//                params.put("id_number", pref.id_number);
                params.put("gender", pref.gender);
                params.put("contact_number", pref.phone_number);
                //Log.d("subjects", teacher_subject_intrest_string);
                return params;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                try {
                    if (!image_string.equals("none")) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        Bitmap bitmap = BitmapFactory.decodeFile(pref.picture_url, options);
                        Log.d("Image String", image_string);
                        params.put("image", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(ManagerEditProfile.this, bitmap), "image/jpeg"));

                    }

                } catch (Exception $e) {
                    return params;
                }
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(multipartRequest);
    }

    public void cameraIntent() {
        //Log.d("Camera", "Camera intent starting");
        CropImage.activity()
                .setAspectRatio(4, 5)
                .setRequestedSize(600, 400, CropImageView.RequestSizeOptions.RESIZE_FIT)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            pref = EasyPreference.with(getApplicationContext()).getObject("user_pref", UserPref.class);
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == this.RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    user_profile_picture.setImageBitmap(bitmap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    String imageString = "";
                    String filename = "orbit_profile_picture.jpg";
                    File sd = Environment.getExternalStorageDirectory();
                    File dest = new File(sd, filename);
                    try {
                        FileOutputStream out = new FileOutputStream(dest);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        imageString = dest.getAbsolutePath();
                        image_string = imageString;
                        pref.picture_url = imageString;
                        EasyPreference.with(this)
                                .addObject("user_pref", pref)
                                .save();
                        Log.d("ImageString_Loco", imageString);

                    } catch (Exception e) {
                        image_string = "none";
                        Log.d("ImageString_Loco", e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG);
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            updating_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            updating_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    updating_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            updating_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            updating_progress.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
