package com.example.mygrocerystore;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygrocerystore.activities.PlacedOrderActivity;
import com.example.mygrocerystore.adapters.MyCartAdapter;
import com.example.mygrocerystore.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MyCartsFragment extends Fragment {

    private int discountPercentage = 0;

    FirebaseFirestore db;
    FirebaseAuth auth;

    TextView overTotalAmount;

    RecyclerView recyclerView;
    MyCartAdapter cartAdapter;

    List<MyCartModel> cartModelList;

    Toolbar toolbar;

    ProgressBar progressBar;

    int totalBill;

    Button buyNow;

    EditText discount;
    ImageView clickdiscount;

    public MyCartsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_carts, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        buyNow = root.findViewById(R.id.buy_now);

        progressBar = root.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = root.findViewById(R.id.recyclerview);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(getActivity(), cartModelList);
        recyclerView.setAdapter(cartAdapter);

        overTotalAmount = root.findViewById(R.id.textView5);

        discount = root.findViewById(R.id.voucher);
        clickdiscount = root.findViewById(R.id.clickdiscount);


        db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("CurrentUser").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                                String documentId = documentSnapshot.getId();

                                MyCartModel cartModel = documentSnapshot.toObject(MyCartModel.class);
                                cartModelList.add(cartModel);
                                cartAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                cartModel.setDocumentId(documentId);
                            }

                            calculateTotalAmount(cartModelList);

                        }
                    }
                });

        clickdiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String voucher = discount.getText().toString().trim();
                if(voucher.equalsIgnoreCase("vuongdeptrai")) {
                    // Nếu nhập đúng voucher thì giảm giá 5%
                    discountPercentage = 5;
                    Toast.makeText(getActivity(), "Discount code applied!", Toast.LENGTH_SHORT).show();
                } else {
                    discountPercentage = 0;
                    Toast.makeText(getActivity(), "Invalid discount code!", Toast.LENGTH_SHORT).show();
                }
                calculateTotalAmount(cartModelList);
            }

        });
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int totalAmount = calculateTotalAmount(cartModelList); // Lấy totalAmount

                Intent intent = new Intent(getContext(), PlacedOrderActivity.class);
                intent.putExtra("totalAmount", totalAmount); // Truyền totalAmount qua Intent
                intent.putExtra("itemList", (Serializable) cartModelList);
                startActivity(intent);

                // Xoá hết sản phẩm
                cartModelList.clear();
                cartAdapter.notifyDataSetChanged();
                overTotalAmount.setText("Total Amount : 0 VNĐ");
                buyNow.setVisibility(View.GONE);

            }
        });


        return root;

    }


    private int calculateTotalAmount(List<MyCartModel> cartModelList) {
        int totalAmount = 0;

        for (MyCartModel myCartModel : cartModelList) {
            totalAmount += myCartModel.getTotalPrice();
        }

        int discountAmount = totalAmount * discountPercentage / 100;
        totalAmount -= discountAmount;

        overTotalAmount.setText("Total Amount : " + totalAmount + " VNĐ");

        return totalAmount;
    }


}