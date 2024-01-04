package com.codingbhasha.ps.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.ApplyLoan;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_APPLY_LOAN;
@Obfuscate
public class LoanRequestsViewModel extends ViewModel {

    public LiveData<List<ApplyLoan>> getLoanRequests(String plan) {
        MutableLiveData<List<ApplyLoan>> liveDataList = new MutableLiveData<>();
        List<ApplyLoan> applyLoanList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("admin")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    if (queryDocumentSnapshot.exists()) {
                        String docId = queryDocumentSnapshot.getId();
                        FirebaseFirestore.getInstance().collection("admin")
                                .document(docId)
                                .collection(COLLECTION_APPLY_LOAN)
                                .document(docId)
                                .collection(plan)
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    ApplyLoan applyLoan = new ApplyLoan();
                                    applyLoan.setDocId(documentSnapshot.getId());
                                    applyLoan.setEmail(documentSnapshot.get("email").toString());
                                    applyLoan.setUniqueId(documentSnapshot.get("uniqueId").toString());
                                    applyLoan.setAccNum(documentSnapshot.get("accNum").toString());
                                    applyLoan.setAmount(Integer.parseInt(documentSnapshot.get("amount").toString()));
                                    applyLoan.setIFSC(documentSnapshot.get("ifsc").toString());


                                            //documentSnapshot.toObject(ApplyLoan.class);
                                    applyLoanList.add(applyLoan);
                                }
                                liveDataList.setValue(applyLoanList);
                            }
                        });
                    }
                }
            }
        });
        return liveDataList;
    }
}
