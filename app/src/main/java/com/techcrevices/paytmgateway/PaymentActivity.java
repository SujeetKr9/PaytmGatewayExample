package com.techcrevices.paytmgateway;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaymentActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    String customerId = "";
    String orderId = "";
    String mid = "aVZOfL14308651375569";
    String amount = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        customerId = generateString();
        orderId = generateString();
        amount = getIntent().getStringExtra("amount");

        getCheckSum cs = new getCheckSum();
        cs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public class getCheckSum extends AsyncTask<ArrayList<String>, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(PaymentActivity.this);

        String url ="http://bokarocityapp.com/paytm/generateChecksum.php";
        //TODO your server's url here (www.xyz/checksumGenerate.php)
        String varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        String CHECKSUMHASH ="";

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JsonParse jsonParser = new JsonParse(PaymentActivity.this);
            String param=
                    "MID="+mid+
                            "&ORDER_ID=" + orderId+
                            "&CUST_ID="+customerId+
                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+amount+"&WEBSITE=DEFAULT"+
                            "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";

            Log.e("PostData",param);

            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {

                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            //  PaytmPGService Service = PaytmPGService.getStagingService();
            PaytmPGService Service = PaytmPGService.getProductionService();
            HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("MID", mid);
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", customerId);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", amount);
            paramMap.put("WEBSITE", "DEFAULT");
            paramMap.put("CALLBACK_URL" ,varifyurl);
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            Service.startPaymentTransaction(PaymentActivity.this, true,
                    true,
                    PaymentActivity.this  );
        }
    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        Toast.makeText(this, "Payment successful", Toast.LENGTH_LONG).show();
        Log.e("SuccessT", " respon true " + bundle.toString());

       /*
         Bundle[{STATUS=TXN_SUCCESS, CHECKSUMHASH=uFwM18wuGA85AZGpkC8X5tzT/NSvpKFL13Sv2lvGW6PZBri2PR4VPrUvV+ISbLJwWEeO2aLoqi1bYN4zvGjptJxSgHMmFkaepl8dey5OM8c=,
         ORDERID=15820b0e00de4d76b8597a6f88f836d4,
         TXNAMOUNT=1.00,
         TXNDATE=2019-07-11 20:32:33.0,
         MID=aVZOfL98465894658946,
         TXNID=20190711111212800110168227578295333,
         RESPCODE=01,
         PAYMENTMODE=UPI,
         BANKTXNID=919244872866,
         CURRENCY=INR,
         GATEWAYNAME=PPBLC,
         RESPMSG=Txn Success}]
         */

    }

    @Override
    public void networkNotAvailable() {
        Log.e("Trans ", "Network Not Available" );
        Toast.makeText(this, "Network Not Available", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Log.e("Trans ", " Authentication Failed  "+ s );
        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("Trans ", " ui fail respon  "+ s );
        Toast.makeText(this, "UI Error Occurred", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("Trans ", " error loading pagerespon true "+ s + "  s1 " + s1);
        Toast.makeText(this, "onErrorLoadingWebPage", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(this, "Transaction Cancel", Toast.LENGTH_LONG).show();
        Log.e("Trans ", " cancel call back respon  " );
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Toast.makeText(this, "Transaction Cancel", Toast.LENGTH_LONG).show();
        Log.e("Trans ", "  transaction cancel " );

    }

    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

}
