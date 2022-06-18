package com.app.automacaoresidencial;

public class InformationFromJSON {
    private String iluminacaoSala;
    private String iluminacaoQuarto;
    private String iluminacaoJardim;

    public void setEstadoIluminacaoSala(String estadoAtual) {
        this.iluminacaoSala = estadoAtual;
    }

    public String getEstadoIluminacaoSala() {
        return iluminacaoSala;
    }

    public void setEstadoIluminacaoQuarto(String estadoAtual) {
        this.iluminacaoQuarto = estadoAtual;
    }

    public String getEstadoIluminacaoQuarto() {
        return iluminacaoQuarto;
    }

    public void setEstadoIluminacaoJardim(String estadoAtual){
        this.iluminacaoJardim = estadoAtual;
    }

    public String getEstadoIluminacaoJardim() {
        return iluminacaoJardim;
    }
}
