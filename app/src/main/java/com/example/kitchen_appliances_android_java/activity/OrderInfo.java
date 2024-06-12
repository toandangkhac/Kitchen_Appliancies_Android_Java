package com.example.kitchen_appliances_android_java.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.api.ApiResponse;
import com.example.kitchen_appliances_android_java.api.TrustAllCertificatesSSLSocketFactory;

import com.example.kitchen_appliances_android_java.model.Order;
import com.example.kitchen_appliances_android_java.model.OrderDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class OrderInfo extends AppCompatActivity {
    private HurlStack hurlStack;
    private Order order;
    private ArrayList<OrderDetail> orderDetails;
    private double total = 0;
    private int total_product = 0;
    private int testEmployeeId = 1;

    private ListView list_orderdetail;
    private TextView tv_address, tv_employee, tv_name_customer,  tv_status_order, tv_time_order, status_payment, tv_total, count_product;
    private Button btn_delete_order, btn_confirm_order;

    @Override
    @SuppressLint("MissingInflatedId")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_info);

        rtHurlStack();
        order = (Order) getIntent().getSerializableExtra("order");

        link();
        getListItem();
//
        setValue();

        btn_confirm_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmOrder();
            }
        });


        btn_delete_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOrder();
            }
        });


    }

    private void cancelOrder() {
        RequestQueue queue = Volley.newRequestQueue(this, hurlStack);
        String url = "https://10.0.2.2:7161/api/Order/cancel-order/" + String.valueOf(order.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
            response -> {
                ApiResponse apiResponse = new Gson().fromJson(response, ApiResponse.class);
                if(apiResponse.getStatus() == 200) {
                    Toast.makeText(this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,apiResponse.getStatus()+": "+ apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                error.printStackTrace();
                Toast.makeText(this, "Hủy đơn hàng thất bại!", Toast.LENGTH_SHORT).show();
        });

        queue.add(stringRequest);
    }

    private void confirmOrder() {
        RequestQueue queue = Volley.newRequestQueue(this, hurlStack);
        String url = "https://10.0.2.2:7161/api/Order/confirm-order-by-employee";

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("employeeId", testEmployeeId);
            requestData.put("orderId", order.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
            response -> {
                ApiResponse apiResponse = new Gson().fromJson(response, ApiResponse.class);
                if(apiResponse.getStatus() == 200) {
                    Toast.makeText(this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,apiResponse.getStatus()+": "+ apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                error.printStackTrace();
                Toast.makeText(this, "Duyệt đơn hàng thất bại!", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public byte[] getBody() {
                return requestData.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        queue.add(stringRequest);

    }

    private void setValue() {
        tv_name_customer.setText(String.valueOf(order.getCustomerId()));
        if (order.getStatus()==1) {
            tv_status_order.setText("Chưa xác nhận");
        } else if (order.getStatus()==2)    {
            tv_status_order.setText("Đã xác nhận");
            btn_confirm_order.setVisibility(View.GONE);
            btn_delete_order.setVisibility(View.GONE);
        } else if (order.getStatus()==3) {
            btn_confirm_order.setVisibility(View.GONE);
            btn_delete_order.setVisibility(View.GONE);
            tv_status_order.setText("Đã nhận hàng");
        } else if (order.getStatus()==0) {
            tv_status_order.setText("Đã hủy");
            btn_confirm_order.setVisibility(View.GONE);
            btn_delete_order.setVisibility(View.GONE);
        }
        if (order.isPaymentStatus()) {
            status_payment.setText("Đã thanh toán");
        } else {
            status_payment.setText("Chưa thanh toán");
        }

        tv_address.setText(order.getAddressShipping());

        tv_employee.setText(String.valueOf(order.getEmployeeId()));

        tv_time_order.setText(String.valueOf(order.getCreateAt()));
//        Toast.makeText(OrderInfo.this,orderDetails.size(), Toast.LENGTH_SHORT).show();

    }



    public void setListItem() {
        ArrayAdapter<OrderDetail> adapter = new ArrayAdapter<>(this, R.layout.order_detail_item, orderDetails);
        list_orderdetail.setAdapter(adapter);

//        for (int i = 0; i < orderDetails.size(); i++) {
//            View row = list_orderdetail.getChildAt(i);
//            OrderDetail orderDetail = orderDetails.get(i);
//            TextView tvProduct = row.findViewById(R.id.product);
//            TextView tvQuantity = row.findViewById(R.id.quantity);
//            TextView tvPrice = row.findViewById(R.id.price);
//
//            tvProduct.setText("Product: " + String.valueOf(orderDetail.getProductId()));
//            tvQuantity.setText("Quantity: " + String.valueOf(orderDetail.getQuantity()));
//            tvPrice.setText("Price: " + String.valueOf(orderDetail.getPrice()));
//        }
    }

    public void link() {
        list_orderdetail = findViewById(R.id.list_orderdetail);
        tv_name_customer = findViewById(R.id.tv_name_customer);
        tv_status_order = findViewById(R.id.tv_status_order);
        tv_time_order = findViewById(R.id.tv_time_order);
        status_payment = findViewById(R.id.status_payment);
        tv_total = findViewById(R.id.tv_total);
        btn_delete_order = findViewById(R.id.btn_delete_order);
        btn_confirm_order = findViewById(R.id.btn_confirm_order);
        count_product = findViewById(R.id.count_product);
        tv_employee = findViewById(R.id.tv_employee);
        tv_address = findViewById(R.id.tv_address);
    }

    public void getListItem() {
        int id = order.getId();
        RequestQueue queue = Volley.newRequestQueue(OrderInfo.this, hurlStack);
        String url = "https://10.0.2.2:7161/list-order-detail/" + String.valueOf(id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            response -> {
                Gson gson = new Gson();
                ApiResponse<ArrayList<OrderDetail>> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<ArrayList<OrderDetail>>>() {}.getType());
                if (apiResponse != null && apiResponse.getStatus() == 200) {
                    orderDetails = apiResponse.getData();
                    setTotal(orderDetails);
//                    Toast.makeText(OrderInfo.this, apiResponse.getMessage() + String.valueOf(orderDetails.size()), Toast.LENGTH_SHORT).show();
//                    setListItem();
                } else {
                    Toast.makeText(OrderInfo.this,apiResponse.getStatus()+": "+ apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                error.printStackTrace();
                Toast.makeText(OrderInfo.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
    }

    private void setTotal(ArrayList<OrderDetail> ors) {
        for (int i = 0; i < ors.size(); i++) {
            total_product += ors.get(i).getQuantity();
            total += ors.get(i).getQuantity() * ors.get(i).getPrice();
        }

        count_product.setText(String.valueOf(total_product));
        tv_total.setText(String.valueOf(total));
    }

    public void rtHurlStack() {
        hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(new TrustAllCertificatesSSLSocketFactory(null));
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            // Ignore host name verification. It returns true for all host names.
                            return true;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };
    }
}
