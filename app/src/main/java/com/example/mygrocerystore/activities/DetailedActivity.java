package com.example.mygrocerystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mygrocerystore.R;
import com.example.mygrocerystore.models.ViewAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DetailedActivity extends AppCompatActivity {

    int totalQuatity = 1;
    int totalPrice = 0;
    TextView quatity;

    ImageView detailedImg;
    TextView price, rating, description;
    Button addToCart;
    ImageView addItem, removeItem;
    Toolbar toolbar;

    FirebaseAuth auth;

    FirebaseFirestore firestore;


    ViewAllModel viewAllModel = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // thiết lập toolbar làm bằng action Bar ứng dụng
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // cho phép người dùng quay lại nút trươccs đó

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final Object object = getIntent().getSerializableExtra("detail");
        if (object instanceof ViewAllModel) {
            viewAllModel = (ViewAllModel) object;

        }


        quatity = findViewById(R.id.quatity);
        detailedImg = findViewById(R.id.detailed_img);
        addItem = findViewById(R.id.add_item);
        removeItem = findViewById(R.id.remove_item);

        price = findViewById(R.id.detailed_price);
        rating = findViewById(R.id.detailed_rating);
        description = findViewById(R.id.detailed_dec);
        if (viewAllModel != null) {
            Glide.with(getApplicationContext()).load(viewAllModel.getImg_url()).into(detailedImg);
            rating.setText(viewAllModel.getRating());
            description.setText(viewAllModel.getDescription());
            price.setText("Price : " + viewAllModel.getPrice() + " VNĐ/kg");

            totalPrice = viewAllModel.getPrice() * totalQuatity;

            if (viewAllModel.getType().equals("egg")) {
                price.setText("Price : " + viewAllModel.getPrice() + " VNĐ/dozen");
                totalPrice = viewAllModel.getPrice() * totalQuatity;
            } else if (viewAllModel.getType().equals("milk")) {
                price.setText("Price : " + viewAllModel.getPrice() + " VNĐ/box");
                totalPrice = viewAllModel.getPrice() * totalQuatity;
            }

        }

        addToCart = findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addedToCart();
            }
        });
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuatity < 10) {
                    totalQuatity++;
                    quatity.setText(String.valueOf(totalQuatity));
                    totalPrice = viewAllModel.getPrice() * totalQuatity;
                } else {
                    Toast.makeText(DetailedActivity.this, "You can only buy up to 10 products", Toast.LENGTH_SHORT).show();
                }
            }
        });
        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuatity > 0) {
                    totalQuatity--;
                    quatity.setText(String.valueOf(totalQuatity));
                    totalPrice = viewAllModel.getPrice() * totalQuatity;
                } else {
                    Toast.makeText(DetailedActivity.this, "There are no products in the cart", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addedToCart() {
        String saveCurrentDate, saveCurrentTime;
        // khởi tạo đối tượng
        Calendar calForDate = Calendar.getInstance();

        // khai báo và định dạng ngày tháng
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        // khai báo và định dạnh giờ phút
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final HashMap<String, Object> cartMap = new HashMap<>();

        // lưu trữ thông tin sản phẩm vào giỏ hàng
        cartMap.put("productName", viewAllModel.getName());
        cartMap.put("productPrice", price.getText().toString());
        cartMap.put("currentDate", saveCurrentDate);
        cartMap.put("currentTime", saveCurrentTime);
        cartMap.put("totalQuantity", quatity.getText().toString());
        cartMap.put("totalPrice", totalPrice);

        // Tạo collection cho người dùng hiện tại
        firestore.collection("AddToCart")
                .document(auth.getCurrentUser().getUid())
                .collection("CurrentUser")
                .whereEqualTo("productName", viewAllModel.getName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // Không tìm thấy sản phẩm trùng, thêm vào giỏ hàng
                                firestore.collection("AddToCart")
                                        .document(auth.getCurrentUser().getUid())
                                        .collection("CurrentUser")
                                        .add(cartMap)
                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                Toast.makeText(DetailedActivity.this, "Added To A Cart", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                            } else {
                                // Đã tìm thấy sản phẩm trùng, hiển thị thông báo
                                Toast.makeText(DetailedActivity.this, "Product already exists in the cart", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Xảy ra lỗi khi truy vấn Firebase
                            Toast.makeText(DetailedActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


