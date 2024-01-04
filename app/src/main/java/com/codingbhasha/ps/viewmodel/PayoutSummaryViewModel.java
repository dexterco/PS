package com.codingbhasha.ps.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.PayoutSummary;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_PAYOUT_SUMMARY;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class PayoutSummaryViewModel extends ViewModel {
    public Context context;

    public LiveData<List<PayoutSummary>> getPayoutSummary(String plan) {
        List<PayoutSummary> payoutSummaryList = new ArrayList<>();
        MutableLiveData<List<PayoutSummary>> listLiveData = new MutableLiveData<>();
        CollectionReference referralRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("plans")
                .document(plan)
                .collection(COLLECTION_PAYOUT_SUMMARY);

        referralRef.orderBy("date", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    PayoutSummary payoutSummary = documentSnapshot.toObject(PayoutSummary.class);

                    payoutSummaryList.add(payoutSummary);
                }
                listLiveData.setValue(payoutSummaryList);
            }
        });
        return listLiveData;
    }
}
