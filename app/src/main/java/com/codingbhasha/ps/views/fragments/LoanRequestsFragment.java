package com.codingbhasha.ps.views.fragments;

import android.Manifest;
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
import com.codingbhasha.ps.adapters.recyclerviewadapters.LoanRequestsAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentLoanRequestsBinding;
import com.codingbhasha.ps.model.ApplyLoan;
import com.codingbhasha.ps.model.LienOutstandingAmt;
import com.codingbhasha.ps.model.LoanAccount;
import com.codingbhasha.ps.viewmodel.LoanRequestsViewModel;
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
import static com.codingbhasha.ps.utils.Constants.COLLECTION_LIEN_OUTSTANDING;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_LOAN_ACCOUNT;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class LoanRequestsFragment extends BaseFragment<FragmentLoanRequestsBinding> {
    LoanRequestsAdapter adapter;
    LoanRequestsViewModel viewModel;
    AdminActivity mainActivity;
    String plan = "";
    List<ApplyLoan> applyLoanList = new ArrayList<>();

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_loan_requests;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new LoanRequestsAdapter(getActivity());
        viewModel = new ViewModelProvider(getActivity()).get(LoanRequestsViewModel.class);



        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataBinding.recyclerView.setAdapter(adapter);

        dataBinding.btnExportLoanRecovey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                HSSFWorkbook workbook = new HSSFWorkbook();
                List<LienOutstandingAmt> refundsList = new ArrayList<>();

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
                                    .collection(COLLECTION_LIEN_OUTSTANDING)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot refer) {


                                    for (QueryDocumentSnapshot documentSnapshot2 : refer) {

                                        LienOutstandingAmt referralIncome = documentSnapshot2.toObject(LienOutstandingAmt.class);

                                        if (plan.equals("PlanA")){
                                            referralIncome.setMemberid(documentSnapshot.get("planAID").toString());
                                        } else
                                        if (plan.equals("PlanB")){
                                            referralIncome.setMemberid(documentSnapshot.get("planBID").toString());
                                        }
                                        else
                                        if (plan.equals("PlanC")){
                                            referralIncome.setMemberid(documentSnapshot.get("planCID").toString());
                                        }

                                        refundsList.add(referralIncome);
                                    }
                                    cont[0]++;
                                    if (cont[0]==colect.size()){
                                        HSSFSheet sheet = workbook.createSheet();
                                        HSSFRow row = sheet.createRow(0);
                                        row.createCell(0).setCellValue("MemberID");
                                        row.createCell(1).setCellValue("Amount");
                                        row.createCell(2).setCellValue("AdvanceIncomeId");
                                        row.createCell(3).setCellValue("Date");
                                        for (int i = 0; i < refundsList.size(); i++) {
                                            LienOutstandingAmt referralIncome = refundsList.get(i);
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            Date dated = new Date();
                                            dated.setTime(referralIncome.getDate());
                                            String date = dateFormat.format(dated);
                                            HSSFRow hssfRow = sheet.createRow(i + 1);
                                            hssfRow.createCell(0).setCellValue(referralIncome.getMemberid());
                                            hssfRow.createCell(1).setCellValue(referralIncome.getAmount());
                                            hssfRow.createCell(2).setCellValue(referralIncome.getLoanId());
                                            hssfRow.createCell(3).setCellValue(date);
                                        }
                                        Calendar c = Calendar.getInstance();
                                        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                                        //if (!file.exists())
                                        boolean res = file.mkdirs();
                                        File excel = new File(file, "AdvanceIncomeHistory"+"_"+plan+"_" + c.getTimeInMillis() + ".xls");
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
        dataBinding.btnExportLoanAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("btns","adada");
                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                HSSFWorkbook workbook = new HSSFWorkbook();
                List<LoanAccount> refundsList = new ArrayList<>();

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
                                    .collection(COLLECTION_LOAN_ACCOUNT)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot refer) {


                                    for (QueryDocumentSnapshot documentSnapshot2 : refer) {

                                        LoanAccount referralIncome = documentSnapshot2.toObject(LoanAccount.class);

                                        if (plan.equals("PlanA")){
                                            referralIncome.setMemberID(documentSnapshot.get("planAID").toString());
                                        } else
                                        if (plan.equals("PlanB")){
                                            referralIncome.setMemberID(documentSnapshot.get("planBID").toString());
                                        }
                                        else
                                        if (plan.equals("PlanC")){
                                            referralIncome.setMemberID(documentSnapshot.get("planCID").toString());
                                        }

                                        refundsList.add(referralIncome);
                                    }
                                    cont[0]++;
                                    if (cont[0]==colect.size()){
                                        HSSFSheet sheet = workbook.createSheet();
                                        HSSFRow row = sheet.createRow(0);
                                        row.createCell(0).setCellValue("MemberID");
                                        row.createCell(1).setCellValue("IFSC");
                                        row.createCell(2).setCellValue("AdvanceIncomeId");
                                        row.createCell(3).setCellValue("StartDate");
                                        row.createCell(4).setCellValue("EndDate");
                                        row.createCell(5).setCellValue("Status");
                                        row.createCell(6).setCellValue("AdvanceIncomeAmt");
                                        row.createCell(7).setCellValue("RecoveredAmt");
                                        row.createCell(8).setCellValue("OutstandingAmt");

                                        for (int i = 0; i < refundsList.size(); i++) {
                                            LoanAccount referralIncome = refundsList.get(i);
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            Date dated = new Date();
                                            dated.setTime(referralIncome.getDate());
                                            String date = dateFormat.format(dated);
                                            String enddate = String.valueOf(referralIncome.getEnd_date());
                                            if(referralIncome.getEnd_date()!=0){

                                                dated.setTime(referralIncome.getEnd_date());
                                                 enddate = dateFormat.format(dated);
                                            }

                                            HSSFRow hssfRow = sheet.createRow(i + 1);
                                            hssfRow.createCell(0).setCellValue(referralIncome.getMemberID());
                                            hssfRow.createCell(1).setCellValue(referralIncome.getIFSC());
                                            hssfRow.createCell(2).setCellValue(referralIncome.getLoanId());
                                            hssfRow.createCell(3).setCellValue(date);
                                            hssfRow.createCell(4).setCellValue(enddate);
                                            hssfRow.createCell(5).setCellValue(referralIncome.getStatus());
                                            hssfRow.createCell(6).setCellValue(referralIncome.getLoanAmt());
                                            hssfRow.createCell(7).setCellValue(referralIncome.getRecoveredAmt());
                                            hssfRow.createCell(8).setCellValue(referralIncome.getOutstandingAmt());
                                        }
                                        Calendar c = Calendar.getInstance();
                                        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                                        //if (!file.exists())
                                        boolean res = file.mkdirs();
                                        File excel = new File(file, "LoanAccount"+"_"+plan+"_" + c.getTimeInMillis() + ".xls");
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

    private void getLoanReqs() {
        viewModel.getLoanRequests(plan).observe(getActivity(), new Observer<List<ApplyLoan>>() {
            @Override
            public void onChanged(List<ApplyLoan> applyLoans) {
                applyLoanList = applyLoans;
                adapter.setList(applyLoanList);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();

        getLoanReqs();
        dataBinding.btnExportLoanAccount.setVisibility(View.VISIBLE);
        dataBinding.btnExportLoanRecovey.setVisibility(View.VISIBLE);
        dataBinding.btnExportLoanRecovey.setEnabled(true);
        dataBinding.btnExportLoanAccount.setEnabled(true);
        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                mainActivity.fillData(plan);
                getLoanReqs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (AdminActivity) context;
    }
}
