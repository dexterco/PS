package com.codingbhasha.ps.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.base.BaseActivity;
import com.codingbhasha.ps.databinding.ActivityAdminBinding;
import com.codingbhasha.ps.databinding.NavHeaderBinding;
import com.codingbhasha.ps.views.fragments.AdminEarningsFragment;
import com.codingbhasha.ps.views.fragments.AdminFundsFragment;
import com.codingbhasha.ps.views.fragments.AdminHomeFragment;
import com.codingbhasha.ps.views.fragments.AdminLoanFragment;
import com.codingbhasha.ps.views.fragments.AdminSettingsFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class AdminActivity extends BaseActivity<ActivityAdminBinding> {
    FirebaseAuth auth;
    CollectionReference adminCollection = FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    DocumentReference docRef;
    CollectionReference planRef;

    List<String> planList;
    NavHeaderBinding navHeaderBinding;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_admin;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        dataBinding.toolbar.setTitle("Admin");
        setSupportActionBar(dataBinding.toolbar);

        planList = new ArrayList<>();
        navHeaderBinding = NavHeaderBinding.bind(dataBinding.navView.getHeaderView(0));

        docRef = adminCollection.document(auth.getCurrentUser().getEmail());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                boolean planA = (boolean) documentSnapshot.get("planA");
                boolean planB = (boolean) documentSnapshot.get("planB");
                boolean planC = (boolean) documentSnapshot.get("planC");
                if (planA) {
                    planList.add("PlanA");
                }
                if (planB) {
                    planList.add("PlanB");
                }
                if (planC) {
                    planList.add("PlanC");
                }
                 dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminActivity.this, android.R.layout.simple_list_item_1, planList);
                dataBinding.spinnerPlan.setAdapter(adapter);
                dataBinding.spinnerPlan.setSelection(0);
                dataBinding.toolbarTitle.setText("Home");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AdminHomeFragment(AdminActivity.this)).commit();


            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dataBinding.drawer, dataBinding.toolbar, R.string.open_drawer, R.string.close_drawer);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        dataBinding.drawer.addDrawerListener(toggle);
        toggle.syncState();

        dataBinding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.referAndEarn:
                        dataBinding.drawer.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(AdminActivity.this, ReferAndEarnActivity.class));
                        return true;
                    case R.id.referRequests:
                        dataBinding.drawer.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(AdminActivity.this, AdminReferReqActivity.class));
                        return true;
                    case R.id.idStatus:
                        dataBinding.drawer.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(AdminActivity.this, IdStatusActivity.class));
                        return true;
                    case R.id.walletTransactions:
                        dataBinding.drawer.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(AdminActivity.this, AdminWalletTransactionActivity.class));
                        return true;
                    case R.id.logout:
                        auth.signOut();
                        startActivity(new Intent(AdminActivity.this, LoginActivity.class));
                        finish();
                        return true;
                }
                return false;
            }
        });

        dataBinding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
                        dataBinding.toolbarId.setVisibility(View.VISIBLE);
                        dataBinding.toolbarValidity.setVisibility(View.VISIBLE);

                        dataBinding.toolbarTitle.setText("Home");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AdminHomeFragment(AdminActivity.this)).commit();
                        return true;
                    case R.id.earnings:
                        dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
                        dataBinding.toolbarId.setVisibility(View.VISIBLE);
                        dataBinding.toolbarValidity.setVisibility(View.VISIBLE);
                        dataBinding.toolbarTitle.setText("Earnings");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AdminEarningsFragment(AdminActivity.this)).commit();
                        return true;
                    case R.id.funds:
                        dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
                        dataBinding.toolbarId.setVisibility(View.VISIBLE);
                        dataBinding.toolbarValidity.setVisibility(View.VISIBLE);
                        dataBinding.toolbarTitle.setText("Funds");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AdminFundsFragment()).commit();
                        return true;
                    case R.id.loan:
                        dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
                        dataBinding.toolbarId.setVisibility(View.VISIBLE);
                        dataBinding.toolbarValidity.setVisibility(View.VISIBLE);
                        dataBinding.toolbarTitle.setText("AI");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AdminLoanFragment()).commit();
                        return true;
                    case R.id.settings:
                        dataBinding.spinnerPlan.setVisibility(View.GONE);
                        dataBinding.toolbarId.setVisibility(View.GONE);
                        dataBinding.toolbarValidity.setVisibility(View.GONE);
                        dataBinding.toolbarTitle.setText("Settings");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AdminSettingsFragment()).commit();
                        return true;
                }
                return false;
            }
        });

    }


    public void fillData(String plan) {
        planRef = docRef.collection("plans");
        planRef.document(plan).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date dated = new Date();
                dated.setTime( Long.parseLong(documentSnapshot.get("dateOfJoining").toString()));
                String date = dateFormat.format(dated);


                dataBinding.toolbarId.setText("ID: " + documentSnapshot.get("memberId").toString());
                dataBinding.toolbarValidity.setText("Validity: " + documentSnapshot.get("validity").toString() + " days");

                navHeaderBinding.textName.setText("Name: " + documentSnapshot.get("name").toString());
                navHeaderBinding.textEmail.setText("Email: " + documentSnapshot.get("email").toString());
                navHeaderBinding.textPhone.setText("Phone: " + documentSnapshot.get("phone").toString());
                navHeaderBinding.textJoiningDate.setText("Date of joining: " +date );


            }
        });
    }
}
