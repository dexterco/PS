package com.codingbhasha.ps.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.ReferralIncome;
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

import static com.codingbhasha.ps.utils.Constants.COLLECTION_REFERRAL_INCOME;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class ReferralIncomeViewModel extends ViewModel {

    public LiveData<List<ReferralIncome>> getReferralIncomes(String plan) {
        List<ReferralIncome> referralIncomeList = new ArrayList<>();
        MutableLiveData<List<ReferralIncome>> listLiveData = new MutableLiveData<>();
        CollectionReference referralRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("plans")
                .document(plan)
                .collection(COLLECTION_REFERRAL_INCOME);
        referralRef.orderBy("idNumber", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    ReferralIncome referralIncome = documentSnapshot.toObject(ReferralIncome.class);
                    referralIncomeList.add(referralIncome);
                }
                listLiveData.setValue(referralIncomeList);
            }
        });

        return listLiveData;
    }
}
