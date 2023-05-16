package com.example.mygrocerystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygrocerystore.R;
import com.example.mygrocerystore.databinding.FragmentMyOrdersBinding;
import com.example.mygrocerystore.models.MyCartModel;
import com.example.mygrocerystore.zalo.api.CreateOrder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.integrity.e;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PlacedOrderActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    TextView overTotalAmount;
    Button paymentonline;
    Button payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placed_order);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        overTotalAmount = findViewById(R.id.totalmoney);
        paymentonline = findViewById(R.id.directpayment);

        // Nhận giá trị totalAmount từ Intent
        int totalAmount = getIntent().getIntExtra("totalAmount", 0);

        overTotalAmount.setText(totalAmount + " VNĐ");

        payment = findViewById(R.id.btnPayment);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlacedOrderActivity.this, "dc", Toast.LENGTH_SHORT).show();
                CreateOrder orderApi = new CreateOrder();

                try {
                    JSONObject data = orderApi.createOrder("1000");
                    String code = data.getString("return_code");

                    if (code.equals("1")) {
                        String token = data.getString("zp_trans_token");
                        ZaloPaySDK.getInstance().payOrder(PlacedOrderActivity.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                                finish();
                            }

                            @Override
                            public void onPaymentCanceled(String zpTransToken, String appTransID) {

                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


//        List<MyCartModel> list = (ArrayList<MyCartModel>) getIntent().getSerializableExtra("itemList");
//
//        if (list != null && list.size() > 0) {
//            for (MyCartModel model : list) {
//                final HashMap<String, Object> cartMap = new HashMap<>();
//
//                // lưu trữ thông tin sản phẩm vào giỏ hàng
//                cartMap.put("productName", model.getProductName());
//                cartMap.put("productPrice", model.getProductPrice());
//                cartMap.put("currentDate", model.getCurrentDate());
//                cartMap.put("currentTime", model.getCurrentTime());
//                cartMap.put("totalQuantity", model.getTotalQuantity());
//                cartMap.put("totalPrice", model.getTotalPrice());
//                // Check if user is authenticated before accessing UID
//                if (auth.getCurrentUser() != null) {
//                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
//                            .collection("MyOrder").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                                @Override
//                                public void onComplete(@NonNull Task<DocumentReference> task) {
//                                }
//                            });
//                }
//            }
//        }

@Override
protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
        }

        }