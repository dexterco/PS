package com.codingbhasha.ps.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.IdStatus;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class IdStatusViewModel extends ViewModel {


    String plan;
    public Context mainActivity;
    IdStatus idStatus;
    private long referralIncome, monthlyIncome,
            monthlyBonus,
            levelAchievementBonus,
            totalWallet,
            totalRefunds, totalPayouts;
    private long totalLoanRecoveyAmt;

    public LiveData<List<IdStatus>> getIdStatuses() {
        MutableLiveData<List<IdStatus>> liveDataList = new MutableLiveData<>();
        List<IdStatus> idStatusList = new ArrayList<>();



        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.exists()) {
                        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                .document(documentSnapshot.getId())
                                .collection("plans")
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                    if (documentSnapshot1.exists()) {
                                        idStatus = new IdStatus();
                                        plan = documentSnapshot1.getId();

                                        referralIncome = Long.parseLong(documentSnapshot1.get("totalReferralIncome").toString());
                                        monthlyIncome = Long.parseLong(documentSnapshot1.get("totalMonthlyIncome").toString());
                                        monthlyBonus = Long.parseLong(documentSnapshot1.get("totalMonthlyBonus").toString());
                                        levelAchievementBonus = Long.parseLong(documentSnapshot1.get("totalLevelAchievementBonus").toString());
                                        totalWallet = Long.parseLong(documentSnapshot1.get("totalWallet").toString());
                                        totalRefunds = Long.parseLong(documentSnapshot1.get("totalRefunds").toString());
                                        totalPayouts = Long.parseLong(documentSnapshot1.get("totalPayouts").toString());
                                        totalLoanRecoveyAmt = Long.parseLong(documentSnapshot1.get("totalLoanRecoveyAmt").toString());
                                        Log.e("basaas ", "ri =>" + referralIncome + " mi =>" + monthlyIncome + " mb =>" + monthlyBonus + " la =>" + levelAchievementBonus + " tw =>" + totalWallet + " tr =>" + totalRefunds + " tp =>" + totalPayouts);

                                        long earnBalance = referralIncome + monthlyBonus + monthlyIncome + levelAchievementBonus + totalRefunds;
                                        long getBalance = totalWallet + totalPayouts+totalLoanRecoveyAmt;
                                        long total = earnBalance - getBalance;
idStatus.setWalletBalance(total);
                                        idStatus.setPlan(documentSnapshot1.getId());
                                        if (documentSnapshot1.contains("memberId")) {
                                            idStatus.setMemberId(documentSnapshot1.get("memberId").toString());
                                        }
                                        if (documentSnapshot1.contains("dateOfJoining")) {
                                            idStatus.setDateOfJoining(documentSnapshot1.get("dateOfJoining").toString());
                                        }
                                        if (documentSnapshot1.contains("l1IDs")) {
                                            int l1 = Integer.parseInt(documentSnapshot1.get("l1IDs").toString());
                                            int l2 = Integer.parseInt(documentSnapshot1.get("l2IDs").toString());
                                            int l3 = Integer.parseInt(documentSnapshot1.get("l3IDs").toString());
                                            int l4 = Integer.parseInt(documentSnapshot1.get("l4IDs").toString());
                                            idStatus.setTotalIds(l1 + l2 + l3 + l4);
                                        }
                                        if (documentSnapshot1.contains("validity")) {
                                            idStatus.setValidity(Integer.parseInt(documentSnapshot1.get("validity").toString()));

                                        }

                                        idStatusList.add(idStatus);
                                    }
                                }
                                liveDataList.setValue(idStatusList);
                            }
                        });
                    }
                }
            }
        });
        return liveDataList;
    }

}
