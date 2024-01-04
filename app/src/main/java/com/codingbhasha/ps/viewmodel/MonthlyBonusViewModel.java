package com.codingbhasha.ps.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.MonthlyBonus;
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

import static com.codingbhasha.ps.utils.Constants.COLLECTION_MONTHLY_BONUS;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class MonthlyBonusViewModel extends ViewModel {

    public LiveData<List<MonthlyBonus>> getMonthlyBonus(String plan) {
        List<MonthlyBonus> monthlyBonusList = new ArrayList<>();
        MutableLiveData<List<MonthlyBonus>> listLiveData = new MutableLiveData<>();
        CollectionReference monthlyRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("plans")
                .document(plan)
                .collection(COLLECTION_MONTHLY_BONUS);

        monthlyRef.orderBy("date", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    MonthlyBonus monthlyBonus = documentSnapshot.toObject(MonthlyBonus.class);
                    monthlyBonusList.add(monthlyBonus);
                }
                listLiveData.setValue(monthlyBonusList);
            }
        });
        return listLiveData;
    }
}
