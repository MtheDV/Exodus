package com.platypi.exodus;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.*;

public class AndroidLauncher extends AndroidApplication implements AdService {

	private InterstitialAd interstitialAd;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout layout = new RelativeLayout(this);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		View gameView = initializeForView(new PixelPlatformer(this), config);
		layout.addView(gameView);

		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId("ca-app-pub-2631649901063502/4430052061");
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() { }

			@Override
			public void onAdClosed() {
			    loadInterstitialAd();
            }
		});

		loadInterstitialAd();

		setContentView(layout);
	}

	private void loadInterstitialAd() {
		AdRequest adRequest = new AdRequest.Builder().build();
		interstitialAd.loadAd(adRequest);
	}

	@Override
	public void showInterstitial() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (interstitialAd.isLoaded())
					interstitialAd.show();
				else
					loadInterstitialAd();
			}
		});
	}

	@Override
	public boolean isInterstitialLoaded() {
		return interstitialAd.isLoaded();
	}
}
