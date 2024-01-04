package com.codingbhasha.ps.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.BankRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class BankApprovedViewModel extends ViewModel {

    public LiveData<List<BankRequest>> getBanks() {
        MutableLiveData<List<BankRequest>> liveDataList = new MutableLiveData<>();
        List<BankRequest> bankList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("admin")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.exists()) {
                        String docId = documentSnapshot.getId();
                        FirebaseFirestore.getInstance().collection("admin")
                                .document(docId)
                                .collection("bank")
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                    if (documentSnapshot1.exists()) {
                                        BankRequest bankRequest = documentSnapshot1.toObject(BankRequest.class);
                                        bankList.add(bankRequest);
                                    }
                                }
                                liveDataList.setValue(bankList);
                            }
                        });
                    }
                }
            }
        });
        return liveDataList;
    }
}
