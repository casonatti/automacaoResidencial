package com.app.automacaoresidencial;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.MalformedURLException;

public class Menu extends AppCompatActivity {
    String json;
    InformationFromJSON info = new InformationFromJSON();
    Utils util = new Utils();
    private Iluminacao iluminacaoSala = new Iluminacao();
    private Iluminacao iluminacaoQuarto = new Iluminacao();
    private Iluminacao iluminacaoJardim = new Iluminacao();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String url = "http://192.168.0.22/estadoAtual.json";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Response", response);

                try {
                    json = response;
                    info = util.getInformacao(response);
                    iluminacaoSala.setLocal("Sala");
                    iluminacaoSala.setEstadoAtual(info.getEstadoIluminacaoSala());
                    iluminacaoQuarto.setLocal("Quarto");
                    iluminacaoQuarto.setEstadoAtual(info.getEstadoIluminacaoQuarto());
                    iluminacaoJardim.setLocal("Jardim");
                    iluminacaoJardim.setEstadoAtual(info.getEstadoIluminacaoJardim());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Response", "Error! Didn't work!");
            }
        });
        queue.add(getRequest);
    }

    public void viewIluminacao(View view){
        Intent telaIluminacao = new Intent(this, ActivityIluminacao.class);
        telaIluminacao.putExtra("JSON", json);
        telaIluminacao.putExtra("estadoSala", iluminacaoSala.getEstadoAtual());
        telaIluminacao.putExtra("estadoQuarto", iluminacaoQuarto.getEstadoAtual());
        telaIluminacao.putExtra("estadoJardim", iluminacaoJardim.getEstadoAtual());
        startActivity(telaIluminacao);
    }

    public void viewJardim(View view){
        Intent telaJardim = new Intent(this, ActivityJardim.class);
        startActivity(telaJardim);
    }

    public void viewJanelas(View view){
        Intent telaJanelas = new Intent(this, ActivityJanelas.class);
        startActivity(telaJanelas);
    }

    public void viewCameras(View view){
        Intent telaCameras = new Intent(this, ActivityCameras.class);
        startActivity(telaCameras);
    }

    public void viewStatus(View view){
        Intent telaStatus = new Intent(this, ActivityStatus.class);
        startActivity(telaStatus);
    }

}
