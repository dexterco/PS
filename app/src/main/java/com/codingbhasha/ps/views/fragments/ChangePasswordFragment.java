package com.codingbhasha.ps.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentApplyLoanBinding;
import com.codingbhasha.ps.databinding.FragmentChangePasswordBinding;
import com.codingbhasha.ps.views.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class ChangePasswordFragment extends BaseFragment<FragmentChangePasswordBinding>  {
    MainActivity mainActivity;
    DocumentReference userDoc;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_change_password;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textWatchers();

        userDoc = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        dataBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDataValid()) {

                }
            }
        });

    }

    private void textWatchers() {
        dataBinding.editOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataBinding.inputLayoutOldPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dataBinding.editNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataBinding.inputLayoutNewPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dataBinding.editConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataBinding.inputLayoutConfirmPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private boolean isDataValid() {
        String oldPass = dataBinding.editOldPassword.getEditableText().toString().trim();
        String newPass = dataBinding.editNewPassword.getEditableText().toString().trim();
        String confirmPass = dataBinding.editConfirmPassword.getEditableText().toString().trim();
        if (TextUtils.isEmpty(oldPass)) {
            dataBinding.inputLayoutOldPassword.setErrorEnabled(true);
            dataBinding.inputLayoutOldPassword.setError("Required");
            return false;
        } else if (TextUtils.isEmpty(newPass)) {
            dataBinding.inputLayoutNewPassword.setErrorEnabled(true);
            dataBinding.inputLayoutNewPassword.setError("Required");
            return false;
        } else if (TextUtils.isEmpty(confirmPass)) {
            dataBinding.inputLayoutConfirmPassword.setErrorEnabled(true);
            dataBinding.inputLayoutConfirmPassword.setError("Required");
            return false;
        } else if (!newPass.equals(confirmPass)) {
            dataBinding.inputLayoutConfirmPassword.setErrorEnabled(true);
            dataBinding.inputLayoutConfirmPassword.setError("Passwords do not match");
            return false;
        } else {
            userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String password = documentSnapshot.get("password").toString();
                    if (password.equals(oldPass)) {
                        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider.getCredential(user1.getEmail(), oldPass);
                        user1.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user1.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Map<String, Object> passwordMap = new HashMap<>();
                                                    passwordMap.put("password", newPass);
                                                    userDoc.set(passwordMap, SetOptions.merge());
                                                    Toast.makeText(getActivity(), "Password Changed Successfully!", Toast.LENGTH_SHORT).show();
                                                    dataBinding.editOldPassword.setText("");
                                                    dataBinding.editNewPassword.setText("");
                                                    dataBinding.editConfirmPassword.setText("");
                                                }
                                            });
                                        } else
                                            Toast.makeText(getActivity(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        dataBinding.inputLayoutOldPassword.setErrorEnabled(true);
                        dataBinding.inputLayoutOldPassword.setError("Wrong Password");
                        return;
                    }

                }
            });
            return false;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }



}
