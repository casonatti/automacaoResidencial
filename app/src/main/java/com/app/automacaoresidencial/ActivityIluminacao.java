package com.app.automacaoresidencial;



import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityIluminacao extends AppCompatActivity {
    private JSONObject json = new JSONObject();
    private Iluminacao iluminacaoSala = new Iluminacao();
    private Iluminacao iluminacaoQuarto = new Iluminacao();
    private Iluminacao iluminacaoJardim = new Iluminacao();
    private TextView salaStatus;
    private TextView quartoStatus;
    private TextView jardimStatus;
    private Switch swtIluminacaoSala;
    private Switch swtIluminacaoQuarto;
    private Switch swtIluminacaoJardim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iluminacao);

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            iluminacaoSala.setLocal("Sala");
            iluminacaoSala.setEstadoAtual(extras.getString("estadoSala"));

            iluminacaoQuarto.setLocal("Quarto");
            iluminacaoQuarto.setEstadoAtual(extras.getString("estadoQuarto"));

            iluminacaoJardim.setLocal("Jardim");
            iluminacaoJardim.setEstadoAtual(extras.getString("estadoJardim"));
        }

        salaStatus = findViewById(R.id.txtStatusSala);
        quartoStatus = findViewById(R.id.txtStatusQuarto);
        jardimStatus = findViewById(R.id.txtStatusJardim);
        swtIluminacaoSala = findViewById(R.id.swtIluminacaoSala);
        swtIluminacaoQuarto = findViewById(R.id.swtIluminacaoQuarto);
        swtIluminacaoJardim = findViewById(R.id.swtIluminacaoJardim);

        salaStatus.setText(iluminacaoSala.getEstadoAtual());
        quartoStatus.setText(iluminacaoQuarto.getEstadoAtual());
        jardimStatus.setText(iluminacaoJardim.getEstadoAtual());
        setSwitchText(swtIluminacaoSala, iluminacaoSala.getEstadoAtual());
        setSwitchText(swtIluminacaoQuarto, iluminacaoQuarto.getEstadoAtual());
        setSwitchText(swtIluminacaoJardim, iluminacaoJardim.getEstadoAtual());

        swtIluminacaoSala.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //MUDAR O JSON PARA ENVIAR NUM POST
                    JSONObject jsonObj = new JSONObject();
                    JSONObject statusObj = new JSONObject();
                    JSONObject iluminacaoObj = new JSONObject();

                    try {
                        jsonObj.put("status", statusObj);
                        statusObj.put("iluminacao", iluminacaoObj);
                        iluminacaoObj.put("sala", iluminacaoSala.getEstadoAtual());
                        iluminacaoObj.put("jardim", iluminacaoJardim.getEstadoAtual());
                        iluminacaoObj.put("quarto", iluminacaoQuarto.getEstadoAtual());
                        json = jsonObj;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //CRIAR HTTP POST COM O JSON GERADO

                }else{

                }
            }
        });
    }

    private void setSwitchText(Switch swt, String status){
        if(status.equals("ligado")){
            swt.setText("Desligar");
            swt.setChecked(true);
        }else{
            swt.setText("Ligar");
            swt.setChecked(false);
        }
    }

    private void post(){

    }
}
