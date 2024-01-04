package com.codingbhasha.ps.views.activities;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.base.BaseActivity;
import com.codingbhasha.ps.databinding.ActivityReferAndEarnBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_WALLET_TRANSACTION;
@Obfuscate
public class ReferAndEarnActivity extends BaseActivity<ActivityReferAndEarnBinding> {
    FirebaseAuth auth;
    CollectionReference usersCollection = FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    DocumentReference docRef;
    CollectionReference planRef;
    List<String> emailList = new ArrayList<>();

    List<String> planList;
    boolean exists = false;
    long walletBalance = 0;
    private long referralIncome, monthlyIncome,
            monthlyBonus,
            levelAchievementBonus,
            totalWallet,
            totalRefunds, totalPayouts;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private long totalLoanRecoveyAmt;
    private Dialog load;


    @Override
    public int getLayoutResId() {
        return R.layout.activity_refer_and_earn;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load = new Dialog(this);
        load.setContentView(R.layout.load);
        load.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        load.setCancelable(false);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        auth = FirebaseAuth.getInstance();

        setSupportActionBar(dataBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = getSharedPreferences("referPref", Context.MODE_PRIVATE);
        editor = preferences.edit();
        dataBinding.toolbar.setTitle("Refer and Earn");

        planList = new ArrayList<>();
        docRef = usersCollection.document(auth.getCurrentUser().getEmail());
        planRef = docRef.collection("plans");
        planRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.getId().equals("PlanA")) {
                            planList.add("PlanA");
                        } else if (documentSnapshot.getId().equals("PlanB")) {
                            planList.add("PlanB");
                        } else if (documentSnapshot.getId().equals("PlanC")) {
                            planList.add("PlanC");
                        }
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ReferAndEarnActivity.this, android.R.layout.simple_list_item_1, planList);
                dataBinding.planSpinner.setAdapter(adapter);
                dataBinding.planSpinner.setText(dataBinding.planSpinner.getAdapter().getItem(0).toString(), false);
                dataBinding.planSpinner.setSelection(0);

                getBalance();

                dataBinding.planSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String planSelected = adapterView.getItemAtPosition(i).toString();

