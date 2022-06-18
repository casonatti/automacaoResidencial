#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <WebSocketsServer.h>
#include <FS.h>

#define LIGADO true
#define DESLIGADO false

ESP8266WebServer server(80);
WebSocketsServer webSocket(81);

File fsUploadFile;

const char* mdnsName = "esp";
const char* ssid = "Casonatti";
const char* password = "N@rberto";

const int iluminacaoSala = 4;
const int iluminacaoQuarto = 5;
const int iluminacaoJardim = 0;

bool estadoIluminacaoSala;
bool estadoIluminacaoQuarto;
bool estadoIluminacaoJardim;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  delay(10);

  pinMode(iluminacaoSala, OUTPUT);
  pinMode(iluminacaoQuarto, OUTPUT);
  pinMode(iluminacaoJardim, OUTPUT);
  estadoIluminacaoSala = false;
  estadoIluminacaoQuarto = false;
  estadoIluminacaoJardim = false;
  digitalWrite(iluminacaoSala, LOW);
  digitalWrite(iluminacaoQuarto, LOW);
  digitalWrite(iluminacaoJardim, LOW);

  startWiFi();
  startSPIFFS();
  startWebSocket();
  startMDNS();
  startServer();
}

void loop() {
  // put your main code here, to run repeatedly:
  webSocket.loop();
  server.handleClient();
}

