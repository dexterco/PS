package com.codingbhasha.ps.views.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentLoanAccountBinding;
import com.codingbhasha.ps.model.LoanAccount;
import com.codingbhasha.ps.views.activities.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_LOAN_ACCOUNT;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class LoanAccountFragment extends BaseFragment<FragmentLoanAccountBinding> {
    MainActivity mainActivity;
    String plan;
    CollectionReference loanAccRef;
    DocumentReference userDoc;

    List<String> loanIdList;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_loan_account;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.dataBinding.toolbarId.setVisibility(View.VISIBLE);
        mainActivity.dataBinding.toolbarValidity.setVisibility(View.VISIBLE);
        mainActivity.dataBinding.spinnerPlan.setVisibility(View.VISIBLE);


    }

    private void setup() {
        ProgressDialog progressDialog = new ProgressDialog(mainActivity);
        progressDialog.show();
        loanAccRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("plans").document(plan)
                .collection(COLLECTION_LOAN_ACCOUNT);
        userDoc = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("plans")
                .document(plan);

        loanIdList = new ArrayList<>();

        loanAccRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    loanIdList.add(documentSnapshot.getId());
                }

                if(loanIdList.size() == 0) {
                    dataBinding.containerPlaceholder.setVisibility(View.VISIBLE);
                    dataBinding.containerContent.setVisibility(View.GONE);
                } else {
                    dataBinding.containerPlaceholder.setVisibility(View.GONE);
                    dataBinding.containerContent.setVisibility(View.VISIBLE);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, loanIdList);
                dataBinding.spinnerLoanId.setAdapter(adapter);
                dataBinding.spinnerLoanId.setSelection(0);
                dataBinding.spinnerLoanId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ((TextView) view).setTextColor(getResources().getColor(R.color.teal_700));
                        String loanId = adapterView.getItemAtPosition(i).toString();
                        DocumentReference docRef = loanAccRef.document(loanId);
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                LoanAccount loanAccount = documentSnapshot.toObject(LoanAccount.class);
                                Date date = new Date(loanAccount.getDate());
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                                if (loanAccount.getEnd_date()!=0){
                                Date enddate = new Date(loanAccount.getEnd_date());
                                    String enddated = dateFormat.format(enddate);
                                    dataBinding.textEndDate.setText(enddated);
                                }
                                else {
                                    dataBinding.textEndDate.setText("Account Active");
                                }


                                String dated = dateFormat.format(date);
                                dataBinding.textDate.setText(dated);
                                dataBinding.setLoanAccount(loanAccount);
                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });
        progressDialog.dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
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
