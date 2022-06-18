package com.app.automacaoresidencial;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

public class Utils {
    public InformationFromJSON getInformacao(String json) throws MalformedURLException {
        InformationFromJSON retorno;
        Log.i("Resultado", json);
        retorno = parseJson(json);

        return retorno;
    }

    private InformationFromJSON parseJson(String json){
        try{
            InformationFromJSON info = new InformationFromJSON();

            JSONObject jsonObj = new JSONObject(json);
            JSONObject statusObj = jsonObj.getJSONObject("status");

            //json = {"status":{"iluminacao":{ .....
            JSONObject iluminacaoObj = statusObj.getJSONObject("iluminacao");
            info.setEstadoIluminacaoSala(iluminacaoObj.getString("sala"));
            info.setEstadoIluminacaoQuarto(iluminacaoObj.getString("quarto"));
            info.setEstadoIluminacaoJardim(iluminacaoObj.getString("jardim"));

            return info;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
