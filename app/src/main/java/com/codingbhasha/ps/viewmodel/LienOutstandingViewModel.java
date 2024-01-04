package com.codingbhasha.ps.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.LienOutstandingAmt;
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

import static com.codingbhasha.ps.utils.Constants.COLLECTION_LIEN_OUTSTANDING;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class LienOutstandingViewModel extends ViewModel {
    public Context context;

    public LiveData<List<LienOutstandingAmt>> getLoans(String plan) {
        List<LienOutstandingAmt> lienOutstandingAmtList = new ArrayList<>();
        MutableLiveData<List<LienOutstandingAmt>> listLiveData = new MutableLiveData<>();
        CollectionReference loanAccountRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("plans")
                .document(plan)
                .collection(COLLECTION_LIEN_OUTSTANDING);

        loanAccountRef.orderBy("date", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    LienOutstandingAmt lienOutstandingAmt = documentSnapshot.toObject(LienOutstandingAmt.class);
                    lienOutstandingAmtList.add(lienOutstandingAmt);
                }
                listLiveData.setValue(lienOutstandingAmtList);
            }
        });
        return listLiveData;
    }
}
