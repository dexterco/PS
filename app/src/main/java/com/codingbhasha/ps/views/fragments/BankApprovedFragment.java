package com.codingbhasha.ps.views.fragments;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.recyclerviewadapters.BankApprovedAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentBankApprovedBinding;
import com.codingbhasha.ps.model.BankRequest;
import com.codingbhasha.ps.viewmodel.BankApprovedViewModel;
import com.codingbhasha.ps.views.activities.AdminActivity;

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
@Obfuscate
public class BankApprovedFragment extends BaseFragment<FragmentBankApprovedBinding> {
    BankApprovedAdapter adapter;
    List<BankRequest> bankList = new ArrayList<>();
    BankApprovedViewModel viewModel;
    AdminActivity mainActivity;
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("Banks Approved");

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_bank_approved;
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

        adapter = new BankApprovedAdapter();

        viewModel = new ViewModelProvider(getActivity()).get(BankApprovedViewModel.class);
        viewModel.getBanks().observe(getActivity(), new Observer<List<BankRequest>>() {
            @Override
            public void onChanged(List<BankRequest> bankRequests) {
                bankList = bankRequests;
                adapter.setList(bankList);
                dataBinding.recyclerView.setAdapter(adapter);
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

                row.createCell(0).setCellValue("Email");
                row.createCell(1).setCellValue("Account Holder");
                row.createCell(2).setCellValue("Account Number");
                row.createCell(3).setCellValue("IFSC");
                row.createCell(4).setCellValue("Approved On");

                for (int i = 0; i < bankList.size(); i++) {
                    BankRequest bankRequest = bankList.get(i);
                    HSSFRow hssfRow = sheet.createRow(i + 1);

                    hssfRow.createCell(0).setCellValue(bankRequest.getEmail());
                    hssfRow.createCell(1).setCellValue(bankRequest.getAccHolderName());
                    hssfRow.createCell(2).setCellValue(bankRequest.getAccNumber());
                    hssfRow.createCell(3).setCellValue(bankRequest.getIfsc());
                    hssfRow.createCell(4).setCellValue(bankRequest.getDate());
                }
                Calendar c = Calendar.getInstance();
                File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                //if (!file.exists())
                boolean res = file.mkdirs();
                File excel = new File(file, "BankApproved_" + c.getTimeInMillis() + ".xls");
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
}
