package dev.salgino.gasapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.webkit.WebView;

import dev.salgino.gasapp.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // get our html content
        String htmlAsString = getString(R.string.help_string);      // used by WebView
        Spanned htmlAsSpanned = Html.fromHtml(htmlAsString); // used by TextView

        // set the html content on a TextView
        //TextView textView = (TextView) findViewById(R.id.textView);
        //textView.setText(htmlAsSpanned);

        WebView webView = findViewById(R.id.webView);
        webView.loadDataWithBaseURL(null, htmlAsString, "text/html", "utf-8", null);
    }

    private void intentToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void intentToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){

        if (getIntent().hasExtra("from")) {
            intentToLogin();
        }else{
            intentToMain();
        }


    }
}
