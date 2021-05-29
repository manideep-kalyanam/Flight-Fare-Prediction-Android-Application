package com.example.flightfarepredictor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Calendar;

public class MainScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Interpreter interpreter;
    int pid,prev1=0,prev2=5;
    String Dd, Dm, DeH, DeM, ArH, ArM;
    float DepMins, ArMins, TotalMins;
    String[] source={"Source","Banglore", "Chennai", "Delhi", "Kolkata","Mumbai"};
    String[] destination={"Destination","Banglore", "Cochin", "Delhi", "Hyderabad", "Kolkata", "New Delhi"};
    String[] stops={"Stoppage","Non-stop","1 stop","2 stops","3 stops","4 stops"};

    TextView tvDate,tvDepTime,tvArrTime,tvAA,tvAI,tvGA,tvIG,tvJet,tvJetB,tvMC,tvMCP,tvSJ,tvTJ,tvVis,tvVisP;
    private int mYear, mMonth, mDay, mDHour, mDMinute, mAHour, mAMinute;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    float[] input = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                     0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);

        try {
            interpreter = new Interpreter(loadModelFile(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        tvDate=(TextView) findViewById(R.id.textViewDate);
        tvDepTime=(TextView)findViewById(R.id.textViewDep);
        tvArrTime=(TextView)findViewById(R.id.textViewArr);
        tvAA = (TextView) findViewById(R.id.textAirAsia) ;
        tvAI = (TextView) findViewById(R.id.textAirIndia);
        tvGA = (TextView) findViewById(R.id.textGoAir);
        tvIG = (TextView) findViewById(R.id.textIndigo);
        tvJet = (TextView) findViewById(R.id.textJet);
        tvJetB = (TextView) findViewById(R.id.textJetB);
        tvMC = (TextView) findViewById(R.id.textMulti);
        tvMCP = (TextView) findViewById(R.id.textMultiPrem);
        tvSJ = (TextView) findViewById(R.id.textSpice);
        tvTJ = (TextView) findViewById(R.id.textTrujet);
        tvVis = (TextView) findViewById(R.id.textVistara);
        tvVisP = (TextView) findViewById(R.id.textVistaraPrem);


        Spinner spin1 = (Spinner) findViewById(R.id.spinner1);
        spin1.setOnItemSelectedListener(this);
        ArrayAdapter aa1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,source);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(aa1);

        Spinner spin2 = (Spinner) findViewById(R.id.spinner2);
        spin2.setOnItemSelectedListener(this);
        ArrayAdapter aa2 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,destination);
        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(aa2);

        Spinner spin3 = (Spinner) findViewById(R.id.spinner3);
        spin3.setOnItemSelectedListener(this);
        ArrayAdapter aa3 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,stops);
        aa3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin3.setAdapter(aa3);

    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("FlightFare.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length);
    }

    public float doInference(float[][] val) {
        float[][] output = new float[1][1];
        interpreter.run(val, output);
        return output[0][0];
    }


    @Override
    public void onItemSelected(AdapterView<?> x, View arg1, int position,long id) {
        pid=x.getId();
        if(position>0 && pid==R.id.spinner1) {
            input[prev1]=0;
            Toast.makeText(getApplicationContext(), source[position], Toast.LENGTH_LONG).show();
            input[position - 1] = 1;
            prev1=position-1;
            Log.i("spinner1", Arrays.toString(input));
        }
        if(position>0 && pid==R.id.spinner2) {
            input[prev2]=0;
            Toast.makeText(getApplicationContext(), destination[position], Toast.LENGTH_LONG).show();
            input[5+position - 1] = 1;
            prev2=5+position-1;
            Log.i("spinner2", Arrays.toString(input));
        }
        if(position>0 && pid==R.id.spinner3) {
            Toast.makeText(getApplicationContext(), stops[position], Toast.LENGTH_LONG).show();
            input[19] = position-1;
            Log.i("spinner3", Arrays.toString(input));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> x) {
    }


    public void date(View view) {
        final Calendar c= Calendar.getInstance();
        mYear=c.get(Calendar.YEAR);
        mMonth=c.get(Calendar.MONTH);
        mDay=c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainScreen.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                input[11] = dayOfMonth;
                input[12] = monthOfYear+1;
                if (dayOfMonth<10){
                    Dd = "0" + String.valueOf(dayOfMonth);
                }
                else{
                    Dd = String.valueOf(dayOfMonth);
                }
                if (monthOfYear<9){
                    Dm = "0" + String.valueOf(monthOfYear+1);
                }
                else{
                    Dm = String.valueOf(monthOfYear+1);
                }
                tvDate.setText(Dd + "-" + Dm + "-" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    public void Departure(View view) {

        final Calendar c = Calendar.getInstance();
        mDHour = c.get(Calendar.HOUR_OF_DAY);
        mDMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(MainScreen.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                input[13]=hourOfDay;
                input[14]=minute;
                if (hourOfDay<10){
                    DeH = "0" + String.valueOf(hourOfDay);
                }
                else{
                    DeH = String.valueOf(hourOfDay);
                }
                if (minute<10){
                    DeM = "0" + String.valueOf(minute);
                }
                else{
                    DeM = String.valueOf(minute);
                }
                tvDepTime.setText(DeH + ":" + DeM);
            }
        }, mDHour, mDMinute, false);
        timePickerDialog.show();
    }


    public void Arrival(View view) {

        final Calendar c = Calendar.getInstance();
        mAHour = c.get(Calendar.HOUR_OF_DAY);
        mAMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(MainScreen.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                input[15]=hourOfDay;
                input[16]=minute;
                if (hourOfDay<10){
                    ArH = "0" + String.valueOf(hourOfDay);
                }
                else{
                    ArH = String.valueOf(hourOfDay);
                }
                if (minute<10){
                    ArM = "0" + String.valueOf(minute);
                }
                else{
                    ArM = String.valueOf(minute);
                }
                tvArrTime.setText(ArH + ":" + ArM);
            }
        }, mAHour, mAMinute, false);
        timePickerDialog.show();
    }


    public void show(View view) {
        DepMins = input[13]*60 + input[14];
        ArMins = input[15]*60 + input[16];
        TotalMins = ArMins - DepMins;
        input[17]= TotalMins/60;
        input[18]= TotalMins%60;

        float[] dummy = input;
        float[][] dummyinput = {dummy};

        dummyinput[0][20]=1;
        float dummyout = doInference(dummyinput);
        tvAA.setText("Air Asia - Rs."+String.valueOf(dummyout));

        dummyinput[0][20]=0;
        dummyinput[0][21]=1;
        dummyout = doInference(dummyinput);
        tvAI.setText("Air India - Rs."+String.valueOf(dummyout));

        dummyinput[0][21]=0;
        dummyinput[0][22]=1;
        dummyout = doInference(dummyinput);
        tvGA.setText("Go Air - Rs."+String.valueOf(dummyout));

        dummyinput[0][22]=0;
        dummyinput[0][23]=1;
        dummyout = doInference(dummyinput);
        tvIG.setText("IndiGo - Rs."+String.valueOf(dummyout));

        dummyinput[0][23]=0;
        dummyinput[0][24]=1;
        dummyout = doInference(dummyinput);
        tvJet.setText("Jet Airways - Rs."+String.valueOf(dummyout));

        dummyinput[0][24]=0;
        dummyinput[0][25]=1;
        dummyout = doInference(dummyinput);
        tvJetB.setText("Jet Airways Business - Rs."+String.valueOf(dummyout));

        dummyinput[0][25]=0;
        dummyinput[0][26]=1;
        dummyout = doInference(dummyinput);
        tvMC.setText("Multiple carriers - Rs."+String.valueOf(dummyout));

        dummyinput[0][26]=0;
        dummyinput[0][27]=1;
        dummyout = doInference(dummyinput);
        tvMCP.setText("Multiple carriers Premium economy - Rs."+String.valueOf(dummyout));

        dummyinput[0][27]=0;
        dummyinput[0][28]=1;
        dummyout = doInference(dummyinput);
        tvSJ.setText("SpiceJet - Rs."+String.valueOf(dummyout));

        dummyinput[0][28]=0;
        dummyinput[0][29]=1;
        dummyout = doInference(dummyinput);
        tvTJ.setText("Trujet - Rs."+String.valueOf(dummyout));

        dummyinput[0][29]=0;
        dummyinput[0][30]=1;
        dummyout = doInference(dummyinput);
        tvVis.setText("Vistara - Rs."+String.valueOf(dummyout));

        dummyinput[0][30]=0;
        dummyinput[0][31]=1;
        dummyout = doInference(dummyinput);
        tvVisP.setText("Vistara Premium economy - Rs."+String.valueOf(dummyout));

    }
}