void startWiFi() {
  Serial.print("Conectando a: ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println('\n');
  Serial.print("Conectado a rede sem fio ");
  Serial.println(ssid);
  Serial.print("IP Adress (ESP8266):\t");
  Serial.println(WiFi.localIP());
}

void startSPIFFS() {
  SPIFFS.begin();
  Serial.println("\nSPIFFS started. Contents:\t");
  {
    Dir dir = SPIFFS.openDir("/");
    while (dir.next()) {
      String fileName = dir.fileName();
      size_t fileSize = dir.fileSize();
      Serial.printf("FS File: %s, size: %s\r\n", fileName.c_str(), formatBytes(fileSize).c_str());
    }
    Serial.printf("\n");
  }
}

void startWebSocket() {
  webSocket.begin();
  webSocket.onEvent(webSocketEvent);
  Serial.println("WebSocket server started.");
}

void startMDNS() {
  MDNS.begin(mdnsName);
  Serial.print("mDNS responder started: http://");
  Serial.print(mdnsName);
  Serial.println(".local");
}

void startServer() {
  server.on("/", HTTP_POST, []() {
    server.send(200, "text/plain", "");
  }, handleFileUpload);

  server.onNotFound(handleNotFound);

  server.begin();
  Serial.println("HTTP server started.");
}

void handleNotFound() {
  if (!handleFileRead(server.uri())) {
    server.send(404, "text/plain", "404: File Not Found");
  }
}

bool handleFileRead(String path) {
  Serial.println("handleFileRead: " + path);
  if (path.endsWith("/"))
    path += "automacao.html";
  String contentType = getContentType(path);
  String pathWithGz = path + ".gz";
  if (SPIFFS.exists(pathWithGz) || SPIFFS.exists(path)) {
    if (SPIFFS.exists(pathWithGz))
      path += ".gz";
    File file = SPIFFS.open(path, "r");
    size_t sent = server.streamFile(file, contentType);
    file.close();
    Serial.println(String("\tSent file: ") + path);
    return true;
  }
  Serial.println(String("\tFile Not Found: ") + path);
  return false;
}

void handleFileUpload() {
  HTTPUpload& upload = server.upload();
  String path;
  if (upload.status == UPLOAD_FILE_START) {
    path = upload.filename;
    if (!path.startsWith("/"))
      path = "/" + path;
    if (!path.endsWith(".gz")) {
      String pathWithGz = path + ".gz";
      if (SPIFFS.exists(pathWithGz))
        SPIFFS.remove(pathWithGz);
    }
    Serial.print("handleFileUpload Name: ");
    fsUploadFile = SPIFFS.open(path, "w");
    path = String();
  } else if (upload.status == UPLOAD_FILE_WRITE) {
    if (fsUploadFile)
      fsUploadFile.write(upload.buf, upload.currentSize);
  } else if (upload.status == UPLOAD_FILE_END) {
    if (fsUploadFile) {
      fsUploadFile.close();
      Serial.print("handleFileUpload Size: ");
      Serial.println(upload.totalSize);
      server.sendHeader("Location", "/");
      server.send(303);
    } else {
      server.send(500, "text/plain", "500: couldn't create file");
    }
  }
}

void webSocketEvent(uint8_t num, WStype_t type, uint8_t* payload, size_t lenght) {
  switch (type) {
    case WStype_DISCONNECTED:
      Serial.printf("[%u] Disconnected!\n", num);
      break;
    case WStype_CONNECTED: {
        IPAddress ip = webSocket.remoteIP(num);
        Serial.printf("[%u] Connected from %d.%d.%d.%d url: %s\n", num, ip[0], ip[1], ip[2], ip[3], payload);
      }
      break;
    case WStype_TEXT:
      Serial.printf("[%u] get Text: %s\n", num, payload);
      String payloadStr = (char*) payload;

      if (payloadStr == "carregar") {
        File fileRead = SPIFFS.open("/estadoAtual.json", "r");
        String fileContent;
        while (fileRead.available())
          fileContent += char(fileRead.read());
        Serial.println(fileContent);
        webSocket.broadcastTXT(fileContent);
        setStatus(fileContent);
      } else {
        //recebe string no formato .json do client
        //reescreve o arquivo .json com as atualizações dos estados
        //muda o estado dos 'leds'
        File fileWrite = SPIFFS.open("/estadoAtual.json", "w");
        String json = payloadStr;
        int bytesWritten = fileWrite.print(json);
        if (bytesWritten > 0)
          Serial.printf("Arquivo atualizado com sucesso!\n\n");
        else
          Serial.printf("ERRO! Falha ao atualizar o arquivo estadoAtual.json");
        
        fileWrite.close();

        setStatus(json);
      }
      break;
  }
}

String formatBytes(size_t bytes) {
  if (bytes < 1024) {
    return String(bytes) + "B";
  } else if (bytes < (1024 * 1024)) {
    return String(bytes / 1024.0) + "KB";
  } else if (bytes < (1024 * 1024 * 1024)) {
    return String(bytes / 1024.0 / 1024.0) + "MB";
  }
}

String getContentType(String filename) { // determine the filetype of a given filename, based on the extension
  if (filename.endsWith(".html")) return "text/html";
  else if (filename.endsWith(".css")) return "text/css";
  else if (filename.endsWith(".js")) return "application/javascript";
  else if (filename.endsWith(".ico")) return "image/x-icon";
  else if (filename.endsWith(".gz")) return "application/x-gzip";
  else if (filename.endsWith(".json")) return "application/json";
  return "text/plain";
}

void setStatus(String estadoAtual) {
  const size_t capacity = 2 * JSON_OBJECT_SIZE(1) + JSON_OBJECT_SIZE(3) + 100;
  DynamicJsonDocument doc(capacity);

  deserializeJson(doc, estadoAtual);

  const char* status_iluminacao_sala = doc["status"]["iluminacao"]["sala"];
  const char* status_iluminacao_quarto = doc["status"]["iluminacao"]["quarto"];
  const char* status_iluminacao_jardim = doc["status"]["iluminacao"]["jardim"];
  String iluminacaoSalaStr = status_iluminacao_sala;
  String iluminacaoQuartoStr = status_iluminacao_quarto;
  String iluminacaoJardimStr = status_iluminacao_jardim;

  if (iluminacaoSalaStr == "ligado") {
    estadoIluminacaoSala = LIGADO;
    digitalWrite(iluminacaoSala, HIGH);
  } else {
    estadoIluminacaoSala = DESLIGADO;
    digitalWrite(iluminacaoSala, LOW);
  }

  if (iluminacaoQuartoStr == "ligado") {
    estadoIluminacaoQuarto = LIGADO;
    digitalWrite(iluminacaoQuarto, HIGH);
  } else {
    estadoIluminacaoQuarto = DESLIGADO;
    digitalWrite(iluminacaoQuarto, LOW);
  }

  if (iluminacaoJardimStr == "ligado") {
    estadoIluminacaoJardim = LIGADO;
    digitalWrite(iluminacaoJardim, HIGH);
  } else {
    estadoIluminacaoJardim = DESLIGADO;
    digitalWrite(iluminacaoJardim, LOW);
  }
}
