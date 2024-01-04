package com.codingbhasha.ps.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class UserProfileViewModel extends ViewModel {

    public LiveData<List<UserProfile>> getUserProfiles() {
        MutableLiveData<List<UserProfile>> liveDataList = new MutableLiveData<>();
        List<UserProfile> userProfileList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.exists()) {
                        UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                        userProfileList.add(userProfile);
                    }
                }
                liveDataList.setValue(userProfileList);
            }
        });
        return liveDataList;
    }
}
