package com.qk.alertdialogtest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private Button mainBtn;
    private EditText nameText;
    private EditText jobText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameText = (EditText)  findViewById(R.id.edittext);
        jobText = (EditText)  findViewById(R.id.edittext1);
        mainBtn = (Button) findViewById(R.id.button);
        mainBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                openAlert(v);
            }
        });
    }

    private void openAlert(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setTitle(this.getTitle()+ " decision");
        alertDialogBuilder.setMessage("Do you want to register?");
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // go to a new activity of the app
                //Intent positveActivity = new Intent(getApplicationContext(), PositiveActivity.class);
                //startActivity(positveActivity);
                // WebServer Request URL
                String serverURL = "https://reqres.in/api/users";
                String myname = nameText.getText().toString();
                String myjob = jobText.getText().toString();
                // Use AsyncTask execute to register
                new LongOperation().execute(serverURL,myname,myjob);
            }
        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // cancel the alert box and put a Toast to the user
                dialog.cancel();
                Toast.makeText(getApplicationContext(), "You chose a negative answer",
                        Toast.LENGTH_LONG).show();
            }
        });
        // set neutral button: Exit the app message
        alertDialogBuilder.setNeutralButton("Exit the app",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // exit the app and go to the HOME
                MainActivity.this.finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }


    private class LongOperation  extends AsyncTask<String, Void, Void> {
        // Required initialization
        private String Content;
        private String Error = null;
        //private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
        private AlertDialog alertDialog;
        String data ="";
        int sizeData = 0;

        protected void onPreExecute() {
            //Start Progress Dialog (Message)
            //Dialog.setMessage("Please wait..");
            alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            BufferedReader reader=null;
            String USER_AGENT = "Mozilla/5.0";

            // Send data
            try {
                // Defined URL  where to send data
                URL url = new URL(urls[0]);

                // Send POST data request
                HttpsURLConnection conn = (HttpsURLConnection ) url.openConnection();
                TrustModifier.relaxHostChecking(conn);
                conn.setRequestProperty("User-Agent", USER_AGENT);
                conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                //conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("POST");

                String urlParameters = "name=" + urls[1] + "&job=" + urls[2];

                conn.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                // Get the server response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                // Append Server Response To Content String
                Content = response.toString()+"String Added";
            }
            catch(Exception ex)
            {
                Error = ex.getMessage();
            }
            finally
            {
                try
                {

                    reader.close();
                }

                catch(Exception ex) {}
            }

            /*****************************************************/
            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            if (Error != null) {
                alertDialog.setTitle("Failed");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setMessage("Failed!" + Error);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });

                alertDialog.show();
                //Toast.makeText(getApplicationContext(), "Failed" + Error, Toast.LENGTH_LONG).show();

            } else {
                alertDialog.setTitle("Success");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setMessage("Success!" + Content);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });

                alertDialog.show();

               // Toast.makeText(getApplicationContext(), "success content " + Content, Toast.LENGTH_LONG).show();

            }

        }

    }

}