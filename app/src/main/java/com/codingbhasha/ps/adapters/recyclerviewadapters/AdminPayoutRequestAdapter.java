package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemAdminPayoutRequestBinding;
import com.codingbhasha.ps.model.PayoutSummary;
import com.codingbhasha.ps.model.PayputRequest;
import com.codingbhasha.ps.views.activities.AdminActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_PAYOUT_REQUEST;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_PAYOUT_SUMMARY;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class AdminPayoutRequestAdapter extends RecyclerView.Adapter<AdminPayoutRequestViewHolder> {
    List<PayputRequest> payputRequestList = new ArrayList<>();
    Context context;
    AdminActivity mainActivity;
    String plan;
    private Dialog load;

    public AdminPayoutRequestAdapter(Context context) {
        this.context = context;
        mainActivity = (AdminActivity) context;
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
    }

    @NonNull
    @Override
    public AdminPayoutRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemAdminPayoutRequestBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_admin_payout_request, parent, false);
        load = new Dialog(mainActivity);
        load.setContentView(R.layout.load);
        load.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        load.setCancelable(false);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        return new AdminPayoutRequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPayoutRequestViewHolder holder, int position) {
        PayputRequest currentItem = payputRequestList.get(position);
        holder.binding.setPayoutReq(currentItem);
        holder.binding.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approveReq(currentItem);
            }
        });
        holder.binding.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load.show();
                //Reject request
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                DocumentReference delete_admin =   FirebaseFirestore.getInstance().collection("admin")
                        .document(currentItem.getEmail())
                        .collection(COLLECTION_PAYOUT_REQUEST)
                        .document(currentItem.getEmail())
                        .collection(plan)
                        .document(currentItem.getDocid());

                batch.delete(delete_admin);
                DocumentReference update_payout  = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                        .document(currentItem.getEmail())
                        .collection("plans")
                        .document(plan);
                batch.update(update_payout,"totalPayouts" , FieldValue.increment(-currentItem.getAmount()));

                DocumentReference update_payout_reqest =update_payout.collection(COLLECTION_PAYOUT_SUMMARY)
                        .document(currentItem.getDocid());
                batch.update(update_payout_reqest,"transactionID" , "Rejected");
                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        load.dismiss();
                        payputRequestList.remove(currentItem);
                        setList(payputRequestList);
                        Toast.makeText(context, "Request is Rejected", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        load.dismiss();
                        Toast.makeText(context, "Try again later", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void approveReq(PayputRequest currentItem) {
        //Approve request
        View v = LayoutInflater.from(context).inflate(R.layout.layout_payout_request, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(v)
                .create();
        dialog.show();
        TextInputLayout inputLayoutTransID = v.findViewById(R.id.inputLayoutTransactionID);
        TextInputEditText editTransID = v.findViewById(R.id.editID);
        TextInputLayout inputLayoutAmount = v.findViewById(R.id.inputLayoutAmount);
        TextInputEditText editAmount = v.findViewById(R.id.editAmount);
        ImageButton btnDone = v.findViewById(R.id.btnDone);
        editTransID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputLayoutTransID.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editAmount.setText(String.valueOf(currentItem.getAmount()));
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editTransID.getEditableText().toString().trim())) {
                    inputLayoutTransID.setErrorEnabled(true);
                    inputLayoutTransID.setError("Required");
                    return;
                } else if (TextUtils.isEmpty(editAmount.getEditableText().toString().trim())) {
                    inputLayoutAmount.setErrorEnabled(true);
                    inputLayoutAmount.setError("Required");
                    return;
                } else {
                    load.show();
                    WriteBatch batch =  FirebaseFirestore.getInstance().batch();
                    PayoutSummary payoutSummary = new PayoutSummary();
                    payoutSummary.setTransactionID(editTransID.getEditableText().toString().trim());
                    payoutSummary.setAmount(Integer.parseInt(editAmount.getEditableText().toString().trim()));
                    payoutSummary.setDate(Calendar.getInstance().getTimeInMillis());
                    payoutSummary.setAccNum(currentItem.getAccNum());
                    payoutSummary.setUserID(currentItem.getUniqueId());

                    DocumentReference update_payout_Request = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                            .document(currentItem.getEmail())
                            .collection("plans")
                            .document(plan)
                            .collection(COLLECTION_PAYOUT_SUMMARY)
                            .document(currentItem.getDocid());
                    batch.update(update_payout_Request,"transactionID" , editTransID.getEditableText().toString().trim());




                    DocumentReference update_payout =   FirebaseFirestore.getInstance().collection("admin")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                            .collection(COLLECTION_PAYOUT_SUMMARY)
                            .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                            .collection(plan)
                            .document(currentItem.getDocid());
                    batch.set(update_payout,payoutSummary);

                    DocumentReference Delete = FirebaseFirestore.getInstance().collection("admin")
                            .document(currentItem.getEmail())
                            .collection(COLLECTION_PAYOUT_REQUEST)
                            .document(currentItem.getEmail())
                            .collection(plan)
                            .document(currentItem.getDocid());

                    batch.delete(Delete);

                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            payputRequestList.remove(currentItem);
                            setList(payputRequestList);
                            Toast.makeText(context, "Approved!", Toast.LENGTH_SHORT).show();
                            load.dismiss();
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                   dialog.dismiss();
                            Toast.makeText(context, "Try again!", Toast.LENGTH_SHORT).show();
                            load.dismiss();

                        }
                    });



                    FirebaseFirestore.getInstance().collection("admin")
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot.exists()) {
                                    String docId = documentSnapshot.getId();
                                    FirebaseFirestore.getInstance().collection("admin")
                                            .document(docId)
                                            .collection(COLLECTION_PAYOUT_REQUEST)
                                            .document(docId)
                                            .collection(plan)
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                                if (documentSnapshot1.exists()) {
                                                    String reqId = documentSnapshot1.getId();
                                                    Calendar c = Calendar.getInstance();
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                    String date = dateFormat.format(c.getTime());
                                                    PayputRequest payputRequest = documentSnapshot1.toObject(PayputRequest.class);
                                                    if (payputRequest.getAccNum().equals(currentItem.getAccNum())) {
                                                        Log.e("reee",reqId);
                                                        //Add to payout summary

                                                    }

                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return payputRequestList.size();
    }

    public void setList(List<PayputRequest> payputRequestList) {
        this.payputRequestList = payputRequestList;
        notifyDataSetChanged();
    }
}
@Obfuscate
class AdminPayoutRequestViewHolder extends RecyclerView.ViewHolder {

    LayoutItemAdminPayoutRequestBinding binding;

    public AdminPayoutRequestViewHolder(LayoutItemAdminPayoutRequestBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
