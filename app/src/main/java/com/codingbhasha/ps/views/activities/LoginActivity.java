package com.codingbhasha.ps.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.base.BaseActivity;
import com.codingbhasha.ps.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class LoginActivity extends BaseActivity<ActivityLoginBinding> {
    FirebaseAuth auth;
    CollectionReference usersCollection = FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    private Dialog load;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); load = new Dialog(this);
        load.setContentView(R.layout.load);
        load.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        load.setCancelable(false);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        dataBinding.editID.setAllCaps(false);
dataBinding.editID.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        auth = FirebaseAuth.getInstance();
        textWatchers();
        clickListeners();

    }

    private void clickListeners() {
        dataBinding.btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this).create();
                View v = getLayoutInflater().inflate(R.layout.layout_forgot_password, null);
                dialog.setView(v);
                dialog.show();
                TextInputLayout inputLayoutEmail = v.findViewById(R.id.inputLayoutID);
                TextInputEditText editEmail = v.findViewById(R.id.editID);
                ImageButton btnDone = v.findViewById(R.id.btnDone);

                editEmail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        inputLayoutEmail.setErrorEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = editEmail.getEditableText().toString().trim();
                        if (TextUtils.isEmpty(email)) {
                            inputLayoutEmail.setErrorEnabled(true);
                            inputLayoutEmail.setError("Required");
                        } else {
                            auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Reset Email sent to " + email, Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                load.dismiss();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                load.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });

            }
        });

        dataBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("asdad",dataBinding.editID.getEditableText().toString().trim());
                if (isDataValid()) {
                    login();
                }
            }
        });
    }

    private void login() {
        load.show();
        String uniqueId = dataBinding.editID.getEditableText().toString().trim();
        String password = dataBinding.editPassword.getEditableText().toString().trim();
        auth.signInWithEmailAndPassword(uniqueId, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (uniqueId.equals("spurandhar0@gmail.com")) {
                                load.dismiss();
                                Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                finish();
                            } else {


                                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> tak) {

                                        String Tocken  = tak.getResult();
                                        FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(task.getResult().getUser().getEmail()).update("Token", FieldValue.arrayUnion(Tocken));
                                        load.dismiss();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();

                                    }
                                });




                            }
                        } else {
                            load.dismiss();
                            Toast.makeText(LoginActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isDataValid() {
        String uniqueId = dataBinding.editID.getEditableText().toString().trim();
        String password = dataBinding.editPassword.getEditableText().toString().trim();
        if (TextUtils.isEmpty(uniqueId)) {
            dataBinding.inputLayoutID.setErrorEnabled(true);
            dataBinding.inputLayoutID.setError("Required");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            dataBinding.inputLayoutPassword.setErrorEnabled(true);
            dataBinding.inputLayoutPassword.setError("Required");
            return false;
        }
        return true;
    }

    private void textWatchers() {
        dataBinding.editID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataBinding.inputLayoutID.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dataBinding.editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataBinding.inputLayoutPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            if (!user.getEmail().equals("spurandhar0@gmail.com")) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(this, AdminActivity.class));
                finish();
            }
        }
    }
}
