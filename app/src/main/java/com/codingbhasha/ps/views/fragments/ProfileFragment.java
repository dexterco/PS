package com.codingbhasha.ps.views.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.base.BaseFragment;
import com.codingbhasha.ps.databinding.FragmentApplyLoanBinding;
import com.codingbhasha.ps.databinding.FragmentProfileBinding;
import com.codingbhasha.ps.databinding.NavHeaderBinding;
import com.codingbhasha.ps.model.Users;
import com.codingbhasha.ps.views.activities.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class ProfileFragment extends BaseFragment<FragmentProfileBinding>   {
    MainActivity mainActivity;
    String plan;
    DocumentReference userDoc;

    public ProfileFragment(Context context) {
        mainActivity = (MainActivity) context;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_profile;
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

    private void Setup(String plan){
        userDoc = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("plans").document(plan);

        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ProgressDialog progressDialog = new ProgressDialog(mainActivity);
                progressDialog.show();
                Users user = documentSnapshot.toObject(Users.class);
                Date date = new Date(user.getDateOfJoining());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dated = dateFormat.format(date);
                dataBinding.textDate.setText(dated);
                dataBinding.setUser(user);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();


        plan = mainActivity.dataBinding.spinnerPlan.getSelectedItem().toString();
        Setup(plan);
        mainActivity.dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                plan = adapterView.getItemAtPosition(i).toString();
                mainActivity.fillData(plan);
                Setup(plan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }
}
