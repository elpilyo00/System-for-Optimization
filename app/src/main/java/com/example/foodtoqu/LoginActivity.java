package com.example.foodtoqu;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    AppCompatButton registerBtn, loginBtn;
    ImageView image;
    TextView slogan1, loginTxt;
    TextInputLayout username, password;
    ProgressDialog progressDialog;
    private AlertDialog dialog;
    private FirebaseAuth mAuth;

    private Boolean validateUsername() {
        String val = username.getEditText().getText().toString();
        if (val.isEmpty()) {
            username.setError("Field cannot be empty");
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString();
        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.loginBtn);
        image = findViewById(R.id.logoImg);
        slogan1 = findViewById(R.id.logoName);
        loginTxt = findViewById(R.id.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validatePassword()) {
                    return;
                }

                progressDialog.show();

                String enteredUsername = username.getEditText().getText().toString().trim();
                String enteredPassword = password.getEditText().getText().toString().trim();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(enteredUsername, enteredPassword)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    if (user.isEmailVerified()) {
                                        String userId = user.getUid();
                                        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId);
                                        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("Admin").child(userId);

                                        ValueEventListener studentListener = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    // User is a student, navigate to StudentPageActivity
                                                    startActivity(new Intent(LoginActivity.this, UserActivity.class));
                                                    overridePendingTransition(0,0);
                                                    finish();
                                                } else {
                                                    // User is not a student, check if user is an admin
                                                    ValueEventListener adminListener = new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                // User is an admin, navigate to AdminPageActivity
                                                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                                                overridePendingTransition(0,0);
                                                                finish();
                                                            } else {
                                                                // User is neither a student nor an admin
                                                                Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            // Handle database error if necessary
                                                        }
                                                    };

                                                    adminRef.addListenerForSingleValueEvent(adminListener);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                // Handle database error if necessary
                                            }
                                        };

                                        studentRef.addListenerForSingleValueEvent(studentListener);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Please verify your email before logging in " + enteredUsername, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // User is null, handle the case if needed
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Login failed. Please check your credentials " + enteredUsername, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        Button forgotPasswordButton = findViewById(R.id.forget);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the "Forgot Password" popup
                forgotYourPassword();
            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(slogan1, "slogan1");
                pairs[2] = new Pair<View, String>(loginTxt, "loginTxt");
                pairs[3] = new Pair<View, String>(username, "username");
                pairs[4] = new Pair<View, String>(password, "password");
                pairs[5] = new Pair<View, String>(loginBtn, "login_trans");
                pairs[6] = new Pair<View, String>(registerBtn, "register_trans");


                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent, options.toBundle());
                finish();
            }
        });

    }

    private void showForgotPasswordPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.forgot_password, null);
        builder.setView(dialogView);

        TextInputLayout usernameLayout = dialogView.findViewById(R.id.username);
        TextInputLayout newPasswordLayout = dialogView.findViewById(R.id.newPassword);
        Button sendResetEmailButton = dialogView.findViewById(R.id.sendResetEmailButton);

        AlertDialog dialog = builder.create();

        sendResetEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameLayout.getEditText().getText().toString();
                String newPassword = newPasswordLayout.getEditText().getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(newPassword)) {
                    // Handle empty input fields
                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Assuming you have a Firebase Realtime Database reference
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("User");

                // Check if the username exists in the database
                usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Username exists, update the password
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                userSnapshot.getRef().child("password").setValue(newPassword);
                            }

                            Toast.makeText(getApplicationContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Username not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle database errors if necessary
                    }
                });
            }
        });

        dialog.show();
    }



    private void forgotYourPassword() {
        LayoutInflater li = LayoutInflater.from(LoginActivity.this);
        View promptsView = li.inflate(R.layout.activity_english_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userMail = (EditText) promptsView.findViewById(R.id.forgot_pass);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = userMail.getText().toString().trim();
                if (!TextUtils.isEmpty(email)) {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        userMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                String mailId = userMail.getText().toString().trim();
                if (mailId.isEmpty()) {
                    okButton.setEnabled(false);
                } else {
                    okButton.setEnabled(true);
                }
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Check if the user is already authenticated
            if (currentUser.isEmailVerified()) {
                // User is authenticated and email is verifie
                String userId = currentUser.getUid();
                DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId);
                DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("Admin").child(userId);
                ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "", "Loading...", true);
                ValueEventListener studentListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("height")) {
                                // User has a height, go to fem_page
                                startActivity(new Intent(LoginActivity.this, UserActivity.class));
                                overridePendingTransition(0,0);
                                finish();
                            } else {
                                // User does not have a height, go to details activity
                                startActivity(new Intent(LoginActivity.this,bodymass_index.class));
                                overridePendingTransition(0,0);
                                finish();
                            }
                        } else {
                            ValueEventListener adminListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    progressDialog.dismiss();
                                    if (dataSnapshot.exists()) {
                                        // User is an admin, navigate to AdminPageActivity
                                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                        finish();
                                    } else {


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle database error if necessary
                                }
                            };

                            adminRef.addListenerForSingleValueEvent(adminListener);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error if necessary
                    }
                };

                studentRef.addListenerForSingleValueEvent(studentListener);
            } else {
                // User's email is not verified, prompt them to verify it
                Toast.makeText(LoginActivity.this, "Please verify your email before logging in " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
            }
        }
    }


}
