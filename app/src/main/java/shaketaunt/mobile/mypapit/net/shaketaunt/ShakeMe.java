/*
Copyright 2018 Mohammad Hafiz bin Ismail <mypapit@gmail.com>
 http://blog.mypapit.net

Redistribution and use in source and binary forms, with or without modification, are permitted
provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions
and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.

 */

package shaketaunt.mobile.mypapit.net.shaketaunt;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.franmontiel.attributionpresenter.AttributionPresenter;
import com.franmontiel.attributionpresenter.entities.Attribution;
import com.franmontiel.attributionpresenter.entities.Library;
import com.franmontiel.attributionpresenter.entities.License;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.squareup.seismic.ShakeDetector;

import java.security.SecureRandom;
import java.util.Locale;

public class ShakeMe extends AppCompatActivity implements ShakeDetector.Listener {

    public final String message[] = new String[]{"just you are", "beautiful!", "naughty!", "cowardly lion",
            "lazy", "hardworking", "ugly duckly", "fabulous", "mean", "moody", "selfish", "prudent", "heroic",
            "tough as nail", "kind", "glamorous", "vain", "strong", "like a lady",
            "gorgeous!", "strong", "stupendous", "superb", "pompous!", "amazing", "great", "bossy", "cuckoo", "outrageous",
            "courageous", "adaptable", "adventurous", "frank", "passionate", "rational", "witty", "childish", "friendly", "super friendly", "clever", "industrious",
            "Prince Charming", "grumpy", "jealous", "mean", "vulgar", "silly!"};
    TextToSpeech speech;
    TextView tvStatus;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_me);


        tvStatus = findViewById(R.id.tvStatus);
        MobileAds.initialize(this, getString(R.string.admobid));

        AdView mAdView = findViewById(R.id.adView);

        Bundle extras = new Bundle();
        extras.putString("max_ad_content_rating", "G");

        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();


        mAdView.loadAd(adRequest);



        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interadmob));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

        speech = new TextToSpeech(ShakeMe.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speech.setLanguage(Locale.US);


                }
            }
        });






    }


    public void onResume(){
        speech = new TextToSpeech(ShakeMe.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speech.setLanguage(Locale.US);


                }
            }
        });
        super.onResume();


    }

    public void onPause() {
        if (speech != null) {
            speech.stop();
            speech.shutdown();
        }

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("mypapitx", "The interstitial wasn't loaded yet.");
        }


        super.onPause();

    }

    @Override
    public void hearShake() {

        int maxnum = message.length;

        SecureRandom random = new SecureRandom();

        int num = random.nextInt(maxnum);

        String text = new String("You are " + message[num]);
        tvStatus.setText(text);

        Log.d("speex",text);



        speech.speak(text, TextToSpeech.QUEUE_FLUSH, null);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuabout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:
                showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showHelp() {
        AttributionPresenter attributionPresenter = new AttributionPresenter.Builder(this)
                .addAttributions(
                        new Attribution.Builder(getString(R.string.app_name))
                                .addCopyrightNotice(getString(R.string.copyright))
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/mypapit/")
                                .build()
                )

                .build();

        attributionPresenter.showDialog("About");
    }


}
