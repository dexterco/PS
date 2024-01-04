package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemLoanRequestBinding;
import com.codingbhasha.ps.model.ApplyLoan;
import com.codingbhasha.ps.model.LoanAccount;
import com.codingbhasha.ps.views.activities.AdminActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_APPLY_LOAN;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_LOAN_ACCOUNT;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class LoanRequestsAdapter extends RecyclerView.Adapter<LoanRequestsViewHolder> {
    List<ApplyLoan> applyLoanList = new ArrayList<>();
    AdminActivity mainActivity;
    String plan = "";
    private Dialog load;

    public LoanRequestsAdapter(Context context) {
        mainActivity = (AdminActivity) context;
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
    }

    @NonNull
    @Override
    public LoanRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemLoanRequestBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_loan_request, parent, false);
        load = new Dialog(mainActivity);
        load.setContentView(R.layout.load);
        load.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        load.setCancelable(false);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        return new LoanRequestsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanRequestsViewHolder holder, int position) {


        ApplyLoan currentItem = applyLoanList.get(position);
        holder.binding.setApplyLoan(currentItem);
        holder.binding.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approve(currentItem);
            }
        });
        holder.binding.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                DocumentReference delete = FirebaseFirestore.getInstance().collection("admin")
                        .document(currentItem.getEmail())
                        .collection(COLLECTION_APPLY_LOAN)
                        .document(currentItem.getEmail())
                        .collection(plan).document(currentItem.getDocId());

                batch.delete(delete);
                DocumentReference user = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                        .document(currentItem.getEmail())
                        .collection("plans").
                                document(plan);
                batch.update(user, "requestForloan", false);

                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        applyLoanList.remove(currentItem);
                        setList(applyLoanList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mainActivity, "Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });
    }

    private void approve(ApplyLoan currentItem) {
        View v = LayoutInflater.from(mainActivity).inflate(R.layout.layout_loan_request, null);
        AlertDialog dialog = new AlertDialog.Builder(mainActivity)
                .setView(v)
                .create();
        dialog.show();
        TextInputLayout inputLayoutLoanID = v.findViewById(R.id.inputLayoutLoanID);
        TextInputEditText editLoanID = v.findViewById(R.id.editID);
        TextInputLayout inputLayoutAmount = v.findViewById(R.id.inputLayoutAmount);
        TextInputEditText editAmount = v.findViewById(R.id.editAmount);
        ImageButton btnDone = v.findViewById(R.id.btnDone);
        editLoanID.setText(String.valueOf("PS"+Calendar.getInstance().getTimeInMillis()));
        editAmount.setText(String.valueOf(currentItem.getAmount()));
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editLoanID.getEditableText().toString().trim())) {
                    inputLayoutLoanID.setErrorEnabled(true);
                    inputLayoutLoanID.setError("Required");
                    return;
                } else if (TextUtils.isEmpty(editAmount.getEditableText().toString().trim())) {
                    inputLayoutAmount.setErrorEnabled(true);
                    inputLayoutAmount.setError("Required");
                    return;
                } else {
                    load.show();

                    WriteBatch batch = FirebaseFirestore.getInstance().batch();
                    long start = Calendar.getInstance().getTimeInMillis();
                    LoanAccount loanAccount = new LoanAccount();
                    loanAccount.setLoanId(editLoanID.getEditableText().toString().trim());
                    loanAccount.setDate(start);
                    loanAccount.setEnd_date(0);
                    loanAccount.setLoanAmt(Integer.parseInt(editAmount.getEditableText().toString().trim()));
                    loanAccount.setAccNum(currentItem.getAccNum());
                    loanAccount.setIFSC(currentItem.getIFSC());
                    loanAccount.setRecoveredAmt(0);
                    loanAccount.setOutstandingAmt(Integer.parseInt(editAmount.getEditableText().toString().trim()));
                    loanAccount.setStatus("Active");

                    DocumentReference loanAcount = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                            .document(currentItem.getEmail())
                            .collection("plans")
                            .document(plan)
                            .collection(COLLECTION_LOAN_ACCOUNT)
                            .document(editLoanID.getEditableText().toString().trim());

                    batch.set(loanAcount, loanAccount);

                    Map<String, Object> loanMap = new HashMap<>();
                    loanMap.put("currentLoan", true);
                    loanMap.put("currentLoanID", editLoanID.getEditableText().toString().trim());
                    loanMap.put("loanStartDate", start);
                    loanMap.put("loanEndDate", 0);
                    loanMap.put("requestForloan", false);
                    loanMap.put("eligibleLoanAmount",0);


                    DocumentReference user = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                            .document(currentItem.getEmail())
                            .collection("plans")
                            .document(plan);
                    batch.update(user, loanMap);

                    DocumentReference delete = FirebaseFirestore.getInstance().collection("admin")
                            .document(currentItem.getEmail())
                            .collection(COLLECTION_APPLY_LOAN)
                            .document(currentItem.getEmail())
                            .collection(plan).document(currentItem.getDocId());
                    batch.delete(delete);
                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            applyLoanList.remove(currentItem);
                            setList(applyLoanList);
                            Toast.makeText(mainActivity, "Approved!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            load.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mainActivity, "Try Again Later!", Toast.LENGTH_SHORT).show();
                            load.dismiss();
                        }
                    });



                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return applyLoanList.size();
    }

    public void setList(List<ApplyLoan> applyLoanList) {
        this.applyLoanList = applyLoanList;
        notifyDataSetChanged();
    }
}

class LoanRequestsViewHolder extends RecyclerView.ViewHolder {

    LayoutItemLoanRequestBinding binding;

    public LoanRequestsViewHolder(LayoutItemLoanRequestBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
