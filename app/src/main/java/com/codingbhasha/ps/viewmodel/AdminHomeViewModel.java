package com.codingbhasha.ps.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.AdminHome;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class AdminHomeViewModel extends ViewModel {
    private int referralIncome, monthlyIncome,
            monthlyBonus,
            levelAchievementBonus,
            totalWallet,
            totalRefunds, totalPayouts;
    public LiveData<List<AdminHome>> getAdminHome(String plan) {
        List<AdminHome> monthlyBonusList = new ArrayList<>();
        MutableLiveData<List<AdminHome>> listLiveData = new MutableLiveData<>();

                        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                .collection("plans").document(plan)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot1) {

                                    if (documentSnapshot1.exists()) {
                                        AdminHome user =  new AdminHome();
                                        String plan =  documentSnapshot1.getId();
                                        user.setPlan(documentSnapshot1.getId());

                                        if (documentSnapshot1.contains("memberId")) {
                                            user.setMemberId(documentSnapshot1.get("memberId").toString());

                                        }
                                        if (documentSnapshot1.contains("dateOfJoining")) {
                                            user.setDateOfJoining(documentSnapshot1.get("dateOfJoining").toString());
                                        }
                                        if (documentSnapshot1.contains("l1IDs")) {
                                            int l1 = Integer.parseInt(documentSnapshot1.get("l1IDs").toString());
                                            int l2 = Integer.parseInt(documentSnapshot1.get("l2IDs").toString());
                                            int l3 = Integer.parseInt(documentSnapshot1.get("l3IDs").toString());
                                            int l4 = Integer.parseInt(documentSnapshot1.get("l4IDs").toString());
                                            user.setTotalIds(l1 + l2 + l3 + l4);
                                        }
                                        if (documentSnapshot1.contains("validity")) {
                                            user.setValidity(Integer.parseInt(documentSnapshot1.get("validity").toString()));

                                        }

                                        Log.e("dooo", String.valueOf(documentSnapshot1));

                                        user.setActivel1IDs(documentSnapshot1.get("activel1IDs").toString());
                                        user.setActivel2IDs(documentSnapshot1.get("activel2IDs").toString());
                                        user.setActivel3IDs(documentSnapshot1.get("activel3IDs").toString());
                                        user.setActivel4IDs(documentSnapshot1.get("activel4IDs").toString());

                                        user.setDactl1IDs(documentSnapshot1.get("dactl1IDs").toString());
                                        user.setDactl2IDs(documentSnapshot1.get("dactl2IDs").toString());
                                        user.setDactl3IDs(documentSnapshot1.get("dactl3IDs").toString());
                                        user.setDactl4IDs(documentSnapshot1.get("dactl4IDs").toString());

                                        referralIncome = Integer.parseInt(documentSnapshot1.get("totalReferralIncome").toString());
                                        monthlyIncome = Integer.parseInt(documentSnapshot1.get("totalMonthlyIncome").toString());
                                        monthlyBonus = Integer.parseInt(documentSnapshot1.get("totalMonthlyBonus").toString());
                                        levelAchievementBonus = Integer.parseInt(documentSnapshot1.get("totalLevelAchievementBonus").toString());
                                        totalWallet = Integer.parseInt(documentSnapshot1.get("totalWallet").toString());
                                        totalRefunds = Integer.parseInt(documentSnapshot1.get("totalRefunds").toString());
                                        totalPayouts = Integer.parseInt(documentSnapshot1.get("totalPayouts").toString());
                                        int earnBalance = referralIncome + monthlyBonus + monthlyIncome + levelAchievementBonus + totalRefunds;
                                        int getBalance = totalWallet + totalPayouts;
                                        int total = earnBalance - getBalance;

                                        user.setWalletBalance(total);
                                        int Refund = 0 ;
                                        if (plan.equals("PlanA")) {
                                            if (2000>earnBalance){
                                                Refund = 2000-earnBalance;
                                            } else {
                                                Refund =  0;
                                            }
                                            user.setRefund(Refund);
                                        } else if (plan.equals("PlanB")) {
                                            if (5000>earnBalance){
                                                Refund = 5000-earnBalance;
                                            } else {
                                                Refund =  0;
                                            }
                                            user.setRefund(Refund);

                                        } else if (plan.equals("PlanC")) {
                                            if (10000>earnBalance){
                                                Refund = 10000-earnBalance;
                                            } else {
                                                Refund =  0;
                                            }
                                            user.setRefund(Refund);
                                        }



                                        monthlyBonusList.add(user);
                                    }

                                listLiveData.setValue(monthlyBonusList);

                            }
                        });


        return listLiveData;
    }
}
