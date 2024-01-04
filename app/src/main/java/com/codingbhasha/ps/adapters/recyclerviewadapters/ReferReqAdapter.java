package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemReferReqBinding;
import com.codingbhasha.ps.model.ReferReq;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_REFERRAL_INCOME;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_WALLET_TRANSACTION;
@Obfuscate
public class ReferReqAdapter extends RecyclerView.Adapter<ReferReqViewHolder> {
    List<ReferReq> referReqList = new ArrayList<>();
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FirebaseAuth auth1, auth2;

    TextInputLayout inputLayoutMemberId;
    TextInputEditText editMemberId;
    TextInputEditText editPassword;
    TextInputEditText editEmail;
    TextInputLayout inputLayoutValidity;
    TextInputEditText editValidity;
    ImageButton btnDone;
    private Dialog load;

    public ReferReqAdapter(Context context, int memberId, FirebaseAuth auth1, FirebaseAuth auth2) {
        this.context = context;
        preferences = context.getSharedPreferences("referPref", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("memberId", memberId);
        editor.apply();
        this.auth1 = auth1;
        this.auth2 = auth2;
    }

    @NonNull
    @Override
    public ReferReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutItemReferReqBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_refer_req, parent, false);
        load = new Dialog(context);
        load.setContentView(R.layout.load);
        load.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        load.setCancelable(false);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        return new ReferReqViewHolder(binding);
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
    @Override
    public void onBindViewHolder(@NonNull ReferReqViewHolder holder, int position) {
        ReferReq currentItem = referReqList.get(position);
        holder.binding.setReferReq(currentItem);
        holder.binding.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Approve request
                AlertDialog dialog = new AlertDialog.Builder(context).create();
                View v = LayoutInflater.from(context).inflate(R.layout.layout_approve_refer_req, null);
                dialog.setView(v);
                dialog.show();


                inputLayoutMemberId = v.findViewById(R.id.inputLayoutMemberId);
                editMemberId = v.findViewById(R.id.editMemberId);
                editEmail = v.findViewById(R.id.editEmail);
                editPassword = v.findViewById(R.id.editPassword);
                editPassword.setText(getSaltString());
                editEmail.setText(currentItem.getNewUserEmail());
                inputLayoutValidity = v.findViewById(R.id.inputLayoutValidity);
                editValidity = v.findViewById(R.id.editValidity);
                btnDone = v.findViewById(R.id.btnDone);
                editMemberId.setText("" + Calendar.getInstance().getTimeInMillis());

                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String memberId = editMemberId.getEditableText().toString().trim();
                        String validity = editValidity.getEditableText().toString().trim();
                        if (TextUtils.isEmpty(memberId)) {
                            inputLayoutMemberId.setErrorEnabled(true);
                            inputLayoutMemberId.setError("Required");
                        } else if (TextUtils.isEmpty(validity)) {
                            inputLayoutValidity.setErrorEnabled(true);
                            inputLayoutValidity.setError("Required");
                        } else {

                            approveReq(currentItem);
                            dialog.dismiss();

                        }
                    }
                });
            }
        });

        holder.binding.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("asdad" ,currentItem.getReferrerEmail()+" id "+currentItem.getReferrerid());
load.show();

                WriteBatch batch =  FirebaseFirestore.getInstance().batch();


                if(currentItem.getPaymentMode().equals("Wallet")){
                    DocumentReference delete = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                            .document(currentItem.getReferrerEmail())
                            .collection(COLLECTION_WALLET_TRANSACTION)
                            .document(currentItem.getReferrerid());
                    batch.delete(delete);
                    DocumentReference update = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                            .document(currentItem.getReferrerEmail())
                            .collection("plans").document(currentItem.getPlan());

                    batch.update(update,"totalWallet",FieldValue.increment(-Long.parseLong(currentItem.getPlanAmt())));

                }

                DocumentReference adminRef = FirebaseFirestore.getInstance().collection("admin")
                        .document(currentItem.getReferrerEmail()).collection("Refer").document(currentItem.getReferrerid());
