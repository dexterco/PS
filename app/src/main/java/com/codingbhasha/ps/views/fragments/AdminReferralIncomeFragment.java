package com.codingbhasha.ps.views.fragments;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_REFERRAL_INCOME;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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
import androidx.databinding.InverseBindingListener;
import androidx.databinding.adapters.AdapterViewBindingAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.recyclerviewadapters.ReferrerIncomeAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentReferralIncomeBinding;
import com.codingbhasha.ps.model.ReferralIncome;
import com.codingbhasha.ps.viewmodel.ReferralIncomeViewModel;
import com.codingbhasha.ps.views.activities.AdminActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class AdminReferralIncomeFragment extends BaseFragment<FragmentReferralIncomeBinding> {
    AdminActivity mainActivity;
    ReferralIncomeViewModel viewModel;
    List<ReferralIncome> referralIncomeList = new ArrayList<>();
    ReferrerIncomeAdapter adapter;
    String plan = "";
    ProgressDialog progressDialog;
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("Referral Incomes");



    @Override
    public int getLayoutRes() {
        return R.layout.fragment_referral_income;
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
       // mainActivity.dataBinding.spinnerPlan.setSelection(0);
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
        dataBinding.btnExport.setVisibility(View.VISIBLE);
        dataBinding.btnExportUserData.setVisibility(View.VISIBLE);

        viewModel = new ViewModelProvider(getActivity()).get(ReferralIncomeViewModel.class);

        adapter = new ReferrerIncomeAdapter();
        dataBinding.setIsDataAvailable(false);






        dataBinding.btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                HSSFRow row = sheet.createRow(0);
                row.createCell(0).setCellValue("ID");
                row.createCell(1).setCellValue("Amount");
                row.createCell(2).setCellValue("Level");
                row.createCell(3).setCellValue("Date");
                for (int i = 0; i < referralIncomeList.size(); i++) {
                    ReferralIncome referralIncome = referralIncomeList.get(i);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dated = new Date();
                    dated.setTime(referralIncome.getDate());
                    String date = dateFormat.format(dated);
                    HSSFRow hssfRow = sheet.createRow(i + 1);
                    hssfRow.createCell(0).setCellValue(referralIncome.getIdNumber());
                    hssfRow.createCell(1).setCellValue(referralIncome.getAmount());
                    hssfRow.createCell(2).setCellValue(referralIncome.getLevelNum());
                    hssfRow.createCell(3).setCellValue(date);
                }
                Calendar c = Calendar.getInstance();
                File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                //if (!file.exists())
                boolean res = file.mkdirs();
                File excel = new File(file, "ReferralIncome_Admin_" + c.getTimeInMillis() + ".xls");
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
                List<ReferralIncome> referralIncomes = new ArrayList<>();

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
                                    .collection(COLLECTION_REFERRAL_INCOME)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot refer) {


                                    for (QueryDocumentSnapshot documentSnapshot2 : refer) {

                                        ReferralIncome referralIncome = documentSnapshot2.toObject(ReferralIncome.class);
                                        Log.e("adaasd" , referralIncome.getUserID() +" "+referralIncome.getAmount());
                                        referralIncomes.add(referralIncome);
                                    }
                                    cont[0]++;
                                    if (cont[0]==colect.size()){
                                        HSSFSheet sheet = workbook.createSheet();
                                        HSSFRow row = sheet.createRow(0);
                                        row.createCell(0).setCellValue("Referring ID");
                                        row.createCell(1).setCellValue("Amount");
                                        row.createCell(2).setCellValue("Level");
                                        row.createCell(3).setCellValue("Date");
                                        row.createCell(4).setCellValue("Plan");
                                        row.createCell(5).setCellValue("Referral ID");
                                        for (int i = 0; i < referralIncomes.size(); i++) {
                                            ReferralIncome referralIncome = referralIncomes.get(i);
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            Date dated = new Date();
                                            dated.setTime(referralIncome.getDate());
                                            String date = dateFormat.format(dated);
                                            HSSFRow hssfRow = sheet.createRow(i + 1);
                                            hssfRow.createCell(0).setCellValue(referralIncome.getUserID());
                                            hssfRow.createCell(1).setCellValue(referralIncome.getAmount());
                                            hssfRow.createCell(2).setCellValue(referralIncome.getLevelNum());
                                            hssfRow.createCell(3).setCellValue(date);
                                            hssfRow.createCell(4).setCellValue(plan);
                                            hssfRow.createCell(5).setCellValue(referralIncome.getIdNumber());
                                        }
                                        Calendar c = Calendar.getInstance();
                                        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                                        //if (!file.exists())
                                        boolean res = file.mkdirs();
                                        File excel = new File(file, "RefrralIncome_all_" + c.getTimeInMillis() + ".xls");
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

    private void getReferralIncomes() {
        progressDialog.show();
        viewModel.getReferralIncomes(plan).observe(getActivity(), new Observer<List<ReferralIncome>>() {
            @Override
            public void onChanged(List<ReferralIncome> referralIncomes) {
                referralIncomeList = referralIncomes;
                if (referralIncomeList.size() > 0) {
                    dataBinding.setIsDataAvailable(true);
                    adapter.setList(referralIncomeList);
                    adapter.notifyDataSetChanged();
                } else
                    dataBinding.setIsDataAvailable(false);

            }
        });
        progressDialog.dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (AdminActivity) context;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
        getReferralIncomes();
        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterViewBindingAdapter.OnItemSelectedComponentListener(new AdapterViewBindingAdapter.OnItemSelected() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {

                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                mainActivity.fillData(plan);
                getReferralIncomes();
            }
        }, new AdapterViewBindingAdapter.OnNothingSelected() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }, new InverseBindingListener() {
            @Override
            public void onChange() {

            }
        }));
    }

}
