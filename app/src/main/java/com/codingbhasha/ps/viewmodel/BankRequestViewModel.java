package com.codingbhasha.ps.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.BankRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class BankRequestViewModel extends ViewModel {

    public LiveData<List<BankRequest>> getBankReqs() {
        List<BankRequest> bankRequestList = new ArrayList<>();
        MutableLiveData<List<BankRequest>> listLiveData = new MutableLiveData<>();
        CollectionReference bankReqRef = FirebaseFirestore.getInstance().collection("admin");

        bankReqRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String user = documentSnapshot.getId();
                    bankReqRef.document(user).collection("bankDetails").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                BankRequest bankRequest = documentSnapshot1.toObject(BankRequest.class);
                                bankRequestList.add(bankRequest);
                            }
                            listLiveData.setValue(bankRequestList);
                        }
                    });
                }
            }
        });
        return listLiveData;
    }
}
