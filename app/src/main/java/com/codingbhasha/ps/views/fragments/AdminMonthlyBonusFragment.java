package com.codingbhasha.ps.views.fragments;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.recyclerviewadapters.MonthlyBonusAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentAdminBonusIncomeBinding;
import com.codingbhasha.ps.databinding.FragmentAdminMonthlyBonusBinding;
import com.codingbhasha.ps.databinding.FragmentMonthlyBonusBinding;
import com.codingbhasha.ps.model.MonthlyBonus;
import com.codingbhasha.ps.viewmodel.MonthlyBonusViewModel;
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
import static com.codingbhasha.ps.utils.Constants.COLLECTION_MONTHLY_BONUS;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class AdminMonthlyBonusFragment extends BaseFragment<FragmentAdminMonthlyBonusBinding> {
    AdminActivity mainActivity;
    String plan="";
    MonthlyBonusViewModel viewModel;
    List<MonthlyBonus> monthlyBonusList = new ArrayList<>();
    MonthlyBonusAdapter adapter;
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("Monthly Bonus");


    @Override
    public int getLayoutRes() {
        return R.layout.fragment_admin_monthly_bonus;
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



        viewModel = new ViewModelProvider(getActivity()).get(MonthlyBonusViewModel.class);

        adapter = new MonthlyBonusAdapter();
        dataBinding.setIsDataAvailable(false);
        dataBinding.btnExport.setVisibility(View.VISIBLE);
        dataBinding.btnExportUserData.setVisibility(View.VISIBLE);


        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataBinding.recyclerView.setAdapter(adapter);
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
                row.createCell(4).setCellValue("L2 IDs");
                row.createCell(5).setCellValue("L3 IDs");
                row.createCell(6).setCellValue("L4 IDs");
                row.createCell(7).setCellValue("Total IDS");
                row.createCell(8).setCellValue("Bonus Amount");

                for (int i = 0; i < monthlyBonusList.size(); i++) {
                    MonthlyBonus referralIncome = monthlyBonusList.get(i);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dated = new Date();
                    dated.setTime(referralIncome.getDate());
                    String date = dateFormat.format(dated);
                    HSSFRow hssfRow = sheet.createRow(i + 1);
                    hssfRow.createCell(0).setCellValue(referralIncome.getId());
                    hssfRow.createCell(1).setCellValue(referralIncome.getMonth());
                    hssfRow.createCell(2).setCellValue(date);
                    hssfRow.createCell(3).setCellValue(referralIncome.getL1IDs());
                    hssfRow.createCell(4).setCellValue(referralIncome.getL2IDs());
                    hssfRow.createCell(5).setCellValue(referralIncome.getL3IDs());
                    hssfRow.createCell(6).setCellValue(referralIncome.getL4IDs());
                    hssfRow.createCell(7).setCellValue(referralIncome.getL1IDs()+referralIncome.getL2IDs()+referralIncome.getL3IDs()+referralIncome.getL4IDs());
                    hssfRow.createCell(8).setCellValue(referralIncome.getBonus());
                }
                Calendar c = Calendar.getInstance();
                File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                //if (!file.exists())
                boolean res = file.mkdirs();
                File excel = new File(file, "MontlyBonous_admin_" + c.getTimeInMillis() + ".xls");
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
                List<MonthlyBonus> referralIncomes = new ArrayList<>();

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
                                    .collection(COLLECTION_MONTHLY_BONUS)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot refer) {


                                    for (QueryDocumentSnapshot documentSnapshot2 : refer) {

                                        MonthlyBonus referralIncome = documentSnapshot2.toObject(MonthlyBonus.class);

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
                                        row.createCell(4).setCellValue("L2 IDs");
                                        row.createCell(5).setCellValue("L3 IDs");
                                        row.createCell(6).setCellValue("L4 IDs");
                                        row.createCell(7).setCellValue("Total IDS");
                                        row.createCell(8).setCellValue("Bonus Amount");
                                        for (int i = 0; i < referralIncomes.size(); i++) {
                                            MonthlyBonus referralIncome = referralIncomes.get(i);
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            Date dated = new Date();
                                            dated.setTime(referralIncome.getDate());
                                            String date = dateFormat.format(dated);
                                            HSSFRow hssfRow = sheet.createRow(i + 1);
                                            hssfRow.createCell(0).setCellValue(referralIncome.getId());
                                            hssfRow.createCell(1).setCellValue(referralIncome.getMonth());
                                            hssfRow.createCell(2).setCellValue(date);
                                            hssfRow.createCell(3).setCellValue(referralIncome.getL1IDs());
                                            hssfRow.createCell(4).setCellValue(referralIncome.getL2IDs());
                                            hssfRow.createCell(5).setCellValue(referralIncome.getL3IDs());
                                            hssfRow.createCell(6).setCellValue(referralIncome.getL4IDs());
                                            hssfRow.createCell(7).setCellValue(referralIncome.getL1IDs()+referralIncome.getL2IDs()+referralIncome.getL3IDs()+referralIncome.getL4IDs());
                                            hssfRow.createCell(8).setCellValue(referralIncome.getBonus());
                                        }
                                        Calendar c = Calendar.getInstance();
                                        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                                        //if (!file.exists())
                                        boolean res = file.mkdirs();
                                        File excel = new File(file, "MothlyBonus_all_" + c.getTimeInMillis() + ".xls");
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

    private void setup() {
        ProgressDialog progressDialog = new ProgressDialog(mainActivity);
        progressDialog.show();
        viewModel.getMonthlyBonus(plan).observe(getActivity(), new Observer<List<MonthlyBonus>>() {
            @Override
            public void onChanged(List<MonthlyBonus> monthlyBonuses) {
                monthlyBonusList = monthlyBonuses;
                if (monthlyBonusList.size() > 0) {
                    dataBinding.setIsDataAvailable(true);
                    adapter.setList(monthlyBonusList);
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
