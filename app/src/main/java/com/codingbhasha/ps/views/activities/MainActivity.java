package com.codingbhasha.ps.views.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.base.BaseActivity;
import com.codingbhasha.ps.databinding.ActivityMainBinding;
import com.codingbhasha.ps.databinding.NavHeaderBinding;
import com.codingbhasha.ps.views.fragments.EarningsFragment;
import com.codingbhasha.ps.views.fragments.FundsFragment;
import com.codingbhasha.ps.views.fragments.HomeFragment;
import com.codingbhasha.ps.views.fragments.LoanFragment;
import com.codingbhasha.ps.views.fragments.SettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;
@Obfuscate
public class MainActivity extends BaseActivity<ActivityMainBinding> {
    FirebaseAuth auth;
    CollectionReference usersCollection = FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    DocumentReference docRef;
    CollectionReference planRef;

    List<String> planList;
    NavHeaderBinding navHeaderBinding;
    static public Object memberID;
    Fragment currentFragment;
    Fragment homeFragment;
    Fragment earningsFragment = new EarningsFragment();
    Fragment fundsFragment = new FundsFragment();
    Fragment loanFragment = new LoanFragment();
    Fragment settingsFragment = new SettingsFragment();
    private int validitt = 0;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        //Last login
        long timestamp = auth.getCurrentUser().getMetadata().getLastSignInTimestamp();


        setSupportActionBar(dataBinding.toolbar);

        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        preferences.edit().putString("plan", "PlanA").apply();

        planList = new ArrayList<>();
        navHeaderBinding = NavHeaderBinding.bind(dataBinding.navView.getHeaderView(0));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, planList);
        dataBinding.spinnerPlan.setAdapter(adapter);
        dataBinding.spinnerPlan.setSelection(0);

        docRef = usersCollection.document(auth.getCurrentUser().getEmail());
        planRef = docRef.collection("plans");
        planRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    //    Log.e("data", String.valueOf(documentSnapshot));
                    if (documentSnapshot.exists()) {
                        memberID = documentSnapshot.get("memberId");
                        if (documentSnapshot.getId().equals("PlanA")) {
                            planList.add("PlanA");
                        } else if (documentSnapshot.getId().equals("PlanB")) {
                            planList.add("PlanB");
                        } else if (documentSnapshot.getId().equals("PlanC")) {
                            planList.add("PlanC");
                        }
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, planList);
                dataBinding.spinnerPlan.setAdapter(adapter);
                dataBinding.spinnerPlan.setSelection(0);
                if (currentFragment == null) {
                    dataBinding.toolbarTitle.setText("Home");
                    homeFragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, homeFragment).commit();
                    currentFragment = homeFragment;
                }

                // Log.e("sdda" ,  dataBinding.spinnerPlan.getSelectedItem().toString());
                dataBinding.spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                        String item = adapterView.getItemAtPosition(i).toString();
                        fillData(item);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
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
                    case R.id.pamphlet:
                        dataBinding.drawer.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(MainActivity.this, pamphlet.class));
                        return true;
                    case R.id.referAndEarn:
                        if (validitt == 0) {
                            Toast.makeText(MainActivity.this, "Your account is deactivate please contact to admin", Toast.LENGTH_LONG).show();
                        } else {
                            dataBinding.drawer.closeDrawer(GravityCompat.START);
                            startActivity(new Intent(MainActivity.this, ReferAndEarnActivity.class));
                        }
                        return true;
                    case R.id.walletTransactions:
                        dataBinding.drawer.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(MainActivity.this, WalletTransactionActivity.class));
                        return true;
                    case R.id.contactUs:
                        dataBinding.drawer.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
                        return true;
                    case R.id.planDetails:
                        dataBinding.drawer.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(MainActivity.this, PlanDetailsActivity.class));
                        return true;
                    case R.id.howWeWork:
                        dataBinding.drawer.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(MainActivity.this, HowWeWorkActivity.class));
                        return true;
                    case R.id.logout:

                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> tak) {

                                String Tocken = tak.getResult();
                                FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(auth.getCurrentUser().getEmail()).update("Token", FieldValue.arrayRemove(Tocken));


                                auth.signOut();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();

                            }
                        });


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
                        homeFragment.onDestroy();
                        Log.e("asad", "asdad");
                        homeFragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, homeFragment).commit();
                        currentFragment = homeFragment;
                        return true;
                    case R.id.earnings:
                        dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
                        dataBinding.toolbarId.setVisibility(View.VISIBLE);
                        dataBinding.toolbarValidity.setVisibility(View.VISIBLE);

                        dataBinding.toolbarTitle.setText("Earnings");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, earningsFragment).commit();
                        currentFragment = earningsFragment;
                        return true;
                    case R.id.funds:
                        dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
                        dataBinding.toolbarId.setVisibility(View.VISIBLE);
                        dataBinding.toolbarValidity.setVisibility(View.VISIBLE);

                        dataBinding.toolbarTitle.setText("Funds");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fundsFragment).commit();
                        currentFragment = fundsFragment;
                        return true;
                    case R.id.loan:
                        dataBinding.spinnerPlan.setVisibility(View.VISIBLE);
                        dataBinding.toolbarId.setVisibility(View.VISIBLE);
                        dataBinding.toolbarValidity.setVisibility(View.VISIBLE);

                        dataBinding.toolbarTitle.setText("Advance Income");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, loanFragment).commit();
                        currentFragment = loanFragment;
                        return true;
                    case R.id.settings:
                        dataBinding.toolbarTitle.setText("Settings");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, settingsFragment).commit();
                        currentFragment = settingsFragment;
                        return true;
                }
                return false;
            }
        });

    }

    public void fillData(String item) {
        //preferences.edit().putString("plan", item).apply();
        planRef.document(dataBinding.spinnerPlan.getSelectedItem().toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                validitt = Integer.parseInt(documentSnapshot.get("validity").toString());
                dataBinding.toolbarId.setText("ID: " + documentSnapshot.get("memberId").toString());
                dataBinding.toolbarValidity.setText("Validity: " + documentSnapshot.get("validity").toString() + " days");
                navHeaderBinding.textName.setText("Name: " + documentSnapshot.get("name").toString());
                navHeaderBinding.textEmail.setText("Email: " + documentSnapshot.get("email").toString());
                navHeaderBinding.textPhone.setText("Phone: " + documentSnapshot.get("phone").toString());
                Date date = new Date(Long.parseLong(documentSnapshot.get("dateOfJoining").toString()));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dated = dateFormat.format(date);
                navHeaderBinding.textJoiningDate.setText("Date of joining: " + dated);


            }
        });
    }

    @Override
    public void onBackPressed() {
        if (dataBinding.drawer.isDrawerOpen(GravityCompat.START)) {
            dataBinding.drawer.closeDrawer(GravityCompat.START);
            return;
        } else if (currentFragment != homeFragment) {
            dataBinding.bottomNav.setSelectedItemId(R.id.home);
            return;
        }
        super.onBackPressed();
    }
}