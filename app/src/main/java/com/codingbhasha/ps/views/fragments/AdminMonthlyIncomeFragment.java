package com.codingbhasha.ps.views.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.recyclerviewadapters.MonthlyIncomeAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentAdminMonthlyIncomeBinding;
import com.codingbhasha.ps.databinding.FragmentReferralIncomeBinding;
import com.codingbhasha.ps.model.MonthlyIncome;
import com.codingbhasha.ps.viewmodel.MonthlyIncomeViewModel;
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

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_MONTHLY_INCOME;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class AdminMonthlyIncomeFragment extends BaseFragment<FragmentAdminMonthlyIncomeBinding>  {
    AdminActivity mainActivity;
    String plan;
    MonthlyIncomeViewModel viewModel;
    List<MonthlyIncome> monthlyIncomeList = new ArrayList<>();
    MonthlyIncomeAdapter adapter;
    ProgressDialog progressDialog;
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("Monthly Incomes");



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (AdminActivity) context;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_admin_monthly_income;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
        dataBinding.btnExport.setVisibility(View.VISIBLE);
        dataBinding.btnExportUserData.setVisibility(View.VISIBLE);
        viewModel = new ViewModelProvider(getActivity()).get(MonthlyIncomeViewModel.class);
        adapter = new MonthlyIncomeAdapter(getActivity());
        dataBinding.setIsDataAvailable(false);
        dataBinding.btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                HSSFRow row = sheet.createRow(0);
                row.createCell(0).setCellValue("ID");
                row.createCell(1).setCellValue("Month");
                row.createCell(2).setCellValue("Date");
                row.createCell(3).setCellValue("L1 IDs");
                row.createCell(4).setCellValue("L1 Amt");
                row.createCell(5).setCellValue("L2 IDs");
                row.createCell(6).setCellValue("L2 Amt");
                row.createCell(7).setCellValue("L3 IDs");
                row.createCell(8).setCellValue("L3 Amt");
                row.createCell(9).setCellValue("L4 IDs");
                row.createCell(10).setCellValue("L4 Amt");
                row.createCell(11).setCellValue("Total IDS");
                row.createCell(12).setCellValue("Total Amount");

                for (int i = 0; i < monthlyIncomeList.size(); i++) {
                    MonthlyIncome referralIncome = monthlyIncomeList.get(i);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dated = new Date();
                    dated.setTime(referralIncome.getDate());
                    String date = dateFormat.format(dated);
                    HSSFRow hssfRow = sheet.createRow(i + 1);
                    hssfRow.createCell(0).setCellValue(referralIncome.getId());
                    hssfRow.createCell(1).setCellValue(referralIncome.getMonth());
                    hssfRow.createCell(2).setCellValue(date);
                    hssfRow.createCell(3).setCellValue(referralIncome.getL1IDs());
                    hssfRow.createCell(4).setCellValue(referralIncome.getL1Amt());

                    hssfRow.createCell(5).setCellValue(referralIncome.getL2IDs());
                    hssfRow.createCell(6).setCellValue(referralIncome.getL2Amt());

                    hssfRow.createCell(7).setCellValue(referralIncome.getL3IDs());
                    hssfRow.createCell(8).setCellValue(referralIncome.getL3Amt());

                    hssfRow.createCell(9).setCellValue(referralIncome.getL4IDs());
                    hssfRow.createCell(10).setCellValue(referralIncome.getL4Amt());

                    hssfRow.createCell(11).setCellValue(referralIncome.getL1IDs()+referralIncome.getL2IDs()+referralIncome.getL3IDs()+referralIncome.getL4IDs());
                    hssfRow.createCell(12).setCellValue(referralIncome.getTotal());
                }
                Calendar c = Calendar.getInstance();
                File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                //if (!file.exists())
                boolean res = file.mkdirs();
                File excel = new File(file, "MothlyIncome_Admin_" + c.getTimeInMillis() + ".xls");
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
                List<MonthlyIncome> referralIncomes = new ArrayList<>();

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
                                    .collection(COLLECTION_MONTHLY_INCOME)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot refer) {


                                    for (QueryDocumentSnapshot documentSnapshot2 : refer) {

                                        MonthlyIncome referralIncome = documentSnapshot2.toObject(MonthlyIncome.class);

                                        referralIncomes.add(referralIncome);
                                    }
                                    cont[0]++;
                                    if (cont[0]==colect.size()){
                                        HSSFSheet sheet = workbook.createSheet();
                                        HSSFRow row = sheet.createRow(0);
                                        row.createCell(0).setCellValue("ID");
                                        row.createCell(1).setCellValue("Month");
                                        row.createCell(2).setCellValue("Date");
                                        row.createCell(3).setCellValue("L1 IDs");
                                        row.createCell(4).setCellValue("L1 Amt");
                                        row.createCell(5).setCellValue("L2 IDs");
                                        row.createCell(6).setCellValue("L2 Amt");
                                        row.createCell(7).setCellValue("L3 IDs");
                                        row.createCell(8).setCellValue("L3 Amt");
                                        row.createCell(9).setCellValue("L4 IDs");
                                        row.createCell(10).setCellValue("L4 Amt");
                                        row.createCell(11).setCellValue("Total IDS");
                                        row.createCell(12).setCellValue("Total Amount");

                                        for (int i = 0; i < referralIncomes.size(); i++) {
                                            MonthlyIncome referralIncome = referralIncomes.get(i);
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            Date dated = new Date();
                                            dated.setTime(referralIncome.getDate());
                                            String date = dateFormat.format(dated);
                                            HSSFRow hssfRow = sheet.createRow(i + 1);
                                            hssfRow.createCell(0).setCellValue(referralIncome.getId());
                                            hssfRow.createCell(1).setCellValue(referralIncome.getMonth());
                                            hssfRow.createCell(2).setCellValue(date);
                                            hssfRow.createCell(3).setCellValue(referralIncome.getL1IDs());
                                            hssfRow.createCell(4).setCellValue(referralIncome.getL1Amt());

                                            hssfRow.createCell(5).setCellValue(referralIncome.getL2IDs());
                                            hssfRow.createCell(6).setCellValue(referralIncome.getL2Amt());

                                            hssfRow.createCell(7).setCellValue(referralIncome.getL3IDs());
                                            hssfRow.createCell(8).setCellValue(referralIncome.getL3Amt());

                                            hssfRow.createCell(9).setCellValue(referralIncome.getL4IDs());
                                            hssfRow.createCell(10).setCellValue(referralIncome.getL4Amt());

                                            hssfRow.createCell(11).setCellValue(referralIncome.getL1IDs()+referralIncome.getL2IDs()+referralIncome.getL3IDs()+referralIncome.getL4IDs());
                                            hssfRow.createCell(12).setCellValue(referralIncome.getTotal());
                                        }
                                        Calendar c = Calendar.getInstance();
                                        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                                        //if (!file.exists())
                                        boolean res = file.mkdirs();
                                        File excel = new File(file, "MothlyIncome_all_" + c.getTimeInMillis() + ".xls");
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


    }

    private void getMonthlyIncome() {
        progressDialog.show();
        viewModel.getMonthlyIncomes(plan).observe(getActivity(), new Observer<List<MonthlyIncome>>() {
            @Override
            public void onChanged(List<MonthlyIncome> referralIncomes) {
                monthlyIncomeList = referralIncomes;
                if (monthlyIncomeList.size() > 0) {
                    dataBinding.setIsDataAvailable(true);
                    Log.e("asa", monthlyIncomeList.toString());
                    adapter.setList(monthlyIncomeList,plan);
                    adapter.notifyDataSetChanged();
                } else {
                    dataBinding.setIsDataAvailable(false);
                }
            }
        });
        progressDialog.dismiss();
        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataBinding.recyclerView.setAdapter(adapter);
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.e("rasdadsada","resu" +
                "");
        //mainActivity.dataBinding.spinnerPlan.setSelection(0);
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
        getMonthlyIncome();

        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                mainActivity.fillData(plan);
                getMonthlyIncome();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}