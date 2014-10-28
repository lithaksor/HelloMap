package com.mapass.mapass;

import android.graphics.Color;
import android.view.View;
import android.widget.Spinner;

public class ViewsInitializer {

	private MainActivity actividadMapa;
	
	public ViewsInitializer (MainActivity views){
		actividadMapa = views;
	}
	
	public void initializeViews(){

		// Needed as reference for other UI objects
        View invisiblereferencebutton=actividadMapa.findViewById(R.id.buttonModifymarker);
        invisiblereferencebutton.setVisibility(View.INVISIBLE);
        
        // Make inputs invisible and set clicklisteners
        View inputs=actividadMapa.findViewById(R.id.markerTitle);
        inputs.setVisibility(View.INVISIBLE);
        inputs.setOnClickListener(actividadMapa);
        
        inputs=actividadMapa.findViewById(R.id.markerDescription);
        inputs.setVisibility(View.GONE);
        inputs.setOnClickListener(actividadMapa);
        
        inputs=actividadMapa.findViewById(R.id.buttonAccept);
        inputs.setVisibility(View.INVISIBLE);
        inputs.setOnClickListener(actividadMapa);
        
        inputs=actividadMapa.findViewById(R.id.buttonSave);
        inputs.setVisibility(View.GONE);
        inputs.setOnClickListener(actividadMapa);
        
        inputs=actividadMapa.findViewById(R.id.buttonCancel);
        inputs.setVisibility(View.GONE);
        inputs.setOnClickListener(actividadMapa);
        
        inputs=actividadMapa.findViewById(R.id.buttonDeletemarker);
        inputs.setOnClickListener(actividadMapa);
        inputs.setVisibility(View.INVISIBLE); 
        
        inputs=actividadMapa.findViewById(R.id.buttonChangemap);
        inputs.setOnClickListener(actividadMapa);
        
        inputs=actividadMapa.findViewById(R.id.buttonLeft);
        inputs.setOnClickListener(actividadMapa);
        
        inputs=actividadMapa.findViewById(R.id.buttonRight);
        inputs.setOnClickListener(actividadMapa);
        
        inputs=actividadMapa.findViewById(R.id.buttonSearch);
        inputs.setOnClickListener(actividadMapa);
        inputs.setVisibility(View.VISIBLE);
        
        inputs=actividadMapa.findViewById(R.id.buttonOK);
        inputs.setOnClickListener(actividadMapa);
        inputs.setVisibility(View.GONE);
        
        inputs=actividadMapa.findViewById(R.id.editSearch);
        inputs.setVisibility(View.GONE);
        
        inputs=actividadMapa.findViewById(R.id.buttonHelp);
        inputs.setOnClickListener(actividadMapa);
        inputs.setVisibility(View.VISIBLE);
        
        inputs=actividadMapa.findViewById(R.id.buttonLang);
        inputs.setOnClickListener(actividadMapa);
        
        inputs=actividadMapa.findViewById(R.id.editplaceholder);
        inputs.setVisibility(View.INVISIBLE);
        
        inputs=actividadMapa.findViewById(R.id.buttonplaceholder);
        inputs.setVisibility(View.INVISIBLE);
        
        inputs=actividadMapa.findViewById(R.id.buttonTrazaruta);
        inputs.setVisibility(View.GONE);
        inputs.setOnClickListener(actividadMapa);
        
        inputs=actividadMapa.findViewById(R.id.buttonUpdateMarker);
        inputs.setVisibility(View.GONE);
        inputs.setOnClickListener(actividadMapa);
        
        inputs=actividadMapa.findViewById(R.id.buttonPositionToMarker);
        inputs.setVisibility(View.GONE);
        inputs.setOnClickListener(actividadMapa);
        
        inputs=actividadMapa.findViewById(R.id.buttonMarkerToMarker);
        inputs.setVisibility(View.GONE);
        inputs.setOnClickListener(actividadMapa);
        
        inputs=actividadMapa.findViewById(R.id.radioGroup1);
        inputs.setVisibility(View.GONE);
        inputs.setOnClickListener(actividadMapa);
        
        Spinner spinner=(Spinner) actividadMapa.findViewById(R.id.spinnerColor);
        spinner.setVisibility(View.GONE);
        spinner=(Spinner) actividadMapa.findViewById(R.id.spinnerMarkers);
        spinner.setVisibility(View.GONE);
	}
	
}
