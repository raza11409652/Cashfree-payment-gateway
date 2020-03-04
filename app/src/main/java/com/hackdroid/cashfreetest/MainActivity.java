package com.hackdroid.cashfreetest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gocashfree.cashfreesdk.CFPaymentService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_CURRENCY;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_NOTE;

public class MainActivity extends AppCompatActivity {
    //Cash free URL for token generate
    String URL_CASH_FREE = "https://test.cashfree.com/api/v2/cftoken/order";
    String cfToken = null;
    String TAG   = MainActivity.class.getSimpleName() ;
//    CFPaymentService cfPaymentService ;
    String appId = "1367591e510788f2b5052c7bc57631";
    String orderId = "Order_CF_TEST0002";
    String orderAmount = "1";
    String orderNote = "Test Order";
    String customerName = "John Doe";
    String customerPhone = "9900012345";
    String customerEmail = "test@gmail.com";
    String stage = "TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        Log.d(TAG, "onCreate: "+cfToken);
        Map<String, String> params = new HashMap<>();

        params.put(PARAM_APP_ID, appId);
        params.put(PARAM_ORDER_ID, orderId);
        params.put(PARAM_ORDER_AMOUNT, orderAmount);
        params.put(PARAM_ORDER_NOTE, orderNote);
        params.put(PARAM_CUSTOMER_NAME, customerName);
        params.put(PARAM_CUSTOMER_PHONE, customerPhone);
        params.put(PARAM_CUSTOMER_EMAIL,customerEmail);
        params.put(PARAM_ORDER_CURRENCY , "INR");
        for(Map.Entry entry : params.entrySet()) {
            Log.d("CFSKDSample", entry.getKey() + " " + entry.getValue());
        }
        genrateCFTOKEN(params);



    }

    private void genrateCFTOKEN(final Map<String, String> params) {

        JSONObject jsonObject=null;
        try {
             jsonObject = new JSONObject("{\"orderId\":\"Order_CF_TEST0002\" ," +
                    "\"orderAmount\":\"1\",\"orderCurrency\":\"INR\"}");
            Log.d(TAG, "genrateCFTOKEN: "+jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST ,URL_CASH_FREE,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.d(TAG, "onResponse: "+response);
                if(response!=null){
                    try {
                        String cftoken = response.getString("cftoken");
                        Log.d(TAG, "onResponse: "+cftoken);
                        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
                        cfPaymentService.setOrientation(0);
                        cfPaymentService.doPayment(MainActivity.this, params, cftoken, stage, "#000000", "#FFFFFF", true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String>map = new HashMap<>();
                map.put("x-client-id" , "1367591e510788f2b5052c7bc57631");
                map.put("x-client-secret" , "0f3e8c7f3648df360f878b598a956897c19a2d1c");
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

//        return token[0];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "ReqCode : " + CFPaymentService.REQ_CODE);
        Log.d(TAG, "API Response : ");
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null)
                for (String key : bundle.keySet()) {
                    if (bundle.getString(key) != null) {
                        Log.d(TAG, key + " : " + bundle.getString(key));
                    }
                }
        }
    }
}
