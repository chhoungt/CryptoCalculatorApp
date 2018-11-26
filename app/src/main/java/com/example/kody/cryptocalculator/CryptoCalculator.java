package com.example.kody.cryptocalculator;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class CryptoCalculator extends AppCompatActivity {

    StringBuffer inputDisplay = new StringBuffer("0");
    JSONObject tickerData = new JSONObject();
    HashMap<String, Double> cryptoData = new HashMap<String, Double>();
    // String APIKey = "57767db4-a9c8-4ba0-863e-7e299d41363a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_crypto_calculator);
        getCryptoCurrencyData();
        updateResult();
    }

    private void getCryptoCurrencyData() {
        final TextView displayBox = (TextView)findViewById(R.id.textView);
        RequestQueue queue = Volley.newRequestQueue(this);
        String tickerURL ="https://api.coinmarketcap.com/v2/ticker/?limit=0";
        System.out.println("test");
        // Obtains all ticker data
        JsonObjectRequest tickerRequest = new JsonObjectRequest
                (Request.Method.GET, tickerURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        tickerData = response;
                        try {
                            JSONObject dataObject = tickerData.getJSONObject("data");
                            Iterator<?> keys = dataObject.keys();
                            while( keys.hasNext()) {
                                String key = (String) keys.next();
                                JSONObject currencyObject = dataObject.getJSONObject(key);
                                String currencyName = currencyObject.getString("name");
                                String currencySymbol = currencyObject.getString("symbol");
                                JSONObject currencyQuotes = currencyObject.getJSONObject("quotes");
                                JSONObject currencyFiat = currencyQuotes.getJSONObject("USD");
                                Double currencyPrice = currencyFiat.getDouble("price");
                                cryptoData.put(currencyName + " (" + currencySymbol + ")", currencyPrice);
                            }
                            System.out.println("Done");
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(tickerRequest);
    }

    public void search(View v) {
        Intent intent = new Intent(this, CryptoCurrencySearch.class);
        intent.putExtra("cryptoData", cryptoData);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                System.out.println(data.getStringExtra("currencyData"));
                // String strEditText = data.getStringExtra("editTextValue");
            }
        }
    }

    public void updateResult() {
        TextView displayBox = (TextView)findViewById(R.id.textView);
        displayBox.setText(inputDisplay);
    }

    public void appendResult(View v) {
        Button inputButton = (Button)v;
        if (inputButton.getText().toString().equals(".") && inputDisplay.indexOf(".") > -1) {
            return;
        }
        else if (inputButton.getText().toString().equals("⌫")) {
            if (inputDisplay.length() == 0) {
                return;
            }
            inputDisplay.deleteCharAt(inputDisplay.length() - 1);
        } else {
            if (inputDisplay.length() == 1 && inputDisplay.charAt(0) == '0') {
                inputDisplay.replace(0,1, inputButton.getText().toString());
            } else {
                inputDisplay.append(inputButton.getText().toString());
            }
        }
        updateResult();
    }

    public void clearResult(View v) {
        inputDisplay = new StringBuffer("0");
        updateResult();
    }
}
