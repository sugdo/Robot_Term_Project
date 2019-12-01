#include <DHT.h>
#include <SoftwareSerial.h>

#define DHTPIN A1
#define DHTTYPE DHT11
#define BT_RXD 7
#define BT_TXD 8

SoftwareSerial bluetooth(BT_RXD, BT_TXD);

DHT dht(DHTPIN, DHTTYPE);
int humidity;
int temperature;
byte data;

// 데이터를 수신 받을 버퍼
byte buffer[100];

//버퍼에 데이터를 저장할 때 기록할 위치
int bufferPosition;


String str;


byte mybuffer[4];


char s1[10];
char s2[10];



void setup() {
  Serial.begin(9600);
  bluetooth.begin(9600);
}






void loop() {

  
   humidity = dht.readHumidity();
   temperature = dht.readTemperature();



    //mybuffer[0] = (byte)humidity;
    //Serial.write((byte)s[0]);
    //Serial.write((byte)s[1]);

   



   

   if( humidity != 0 || temperature != 0) {

   Serial.print("humidity:");         
   Serial.println(humidity);      
            
   Serial.print("temperature:");       
   Serial.println(temperature);

    itoa(temperature,s1,10);
    itoa(humidity,s2,10);
    
    if(bluetooth.available()) {
      data = bluetooth.read();
      Serial.write(data);
      buffer[bufferPosition++] = data;

      

    }

    //문자열 종료 표시 
    if(data = '\n') {

      delay(1000);
      buffer[bufferPosition++] = (byte)s1[0];
      buffer[bufferPosition++] = (byte)s1[1];
      buffer[bufferPosition++] = (',');
      buffer[bufferPosition++] = (byte)s2[0];
      buffer[bufferPosition++] = (byte)s2[1];
      buffer[bufferPosition++] = (',');
      
      buffer[bufferPosition]='\0';
      bluetooth.write(buffer,bufferPosition);
      bufferPosition=0;
    }

  }
   delay (100); 
  
}