batch.delete(adminRef);
batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
    @Override
    public void onSuccess(Void aVoid) {
        Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show();
        load.dismiss();
        referReqList.remove(currentItem);
        setList(referReqList);
    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(context, "Try again later", Toast.LENGTH_SHORT).show();
        load.dismiss();

    }
});





            }
        });
    }

    private void approveReq(ReferReq currentItem) {

        String memberId = editMemberId.getEditableText().toString().trim();
        String validity = editValidity.getEditableText().toString().trim();
        int val = Integer.parseInt(validity);
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please Wait");
        progressDialog.show();
        long amount = 0;


        String user = currentItem.getReferrerEmail();


        DocumentReference adminRef = FirebaseFirestore.getInstance().collection("admin").document(currentItem.getReferrerEmail()).collection("Refer").document(currentItem.getReferrerid());
        auth2.createUserWithEmailAndPassword(currentItem.getNewUserEmail(), editPassword.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(context, "User Created", Toast.LENGTH_SHORT).show();
                            auth2.signOut();
                            //AuthCredential credential = EmailAuthProvider.getCredential("s_purandhar@yahoo.com", "Purandhar@1234");
                            //firebaseAuth.getCurrentUser().reauthenticate(credential);
                        }
                    }
                });

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", currentItem.getNewUserEmail());
        userMap.put("name", currentItem.getNewUserName());
        userMap.put("phone", currentItem.getNewUserPhone());

        long validty =90;
        long l2amount = 0;
        long l3amount = 0;
        long l4amount = 0;
        if (currentItem.getPlan().equals("PlanA")) {
            userMap.put("planA", true);
            userMap.put("planAID", memberId);
            amount = 400;
            l2amount = 300;
            l3amount = 200;
            l4amount = 100;
        }
        if (currentItem.getPlan().equals("PlanB")) {
            userMap.put("planB", true);
            userMap.put("planBID", memberId);
            amount = 1000;
            l2amount = 750;
            l3amount = 500;
            l4amount = 250;
        }
        if (currentItem.getPlan().equals("PlanC")) {
            userMap.put("planC", true);
            userMap.put("planCID", memberId);
            amount = 2000;
            l2amount = 1500;
            l3amount = 1000;
            l4amount = 500;
        }
        FirebaseFirestore.getInstance().collection("users")
                .document(currentItem.getNewUserEmail())
                .set(userMap, SetOptions.merge());

        DocumentReference referrerRef = FirebaseFirestore.getInstance().collection("users")
                .document(user).collection("plans")
                .document(currentItem.getPlan());
        long finalAmountl1 = amount;
        long finalAmountl2 = l2amount;
        long finalAmountl3 = l3amount;
        long finalAmountl4 = l4amount;
        Calendar c = Calendar.getInstance();
        referrerRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String l1ReferrerId = documentSnapshot.get("memberId").toString();
                        if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String date = dateFormat.format(c.getTime());
                            Map<String, Object> referralMap = new HashMap<>();
                            referralMap.put("amount", finalAmountl1);
                            referralMap.put("date", c.getTimeInMillis());
                            referralMap.put("idNumber", memberId);
                            referralMap.put("email", currentItem.getNewUserEmail());
                            referralMap.put("levelNum", 1);
                            referralMap.put("userID", documentSnapshot.get("memberId").toString());
                            referrerRef.update("l1IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl1));
                            referrerRef.collection(COLLECTION_REFERRAL_INCOME)
                                    .document().set(referralMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    if (documentSnapshot.get("referrerEmail").toString() == null) {
                                        progressDialog.dismiss();
                                        return;
                                    }
                                    String l2Email = documentSnapshot.get("referrerEmail").toString();
                                    //Toast.makeText(context, "" + l2Email, Toast.LENGTH_SHORT).show();
                                    if (l2Email.equals("none")) {
                                        progressDialog.dismiss();
                                        return;
                                    }
                                    if (!l2Email.equals("none")) {
                                        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                .document(l2Email).collection("plans")
                                                .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {
                                                    Map<String, Object> l2referralMap = new HashMap<>();
                                                    l2referralMap.put("amount", finalAmountl2);
                                                    l2referralMap.put("date", c.getTimeInMillis());
                                                    l2referralMap.put("idNumber", memberId);
                                                    l2referralMap.put("email", currentItem.getNewUserEmail());
                                                    l2referralMap.put("levelNum", 2);
                                                    l2referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                            .document(l2Email).collection("plans")
                                                            .document(currentItem.getPlan()).update("l2IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl2));
                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                            .document(l2Email).collection("plans")
                                                            .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                            .document().set(l2referralMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            if (documentSnapshot.get("referrerEmail").toString() == null) {
                                                                progressDialog.dismiss();
                                                                return;
                                                            }
                                                            String l3Email = documentSnapshot.get("referrerEmail").toString();
                                                            //Toast.makeText(context, "" + l3Email, Toast.LENGTH_SHORT).show();
                                                            if (l3Email.equals("none")) {
                                                                progressDialog.dismiss();
                                                                return;
                                                            }
                                                            if (!l3Email.equals("none")) {

                                                                FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                        .document(l3Email).collection("plans")
                                                                        .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {

                                                                            Map<String, Object> l3referralMap = new HashMap<>();
                                                                            l3referralMap.put("amount", finalAmountl3);
                                                                            l3referralMap.put("date", c.getTimeInMillis());
                                                                            l3referralMap.put("idNumber", memberId);
                                                                            l3referralMap.put("email", currentItem.getNewUserEmail());
                                                                            l3referralMap.put("levelNum", 3);
                                                                            l3referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                    .document(l3Email).collection("plans")
                                                                                    .document(currentItem.getPlan()).update("l3IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl3));
                                                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                    .document(l3Email).collection("plans")
                                                                                    .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                                                    .document().set(l3referralMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {

                                                                                    if (documentSnapshot.get("referrerEmail").toString() == null) {
                                                                                        progressDialog.dismiss();
                                                                                        return;
                                                                                    }
                                                                                    String l4Email = documentSnapshot.get("referrerEmail").toString();
                                                                                    //Toast.makeText(context, "" + l4Email, Toast.LENGTH_SHORT).show();
                                                                                    if (l4Email.equals("none")) {
                                                                                        progressDialog.dismiss();
                                                                                        return;
                                                                                    }
                                                                                    if (!l4Email.equals("none")) {
                                                                                        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                                .document(l4Email).collection("plans")
                                                                                                .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                            @Override
                                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                                if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {
                                                                                                    Map<String, Object> l4referralMap = new HashMap<>();
                                                                                                    l4referralMap.put("amount", finalAmountl4);
                                                                                                    l4referralMap.put("date", c.getTimeInMillis());
                                                                                                    l4referralMap.put("idNumber", memberId);
                                                                                                    l4referralMap.put("email", currentItem.getNewUserEmail());
                                                                                                    l4referralMap.put("levelNum", 4);
                                                                                                    l4referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                                                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                                            .document(l4Email).collection("plans")
                                                                                                            .document(currentItem.getPlan()).update("l4IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl4));
                                                                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                                            .document(l4Email).collection("plans")
                                                                                                            .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                                                                            .document().set(l4referralMap);
                                                                                                    progressDialog.dismiss();
                                                                                                } else {
                                                                                                    progressDialog.dismiss();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }


                                                                                }
                                                                            });
                                                                        }
                                                                        else {
                                                                            if (documentSnapshot.get("referrerEmail").toString() == null) {
                                                                                progressDialog.dismiss();
                                                                                return;
                                                                            }
                                                                            String l4Email = documentSnapshot.get("referrerEmail").toString();
                                                                            //Toast.makeText(context, "" + l4Email, Toast.LENGTH_SHORT).show();
                                                                            if (l4Email.equals("none")) {
                                                                                progressDialog.dismiss();
                                                                                return;
                                                                            }
                                                                            if (!l4Email.equals("none")) {
                                                                                FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                        .document(l4Email).collection("plans")
                                                                                        .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                        if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {
                                                                                            Map<String, Object> l4referralMap = new HashMap<>();
                                                                                            l4referralMap.put("amount", finalAmountl4);
                                                                                            l4referralMap.put("date", c.getTimeInMillis());
                                                                                            l4referralMap.put("idNumber", memberId);
                                                                                            l4referralMap.put("email", currentItem.getNewUserEmail());
                                                                                            l4referralMap.put("levelNum", 4);
                                                                                            l4referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                                                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                                    .document(l4Email).collection("plans")
                                                                                                    .document(currentItem.getPlan()).update("l4IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl4));
                                                                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                                    .document(l4Email).collection("plans")
                                                                                                    .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                                                                    .document().set(l4referralMap);
                                                                                            progressDialog.dismiss();
                                                                                        } else {
                                                                                            progressDialog.dismiss();
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
                                                else {
                                                    if (documentSnapshot.get("referrerEmail").toString() == null) {
                                                        progressDialog.dismiss();
                                                        return;
                                                    }
                                                    String l3Email = documentSnapshot.get("referrerEmail").toString();
                                                    //Toast.makeText(context, "" + l3Email, Toast.LENGTH_SHORT).show();
                                                    if (l3Email.equals("none")) {
                                                        progressDialog.dismiss();
                                                        return;
                                                    }
                                                    if (!l3Email.equals("none")) {

                                                        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                .document(l3Email).collection("plans")
                                                                .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {

                                                                    Map<String, Object> l3referralMap = new HashMap<>();
                                                                    l3referralMap.put("amount", finalAmountl3);
                                                                    l3referralMap.put("date", c.getTimeInMillis());
                                                                    l3referralMap.put("idNumber", memberId);
                                                                    l3referralMap.put("email", currentItem.getNewUserEmail());
                                                                    l3referralMap.put("levelNum", 3);
                                                                    l3referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                            .document(l3Email).collection("plans")
                                                                            .document(currentItem.getPlan()).update("l3IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl3));

                                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                            .document(l3Email).collection("plans")
                                                                            .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                                            .document().set(l3referralMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {

                                                                            if (documentSnapshot.get("referrerEmail").toString() == null) {
                                                                                progressDialog.dismiss();
                                                                                return;
                                                                            }
                                                                            String l4Email = documentSnapshot.get("referrerEmail").toString();
                                                                            //Toast.makeText(context, "" + l4Email, Toast.LENGTH_SHORT).show();
                                                                            if (l4Email.equals("none")) {
                                                                                progressDialog.dismiss();
                                                                                return;
                                                                            }
                                                                            if (!l4Email.equals("none")) {
                                                                                FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                        .document(l4Email).collection("plans")
                                                                                        .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                        if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {
                                                                                            Map<String, Object> l4referralMap = new HashMap<>();
                                                                                            l4referralMap.put("amount", finalAmountl4);
                                                                                            l4referralMap.put("date", c.getTimeInMillis());
                                                                                            l4referralMap.put("idNumber", memberId);
                                                                                            l4referralMap.put("email", currentItem.getNewUserEmail());
                                                                                            l4referralMap.put("levelNum", 4);
                                                                                            l4referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                                                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                                    .document(l4Email).collection("plans")
                                                                                                    .document(currentItem.getPlan()).update("l4IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl4));
                                                                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                                    .document(l4Email).collection("plans")
                                                                                                    .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                                                                    .document().set(l4referralMap);
                                                                                            progressDialog.dismiss();
                                                                                        } else {
                                                                                            progressDialog.dismiss();
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }


                                                                        }
                                                                    });
                                                                }
                                                                else {
                                                                    if (documentSnapshot.get("referrerEmail").toString() == null) {
                                                                        progressDialog.dismiss();
                                                                        return;
                                                                    }
                                                                    String l4Email = documentSnapshot.get("referrerEmail").toString();
                                                                    //Toast.makeText(context, "" + l4Email, Toast.LENGTH_SHORT).show();
                                                                    if (l4Email.equals("none")) {
                                                                        progressDialog.dismiss();
                                                                        return;
                                                                    }
                                                                    if (!l4Email.equals("none")) {
                                                                        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                .document(l4Email).collection("plans")
                                                                                .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {
                                                                                    Map<String, Object> l4referralMap = new HashMap<>();
                                                                                    l4referralMap.put("amount", finalAmountl4);
                                                                                    l4referralMap.put("date", c.getTimeInMillis());
                                                                                    l4referralMap.put("idNumber", memberId);
                                                                                    l4referralMap.put("email", currentItem.getNewUserEmail());
                                                                                    l4referralMap.put("levelNum", 4);
                                                                                    l4referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                            .document(l4Email).collection("plans")
                                                                                            .document(currentItem.getPlan()).update("l4IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl4));
                                                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                            .document(l4Email).collection("plans")
                                                                                            .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                                                            .document().set(l4referralMap);
                                                                                    progressDialog.dismiss();
                                                                                } else {
                                                                                    progressDialog.dismiss();
                                                                                }
                                                                            }
                                                                        });
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
                        else {
                            if (documentSnapshot.get("referrerEmail").toString() == null) {
                                progressDialog.dismiss();
                                return;
                            }
                            String l2Email = documentSnapshot.get("referrerEmail").toString();
                            //Toast.makeText(context, "" + l2Email, Toast.LENGTH_SHORT).show();
                            if (l2Email.equals("none")) {
                                progressDialog.dismiss();
                                return;
                            }
                            if (!l2Email.equals("none")) {
                                FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                        .document(l2Email).collection("plans")
                                        .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {
                                            Map<String, Object> l2referralMap = new HashMap<>();
                                            l2referralMap.put("amount", finalAmountl2);
                                            l2referralMap.put("date", c.getTimeInMillis());
                                            l2referralMap.put("idNumber", memberId);
                                            l2referralMap.put("email", currentItem.getNewUserEmail());
                                            l2referralMap.put("levelNum", 2);
                                            l2referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                    .document(l2Email).collection("plans")
                                                    .document(currentItem.getPlan()).update("l2IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl2));
                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                    .document(l2Email).collection("plans")
                                                    .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                    .document().set(l2referralMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    if (documentSnapshot.get("referrerEmail").toString() == null) {
                                                        progressDialog.dismiss();
                                                        return;
                                                    }
                                                    String l3Email = documentSnapshot.get("referrerEmail").toString();
                                                    //Toast.makeText(context, "" + l3Email, Toast.LENGTH_SHORT).show();
                                                    if (l3Email.equals("none")) {
                                                        progressDialog.dismiss();
                                                        return;
                                                    }
                                                    if (!l3Email.equals("none")) {

                                                        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                .document(l3Email).collection("plans")
                                                                .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {

                                                                    Map<String, Object> l3referralMap = new HashMap<>();
                                                                    l3referralMap.put("amount", finalAmountl3);
                                                                    l3referralMap.put("date", c.getTimeInMillis());
                                                                    l3referralMap.put("idNumber", memberId);
                                                                    l3referralMap.put("email", currentItem.getNewUserEmail());
                                                                    l3referralMap.put("levelNum", 3);
                                                                    l3referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                            .document(l3Email).collection("plans")
                                                                            .document(currentItem.getPlan()).update("l3IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl3));

                                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                            .document(l3Email).collection("plans")
                                                                            .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                                            .document().set(l3referralMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {

                                                                            if (documentSnapshot.get("referrerEmail").toString() == null) {
                                                                                progressDialog.dismiss();
                                                                                return;
                                                                            }
                                                                            String l4Email = documentSnapshot.get("referrerEmail").toString();
                                                                            //Toast.makeText(context, "" + l4Email, Toast.LENGTH_SHORT).show();
                                                                            if (l4Email.equals("none")) {
                                                                                progressDialog.dismiss();
                                                                                return;
                                                                            }
                                                                            if (!l4Email.equals("none")) {
                                                                                FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                        .document(l4Email).collection("plans")
                                                                                        .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                        if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {
                                                                                            Map<String, Object> l4referralMap = new HashMap<>();
                                                                                            l4referralMap.put("amount", finalAmountl4);
                                                                                            l4referralMap.put("date", c.getTimeInMillis());
                                                                                            l4referralMap.put("idNumber", memberId);
                                                                                            l4referralMap.put("email", currentItem.getNewUserEmail());
                                                                                            l4referralMap.put("levelNum", 4);
                                                                                            l4referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                                                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                                    .document(l4Email).collection("plans")
                                                                                                    .document(currentItem.getPlan()).update("l4IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl4));
                                                                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                                    .document(l4Email).collection("plans")
                                                                                                    .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                                                                    .document().set(l4referralMap);
                                                                                            progressDialog.dismiss();
                                                                                        } else {
                                                                                            progressDialog.dismiss();
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }


                                                                        }
                                                                    });
                                                                }
                                                                else {
                                                                    if (documentSnapshot.get("referrerEmail").toString() == null) {
                                                                        progressDialog.dismiss();
                                                                        return;
                                                                    }
                                                                    String l4Email = documentSnapshot.get("referrerEmail").toString();
                                                                    //Toast.makeText(context, "" + l4Email, Toast.LENGTH_SHORT).show();
                                                                    if (l4Email.equals("none")) {
                                                                        progressDialog.dismiss();
                                                                        return;
                                                                    }
                                                                    if (!l4Email.equals("none")) {
                                                                        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                .document(l4Email).collection("plans")
                                                                                .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {
                                                                                    Map<String, Object> l4referralMap = new HashMap<>();
                                                                                    l4referralMap.put("amount", finalAmountl4);
                                                                                    l4referralMap.put("date", c.getTimeInMillis());
                                                                                    l4referralMap.put("idNumber", memberId);
                                                                                    l4referralMap.put("email", currentItem.getNewUserEmail());
                                                                                    l4referralMap.put("levelNum", 4);
                                                                                    l4referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                            .document(l4Email).collection("plans")
                                                                                            .document(currentItem.getPlan()).update("l4IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl4));
                                                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                            .document(l4Email).collection("plans")
                                                                                            .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                                                            .document().set(l4referralMap);
                                                                                    progressDialog.dismiss();
                                                                                } else {
                                                                                    progressDialog.dismiss();
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
                                        else {
                                            if (documentSnapshot.get("referrerEmail").toString() == null) {
                                                progressDialog.dismiss();
                                                return;
                                            }
                                            String l3Email = documentSnapshot.get("referrerEmail").toString();
                                            //Toast.makeText(context, "" + l3Email, Toast.LENGTH_SHORT).show();
                                            if (l3Email.equals("none")) {
                                                progressDialog.dismiss();
                                                return;
                                            }
                                            if (!l3Email.equals("none")) {

                                                FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                        .document(l3Email).collection("plans")
                                                        .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {

                                                            Map<String, Object> l3referralMap = new HashMap<>();
                                                            l3referralMap.put("amount", finalAmountl3);
                                                            l3referralMap.put("date", c.getTimeInMillis());
                                                            l3referralMap.put("idNumber", memberId);
                                                            l3referralMap.put("email", currentItem.getNewUserEmail());
                                                            l3referralMap.put("levelNum", 3);
                                                            l3referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                    .document(l3Email).collection("plans")
                                                                    .document(currentItem.getPlan()).update("l3IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl3));

                                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                    .document(l3Email).collection("plans")
                                                                    .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                                    .document().set(l3referralMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                    if (documentSnapshot.get("referrerEmail").toString() == null) {
                                                                        progressDialog.dismiss();
                                                                        return;
                                                                    }
                                                                    String l4Email = documentSnapshot.get("referrerEmail").toString();
                                                                    //Toast.makeText(context, "" + l4Email, Toast.LENGTH_SHORT).show();
                                                                    if (l4Email.equals("none")) {
                                                                        progressDialog.dismiss();
                                                                        return;
                                                                    }
                                                                    if (!l4Email.equals("none")) {
                                                                        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                .document(l4Email).collection("plans")
                                                                                .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {
                                                                                    Map<String, Object> l4referralMap = new HashMap<>();
                                                                                    l4referralMap.put("amount", finalAmountl4);
                                                                                    l4referralMap.put("date", c.getTimeInMillis());
                                                                                    l4referralMap.put("idNumber", memberId);
                                                                                    l4referralMap.put("email", currentItem.getNewUserEmail());
                                                                                    l4referralMap.put("levelNum", 4);
                                                                                    l4referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                            .document(l4Email).collection("plans")
                                                                                            .document(currentItem.getPlan()).update("l4IDs", FieldValue.increment(1),"validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl4));
                                                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                            .document(l4Email).collection("plans")
                                                                                            .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                                                            .document().set(l4referralMap);
                                                                                    progressDialog.dismiss();
                                                                                } else {
                                                                                    progressDialog.dismiss();
                                                                                }
                                                                            }
                                                                        });
                                                                    }


                                                                }
                                                            });
                                                        }
                                                        else {
                                                            if (documentSnapshot.get("referrerEmail").toString() == null) {
                                                                progressDialog.dismiss();
                                                                return;
                                                            }
                                                            String l4Email = documentSnapshot.get("referrerEmail").toString();
                                                            //Toast.makeText(context, "" + l4Email, Toast.LENGTH_SHORT).show();
                                                            if (l4Email.equals("none")) {
                                                                progressDialog.dismiss();
                                                                return;
                                                            }
                                                            if (!l4Email.equals("none")) {
                                                                FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                        .document(l4Email).collection("plans")
                                                                        .document(currentItem.getPlan()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        if (Integer.parseInt(documentSnapshot.get("validity").toString()) > 0) {
                                                                            Map<String, Object> l4referralMap = new HashMap<>();
                                                                            l4referralMap.put("amount", finalAmountl4);
                                                                            l4referralMap.put("date", c.getTimeInMillis());
                                                                            l4referralMap.put("idNumber", memberId);
                                                                            l4referralMap.put("email", currentItem.getNewUserEmail());
                                                                            l4referralMap.put("levelNum", 4);
                                                                            l4referralMap.put("userID", documentSnapshot.get("memberId").toString());
                                                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                    .document(l4Email).collection("plans")
                                                                                    .document(currentItem.getPlan()).update("l4IDs", FieldValue.increment(1), "validity",validty,"totalReferralIncome",FieldValue.increment(finalAmountl4));
                                                                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                                                    .document(l4Email).collection("plans")
                                                                                    .document(currentItem.getPlan()).collection(COLLECTION_REFERRAL_INCOME)
                                                                                    .document().set(l4referralMap);
                                                                            progressDialog.dismiss();
                                                                        } else {
                                                                            progressDialog.dismiss();
                                                                        }
                                                                    }
                                                                });
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

                        Map<String, Object> planMap = new HashMap<>();
                        planMap.put("balance", 0);
                        planMap.put("dateOfJoining", c.getTimeInMillis());
                        planMap.put("eligibleLoanAmount", 0);
                        planMap.put("email", currentItem.getNewUserEmail());
                        planMap.put("l1IDs", 0);
                        planMap.put("l2IDs", 0);
                        planMap.put("l3IDs", 0);
                        planMap.put("l4IDs", 0);
                        planMap.put("activel1IDs", 0);
                        planMap.put("activel2IDs", 0);
                        planMap.put("activel3IDs", 0);
                        planMap.put("activel4IDs", 0);
                        planMap.put("dactl1IDs", 0);
                        planMap.put("dactl2IDs", 0);
                        planMap.put("dactl3IDs", 0);
                        planMap.put("dactl4IDs", 0);
                        planMap.put("loanActive", false);
                        planMap.put("loanStartDate", 0);
                        planMap.put("loanEndDate", 0);
                        planMap.put("memberId", memberId);
                        planMap.put("name", currentItem.getNewUserName());
                        planMap.put("phone", currentItem.getNewUserPhone());
                        planMap.put("referrerId", l1ReferrerId);
                        planMap.put("referrerEmail", user);
                        planMap.put("status", "active");
                        planMap.put("requestForloan", false);
                        planMap.put("currentLoanID", 0);
                        planMap.put("currentLoan", false);

                        planMap.put("totalWallet", 0);
                        planMap.put("totalLoanRecoveyAmt", 0);
                        planMap.put("totalRefunds", 0);
                        planMap.put("totalPayouts", 0);
                        planMap.put("totalLevelAchievementBonus", 0);
                        planMap.put("totalMonthlyBonus", 0);
                        planMap.put("totalMonthlyIncome", 0);
                        planMap.put("totalReferralIncome", 0);

                        planMap.put("validity", val);
                        Map<String, Object> idMap = new HashMap<>();
                        idMap.put("id", memberId);
                        if (currentItem.getPaymentMode().equals("Wallet")) {
                            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                    .document(user).collection(COLLECTION_WALLET_TRANSACTION)
                                    .document(currentItem.getNewUserEmail() + currentItem.getPlan())
                                    .set(idMap, SetOptions.merge());
                        }
                        FirebaseFirestore.getInstance().collection("users")
                                .document(currentItem.getNewUserEmail())
                                .collection("plans")
                                .document(currentItem.getPlan())
                                .set(planMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                load.dismiss();
                                referReqList.remove(currentItem);
                                setList(referReqList);
                                Toast.makeText(context, "Added with details", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Try Agian Later", Toast.LENGTH_SHORT).show();
                                load.dismiss();
                            }
                        });
                    }
                });
        adminRef.delete();
        //////////TODO



    }

    @Override
    public int getItemCount() {
        return referReqList.size();
    }

    public void setList(List<ReferReq> referReqList) {
        this.referReqList = referReqList;
        notifyDataSetChanged();
    }
}

class ReferReqViewHolder extends RecyclerView.ViewHolder {
    LayoutItemReferReqBinding binding;

    public ReferReqViewHolder(LayoutItemReferReqBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
