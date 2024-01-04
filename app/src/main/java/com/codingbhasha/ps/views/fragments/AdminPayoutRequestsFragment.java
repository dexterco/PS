package com.codingbhasha.ps.views.fragments;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
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
import com.codingbhasha.ps.adapters.recyclerviewadapters.AdminPayoutRequestAdapter;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentAdminPayoutRequestsBinding;
import com.codingbhasha.ps.model.PayputRequest;
import com.codingbhasha.ps.viewmodel.AdminPayoutRequestViewModel;
import com.codingbhasha.ps.views.activities.AdminActivity;

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
@Obfuscate
public class AdminPayoutRequestsFragment extends BaseFragment<FragmentAdminPayoutRequestsBinding> {
    AdminPayoutRequestAdapter adapter;
    AdminPayoutRequestViewModel viewModel;
    AdminActivity mainActivity;
    String plan;
    List<PayputRequest> payputRequestList = new ArrayList<>();
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("Payout Request");
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_admin_payout_requests;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataBinding.btnExportAll.setVisibility(View.VISIBLE);
        adapter = new AdminPayoutRequestAdapter(getActivity());
        viewModel = new ViewModelProvider(getActivity()).get(AdminPayoutRequestViewModel.class);
        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataBinding.recyclerView.setAdapter(adapter);
        dataBinding.btnExportAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                HSSFRow row = sheet.createRow(0);
                row.createCell(0).setCellValue("ID");
                row.createCell(1).setCellValue("Account Number");
                row.createCell(2).setCellValue("IFSC");
                row.createCell(3).setCellValue("Amount");
                row.createCell(4).setCellValue("Date & Time");
                for (int i = 0; i < payputRequestList.size(); i++) {
                    PayputRequest payoutSummary = payputRequestList.get(i);
                    String date = payoutSummary.getDate();
                    HSSFRow hssfRow = sheet.createRow(i + 1);
                    hssfRow.createCell(0).setCellValue(payoutSummary.getUniqueId());
                    hssfRow.createCell(1).setCellValue(payoutSummary.getAccNum());
                    hssfRow.createCell(2).setCellValue(payoutSummary.getIFSC());
                    hssfRow.createCell(3).setCellValue("\u20B9" + payoutSummary.getAmount());
                    hssfRow.createCell(4).setCellValue(date);
                }
                Calendar c = Calendar.getInstance();
                File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                //if (!file.exists())
                boolean res = file.mkdirs();
                File excel = new File(file, "PayoutRequest_" + c.getTimeInMillis() + ".xls");
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

    private void getPayoutReqs() {
        viewModel.getPayoutRequests(plan).observe(getActivity(), new Observer<List<PayputRequest>>() {
            @Override
            public void onChanged(List<PayputRequest> payputRequests) {
                payputRequestList = payputRequests;
                adapter.setList(payputRequestList);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
        getPayoutReqs();
        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                mainActivity.fillData(plan);
                getPayoutReqs();
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
