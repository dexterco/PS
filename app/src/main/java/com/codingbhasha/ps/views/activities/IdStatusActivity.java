package com.codingbhasha.ps.views.activities;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.adapters.recyclerviewadapters.IdStatusAdapter;
import com.codingbhasha.ps.base.BaseActivity;
import com.codingbhasha.ps.databinding.ActivityIdStatusBinding;
import com.codingbhasha.ps.model.IdStatus;
import com.codingbhasha.ps.viewmodel.IdStatusViewModel;

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
public class IdStatusActivity extends BaseActivity<ActivityIdStatusBinding> {
    IdStatusAdapter adapter;
    IdStatusViewModel viewModel;
    List<IdStatus> idStatusList = new ArrayList<>();
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("Id Statuses");

    @Override
    public int getLayoutResId() {
        return R.layout.activity_id_status;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(dataBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new IdStatusAdapter();
        viewModel = new ViewModelProvider(this).get(IdStatusViewModel.class);

        viewModel.mainActivity = this;
        viewModel.getIdStatuses().observe(this, new Observer<List<IdStatus>>() {
            @Override
            public void onChanged(List<IdStatus> idStatuses) {
                idStatusList = idStatuses;
                adapter.setList(idStatuses);
            }
        });

        dataBinding.recyclerView.setHasFixedSize(true);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataBinding.recyclerView.setAdapter(adapter);

        dataBinding.btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(IdStatusActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(IdStatusActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                HSSFRow row = sheet.createRow(0);
                row.createCell(0).setCellValue("ID");
                row.createCell(1).setCellValue("Plan");
                row.createCell(2).setCellValue("Total ids");
                row.createCell(3).setCellValue("Validity");
                row.createCell(4).setCellValue("Balance");
                row.createCell(5).setCellValue("Date");
                for (int i = 0; i < idStatusList.size(); i++) {
                    IdStatus idStatus = idStatusList.get(i);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dated = new Date();
                    dated.setTime(Long.parseLong(idStatus.getDateOfJoining()));
                    String date = dateFormat.format(dated);
                    HSSFRow hssfRow = sheet.createRow(i + 1);
                    hssfRow.createCell(0).setCellValue(idStatus.getMemberId());
                    hssfRow.createCell(1).setCellValue(idStatus.getPlan());
                    hssfRow.createCell(2).setCellValue(idStatus.getTotalIds());
                    hssfRow.createCell(3).setCellValue(idStatus.getValidity());
                    hssfRow.createCell(4).setCellValue("\u20B9" + idStatus.getWalletBalance());
                    hssfRow.createCell(5).setCellValue(date);
                }
                Calendar c = Calendar.getInstance();
                File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
                //if (!file.exists())
                boolean res = file.mkdirs();
                File excel = new File(file, "IdStatuses_" + c.getTimeInMillis() + ".xls");
                try {
                    excel.createNewFile();
                    Environment.getExternalStorageDirectory().setWritable(true);
                    FileOutputStream fileOutputStream = new FileOutputStream(excel);
                    workbook.write(fileOutputStream);
                    Toast.makeText(IdStatusActivity.this, "Stored at: " + excel.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(IdStatusActivity.this, "FNF " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(IdStatusActivity.this, "IO " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
