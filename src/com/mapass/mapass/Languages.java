package com.mapass.mapass;

import java.util.ArrayList;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Languages {

	MainActivity actividadPrincipal;
	
	public Languages(MainActivity actividadPrincipal){
		this.actividadPrincipal = actividadPrincipal;
	}
	
	public void toEnglish(){
		Button buttons = (Button) actividadPrincipal.findViewById(R.id.buttonSave);
		buttons.setText("Save");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonCancel);
		buttons.setText("Cancel");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonAccept);
		buttons.setText("Insert");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonDeletemarker);
		buttons.setText("Delete");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonTrazaruta);
		buttons.setText("Trace route");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonUpdateMarker);
		buttons.setText("Modify marker");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonPositionToMarker);
		buttons.setText("Trace route from current position to this marker");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonMarkerToMarker);
		buttons.setText("Trace route from selected marker to another marker");
		buttons = (Button) actividadPrincipal.findViewById(R.id.radioVehiculo);
		buttons.setText("I'll use a vehicle");
		buttons = (Button) actividadPrincipal.findViewById(R.id.radioAndando);
		buttons.setText("I'll use my legs");
		
		EditText edittexts = (EditText) actividadPrincipal.findViewById(R.id.markerTitle);
		edittexts.setText("Title");
		edittexts = (EditText) actividadPrincipal.findViewById(R.id.markerDescription);
		edittexts.setText("Description");
		
		 ArrayList<String> colors = new ArrayList<String>();
		 
		 colors.add("Blue");
		 colors.add("Magenta");
		 colors.add("Cyan");
		 colors.add("Red");
		 colors.add("Rose");
		 colors.add("Violet");
		 colors.add("Yellow");
		 colors.add("Green");
		 colors.add("Orange");
		 
	     ArrayAdapter<String> adapter = new ArrayAdapter<String>(actividadPrincipal, android.R.layout.simple_spinner_item, colors);

	     Spinner spincolores = (Spinner)actividadPrincipal.findViewById(R.id.spinnerColor);
	     spincolores.setAdapter(adapter);
	}
	public void toSpanish(){
		Button buttons = (Button) actividadPrincipal.findViewById(R.id.buttonSave);
		buttons.setText("Guardar");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonCancel);
		buttons.setText("Cancelar");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonAccept);
		buttons.setText("Insertar");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonDeletemarker);
		buttons.setText("Borrar");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonTrazaruta);
		buttons.setText("Trazar ruta");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonUpdateMarker);
		buttons.setText("Modificar marcador");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonPositionToMarker);
		buttons.setText("Trazar ruta desde posición actual al marcador elegido");
		buttons = (Button) actividadPrincipal.findViewById(R.id.buttonMarkerToMarker);
		buttons.setText("Trazar ruta desde el marcador seleccionado a otro");

		buttons = (Button) actividadPrincipal.findViewById(R.id.radioVehiculo);
		buttons.setText("Voy en vehículo");
		buttons = (Button) actividadPrincipal.findViewById(R.id.radioAndando);
		buttons.setText("Voy andando");
		
		EditText edittexts = (EditText) actividadPrincipal.findViewById(R.id.markerTitle);
		edittexts.setText("Título");
		edittexts = (EditText) actividadPrincipal.findViewById(R.id.markerDescription);
		edittexts.setText("Descripción");
		
		ArrayList<String> colors = new ArrayList<String>();
		 
		 colors.add("Azul");
		 colors.add("Magenta");
		 colors.add("Cyan");
		 colors.add("Rojo");
		 colors.add("Rosa");
		 colors.add("Violeta");
		 colors.add("Amarillo");
		 colors.add("Verde");
		 colors.add("Naranja");
		 
	     ArrayAdapter<String> adapter = new ArrayAdapter<String>(actividadPrincipal, android.R.layout.simple_spinner_item, colors);

	     Spinner spincolores = (Spinner)actividadPrincipal.findViewById(R.id.spinnerColor);
	     spincolores.setAdapter(adapter);
		
	}
	
}
