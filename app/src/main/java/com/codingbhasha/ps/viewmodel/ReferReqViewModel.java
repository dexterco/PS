package com.codingbhasha.ps.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.ReferReq;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class ReferReqViewModel extends ViewModel {

    public LiveData<List<ReferReq>> getReferReqs() {
        List<ReferReq> referReqList = new ArrayList<>();
        MutableLiveData<List<ReferReq>> listLiveData = new MutableLiveData<>();
        CollectionReference referReqRef = FirebaseFirestore.getInstance().collection("admin");

        referReqRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String user = documentSnapshot.getId();
                    referReqRef.document(user).collection("Refer").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                ReferReq referReq = documentSnapshot1.toObject(ReferReq.class);
                                referReq.setReferrerid(documentSnapshot1.getId());
                                referReqList.add(referReq);
                            }
                            listLiveData.setValue(referReqList);
                        }
                    });
                }
            }
        });
        return listLiveData;
    }
}
