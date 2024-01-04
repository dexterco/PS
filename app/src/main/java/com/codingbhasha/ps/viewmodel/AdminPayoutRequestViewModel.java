package com.codingbhasha.ps.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.PayputRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_PAYOUT_REQUEST;
@Obfuscate
public class AdminPayoutRequestViewModel extends ViewModel {

    public LiveData<List<PayputRequest>> getPayoutRequests(String plan) {
        MutableLiveData<List<PayputRequest>> liveDataList = new MutableLiveData<>();
        List<PayputRequest> payputRequestList = new ArrayList<>();
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
                                    PayputRequest payputRequest = new PayputRequest();
                                    payputRequest.setUniqueId(documentSnapshot1.get("uniqueId").toString());
                                    payputRequest.setDate(Long.parseLong(documentSnapshot1.get("date").toString()));
                                    payputRequest.setAccNum(documentSnapshot1.get("accNum").toString());
                                    payputRequest.setIFSC(documentSnapshot1.get("ifsc").toString());
                                    payputRequest.setAmount(Integer.parseInt(documentSnapshot1.get("amount").toString()));
                                    payputRequest.setDocid(documentSnapshot1.getId());
                                    payputRequest.setEmail(documentSnapshot.getId());
                                    payputRequestList.add(payputRequest);
                                }
                                liveDataList.setValue(payputRequestList);
                            }
                        });
                    }
                }
            }
        });
        return liveDataList;
    }
}
