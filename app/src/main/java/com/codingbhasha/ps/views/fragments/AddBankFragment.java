package com.codingbhasha.ps.views.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentAddBankAccountBinding;
import com.codingbhasha.ps.databinding.FragmentApplyLoanBinding;
import com.codingbhasha.ps.views.activities.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.michaelrocks.paranoid.Obfuscate;

import static android.app.Activity.RESULT_OK;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_BANK_DETAILS;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class AddBankFragment extends BaseFragment<FragmentAddBankAccountBinding>   {
    private static final int PICK_FILE_REQUEST_CODE = 111;
    MainActivity mainActivity;
    String fileUrl = "";
    int noOfAcc = 0;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_add_bank_account;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("admin")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection(COLLECTION_BANK_DETAILS);
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                noOfAcc = queryDocumentSnapshots.size();
                if (noOfAcc>=1){
                    dataBinding.btnSubmit.setEnabled(false);
                    dataBinding.btnUploadDoc.setEnabled(false);
                }
            }
        });
//        if (!preferences.getString("accHolderName", "").equals("")) {
//            dataBinding.editName.setText(preferences.getString("accHolderName", ""));
//            dataBinding.editAccNumber.setText(preferences.getString("accNumber", ""));
//            dataBinding.editIFSC.setText(preferences.getString("ifsc", ""));
//            dataBinding.btnUploadDoc.setText(preferences.getString("docRef", ""));
//            dataBinding.inputLayoutName.setEnabled(false);
//            dataBinding.inputLayoutAccNumber.setEnabled(false);
//            dataBinding.inputLayoutIFSC.setEnabled(false);
//            dataBinding.btnUploadDoc.setEnabled(false);
//            dataBinding.btnSubmit.setEnabled(false);
//            dataBinding.btnAddBankAcc.setVisibility(View.VISIBLE);
//        }
        textWatchers();

        dataBinding.btnUploadDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDocs();
            }
        });

        dataBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDataValid()) {
                    submitData();
                }
            }
        });

        dataBinding.btnAddBankAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataBinding.btnAddBankAcc.setVisibility(View.GONE);
                dataBinding.inputLayoutName.setEnabled(true);
                dataBinding.editName.setText("");
                dataBinding.inputLayoutAccNumber.setEnabled(true);
                dataBinding.editAccNumber.setText("");
                dataBinding.inputLayoutIFSC.setEnabled(true);
                dataBinding.editIFSC.setText("");
                dataBinding.btnUploadDoc.setEnabled(true);
                dataBinding.btnUploadDoc.setText("Upload document");
                dataBinding.btnSubmit.setEnabled(true);
            }
        });
    }

    private void uploadDocs() {
        Intent choose = new Intent(Intent.ACTION_GET_CONTENT);
        choose.setType("image/*");
        choose = Intent.createChooser(choose, "Choose File From: ");
        startActivityForResult(choose, PICK_FILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://psapp-699b8.appspot.com");
            StorageReference item = storageRef.child(uri.getLastPathSegment());
            UploadTask uploadTask = item.putFile(uri);
            ProgressDialog dialog = new ProgressDialog(mainActivity);
            dialog.setCancelable(false);
            dialog.setTitle("Uploading File");
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    dialog.show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(mainActivity, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    dataBinding.btnUploadDoc.setText(uri.getLastPathSegment());
                    dataBinding.btnUploadDoc.setEnabled(false);
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            dialog.dismiss();
                            fileUrl = uri.toString();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mainActivity, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void textWatchers() {
        dataBinding.editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataBinding.inputLayoutName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dataBinding.editAccNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataBinding.inputLayoutAccNumber.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dataBinding.editIFSC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataBinding.inputLayoutIFSC.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void submitData() {
        String name = dataBinding.editName.getEditableText().toString().trim();
        String accNum = dataBinding.editAccNumber.getEditableText().toString().trim();
        String ifsc = dataBinding.editIFSC.getEditableText().toString().trim();
        String url = fileUrl;
        Calendar c = Calendar.getInstance();
        CollectionReference adminRef = FirebaseFirestore.getInstance().collection("admin");
        Map<String, Object> emailMap = new HashMap<>();
        emailMap.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        adminRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(emailMap, SetOptions.merge());
        Map<String, Object> map = new HashMap<>();
        map.put("accHolderName", name);
        map.put("accNumber", accNum);
        map.put("ifsc", ifsc);
        map.put("docRef", url);
        map.put("date", c.getTimeInMillis());
        map.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        adminRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("bankDetails")
                .document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dataBinding.editAccNumber.setText("");
                dataBinding.editIFSC.setText("");
                dataBinding.editName.setText("");
                dataBinding.btnUploadDoc.setText("Upload Documents");
                dataBinding.btnUploadDoc.setEnabled(false);
                dataBinding.btnSubmit.setEnabled(false);
                Toast.makeText(requireActivity(),"Reqest Send",Toast.LENGTH_LONG).show();

            }
        });

    }

    private boolean isDataValid() {
        String name = dataBinding.editName.getEditableText().toString().trim();
        String accNum = dataBinding.editAccNumber.getEditableText().toString().trim();
        String ifsc = dataBinding.editIFSC.getEditableText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            dataBinding.inputLayoutName.setErrorEnabled(true);
            dataBinding.inputLayoutName.setError("Required");
            return false;
        } else if (TextUtils.isEmpty(accNum)) {
            dataBinding.inputLayoutAccNumber.setErrorEnabled(true);
            dataBinding.inputLayoutAccNumber.setError("Required");
            return false;
        } else if (TextUtils.isEmpty(ifsc)) {
            dataBinding.inputLayoutIFSC.setErrorEnabled(true);
            dataBinding.inputLayoutIFSC.setError("Required");
            return false;
        } else if (dataBinding.btnUploadDoc.isEnabled()) {
            Toast.makeText(mainActivity, "Upload document for verification", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;


    }

}
