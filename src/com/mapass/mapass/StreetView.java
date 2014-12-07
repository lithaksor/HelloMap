package com.mapass.mapass;


import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class StreetView extends FragmentActivity {
	private static final LatLng SAN_FRAN = new LatLng(37.765927, -122.449972);
	private StreetViewPanorama mSvp;
	private StreetViewPanoramaView mSvpView;
	
	 private StreetViewPanorama svp;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_streetview);

	        Intent previousIntent = getIntent();
	        double latitude = previousIntent.getDoubleExtra("lat", -33.87365);
	        double longitude = previousIntent.getDoubleExtra("long", 151.20689);
	        
	        LatLng latlen = new LatLng(latitude, longitude);
	        
	        setUpStreetViewPanoramaIfNeeded(savedInstanceState, latlen);
	    }

	    private void setUpStreetViewPanoramaIfNeeded(Bundle savedInstanceState, LatLng latlen) {
	        if (svp == null) {
	            svp = ((SupportStreetViewPanoramaFragment)
	                getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama))
	                    .getStreetViewPanorama();
	            if (svp != null) {
	                if (savedInstanceState == null) {
	                    svp.setPosition(latlen);
	                }
	            }
	        }
	    }
}