                        getBalance();
                    }
                });
            }
        });

        List<String> paymentMode = new ArrayList<>();
        paymentMode.add("Wallet");
        paymentMode.add("Online");
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, paymentMode);
        dataBinding.paymentModeSpinner.setAdapter(paymentAdapter);
        dataBinding.paymentModeSpinner.setText(dataBinding.paymentModeSpinner.getAdapter().getItem(0).toString(), false);
        dataBinding.paymentModeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (dataBinding.paymentModeSpinner.getText().toString().equals("Wallet"))
                    dataBinding.containerOnline.setVisibility(View.GONE);
                else {
                    dataBinding.containerOnline.setVisibility(View.VISIBLE);
                    List<String> onlinePayment = new ArrayList<>();
                    onlinePayment.add("UPI");
                    onlinePayment.add("Netbanking");
                    ArrayAdapter<String> onlineAdapter = new ArrayAdapter<>(ReferAndEarnActivity.this, android.R.layout.simple_list_item_1, onlinePayment);
                    dataBinding.onlinePaymentModeSpinner.setAdapter(onlineAdapter);
                    dataBinding.onlinePaymentModeSpinner.setText(dataBinding.onlinePaymentModeSpinner.getAdapter().getItem(0).toString(), false);
                }
            }
        });

        textWatchers();

        notRequested();

        dataBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataBinding.btnSubmit.setEnabled(false);
                load.show();
                if (isDataValid()) {
                    String plan = dataBinding.planSpinner.getEditableText().toString().trim();
                    long planAmt = Long.parseLong(dataBinding.editPlanAmount.getEditableText().toString().trim());
                    //planRef.document(plan).set(map, SetOptions.merge());
                    if (dataBinding.paymentModeSpinner.getEditableText().toString().equals("Wallet")) {
                        planRef.document(plan).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        referralIncome = Long.parseLong(documentSnapshot.get("totalReferralIncome").toString());
                                        monthlyIncome = Long.parseLong(documentSnapshot.get("totalMonthlyIncome").toString());
                                        monthlyBonus = Long.parseLong(documentSnapshot.get("totalMonthlyBonus").toString());
                                        levelAchievementBonus = Long.parseLong(documentSnapshot.get("totalLevelAchievementBonus").toString());
                                        totalWallet = Long.parseLong(documentSnapshot.get("totalWallet").toString());
                                        totalRefunds = Long.parseLong(documentSnapshot.get("totalRefunds").toString());
                                        totalPayouts = Long.parseLong(documentSnapshot.get("totalPayouts").toString());
                                        totalLoanRecoveyAmt = Long.parseLong(documentSnapshot.get("totalLoanRecoveyAmt").toString());
                                        Log.e("basaas ", "ri =>" + referralIncome + " mi =>" + monthlyIncome + " mb =>" + monthlyBonus + " la =>" + levelAchievementBonus + " tw =>" + totalWallet + " tr =>" + totalRefunds + " tp =>" + totalPayouts);

                                        long earnBalance = referralIncome + monthlyBonus + monthlyIncome + levelAchievementBonus + totalRefunds;
                                        long getBalance = totalWallet + totalPayouts +totalLoanRecoveyAmt;
                                        long total = earnBalance - getBalance;
                                        if (planAmt > total) {
                                            dataBinding.btnSubmit.setEnabled(true);
                                            load.dismiss();
                                            Toast.makeText(ReferAndEarnActivity.this, "Looks like you dont have enough balance to request this amount", Toast.LENGTH_SHORT).show();


                                        } else {

                                            planRef.document(plan).update("totalWallet", FieldValue.increment(planAmt));
                                            submitData();

                                        }


                                    }
                                });
                    } else
                    {
                        submitData();
                    }



                }
                else {
                    dataBinding.btnSubmit.setEnabled(true);
                    load.dismiss();
                }
            }
        });
    }

    private void getBalance() {
        String plan = dataBinding.planSpinner.getEditableText().toString().trim();
        planRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("plans");
        //planRef.document(plan).set(map, SetOptions.merge());
        planRef.document(plan).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (Boolean.parseBoolean(documentSnapshot.get("currentLoan").toString())) {
                            Toast.makeText(ReferAndEarnActivity.this, "You have an active loan.", Toast.LENGTH_SHORT).show();
                            dataBinding.spinnerPaymentMode.setClickable(false);
                            dataBinding.spinnerPaymentMode.setFocusable(false);
                            dataBinding.spinnerPaymentMode.setEnabled(false);
                            dataBinding.spinnerPaymentMode.setActivated(false);



                            dataBinding.paymentModeSpinner.setText(dataBinding.paymentModeSpinner.getAdapter().getItem(1).toString(), false);
                            dataBinding.containerOnline.setVisibility(View.VISIBLE);
                            List<String> onlinePayment = new ArrayList<>();
                            onlinePayment.add("UPI");
                            onlinePayment.add("Netbanking");
                            ArrayAdapter<String> onlineAdapter = new ArrayAdapter<>(ReferAndEarnActivity.this, android.R.layout.simple_list_item_1, onlinePayment);
                            dataBinding.onlinePaymentModeSpinner.setAdapter(onlineAdapter);
                            dataBinding.onlinePaymentModeSpinner.setText(dataBinding.onlinePaymentModeSpinner.getAdapter().getItem(0).toString(), false);
                        } else
                        {
                            dataBinding.spinnerPaymentMode.setClickable(true);
                            dataBinding.spinnerPaymentMode.setFocusable(true);
                            dataBinding.spinnerPaymentMode.setEnabled(true);
                            dataBinding.spinnerPaymentMode.setActivated(true);
                            dataBinding.paymentModeSpinner.setText(dataBinding.paymentModeSpinner.getAdapter().getItem(0).toString(), false);
                            dataBinding.containerOnline.setVisibility(View.GONE);
                        }


                        dataBinding.editID.setText(documentSnapshot.get("memberId").toString());
                        referralIncome = Long.parseLong(documentSnapshot.get("totalReferralIncome").toString());
                        monthlyIncome = Long.parseLong(documentSnapshot.get("totalMonthlyIncome").toString());
                        monthlyBonus = Long.parseLong(documentSnapshot.get("totalMonthlyBonus").toString());
                        levelAchievementBonus = Long.parseLong(documentSnapshot.get("totalLevelAchievementBonus").toString());
                        totalWallet = Long.parseLong(documentSnapshot.get("totalWallet").toString());
                        totalRefunds = Long.parseLong(documentSnapshot.get("totalRefunds").toString());
                        totalPayouts = Long.parseLong(documentSnapshot.get("totalPayouts").toString());
                        totalLoanRecoveyAmt = Long.parseLong(documentSnapshot.get("totalLoanRecoveyAmt").toString());
                        Log.e("basaas ", "ri =>" + referralIncome + " mi =>" + monthlyIncome + " mb =>" + monthlyBonus + " la =>" + levelAchievementBonus + " tw =>" + totalWallet + " tr =>" + totalRefunds + " tp =>" + totalPayouts);

                        long earnBalance = referralIncome + monthlyBonus + monthlyIncome + levelAchievementBonus + totalRefunds;
                        long getBalance = totalWallet + totalPayouts + totalLoanRecoveyAmt;
                        long total = earnBalance - getBalance;
                        walletBalance = total;
                        dataBinding.textBalance.setText("" + total);

                    }
                });
