package com.codingbhasha.ps.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentFundsBinding;
import com.codingbhasha.ps.databinding.FragmentHomeBinding;
import com.codingbhasha.ps.views.activities.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class HomeFragment extends BaseFragment<FragmentHomeBinding> {
    MainActivity mainActivity;
    String plan;
    CollectionReference planRef;

    private int referralIncome, monthlyIncome,
            monthlyBonus,
            levelAchievementBonus,
            totalWallet,
            totalRefunds, totalPayouts;
    private int totalLoanRecoveyAmt;


    @Override
    public int getLayoutRes() {
        return R.layout.fragment_home;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
        mainActivity.dataBinding.toolbarId.setVisibility(View.VISIBLE);
        mainActivity.dataBinding.toolbarValidity.setVisibility(View.VISIBLE);


    }
    private void setup() {

        planRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("plans");
        //planRef.document(plan).set(map, SetOptions.merge());
        planRef.document(plan).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        dataBinding.activeL1Ids.setText(documentSnapshot.get("activel1IDs").toString());
                        dataBinding.activeL2Ids.setText(documentSnapshot.get("activel2IDs").toString());
                        dataBinding.activeL3Ids.setText(documentSnapshot.get("activel3IDs").toString());
                        dataBinding.activeL4Ids.setText(documentSnapshot.get("activel4IDs").toString());

                        dataBinding.dactiveL1Ids.setText(documentSnapshot.get("dactl1IDs").toString());
                        dataBinding.dactiveL2Ids.setText(documentSnapshot.get("dactl2IDs").toString());
                        dataBinding.dactiveL3Ids.setText(documentSnapshot.get("dactl3IDs").toString());
                        dataBinding.dactiveL4Ids.setText(documentSnapshot.get("dactl4IDs").toString());


                        dataBinding.textTotalIDs.setText(String.valueOf(Integer.parseInt(documentSnapshot.get("l1IDs").toString()) + Integer.parseInt(documentSnapshot.get("l2IDs").toString())
                                + Integer.parseInt(documentSnapshot.get("l3IDs").toString()) + Integer.parseInt(documentSnapshot.get("l4IDs").toString())));

                        referralIncome = Integer.parseInt(documentSnapshot.get("totalReferralIncome").toString());
                        monthlyIncome = Integer.parseInt(documentSnapshot.get("totalMonthlyIncome").toString());
                        monthlyBonus = Integer.parseInt(documentSnapshot.get("totalMonthlyBonus").toString());
                        levelAchievementBonus = Integer.parseInt(documentSnapshot.get("totalLevelAchievementBonus").toString());
                        totalWallet = Integer.parseInt(documentSnapshot.get("totalWallet").toString());
                        totalRefunds = Integer.parseInt(documentSnapshot.get("totalRefunds").toString());
                        totalPayouts = Integer.parseInt(documentSnapshot.get("totalPayouts").toString());
                        totalLoanRecoveyAmt = Integer.parseInt(documentSnapshot.get("totalLoanRecoveyAmt").toString());
                        Log.e("basaas ", "ri =>" + referralIncome +" mi =>" + monthlyIncome +" mb =>" + monthlyBonus +" la =>" + levelAchievementBonus + " tw =>" +totalWallet +" tr =>" + totalRefunds + " tp =>" + totalPayouts);

                        int earnBalance = referralIncome + monthlyBonus + monthlyIncome + levelAchievementBonus + totalRefunds;
                        int getBalance = totalWallet + totalPayouts + totalLoanRecoveyAmt;
                        int total = earnBalance - getBalance;
                        dataBinding.textBalance.setText(" \u20B9" + String.valueOf(total));
                        int Refund = 0 ;
                        if (plan.equals("PlanA")) {
                            if (2000>earnBalance){
                                Refund = 2000-earnBalance;
                            } else {
                                Refund =  0;
                            }

                        } else if (plan.equals("PlanB")) {
                            if (5000>earnBalance){
                                Refund = 5000-earnBalance;
                            } else {
                                Refund =  0;
                            }


                        } else if (plan.equals("PlanC")) {
                            if (10000>earnBalance){
                                Refund = 10000-earnBalance;
                            } else {
                                Refund =  0;
                            }

                        }
                        dataBinding.textRefund.setText(" \u20B9" + String.valueOf(Refund));
                    }
                });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
        setup();
        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                mainActivity.fillData(plan);
                Log.e("sdda", plan);

                setup();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Log.e("adasdas", "adasdasda");
    }
}
