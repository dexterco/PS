package com.codingbhasha.ps.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.MonthlyIncome;
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

import static com.codingbhasha.ps.utils.Constants.COLLECTION_MONTHLY_INCOME;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class MonthlyIncomeViewModel extends ViewModel {

    public LiveData<List<MonthlyIncome>> getMonthlyIncomes(String plan) {
        List<MonthlyIncome> monthlyIncomeList = new ArrayList<>();
        MutableLiveData<List<MonthlyIncome>> listLiveData = new MutableLiveData<>();
        CollectionReference monthlyRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("plans")
                .document(plan)
                .collection(COLLECTION_MONTHLY_INCOME);

        monthlyRef.orderBy("date", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    MonthlyIncome monthlyIncome = documentSnapshot.toObject(MonthlyIncome.class);
                    monthlyIncomeList.add(monthlyIncome);
                }
                listLiveData.setValue(monthlyIncomeList);
            }
        });
        return listLiveData;
    }
}