fillDetails();
    }


    private void notRequested() {
        FirebaseFirestore.getInstance().collection("admin")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.exists()) {
                        String docId = documentSnapshot.getId();
                        FirebaseFirestore.getInstance().collection("admin")
                                .document(docId).collection("Refer")
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                    if (documentSnapshot1.exists()) {
                                        String newEmail = documentSnapshot1.get("newUserEmail").toString();
                                        String plan = documentSnapshot1.get("plan").toString();
                                        emailList.add(newEmail + plan);
                                    }
                                }


                            }
                        });
                    }
                }


            }
        });

    }

    private void fillDetails() {
        //Toast.makeText(this, "" + totals[0], Toast.LENGTH_SHORT).show();
        //Log.i("TAG", "fillDetails: "+walletBalance);
        String plan = dataBinding.planSpinner.getEditableText().toString().trim();
        //planRef = docRef.collection(plan);
        planRef.document(plan).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //dataBinding.textBalance.setText(documentSnapshot.get("balance").toString());
                if (documentSnapshot.contains("memberId"))
                    dataBinding.editID.setText(documentSnapshot.get("memberId").toString());
                else
                    dataBinding.editID.setText("Default Value"); //Set Later

                dataBinding.inputLayoutID.setEnabled(false);
                dataBinding.inputLayoutPlanAmount.setEnabled(false);
                if (plan.equals("PlanA")) {
                    dataBinding.editPlanAmount.setText("2000");
                } else if (plan.equals("PlanB")) {
                    dataBinding.editPlanAmount.setText("5000");
                } else if (plan.equals("PlanC")) {
                    dataBinding.editPlanAmount.setText("10000");
                }
            }
        });
    }

    private void submitData() {
        String transactionID = "";
        String name = dataBinding.editUsername.getEditableText().toString().trim();
        String email = dataBinding.editEmail.getEditableText().toString().toLowerCase().trim();
        String phone = dataBinding.editPhone.getEditableText().toString().trim();
        if (dataBinding.containerOnline.getVisibility() == View.VISIBLE) {
            transactionID = dataBinding.editTransactionID.getEditableText().toString().trim();
        }
        Calendar c = Calendar.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("id", dataBinding.editID.getEditableText().toString().trim());
        map.put("referrerEmail", auth.getCurrentUser().getEmail());
        map.put("newUserName", name);
        map.put("newUserEmail", email.toLowerCase());
        map.put("newUserPhone", phone);
        map.put("plan", dataBinding.planSpinner.getEditableText().toString().trim());
        map.put("planAmt", dataBinding.editPlanAmount.getEditableText().toString().trim());
        map.put("paymentMode", dataBinding.paymentModeSpinner.getEditableText().toString().trim());
        map.put("transactionID", transactionID);
        map.put("onlinePaymentMode", dataBinding.onlinePaymentModeSpinner.getEditableText().toString().trim());
        map.put("date", c.getTimeInMillis());
        Map<String, Object> emailMap = new HashMap<>();
        emailMap.put("email", auth.getCurrentUser().getEmail());
        Map<String, Object> transactionMap = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(c.getTime());
        transactionMap.put("id", "Pending");
        transactionMap.put("userID", dataBinding.editID.getText().toString().trim());
        transactionMap.put("plan", dataBinding.planSpinner.getEditableText().toString().trim());
        transactionMap.put("amount", Integer.parseInt(dataBinding.editPlanAmount.getEditableText().toString().trim()));
        transactionMap.put("date", c.getTimeInMillis());
        if (emailList.contains(email.toLowerCase() + dataBinding.planSpinner.getEditableText().toString().trim())) {
            Toast.makeText(this, email.toLowerCase() + " is already requested to join " + dataBinding.planSpinner.getEditableText().toString().trim(), Toast.LENGTH_SHORT).show();
            return;
        }
        usersCollection.document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot1) {
                if (documentSnapshot1.exists()) {
                    usersCollection.document(email).collection("plans").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot.exists()) {
                                    if (dataBinding.planSpinner.getEditableText().toString().trim().equals("PlanA")) {
                                        if (documentSnapshot.getId().equals("PlanA")) {
                                            dataBinding.btnSubmit.setEnabled(true);
                                            load.dismiss();
                                            Toast.makeText(ReferAndEarnActivity.this, "User already exists in this plan", Toast.LENGTH_SHORT).show();
                                        } else {
                                            fallBackMethod(emailMap, map, email, transactionMap);
                                        }
                                    } else if (dataBinding.planSpinner.getEditableText().toString().trim().equals("PlanB")) {
                                        if (documentSnapshot.getId().equals("PlanB")) {
                                            dataBinding.btnSubmit.setEnabled(true);
                                            load.dismiss();
                                            Toast.makeText(ReferAndEarnActivity.this, "User already exists in this plan", Toast.LENGTH_SHORT).show();
                                        } else {
                                            fallBackMethod(emailMap, map, email, transactionMap);
                                        }
                                    } else if (dataBinding.planSpinner.getEditableText().toString().trim().equals("PlanC")) {
                                        if (documentSnapshot.getId().equals("PlanC")) {
                                            dataBinding.btnSubmit.setEnabled(true);
                                            load.dismiss();
                                            Toast.makeText(ReferAndEarnActivity.this, "User already exists in this plan", Toast.LENGTH_SHORT).show();
                                        } else {
                                            fallBackMethod(emailMap, map, email, transactionMap);
                                        }
                                    }
                                } else {
                                    fallBackMethod(emailMap, map, email, transactionMap);
                                }
                            }
                        }
                    });
                } else {
                    fallBackMethod(emailMap, map, email, transactionMap);
                }
            }
        });

    }

    private void fallBackMethod(Map<String, Object> emailMap, Map<String, Object> map, String email, Map<String, Object> transactionMap) {
        FirebaseFirestore.getInstance().collection("admin").document(auth.getCurrentUser().getEmail())
                .set(emailMap, SetOptions.merge());
        FirebaseFirestore.getInstance().collection("admin").document(auth.getCurrentUser().getEmail())
                .collection("Refer").document(email + dataBinding.planSpinner.getEditableText().toString())
                .set(map);
        if (dataBinding.paymentModeSpinner.getEditableText().toString().equals("Wallet"))
            usersCollection.document(auth.getCurrentUser().getEmail()).collection(COLLECTION_WALLET_TRANSACTION)
                    .document(email + dataBinding.planSpinner.getEditableText().toString()).set(transactionMap);
        Toast.makeText(ReferAndEarnActivity.this, "Request Sent Successfully!", Toast.LENGTH_SHORT).show();
        clearData();
    }

    private void clearData() {
        dataBinding.btnSubmit.setEnabled(true);
        load.dismiss();
        dataBinding.editUsername.setText("");
        dataBinding.editEmail.setText("");
        dataBinding.editPhone.setText("");
        dataBinding.editTransactionID.setText("");
        notRequested();
        getBalance();
    }

    private void textWatchers() {
        dataBinding.editUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataBinding.inputLayoutNewUserName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dataBinding.editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataBinding.inputLayoutNewUserEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dataBinding.editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataBinding.inputLayoutNewUserPhone.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dataBinding.editTransactionID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataBinding.inputLayoutTransactionID.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private boolean isDataValid() {
        String name = dataBinding.editUsername.getEditableText().toString().trim();
        String email = dataBinding.editEmail.getEditableText().toString().trim();
        String phone = dataBinding.editPhone.getEditableText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            dataBinding.inputLayoutNewUserName.setErrorEnabled(true);
            dataBinding.inputLayoutNewUserName.setError("Required");
            return false;
        } else if (TextUtils.isEmpty(email)) {
            dataBinding.inputLayoutNewUserEmail.setErrorEnabled(true);
            dataBinding.inputLayoutNewUserEmail.setError("Required");
            return false;
        } else if (TextUtils.isEmpty(phone)) {
            dataBinding.inputLayoutNewUserPhone.setErrorEnabled(true);
            dataBinding.inputLayoutNewUserPhone.setError("Required");
            return false;
        } else if (!email.contains("@")) {
            dataBinding.inputLayoutNewUserEmail.setErrorEnabled(true);
            dataBinding.inputLayoutNewUserEmail.setError("Enter a valid email");
            return false;
        } else if (phone.length() != 10) {
            dataBinding.inputLayoutNewUserPhone.setErrorEnabled(true);
            dataBinding.inputLayoutNewUserPhone.setError("Enter a valid phone number");
            return false;
        } else {
            if (dataBinding.containerOnline.getVisibility() == View.VISIBLE) {
                String transactionID = dataBinding.editTransactionID.getEditableText().toString().trim();
                if (TextUtils.isEmpty(transactionID)) {
                    dataBinding.inputLayoutTransactionID.setErrorEnabled(true);
                    dataBinding.inputLayoutTransactionID.setError("Required");
                    return false;
                }
            } else if (dataBinding.containerOnline.getVisibility() == View.GONE) {
                int balance = Integer.parseInt(dataBinding.textBalance.getText().toString().trim());
                int planAmt = Integer.parseInt(dataBinding.editPlanAmount.getEditableText().toString().trim());
                if (balance >= planAmt) {
                    return true;
                } else {
                    Toast.makeText(this, "Oh no! Looks like you do not have enough balance! \n Pay online instead.", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        return true;
    }
}
