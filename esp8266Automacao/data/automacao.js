var connection = new WebSocket('ws://' + location.hostname + ':81/', ['arduino']);
connection.onopen = function () {
    connection.send("carregar");
};

connection.onerror = function (error) {
    console.log('WebSocket Error ', error);
};
connection.onmessage = function (event) {
    {processReceivedCommand(event);};
};
connection.onclose = function () {
    console.log('WebSocket connection closed.');
}

function processReceivedCommand(evt){
    var json = evt.data;
    var jsonObj = JSON.parse(json);

    var iluminacaoSala = document.getElementsByName("iluminacao-sala-status");
    var iluminacaoQuarto = document.getElementsByName("iluminacao-quarto-status");
    var iluminacaoJardim = document.getElementsByName("iluminacao-jardim-status");

    if(jsonObj.status.iluminacao.sala == "ligado"){
        iluminacaoSala[0].checked = true;
    }else{
        iluminacaoSala[1].checked = true;
    }

    if(jsonObj.status.iluminacao.quarto == "ligado"){
        iluminacaoQuarto[0].checked = true;
    }else{
        iluminacaoQuarto[1].checked = true;
    }

    if(jsonObj.status.iluminacao.jardim == "ligado"){
        iluminacaoJardim[0].checked = true;
    }else{
        iluminacaoJardim[1].checked = true;
    }
   
}


function atualizar(){
    
    var json;
    var checkboxIluminacaoSala = document.getElementsByName("iluminacao-sala-status");
    var checkboxIluminacaoQuarto = document.getElementsByName("iluminacao-quarto-status");
    var checkboxIluminacaoJardim = document.getElementsByName("iluminacao-jardim-status");

    if(checkboxIluminacaoSala[0].checked){
        json = "{\"status\":{\"iluminacao\":{\"sala\":\"ligado\",";
    }else{
        json = "{\"status\":{\"iluminacao\":{\"sala\":\"desligado\",";
    }

    if(checkboxIluminacaoQuarto[0].checked){
        json += "\"quarto\":\"ligado\",";
    }else{
        json += "\"quarto\":\"desligado\",";
    }

    if(checkboxIluminacaoJardim[0].checked){
        json += "\"jardim\":\"ligado\"}}}";
    }else{
        json += "\"jardim\":\"desligado\"}}}";
    }
    connection.send(json);
}