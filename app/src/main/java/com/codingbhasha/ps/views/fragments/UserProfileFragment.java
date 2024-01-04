package com.codingbhasha.ps.views.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.recyclerviewadapters.UserProfileAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentUserProfileBinding;
import com.codingbhasha.ps.model.UserProfile;
import com.codingbhasha.ps.viewmodel.UserProfileViewModel;
import com.codingbhasha.ps.views.activities.AdminActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class UserProfileFragment extends BaseFragment<FragmentUserProfileBinding> implements UserProfileAdapter.userProfileClickListener {
    List<UserProfile> userProfileList = new ArrayList<>();
    UserProfileAdapter adapter;
    UserProfileViewModel viewModel;
    AdminActivity mainActivity;
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("User Profiles");

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_user_profile;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (AdminActivity) getActivity();

        adapter = new UserProfileAdapter(this);

        viewModel = new ViewModelProvider(getActivity()).get(UserProfileViewModel.class);
        viewModel.getUserProfiles().observe(getActivity(), new Observer<List<UserProfile>>() {
            @Override
            public void onChanged(List<UserProfile> userProfiles) {
                userProfileList = userProfiles;
                adapter.setList(userProfileList);
            }
        });

        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataBinding.recyclerView.setAdapter(adapter);

        dataBinding.btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                HSSFRow row = sheet.createRow(0);
                row.createCell(0).setCellValue("PlanAID");
                row.createCell(1).setCellValue("PlanBID");
                row.createCell(2).setCellValue("PlanCID");
                row.createCell(3).setCellValue("Email");
                row.createCell(4).setCellValue("Username");
                row.createCell(5).setCellValue("Phone Number");
                for (int i = 0; i < userProfileList.size(); i++) {
                    UserProfile userProfile = userProfileList.get(i);
                    HSSFRow hssfRow = sheet.createRow(i + 1);
                    hssfRow.createCell(0).setCellValue(userProfile.getPlanAID());
                    hssfRow.createCell(1).setCellValue(userProfile.getPlanBID());
                    hssfRow.createCell(2).setCellValue(userProfile.getPlanCID());
                    hssfRow.createCell(3).setCellValue(userProfile.getEmail());
                    hssfRow.createCell(4).setCellValue(userProfile.getName());
                    hssfRow.createCell(5).setCellValue(userProfile.getPhone());
                }
                Calendar c = Calendar.getInstance();
                File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                //if (!file.exists())
                boolean res = file.mkdirs();
                File excel = new File(file, "UserProfile_" + c.getTimeInMillis() + ".xls");
                try {
                    excel.createNewFile();
                    Environment.getExternalStorageDirectory().setWritable(true);
                    FileOutputStream fileOutputStream = new FileOutputStream(excel);
                    workbook.write(fileOutputStream);
                    Toast.makeText(mainActivity, "Stored at: " + excel.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(mainActivity, "FNF " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mainActivity, "IO " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onClickUserProfile(UserProfile userProfile, int pos) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_edit_user_profile, null);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        dialog.show();
        TextInputLayout inputLayoutEmail = view.findViewById(R.id.inputLayoutEmail);
        TextInputLayout inputLayoutUsername = view.findViewById(R.id.inputLayoutName);
        TextInputLayout inputLayoutPhone = view.findViewById(R.id.inputLayoutPhone);
        TextInputEditText editEmail = view.findViewById(R.id.editEmail);
        TextInputEditText editName = view.findViewById(R.id.editName);
        TextInputEditText editPhone = view.findViewById(R.id.editPhone);
        ImageButton btnDone = view.findViewById(R.id.btnDone);

        editEmail.setText(userProfile.getEmail());
        editName.setText(userProfile.getName());
        editPhone.setText(userProfile.getPhone());

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
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputLayoutUsername.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputLayoutPhone.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editEmail.getEditableText().toString().trim())) {
                    inputLayoutEmail.setErrorEnabled(true);
                    inputLayoutEmail.setError("Required");
                } else if (TextUtils.isEmpty(editName.getEditableText().toString().trim())) {
                    inputLayoutUsername.setErrorEnabled(true);
                    inputLayoutUsername.setError("Required");
                } else if (TextUtils.isEmpty(editPhone.getEditableText().toString().trim())) {
                    inputLayoutPhone.setErrorEnabled(true);
                    inputLayoutPhone.setError("Required");
                } else {
                    UserProfile updated = new UserProfile();
                    updated.setEmail(editEmail.getEditableText().toString().trim());
                    updated.setName(editName.getEditableText().toString().trim());
                    updated.setPhone(editPhone.getEditableText().toString().trim());
                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String email = documentSnapshot.getId();
                                if (email.equals(updated.getEmail())) {
                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                            .document(email)
                                            .set(updated, SetOptions.merge());
                                    dialog.dismiss();
                                    viewModel.getUserProfiles().observe(getActivity(), new Observer<List<UserProfile>>() {
                                        @Override
                                        public void onChanged(List<UserProfile> userProfiles) {
                                            adapter.setList(userProfiles);
                                        }
                                    });
                                    dataBinding.recyclerView.setAdapter(adapter);

                                }
                            }
                        }
                    });
                }
            }
        });
    }
}
