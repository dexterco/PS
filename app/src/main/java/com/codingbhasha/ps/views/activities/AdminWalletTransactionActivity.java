package com.codingbhasha.ps.views.activities;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.recyclerviewadapters.WalletTransactionAdapter;
import com.codingbhasha.ps.base.BaseActivity;
import com.codingbhasha.ps.databinding.ActivityAdminWalletTransactionsBinding;
import com.codingbhasha.ps.databinding.ActivityWalletTransactionsBinding;
import com.codingbhasha.ps.model.WalletTransaction;
import com.codingbhasha.ps.viewmodel.WalletTransactionViewModel;
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
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_WALLET_TRANSACTION;
@Obfuscate
public class AdminWalletTransactionActivity extends BaseActivity<ActivityAdminWalletTransactionsBinding> {

    AdminWalletTransactionActivity mainActivity;
    WalletTransactionAdapter adapter;
    WalletTransactionViewModel viewModel;
    List<WalletTransaction> walletTransactionList = new ArrayList<>();
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("Wallet Transaction");
    @Override
    public int getLayoutResId() {
        return R.layout.activity_admin_wallet_transactions;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity  = this;
        setSupportActionBar(dataBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new WalletTransactionAdapter();
        dataBinding.setIsDataAvailable(false);
        dataBinding.btnExport.setVisibility(View.VISIBLE);
        dataBinding.btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                HSSFRow row = sheet.createRow(0);
                row.createCell(0).setCellValue("ID");
                row.createCell(1).setCellValue("Amount");
                row.createCell(2).setCellValue("Plan");
                row.createCell(3).setCellValue("Date");
                for (int i = 0; i < walletTransactionList.size(); i++) {
                    WalletTransaction referralIncome = walletTransactionList.get(i);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dated = new Date();
                    dated.setTime(referralIncome.getDate());
                    String date = dateFormat.format(dated);
                    HSSFRow hssfRow = sheet.createRow(i + 1);
                    hssfRow.createCell(0).setCellValue(referralIncome.getId());
                    hssfRow.createCell(1).setCellValue(referralIncome.getAmount());
                    hssfRow.createCell(2).setCellValue(referralIncome.getPlan());
                    hssfRow.createCell(3).setCellValue(date);
                }
                Calendar c = Calendar.getInstance();
                File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                //if (!file.exists())
                boolean res = file.mkdirs();
                File excel = new File(file, "WalletTransaction_Admin_" + c.getTimeInMillis() + ".xls");
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
                List<WalletTransaction> referralIncomes = new ArrayList<>();

                final CollectionReference[] usersCollection = {FirebaseFirestore.getInstance().collection(COLLECTION_USERS)};
                usersCollection[0].get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot colect) {
                        final int[] cont = {0};
                        for (QueryDocumentSnapshot documentSnapshot : colect) {

                            String userId = documentSnapshot.getId();
                            usersCollection[0].document(userId)
                                    .collection(COLLECTION_WALLET_TRANSACTION)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot refer) {


                                    for (QueryDocumentSnapshot documentSnapshot2 : refer) {

                                        WalletTransaction referralIncome = documentSnapshot2.toObject(WalletTransaction.class);

                                        referralIncomes.add(referralIncome);
                                    }
                                    cont[0]++;
                                    if (cont[0]==colect.size()){
                                        HSSFSheet sheet = workbook.createSheet();
                                        HSSFRow row = sheet.createRow(0);
                                        row.createCell(0).setCellValue("Reffring ID");
                                        row.createCell(1).setCellValue("Refrral ID");
                                        row.createCell(2).setCellValue("Amount");
                                        row.createCell(3).setCellValue("Plan");
                                        row.createCell(4).setCellValue("Date");
                                        for (int i = 0; i < referralIncomes.size(); i++) {
                                            WalletTransaction referralIncome = referralIncomes.get(i);
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            Date dated = new Date();
                                            dated.setTime(referralIncome.getDate());
                                            String date = dateFormat.format(dated);
                                            HSSFRow hssfRow = sheet.createRow(i + 1);

                                            hssfRow.createCell(0).setCellValue(referralIncome.getUserID());
                                                hssfRow.createCell(1).setCellValue(referralIncome.getId());
                                            hssfRow.createCell(2).setCellValue(referralIncome.getAmount());
                                            hssfRow.createCell(3).setCellValue(referralIncome.getPlan());
                                            hssfRow.createCell(4).setCellValue(date);
                                        }
                                        Calendar c = Calendar.getInstance();
                                        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                                        //if (!file.exists())
                                        boolean res = file.mkdirs();
                                        File excel = new File(file, "Wallet_Transaction_User" + c.getTimeInMillis() + ".xls");
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
//        dataBinding.btnExportUserData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
//                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//                HSSFWorkbook workbook = new HSSFWorkbook();
//                CollectionReference usersCollection = FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
//                usersCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot colect) {
//                        int finalcolect = 0 ;
//                        for (QueryDocumentSnapshot documentSnapshot : colect) {
//                            finalcolect++;
//                            String userId = documentSnapshot.getId();
//                            HSSFSheet sheet = workbook.createSheet(userId);
//                            HSSFRow row = sheet.createRow(0);
//                            row.createCell(0).setCellValue("ID");
//                            row.createCell(1).setCellValue("Amount");
//                            row.createCell(2).setCellValue("Plan");
//                            row.createCell(3).setCellValue("Date");
//                            usersCollection.document(userId)
//                                    .collection(COLLECTION_WALLET_TRANSACTION)
//                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                @Override
//                                public void onSuccess(QuerySnapshot plans) {
//                                    int i = 0;
//
//                                        for (QueryDocumentSnapshot documentSnapshot2 : plans) {
//                                            i++;
//                                            WalletTransaction referralIncome = documentSnapshot2.toObject(WalletTransaction.class);
//                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//                                            Date dated = new Date();
//                                            dated.setTime(referralIncome.getDate());
//                                            String date = dateFormat.format(dated);
//                                            HSSFRow hssfRow = sheet.createRow(i);
//                                            hssfRow.createCell(0).setCellValue(referralIncome.getId());
//                                            hssfRow.createCell(1).setCellValue(referralIncome.getAmount());
//                                            hssfRow.createCell(2).setCellValue(referralIncome.getPlan());
//                                            hssfRow.createCell(3).setCellValue(date);
//                                        }
//                                    //////
//                                    Calendar c = Calendar.getInstance();
//                                    File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
//                                    //if (!file.exists())
//                                    boolean res = file.mkdirs();
//                                    File excel = new File(file, "Wallet Transactions" + c.getTimeInMillis() + ".xls");
//                                    try {
//                                        excel.createNewFile();
//                                        Environment.getExternalStorageDirectory().setWritable(true);
//                                        FileOutputStream fileOutputStream = new FileOutputStream(excel);
//                                        workbook.write(fileOutputStream);
//                                        Toast.makeText(mainActivity, "Stored at: " + excel.getAbsolutePath(), Toast.LENGTH_LONG).show();
//                                    } catch (FileNotFoundException e) {
//                                        e.printStackTrace();
//                                        Toast.makeText(mainActivity, "FNF " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                        Toast.makeText(mainActivity, "IO " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//
//                                }
//                            });
//                        }
//
//
//                        ///
//                    }
//                });
//            }
//        });
        dataBinding.btnExportUserData.setVisibility(View.VISIBLE);
        viewModel = new ViewModelProvider(this).get(WalletTransactionViewModel.class);
        viewModel.getTransactions().observe(this, new Observer<List<WalletTransaction>>() {
            @Override
            public void onChanged(List<WalletTransaction> walletTransactions) {
                walletTransactionList = walletTransactions;
                if (walletTransactionList.size() > 0) {
                    adapter.setList(walletTransactionList);
                    adapter.notifyDataSetChanged();
                    dataBinding.setIsDataAvailable(true);
                    dataBinding.textTotalIDs.setText("Total IDs: "+walletTransactionList.size());
                }
            }
        });


        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataBinding.recyclerView.setAdapter(adapter);

    }
}
