function carregar(){
    
}



function atualizar(){
    /*  
    Espaço destinado ao
    desenvolvimento da validação
    das mudanças feitas pelo usuário;
    */
    var json;
    var checkboxIluminacaoSala = document.getElementsByName("iluminacao-sala-status");
    var checkboxIluminacaoQuarto = document.getElementsByName("iluminacao-quarto-status");

    if(checkboxIluminacaoSala[0].checked){
        json = "{\"status\":{\"iluminacao\":{\"sala\":\"ligado\","; //\"quarto\":\"ligado\"}}}"
    }else{
        json = "{\"status\":{\"iluminacao\":{\"sala\":\"desligado\"," //\"quarto\":\"ligado\"}}}"
    }

    if(checkboxIluminacaoQuarto[0].checked){
        json += "\"quarto:\":\"ligado\"}}}";
    }

    location.reload();    
}