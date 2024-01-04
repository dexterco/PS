package com.codingbhasha.ps.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingbhasha.ps.model.LevelAchievementBonus;
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

import static com.codingbhasha.ps.utils.Constants.COLLECTION_LEVEL_ACHIEVEMENT_BONUS;
import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class LevelAchievementBonusViewModel extends ViewModel {

    public LiveData<List<LevelAchievementBonus>> getLevelAchievementBonus(String plan) {
        List<LevelAchievementBonus> levelAchievementBonusList = new ArrayList<>();
        MutableLiveData<List<LevelAchievementBonus>> listLiveData = new MutableLiveData<>();
        CollectionReference levelAchievementRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("plans")
                .document(plan)
                .collection(COLLECTION_LEVEL_ACHIEVEMENT_BONUS);

        levelAchievementRef.orderBy("date", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    LevelAchievementBonus levelAchievementBonus = documentSnapshot.toObject(LevelAchievementBonus.class);
                    levelAchievementBonusList.add(levelAchievementBonus);
                }
                listLiveData.setValue(levelAchievementBonusList);
            }
        });
        return listLiveData;
    }
}
