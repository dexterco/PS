package com.codingbhasha.ps.views.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentApplyLoanBinding;
import com.codingbhasha.ps.databinding.FragmentPayoutRequestBinding;
import com.codingbhasha.ps.model.ApplyLoan;
import com.codingbhasha.ps.views.activities.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_APPLY_LOAN;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class ApplyLoanFragment extends BaseFragment<FragmentApplyLoanBinding>  {
    MainActivity mainActivity;
    String plan;
    CollectionReference planRef;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int noOfDays = 0;
    int amount = 0;
    private Dialog load;


    @Override
    public int getLayoutRes() {
        return R.layout.fragment_apply_loan;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        load = new Dialog(requireActivity());
        load.setContentView(R.layout.load);
        load.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        load.setCancelable(false);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();

      set();

        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                mainActivity.fillData(plan);
                set();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
        mainActivity.dataBinding.toolbarId.setVisibility(View.VISIBLE);
        mainActivity.dataBinding.toolbarValidity.setVisibility(View.VISIBLE);


    }

    private void set(){

        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("plans")
                .document(plan)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    dataBinding.editID.setText(documentSnapshot.get("memberId").toString());
                    amount = Integer.parseInt(documentSnapshot.get("eligibleLoanAmount").toString());
                    dataBinding.textEligibleAmount.setText("\u20B9" + amount);
                    dataBinding.editAmount.setText(String.valueOf(amount));

                    Calendar c = Calendar.getInstance();
                    int month =  c.get(Calendar.MONTH)+1;
                    int Year =  c.get(Calendar.YEAR);
                    String date =  "2/"+month+"/"+Year;
                    dataBinding.textDate.setText("Eligible Advance Income Amount as of "+date);


                    if (plan.equals("PlanA")) {
                        if (Integer.parseInt(documentSnapshot.get("eligibleLoanAmount").toString()) < 10000) {
                            Log.e("Loan",plan +" asa " +documentSnapshot.get("eligibleLoanAmount").toString());
                            dataBinding.containerPlaceholder.setVisibility(View.VISIBLE);
                            dataBinding.containerContent.setVisibility(View.GONE);
                        } else if (Boolean.parseBoolean(documentSnapshot.get("currentLoan").toString())) {

                            dataBinding.btnApply.setEnabled(false);
                            Toast.makeText(mainActivity, "You have an active advance income account.", Toast.LENGTH_SHORT).show();
                            dataBinding.containerPlaceholder.setVisibility(View.GONE);
                            dataBinding.containerContent.setVisibility(View.VISIBLE);
                        } else if ( Boolean.parseBoolean(documentSnapshot.get("requestForloan").toString()) ) {

                            dataBinding.btnApply.setEnabled(false);
                            Toast.makeText(mainActivity, "You already sent advance income request.", Toast.LENGTH_SHORT).show();
                            dataBinding.containerPlaceholder.setVisibility(View.GONE);
                            dataBinding.containerContent.setVisibility(View.VISIBLE);
                        } else{
                            dataBinding.containerPlaceholder.setVisibility(View.GONE);
                            dataBinding.containerContent.setVisibility(View.VISIBLE);
                        }
                    } else if (plan.equals("PlanB")) {
                        if (Integer.parseInt(documentSnapshot.get("eligibleLoanAmount").toString()) < 25000) {
                            dataBinding.containerPlaceholder.setVisibility(View.VISIBLE);
                            dataBinding.containerContent.setVisibility(View.GONE);
                        } else if (Boolean.parseBoolean(documentSnapshot.get("currentLoan").toString())) {
                            dataBinding.btnApply.setEnabled(false);
                            dataBinding.containerPlaceholder.setVisibility(View.GONE);
                            dataBinding.containerContent.setVisibility(View.VISIBLE);
                            Toast.makeText(mainActivity, "You have an active advance income account.", Toast.LENGTH_SHORT).show();
                        }else if ( Boolean.parseBoolean(documentSnapshot.get("requestForloan").toString()) ) {

                            dataBinding.btnApply.setEnabled(false);
                            Toast.makeText(mainActivity, "You already sent advance income request.", Toast.LENGTH_SHORT).show();
                            dataBinding.containerPlaceholder.setVisibility(View.GONE);
                            dataBinding.containerContent.setVisibility(View.VISIBLE);
                        }else{
                            dataBinding.containerPlaceholder.setVisibility(View.GONE);
                            dataBinding.containerContent.setVisibility(View.VISIBLE);
                        }
                    } else if (plan.equals("PlanC")) {
                        if (Integer.parseInt(documentSnapshot.get("eligibleLoanAmount").toString()) < 50000) {
                            dataBinding.containerPlaceholder.setVisibility(View.VISIBLE);
                            dataBinding.containerContent.setVisibility(View.GONE);
                        } else if (Boolean.parseBoolean(documentSnapshot.get("currentLoan").toString())) {
                            dataBinding.btnApply.setEnabled(false);
                            dataBinding.containerPlaceholder.setVisibility(View.GONE);
                            dataBinding.containerContent.setVisibility(View.VISIBLE);
                            Toast.makeText(mainActivity, "You have an active advance income account.", Toast.LENGTH_SHORT).show();
                        }else if ( Boolean.parseBoolean(documentSnapshot.get("requestForloan").toString()) ) {

                            dataBinding.btnApply.setEnabled(false);
                            Toast.makeText(mainActivity, "You already sent advance income request.", Toast.LENGTH_SHORT).show();
                            dataBinding.containerPlaceholder.setVisibility(View.GONE);
                            dataBinding.containerContent.setVisibility(View.VISIBLE);
                        }else{
                            dataBinding.containerPlaceholder.setVisibility(View.GONE);
                            dataBinding.containerContent.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        setup();
    }

    private void setup() {
        List<String> accNumList = new ArrayList<>();
        List<String> ifscList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("bank").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                accNumList.add(documentSnapshot.get("accNumber").toString());
                                ifscList.add(documentSnapshot.get("ifsc").toString());
                            }
                        }
                        if (accNumList.size() > 0) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, accNumList);
                            dataBinding.editAccNumber.setAdapter(adapter);
                            dataBinding.editAccNumber.setText(adapter.getItem(0), false);
                            dataBinding.editIFSC.setText(ifscList.get(0));
                            dataBinding.editAccNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    dataBinding.editIFSC.setText(ifscList.get(i));
                                }
                            });
                        }
                    }
                });
        dataBinding.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataBinding.btnApply.setEnabled(false);
                load.show();

                if (isDataValid()) {
                    submitData();
                } else {
                    dataBinding.btnApply.setEnabled(true);
                    load.dismiss();
                }
            }
        });
    }

    private void submitData() {
        Calendar c = Calendar.getInstance();
        ApplyLoan applyLoan = new ApplyLoan();
        applyLoan.setUniqueId(dataBinding.editID.getEditableText().toString());
        applyLoan.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        applyLoan.setAccNum(dataBinding.editAccNumber.getEditableText().toString());
        applyLoan.setIFSC(dataBinding.editIFSC.getEditableText().toString());
        applyLoan.setAmount(Integer.parseInt(dataBinding.editAmount.getEditableText().toString()));

        WriteBatch batch =  FirebaseFirestore.getInstance().batch();
       DocumentReference reference = FirebaseFirestore.getInstance().collection("admin")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection(COLLECTION_APPLY_LOAN)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection(plan)
                .document();
        batch.set(reference,applyLoan);

        DocumentReference user =  FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("plans").
                document(plan);
        batch.update(user,"requestForloan",true);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mainActivity, "Request Sent!", Toast.LENGTH_SHORT).show();
                set();
                dataBinding.btnApply.setEnabled(true);
                load.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dataBinding.btnApply.setEnabled(true);
                load.dismiss();
                Toast.makeText(mainActivity, "Try again later", Toast.LENGTH_SHORT).show();
                set();
            }
        });
    }

    private boolean isDataValid() {
        if (TextUtils.isEmpty(dataBinding.editAccNumber.getEditableText().toString().trim())) {
            Toast.makeText(mainActivity, "Add Bank Account.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(dataBinding.editAmount.getEditableText().toString().trim())) {
            dataBinding.inputLayoutAmount.setErrorEnabled(true);
            dataBinding.inputLayoutAmount.setError("Required");
            return false;
        } else if (Integer.parseInt(dataBinding.editAmount.getEditableText().toString()) > amount) {
            Toast.makeText(mainActivity, "Looks like you requested an amount more than you are eligible for.", Toast.LENGTH_SHORT).show();
            return false;
        } else if(plan.equals("PlanA")) {
            if(Integer.parseInt(dataBinding.editAmount.getEditableText().toString()) > 500000) {
                Toast.makeText(mainActivity, "5,00,000 is the maximum advance income amount for your plan", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if(plan.equals("PlanB")) {
            if(Integer.parseInt(dataBinding.editAmount.getEditableText().toString()) > 1000000) {
                Toast.makeText(mainActivity, "10,00,000 is the maximum advance income amount for your plan", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if(plan.equals("PlanC")) {
            if(Integer.parseInt(dataBinding.editAmount.getEditableText().toString()) > 1500000) {
                Toast.makeText(mainActivity, "15,00,000 is the maximum advance income amount for your plan", Toast.LENGTH_SHORT).show();
                return false;

            }
        }
        return true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }


}
