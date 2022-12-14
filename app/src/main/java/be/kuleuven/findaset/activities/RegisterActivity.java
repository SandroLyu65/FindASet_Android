package be.kuleuven.findaset.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import be.kuleuven.findaset.R;

public class RegisterActivity extends AppCompatActivity {
    private TextView registerError;
    private RequestQueue requestQueue;
    private String baseURL = "https://studev.groept.be/api/a21pt113/";
    //https://studev.groept.be/api/a21pt113/userNameExisted/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerError = findViewById(R.id.failureRegister);
        registerError.setVisibility(View.INVISIBLE);
    }

    public void onBtnRegister_Clicked(View caller) throws NoSuchAlgorithmException {
        EditText registeredUsername = (EditText) findViewById(R.id.etUsername);
        EditText registeredPassword = (EditText) findViewById(R.id.etPassword);
        EditText registeredConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);

        String username = registeredUsername.getText().toString().toLowerCase(Locale.ROOT);
        String pass = registeredPassword.getText().toString();
        String passConfirm = registeredConfirmPassword.getText().toString();

        // check if any of the fields are empty
        if(!username.equals("") && !pass.equals("") && !passConfirm.equals("") && pass.equals(passConfirm)){
            String hash = get_SHA_1_SecurePassword(pass);
            String queryURL = baseURL + "userNameExisted/" + username;
            String requestURL = baseURL + "register" + "/" + username + "/" + hash + "/" + username;
            Boolean nameExisted = false;

            requestQueue = Volley.newRequestQueue( this );

            StringRequest registerRequest = new StringRequest(Request.Method.GET, requestURL,
                    response -> {
                        try {
                            JSONArray responseArray = new JSONArray(response);
                            String responseString = "";
                            for( int i = 0; i < responseArray.length(); i++ )
                            {
                                JSONObject curObject = responseArray.getJSONObject( i );
                                Log.e( "Database", responseString);
                            }
                            registerError.setVisibility(View.INVISIBLE);
                            finish();
                        }
                        catch( JSONException e )
                        {
                            Log.e( "Database", e.getMessage(), e );
                            registerError.setText(getString(R.string.error_database));
                            registerError.setVisibility(View.VISIBLE);
                        }
                    },
                    error -> {
                        registerError.setText(getString(R.string.error_database));
                        registerError.setVisibility(View.VISIBLE);
                    }
            );

            StringRequest submitRequest = new StringRequest(Request.Method.GET, queryURL,
                    response -> {
                            if (response.equals("[]")) {
                                requestQueue.add(registerRequest);
                            }
                            else {
                                registerError.setText(getString(R.string.register_name_existed));
                                registerError.setVisibility(View.VISIBLE);
                            }
                    },
                    error -> {
                        registerError.setText(getString(R.string.error_database));
                        registerError.setVisibility(View.VISIBLE);
                    }
            );

            requestQueue.add(submitRequest);
        }
        else {
            registerError.setText(R.string.register_error);
            registerError.setVisibility(View.VISIBLE);
        }
    }

    // https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
    private static String get_SHA_1_SecurePassword(String passwordToHash) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public void onClick_Back(View caller) {
        finish();
    }
}