package com.rijoksd.qrshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class viewProduct extends AppCompatActivity {
    ListView list;
    ImageView arrow;
    SharedPreferences sh;
    String ip, url, url1, lid;

    String[] productID, productImage,productName,productQuantity,productPrice,sId,productDetails,offerprice;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        list = (ListView) findViewById(R.id.list);
        arrow = (ImageView)findViewById(R.id.arrowLeft);

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getApplicationContext(),viewShop.class);
                startActivity(i);
            }
        });


        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sh.getString("ip", "");
        sh.getString("url", "");
        url = sh.getString("url", "") + "and_view_product";


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            if (jsonObj.getString("status").equalsIgnoreCase("ok")) {

                                JSONArray js = jsonObj.getJSONArray("data");//from python
                                productID = new String[js.length()];
                                productImage = new String[js.length()];
                                productName = new String[js.length()];
                                productQuantity = new String[js.length()];
                                productPrice = new String[js.length()];
                                sId = new String[js.length()];
                                productDetails = new String[js.length()];
                                offerprice = new String[js.length()];



                                for (int i = 0; i < js.length(); i++) {
                                    JSONObject u = js.getJSONObject(i);
                                    //dbcolumn name in double quotes
                                    productID[i] = u.getString("product_id");
                                    productImage[i] = u.getString("image");
                                    productName[i] = u.getString("name");
                                    productDetails[i] = u.getString("details");
                                    productQuantity[i] = u.getString("quantity");
                                    productPrice[i] = u.getString("price");
                                    sId[i] = u.getString("shop_id");
                                    offerprice[i] = u.getString("offerprice");
//                                    SharedPreferences.Editor ed = sh.edit();
//                                    ed.putString("productprice", productPrice[i]);
//                                    ed.commit();
//                                    Toast.makeText(viewProduct.this, ""+productPrice[i], Toast.LENGTH_SHORT).show();

                                }
                                list.setAdapter(new customViewProduct(getApplicationContext(), productID, productImage, productName,productDetails, productQuantity, productPrice,sId,offerprice));//custom_view_service.xml and li is the listview object


                            } else {
                                Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(getApplicationContext(), "eeeee" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

            //                value Passing android to python
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", sh.getString("lid", ""));//passing to python
                params.put("shopID", sh.getString("shopID", ""));//passing to python
                return params;
            }
        };


        int MY_SOCKET_TIMEOUT_MS = 100000;

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(postRequest);

    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),viewShop.class);
        startActivity(i);

    }
}