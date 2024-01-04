package com.codingbhasha.ps.views.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.recyclerviewadapters.LevelAchievementBonusAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentAdminLevelAchievementBonusBinding;
import com.codingbhasha.ps.databinding.FragmentLevelAchievementBonusBinding;
import com.codingbhasha.ps.model.LevelAchievementBonus;
import com.codingbhasha.ps.viewmodel.LevelAchievementBonusViewModel;
import com.codingbhasha.ps.views.activities.AdminActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.michaelrocks.paranoid.Obfuscate;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_LEVEL_ACHIEVEMENT_BONUS;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class AdminLevelAchievementBonusFragment extends BaseFragment<FragmentAdminLevelAchievementBonusBinding> {
    AdminActivity mainActivity;
    String plan;
    CollectionReference levelAchievementRef;
    DocumentReference userDoc;
    LevelAchievementBonusViewModel viewModel;
    List<LevelAchievementBonus> levelAchievementBonusList = new ArrayList<>();
    LevelAchievementBonusAdapter adapter;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("Level Achivement Bonus");

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_admin_level_achievement_bonus;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
        mainActivity.dataBinding.toolbarId.setVisibility(View.VISIBLE);
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
        preferences = mainActivity.getSharedPreferences("labPref", Context.MODE_PRIVATE);
        editor = preferences.edit();
        adapter = new LevelAchievementBonusAdapter();
        dataBinding.setIsDataAvailable(false);
        dataBinding.btnExport.setVisibility(View.VISIBLE);
        dataBinding.btnExportUserData.setVisibility(View.VISIBLE);
        viewModel = new ViewModelProvider(getActivity()).get(LevelAchievementBonusViewModel.class);
        dataBinding.btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                HSSFRow row = sheet.createRow(0);
                row.createCell(0).setCellValue("Total IDS");
                row.createCell(1).setCellValue("Date");
                row.createCell(2).setCellValue("Bonus Amount");


                for (int i = 0; i < levelAchievementBonusList.size(); i++) {
                    LevelAchievementBonus referralIncome = levelAchievementBonusList.get(i);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa");
                    Date dated = new Date();
                    dated.setTime(referralIncome.getDate());
                    String date = dateFormat.format(dated);
                    HSSFRow hssfRow = sheet.createRow(i + 1);
                    hssfRow.createCell(0).setCellValue(referralIncome.getTotalIds());
                    hssfRow.createCell(1).setCellValue(date);
                    hssfRow.createCell(2).setCellValue(referralIncome.getBonus());
                }
                Calendar c = Calendar.getInstance();
                File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                //if (!file.exists())
                boolean res = file.mkdirs();
                File excel = new File(file, "LevelAcivementBouns_Admin" + c.getTimeInMillis() + ".xls");
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
        dataBinding.btnExportUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                HSSFWorkbook workbook = new HSSFWorkbook();
                List<LevelAchievementBonus> referralIncomes = new ArrayList<>();

                final CollectionReference[] usersCollection = {FirebaseFirestore.getInstance().collection(COLLECTION_USERS)};
                usersCollection[0].get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot colect) {
                        final int[] cont = {0};
                        for (QueryDocumentSnapshot documentSnapshot : colect) {

                            String userId = documentSnapshot.getId();
                            usersCollection[0].document(userId)
                                    .collection("plans")
                                    .document(plan)
                                    .collection(COLLECTION_LEVEL_ACHIEVEMENT_BONUS)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot refer) {


                                    for (QueryDocumentSnapshot documentSnapshot2 : refer) {

                                        LevelAchievementBonus referralIncome = documentSnapshot2.toObject(LevelAchievementBonus.class);

                                        referralIncomes.add(referralIncome);
                                    }
                                    cont[0]++;
                                    if (cont[0]==colect.size()){
                                        HSSFSheet sheet = workbook.createSheet();
                                        HSSFRow row = sheet.createRow(0);
                                        row.createCell(0).setCellValue("ID");
                                        row.createCell(1).setCellValue("Total IDS");
                                        row.createCell(2).setCellValue("Date");
                                        row.createCell(3).setCellValue("Bonus Amount");
                                        for (int i = 0; i < referralIncomes.size(); i++) {
                                            LevelAchievementBonus referralIncome = referralIncomes.get(i);
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            Date dated = new Date();
                                            dated.setTime(referralIncome.getDate());
                                            String date = dateFormat.format(dated);
                                            HSSFRow hssfRow = sheet.createRow(i + 1);
                                            hssfRow.createCell(0).setCellValue(referralIncome.getId());
                                            hssfRow.createCell(1).setCellValue(referralIncome.getTotalIds());
                                            hssfRow.createCell(2).setCellValue(date);
                                            hssfRow.createCell(3).setCellValue(referralIncome.getBonus());
                                        }
                                        Calendar c = Calendar.getInstance();
                                        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                                        //if (!file.exists())
                                        boolean res = file.mkdirs();
                                        File excel = new File(file, "LevelAcivement_All_" + c.getTimeInMillis() + ".xls");
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
                                    } else
                                    {
                                        Log.e("okkkk", "asdadqqqqqqqw");
                                    }

                                }
                            });

                        }



                    }
                });
            }
        });



        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataBinding.recyclerView.setAdapter(adapter);
    }

    private void setup() {
        ProgressDialog progressDialog = new ProgressDialog(mainActivity);
        progressDialog.show();

        viewModel.getLevelAchievementBonus(plan).observe(getActivity(), new Observer<List<LevelAchievementBonus>>() {
            @Override
            public void onChanged(List<LevelAchievementBonus> levelAchievementBonuses) {
                levelAchievementBonusList = levelAchievementBonuses;
                if (levelAchievementBonusList.size() > 0) {
                    dataBinding.setIsDataAvailable(true);
                    adapter.setList(levelAchievementBonusList);
                    adapter.notifyDataSetChanged();
                } else
                    dataBinding.setIsDataAvailable(false);
                int totalLevelAchievementBonus = 0;
                for (LevelAchievementBonus levelAchievementBonus : levelAchievementBonusList) {
                    totalLevelAchievementBonus += levelAchievementBonus.getBonus();
                }
                Map<String, Object> map = new HashMap<>();
                map.put("totalLevelAchievementBonus", totalLevelAchievementBonus);
                FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                        .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .collection("plans")
                        .document(plan)
                        .set(map, SetOptions.merge());
            }
        });
        progressDialog.dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (AdminActivity) context;
    }


    @Override
    public void onResume() {
        super.onResume();
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
        setup();
        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                mainActivity.fillData(plan);
                setup();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


}
