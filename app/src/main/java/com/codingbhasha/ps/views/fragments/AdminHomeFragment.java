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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codingbhasha.ps.R;

import com.codingbhasha.ps.adapters.recyclerviewadapters.AdminhomeAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentAdminBonusIncomeBinding;
import com.codingbhasha.ps.databinding.FragmentAdminHomeBinding;
import com.codingbhasha.ps.databinding.FragmentAdminMonthlyBonusBinding;
import com.codingbhasha.ps.databinding.FragmentMonthlyBonusBinding;
import com.codingbhasha.ps.model.AdminHome;
import com.codingbhasha.ps.model.Refund;
import com.codingbhasha.ps.viewmodel.AdminHomeViewModel;
import com.codingbhasha.ps.views.activities.AdminActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import static com.codingbhasha.ps.utils.Constants.COLLECTION_Refund;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class AdminHomeFragment extends BaseFragment<FragmentAdminHomeBinding> {
    AdminActivity mainActivity;
    String plan = "";
    AdminHomeViewModel viewModel;
    List<AdminHome> monthlyBonusList = new ArrayList<>();
    AdminhomeAdapter adapter;
    AdminBonusIncomeFragment bonusIncomeFragment;
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("Refund");

    public AdminHomeFragment(Context context) {
        mainActivity = (AdminActivity) context;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_admin_home;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       mainActivity. dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
               setup();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mainActivity.dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
        mainActivity.dataBinding.toolbarId.setVisibility(View.VISIBLE);
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();

        viewModel = new ViewModelProvider(getActivity()).get(AdminHomeViewModel.class);

        adapter = new AdminhomeAdapter();
        dataBinding.setIsDataAvailable(false);

        dataBinding.btnExportUserData.setVisibility(View.VISIBLE);
        dataBinding.btnExportAll.setVisibility(View.VISIBLE);
        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataBinding.recyclerView.setAdapter(adapter);

        dataBinding.btnExportUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                HSSFWorkbook workbook = new HSSFWorkbook();
                List<Refund> refundsList = new ArrayList<>();

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
                                    .collection(COLLECTION_Refund)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot refer) {


                                    for (QueryDocumentSnapshot documentSnapshot2 : refer) {

                                        Refund referralIncome = documentSnapshot2.toObject(Refund.class);
                                        refundsList.add(referralIncome);
                                    }
                                    cont[0]++;
                                    if (cont[0]==colect.size()){
                                        HSSFSheet sheet = workbook.createSheet();
                                        HSSFRow row = sheet.createRow(0);
                                        row.createCell(0).setCellValue("ID");
                                        row.createCell(1).setCellValue("Refund");
                                        row.createCell(3).setCellValue("Date");
                                        for (int i = 0; i < refundsList.size(); i++) {
                                            Refund referralIncome = refundsList.get(i);
                                            Log.e("asdasdas", referralIncome.getId() + " "+ referralIncome.getDate()+" " + referralIncome.getRefund());
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            Date dated = new Date();
                                            dated.setTime(referralIncome.getDate());
                                            String date = dateFormat.format(dated);
                                            HSSFRow hssfRow = sheet.createRow(i + 1);
                                            hssfRow.createCell(0).setCellValue(referralIncome.getId());
                                            hssfRow.createCell(1).setCellValue(referralIncome.getRefund());
                                            hssfRow.createCell(2).setCellValue(date);
                                        }
                                        Calendar c = Calendar.getInstance();
                                        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                                        //if (!file.exists())
                                        boolean res = file.mkdirs();
                                        File excel = new File(file, "Refunds" + c.getTimeInMillis() + ".xls");
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
        dataBinding.btnExportAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                HSSFWorkbook workbook = new HSSFWorkbook();
                List<AdminHome> referralIncomes = new ArrayList<>();

                final CollectionReference[] usersCollection = {FirebaseFirestore.getInstance().collection(COLLECTION_USERS)};
                usersCollection[0].get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot colect) {
                        final int[] cont = {0};
                        for (QueryDocumentSnapshot documentSnapshot : colect) {

                            String userId = documentSnapshot.getId();
                            usersCollection[0].document(userId)
                                    .collection("plans")
                                    .document(plan).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot1) {
                                    if (documentSnapshot1.exists()) {
                                        AdminHome user =  new AdminHome();
                                        String plan =  documentSnapshot1.getId();
                                        user.setPlan(documentSnapshot1.getId());

                                        if (documentSnapshot1.contains("memberId")) {
                                            user.setMemberId(documentSnapshot1.get("memberId").toString());

                                        }
                                        if (documentSnapshot1.contains("dateOfJoining")) {
                                            user.setDateOfJoining(documentSnapshot1.get("dateOfJoining").toString());
                                        }
                                        if (documentSnapshot1.contains("l1IDs")) {
                                            int l1 = Integer.parseInt(documentSnapshot1.get("l1IDs").toString());
                                            int l2 = Integer.parseInt(documentSnapshot1.get("l2IDs").toString());
                                            int l3 = Integer.parseInt(documentSnapshot1.get("l3IDs").toString());
                                            int l4 = Integer.parseInt(documentSnapshot1.get("l4IDs").toString());
                                            user.setTotalIds(l1 + l2 + l3 + l4);
                                        }
                                        if (documentSnapshot1.contains("validity")) {
                                            user.setValidity(Integer.parseInt(documentSnapshot1.get("validity").toString()));

                                        }

                                        Log.e("dooo", String.valueOf(documentSnapshot1));

                                        user.setActivel1IDs(documentSnapshot1.get("activel1IDs").toString());
                                        user.setActivel2IDs(documentSnapshot1.get("activel2IDs").toString());
                                        user.setActivel3IDs(documentSnapshot1.get("activel3IDs").toString());
                                        user.setActivel4IDs(documentSnapshot1.get("activel4IDs").toString());

                                        user.setDactl1IDs(documentSnapshot1.get("dactl1IDs").toString());
                                        user.setDactl2IDs(documentSnapshot1.get("dactl2IDs").toString());
                                        user.setDactl3IDs(documentSnapshot1.get("dactl3IDs").toString());
                                        user.setDactl4IDs(documentSnapshot1.get("dactl4IDs").toString());

                                      int  referralIncome = Integer.parseInt(documentSnapshot1.get("totalReferralIncome").toString());
                                        int  monthlyIncome = Integer.parseInt(documentSnapshot1.get("totalMonthlyIncome").toString());
                                        int monthlyBonus = Integer.parseInt(documentSnapshot1.get("totalMonthlyBonus").toString());
                                        int levelAchievementBonus = Integer.parseInt(documentSnapshot1.get("totalLevelAchievementBonus").toString());
                                        int totalWallet = Integer.parseInt(documentSnapshot1.get("totalWallet").toString());
                                        int totalRefunds = Integer.parseInt(documentSnapshot1.get("totalRefunds").toString());
                                        int totalPayouts = Integer.parseInt(documentSnapshot1.get("totalPayouts").toString());
                                        int totalLoanRecoveyAmt = Integer.parseInt(documentSnapshot1.get("totalLoanRecoveyAmt").toString());
                                        int earnBalance = referralIncome + monthlyBonus + monthlyIncome + levelAchievementBonus + totalRefunds;
                                        int getBalance = totalWallet + totalPayouts+totalLoanRecoveyAmt;
                                        int total = earnBalance - getBalance;

                                        user.setWalletBalance(total);
                                        int Refund = 0 ;
                                        if (plan.equals("PlanA")) {
                                            if (2000>earnBalance){
                                                Refund = 2000-earnBalance;
                                            } else {
                                                Refund =  0;
                                            }
                                            user.setRefund(Refund);
                                        } else if (plan.equals("PlanB")) {
                                            if (5000>earnBalance){
                                                Refund = 5000-earnBalance;
                                            } else {
                                                Refund =  0;
                                            }
                                            user.setRefund(Refund);

                                        } else if (plan.equals("PlanC")) {
                                            if (10000>earnBalance){
                                                Refund = 10000-earnBalance;
                                            } else {
                                                Refund =  0;
                                            }
                                            user.setRefund(Refund);
                                        }



                                        referralIncomes.add(user);
                                    }



                                    cont[0]++;
                                    if (cont[0]==colect.size()){
                                        HSSFSheet sheet = workbook.createSheet();
                                        HSSFRow row = sheet.createRow(0);

                                        row.createCell(0).setCellValue("ID");
                                        row.createCell(1).setCellValue("DateOfJoining");
                                        row.createCell(2).setCellValue("Total Ids");
                                        row.createCell(3).setCellValue("validity");
                                        row.createCell(4).setCellValue("walletBalance");
                                        row.createCell(5).setCellValue("Refund");
                                        row.createCell(6).setCellValue("activel1IDs");
                                        row.createCell(7).setCellValue("activel2IDs");
                                        row.createCell(8).setCellValue("activel3IDs");
                                        row.createCell(9).setCellValue("activel4IDs");
                                        row.createCell(10).setCellValue("dactl1IDs");
                                        row.createCell(11).setCellValue("dactl2IDs");
                                        row.createCell(12).setCellValue("dactl3IDs");
                                        row.createCell(13).setCellValue("dactl4IDs");

                                        for (int i = 0; i < referralIncomes.size(); i++) {
                                            AdminHome referralIncome = referralIncomes.get(i);
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            Date dated = new Date();
                                            dated.setTime(Long.parseLong(referralIncome.getDateOfJoining()));
                                            String date = dateFormat.format(dated);
                                            HSSFRow hssfRow = sheet.createRow(i + 1);
                                            hssfRow.createCell(0).setCellValue(referralIncome.getMemberId());
                                            hssfRow.createCell(1).setCellValue(date);
                                            hssfRow.createCell(2).setCellValue(referralIncome.getTotalIds());
                                            hssfRow.createCell(3).setCellValue(referralIncome.getValidity());
                                            hssfRow.createCell(4).setCellValue(referralIncome.getWalletBalance());
                                            hssfRow.createCell(5).setCellValue(referralIncome.getRefund());
                                            hssfRow.createCell(6).setCellValue(referralIncome.getActivel1IDs());
                                            hssfRow.createCell(7).setCellValue(referralIncome.getActivel2IDs());
                                            hssfRow.createCell(8).setCellValue(referralIncome.getActivel3IDs());
                                            hssfRow.createCell(9).setCellValue(referralIncome.getActivel4IDs());
                                            hssfRow.createCell(10).setCellValue(referralIncome.getDactl1IDs());
                                            hssfRow.createCell(11).setCellValue(referralIncome.getDactl2IDs());
                                            hssfRow.createCell(12).setCellValue(referralIncome.getDactl3IDs());
                                            hssfRow.createCell(13).setCellValue(referralIncome.getDactl4IDs());
                                        }
                                        Calendar c = Calendar.getInstance();
                                        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                                        //if (!file.exists())
                                        boolean res = file.mkdirs();
                                        File excel = new File(file, "Current_User_status_all_" + c.getTimeInMillis() + ".xls");
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
        ProgressBar progressDialog = new ProgressBar(mainActivity);
        progressDialog.setVisibility(View.VISIBLE);
        viewModel.getAdminHome(plan).observe(getActivity(), new Observer<List<AdminHome>>() {
            @Override
            public void onChanged(List<AdminHome> monthlyBonuses) {
                monthlyBonusList = monthlyBonuses;
                if (monthlyBonusList.size() > 0) {
                    dataBinding.setIsDataAvailable(true);
                    adapter.setList(monthlyBonusList);
                    adapter.notifyDataSetChanged();
                } else
                    dataBinding.setIsDataAvailable(false);

            }
        });
        mainActivity.fillData(plan);
       // progressDialog.dismiss();
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
    }


}
