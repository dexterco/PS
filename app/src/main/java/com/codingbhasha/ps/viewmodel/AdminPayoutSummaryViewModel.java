package com.codingbhasha.ps.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.PayoutSummary;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_PAYOUT_SUMMARY;
@Obfuscate
public class AdminPayoutSummaryViewModel extends ViewModel {

    public LiveData<List<PayoutSummary>> getPayoutSummaries(String plan) {
        MutableLiveData<List<PayoutSummary>> listLiveData = new MutableLiveData<>();
        List<PayoutSummary> payoutSummaryList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("admin")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection(COLLECTION_PAYOUT_SUMMARY)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection(plan)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    //Log.e("kllla", String.valueOf(documentSnapshot));
                    PayoutSummary payoutSummary = documentSnapshot.toObject(PayoutSummary.class);
                    payoutSummaryList.add(payoutSummary);
                }
                listLiveData.setValue(payoutSummaryList);
            }
        });
        return listLiveData;
    }
}
