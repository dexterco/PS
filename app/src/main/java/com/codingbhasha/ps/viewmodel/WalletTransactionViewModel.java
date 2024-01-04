package com.codingbhasha.ps.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.WalletTransaction;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_WALLET_TRANSACTION;
@Obfuscate
public class WalletTransactionViewModel extends ViewModel {

    public LiveData<List<WalletTransaction>> getTransactions() {
        MutableLiveData<List<WalletTransaction>> listLiveData = new MutableLiveData<>();
        List<WalletTransaction> walletTransactionList = new ArrayList<>();
        CollectionReference transactionRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection(COLLECTION_WALLET_TRANSACTION);

        transactionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    WalletTransaction walletTransaction = documentSnapshot.toObject(WalletTransaction.class);
                    walletTransactionList.add(walletTransaction);
                }
                listLiveData.setValue(walletTransactionList);
            }
        });
        return listLiveData;
    }
}
