package com.codingbhasha.ps.views.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.codingbhasha.ps.databinding.FragmentPayoutRequestBinding;
import com.codingbhasha.ps.model.PayoutSummary;
import com.codingbhasha.ps.model.PayputRequest;
import com.codingbhasha.ps.views.activities.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_PAYOUT_REQUEST;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_PAYOUT_SUMMARY;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class PayoutRequestFragment extends BaseFragment<FragmentPayoutRequestBinding> {
    MainActivity mainActivity;
    String plan;
    CollectionReference planRef;
    int walletBalance = 0;
    int totals[] = new int[]{0, 0, 0, 0, 0, 0};
    private int referralIncome, monthlyIncome,
            monthlyBonus,
            levelAchievementBonus,
            totalWallet,
            totalRefunds, totalPayouts;
    Map<String, Object> walletMap = new HashMap<>();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private boolean re;
    private int validity;
    private Dialog load;
    private int totalLoanRecoveyAmt;


    public PayoutRequestFragment(Context context) {
        mainActivity = (MainActivity) context;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_payout_request;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        load = new Dialog(requireActivity());
        load.setContentView(R.layout.load);
        load.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        load.setCancelable(false);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();

        preferences = mainActivity.getSharedPreferences("payoutPref", Context.MODE_PRIVATE);
        editor = preferences.edit();


        dataBinding.editAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataBinding.inputLayoutAmount.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void getBalance() {

        planRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("plans");
        //planRef.document(plan).set(map, SetOptions.merge());
        planRef.document(plan).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        dataBinding.btnSubmit.setEnabled(true);
                        if (Boolean.parseBoolean(documentSnapshot.get("currentLoan").toString())) {
                            dataBinding.btnSubmit.setEnabled(false);
                            Toast.makeText(mainActivity, "You have an active loan.", Toast.LENGTH_SHORT).show();

                        }

                        dataBinding.editID.setText(documentSnapshot.get("memberId").toString());
                        validity = Integer.parseInt(documentSnapshot.get("validity").toString());
                        referralIncome = Integer.parseInt(documentSnapshot.get("totalReferralIncome").toString());
                        monthlyIncome = Integer.parseInt(documentSnapshot.get("totalMonthlyIncome").toString());
                        monthlyBonus = Integer.parseInt(documentSnapshot.get("totalMonthlyBonus").toString());
                        levelAchievementBonus = Integer.parseInt(documentSnapshot.get("totalLevelAchievementBonus").toString());
                        totalWallet = Integer.parseInt(documentSnapshot.get("totalWallet").toString());
                        totalRefunds = Integer.parseInt(documentSnapshot.get("totalRefunds").toString());
                        totalPayouts = Integer.parseInt(documentSnapshot.get("totalPayouts").toString());
                        totalLoanRecoveyAmt = Integer.parseInt(documentSnapshot.get("totalLoanRecoveyAmt").toString());

                        Log.e("basaas ", "ri =>" + referralIncome + " mi =>" + monthlyIncome + " mb =>" + monthlyBonus + " la =>" + levelAchievementBonus + " tw =>" + totalWallet + " tr =>" + totalRefunds + " tp =>" + totalPayouts);

                        int earnBalance = referralIncome + monthlyBonus + monthlyIncome + levelAchievementBonus + totalRefunds;
                        int getBalance = totalWallet + totalPayouts + totalLoanRecoveyAmt;
                        int total = earnBalance - getBalance;
                        walletBalance = total;
                        dataBinding.textBalance.setText("Wallet Balance: \u20B9" + total);

                    }
                });

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
                                Log.e("Sasasd", String.valueOf(documentSnapshot));
                                accNumList.add(documentSnapshot.get("accNumber").toString());
                                ifscList.add(documentSnapshot.get("ifsc").toString());
                            }
                        }
                        if (accNumList.size() > 0) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, accNumList);
                            dataBinding.spinnerAccNumber.setAdapter(adapter);
                            dataBinding.spinnerAccNumber.setText(adapter.getItem(0), false);
                            dataBinding.editIFSC.setText(ifscList.get(0));
                            dataBinding.spinnerAccNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    dataBinding.editIFSC.setText(ifscList.get(i));
                                }
                            });
                        }
                    }
                });
        dataBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load.show();
                if (isDataValid()) {

                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                            .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                            .collection("plans")
                            .document(plan).collection(COLLECTION_PAYOUT_SUMMARY).whereEqualTo("transactionID", "Pending").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.isEmpty()) {
                                planRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("plans");
                                //planRef.document(plan).set(map, SetOptions.merge());
                                planRef.document(plan).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                referralIncome = Integer.parseInt(documentSnapshot.get("totalReferralIncome").toString());
                                                monthlyIncome = Integer.parseInt(documentSnapshot.get("totalMonthlyIncome").toString());
                                                monthlyBonus = Integer.parseInt(documentSnapshot.get("totalMonthlyBonus").toString());
                                                levelAchievementBonus = Integer.parseInt(documentSnapshot.get("totalLevelAchievementBonus").toString());
                                                totalWallet = Integer.parseInt(documentSnapshot.get("totalWallet").toString());
                                                totalRefunds = Integer.parseInt(documentSnapshot.get("totalRefunds").toString());
                                                totalPayouts = Integer.parseInt(documentSnapshot.get("totalPayouts").toString());
                                                totalLoanRecoveyAmt = Integer.parseInt(documentSnapshot.get("totalLoanRecoveyAmt").toString());
                                                Log.e("basaas ", "ri =>" + referralIncome + " mi =>" + monthlyIncome + " mb =>" + monthlyBonus + " la =>" + levelAchievementBonus + " tw =>" + totalWallet + " tr =>" + totalRefunds + " tp =>" + totalPayouts);

                                                int earnBalance = referralIncome + monthlyBonus + monthlyIncome + levelAchievementBonus + totalRefunds;
                                                int getBalance = totalWallet + totalPayouts+totalLoanRecoveyAmt;
                                                int total = earnBalance - getBalance;
                                                if (Integer.parseInt(dataBinding.editAmount.getEditableText().toString().trim()) > total) {
                                                    Toast.makeText(mainActivity, "Looks like you dont have enough balance to request this amount", Toast.LENGTH_SHORT).show();
                                                    re = false;
                                                    load.dismiss();
                                                } else {
                                                    submitData();

                                                }


                                            }
                                        });


                            } else {
                                re = false;
load.dismiss();
                                Toast.makeText(mainActivity, "You have one or many Pending Payout Reqests", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }

            }
        });
    }

    private void submitData() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(c.getTime());
        PayputRequest payputRequest = new PayputRequest();
        payputRequest.setUniqueId(dataBinding.editID.getEditableText().toString().trim());
        payputRequest.setAccNum(dataBinding.spinnerAccNumber.getEditableText().toString().trim());
        payputRequest.setIFSC(dataBinding.editIFSC.getEditableText().toString().trim());
        payputRequest.setAmount(Integer.parseInt(dataBinding.editAmount.getEditableText().toString().trim()));
        payputRequest.setDate(c.getTimeInMillis());
        payputRequest.setPlan(plan);

        WriteBatch batch = FirebaseFirestore.getInstance().batch();



        CollectionReference getAdmin = FirebaseFirestore.getInstance().collection("admin")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection(COLLECTION_PAYOUT_REQUEST)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection(plan);
        String ID = getAdmin.document().getId();

        batch.set(getAdmin.document(ID),payputRequest);

        DocumentReference user = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("plans")
                .document(plan);

        PayoutSummary payoutSummary = new PayoutSummary();
        payoutSummary.setAmount(Integer.parseInt(dataBinding.editAmount.getEditableText().toString().trim()));
        payoutSummary.setTransactionID("Pending");
        payoutSummary.setAccNum(payputRequest.getAccNum());
        payoutSummary.setDate(c.getTimeInMillis());
        payoutSummary.setUserID(dataBinding.editID.getText().toString().trim());

        batch.set(user.collection(COLLECTION_PAYOUT_SUMMARY).document(ID),payoutSummary);

        batch.update(user,"totalPayouts", FieldValue.increment(Long.parseLong(dataBinding.editAmount.getEditableText().toString().trim())));
        batch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mainActivity, "Try Again later", Toast.LENGTH_SHORT).show();
                load.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mainActivity, "Payout Request Sent!", Toast.LENGTH_SHORT).show();
                dataBinding.editAmount.setText("");
                getBalance();
                load.dismiss();
            }
        });
    }

    private boolean isDataValid() {
        re = false;
        //int amount = Integer.parseInt(dataBinding.editAmount.getEditableText().toString().trim());
        if (TextUtils.isEmpty(dataBinding.editIFSC.getEditableText().toString().trim())) {
            Toast.makeText(mainActivity, "Add a bank account.", Toast.LENGTH_SHORT).show();
            re = false;
            load.dismiss();
            return re;
        } else if (TextUtils.isEmpty(dataBinding.editAmount.getEditableText().toString().trim())) {
            dataBinding.inputLayoutAmount.setErrorEnabled(true);
            dataBinding.inputLayoutAmount.setError("Required");
            load.dismiss();
            re = false;
            return re;
        } else if (Integer.parseInt(dataBinding.editAmount.getEditableText().toString().trim()) > walletBalance) {
            Toast.makeText(mainActivity, "Looks like you dont have enough balance to request this amount", Toast.LENGTH_SHORT).show();
            load.dismiss();
            re = false;
            return re;
        } else if (Integer.parseInt(dataBinding.editAmount.getEditableText().toString().trim()) < 500) {
            if (validity <= 0) {
                re = true;
                return re;
            } else {
                Toast.makeText(mainActivity, "Minimum request is \u20B9500", Toast.LENGTH_SHORT).show();
                load.dismiss();
                re = false;
                return re;
            }
        }

        return true;


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
        getBalance();
        setup();
        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                getBalance();
                setup();
                mainActivity.fillData(plan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
