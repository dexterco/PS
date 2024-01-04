package com.codingbhasha.ps.adapters.recyclerviewadapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codingbhasha.ps.R;
import com.codingbhasha.ps.databinding.LayoutItemBankRequestBinding;
import com.codingbhasha.ps.model.BankRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.michaelrocks.paranoid.Obfuscate;

import static com.codingbhasha.ps.utils.Constants.COLLECTION_USERS;

@Obfuscate
public class BankRequestAdapter extends RecyclerView.Adapter<BankRequestViewHolder> {
    List<BankRequest> bankRequestList = new ArrayList<>();
    Context context;

    public BankRequestAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public BankRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemBankRequestBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_bank_request, parent, false);
        return new BankRequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BankRequestViewHolder holder, int position) {
        BankRequest currentItem = bankRequestList.get(position);
        holder.binding.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance().collection("admin")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                String docId = documentSnapshot.getId();
                                FirebaseFirestore.getInstance().collection("admin")
                                        .document(docId).collection("bankDetails")
                                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                            if (documentSnapshot1.exists()) {
                                                String reqId = documentSnapshot1.getId();
                                                BankRequest bankRequest = documentSnapshot1.toObject(BankRequest.class);
                                                if (bankRequest.getAccNumber().equals(currentItem.getAccNumber())) {
                                                    Bitmap bitmap = ((BitmapDrawable) holder.binding.imageDoc.getDrawable()).getBitmap();
                                                    Toast.makeText(context, "Save Image" + saveToInternalStorage(bitmap, bankRequest.getEmail(), bankRequest.getAccNumber()), Toast.LENGTH_SHORT).show();
                                                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bankRequest.getDocRef());
                                                    storageReference.delete();
                                                    Calendar c = Calendar.getInstance();
                                                    bankRequest.setDate(c.getTimeInMillis());
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("accHolderName", bankRequest.getAccHolderName());
                                                    map.put("date", c.getTimeInMillis());
                                                    map.put("accNumber", bankRequest.getAccNumber());
                                                    map.put("email", bankRequest.getEmail());
                                                    map.put("ifsc", bankRequest.getIfsc());
                                                    map.put("docRef", bankRequest.getDocRef());
                                                    FirebaseFirestore.getInstance().collection("admin")
                                                            .document(docId)
                                                            .collection("bank")
                                                            .document()
                                                            .set(map);
                                                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                                                            .document(docId)
                                                            .collection("bank")
                                                            .document().set(map)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    FirebaseFirestore.getInstance().collection("admin")
                                                                            .document(docId).collection("bankDetails")
                                                                            .document(reqId).delete();
                                                                    bankRequestList.remove(currentItem);
                                                                    setList(bankRequestList);
                                                                    notifyDataSetChanged();
                                                                }
                                                            });
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
        holder.binding.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance().collection("admin")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                String docId = documentSnapshot.getId();
                                FirebaseFirestore.getInstance().collection("admin")
                                        .document(docId)
                                        .collection("bankDetails")
                                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                            if (documentSnapshot1.exists()) {
                                                String reqId = documentSnapshot1.getId();
                                                BankRequest bankRequest = documentSnapshot1.toObject(BankRequest.class);
                                                if (bankRequest.getAccNumber().equals(currentItem.getAccNumber())) {
                                                    //Delete item from list
                                                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bankRequest.getDocRef());
                                                    storageReference.delete();
                                                    FirebaseFirestore.getInstance().collection("admin")
                                                            .document(docId)
                                                            .collection("bankDetails")
                                                            .document(reqId).delete();
                                                    bankRequestList.remove(currentItem);
                                                    setList(bankRequestList);
                                                    notifyDataSetChanged();
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
        //Put Image
        Glide.with(context).load(currentItem.getDocRef()).placeholder(R.mipmap.ic_launcher).into(holder.binding.imageDoc);
        holder.binding.setBankReq(currentItem);
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String memberID, String accNumber) {
        String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/PSAPP";
        File folder = new File(folderPath);
        if (!folder.exists()) {

            folder.mkdirs();
        }
        File iam = new File(folder, memberID + "_" + accNumber + ".png");
        try {
            FileOutputStream out = new FileOutputStream(iam);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return iam.getAbsolutePath();
    }

    @Override
    public int getItemCount() {
        return bankRequestList.size();
    }

    public void setList(List<BankRequest> bankRequestList) {
        this.bankRequestList = bankRequestList;
        notifyDataSetChanged();
    }
}

@Obfuscate
class BankRequestViewHolder extends RecyclerView.ViewHolder {

    LayoutItemBankRequestBinding binding;

    public BankRequestViewHolder(LayoutItemBankRequestBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
