#include <LiquidCrystal.h>
#include <Servo.h>

Servo myServo;
//LiquidCrystal lcd(8, 13, 9, 4, 5, 6, 7);
String inputStr;

int pos = 0;    // variable to store the servo position

void setup() {
  myServo.attach(19);  // attaches the servo on pin 19 to the servo object
  myServo.write(90);
  
  // set up the LCD's number of columns and rows:
  //lcd.begin(16, 2);
  
  // Turn on Serial port at 9600 baud
  Serial.begin(9600);
  

  // Print a message to the LCD.
  //lcd.print("Scan a QR Code  ");
}

void loop() {
  // stop servo motor
  myServo.write(90);
  
  //lcd.setCursor(0, 0);
  
  // when data is received
  if (Serial.available() > 0) {
    // read the string received and store into inputStr
    inputStr = Serial.readString();

    // print received string
    Serial.println("Rcvd: " + inputStr);
  
    if (inputStr.equals("on")) {
     myServo.write(85);
    delay(1800);
    myServo.write(90);
    delay(3000);
    myServo.write(96);
    delay(2700);
    myServo.write(90);
      
      Serial.println("Door unlocked!");
      
      //lcd.print("Door unlocked!  ");
      //delay(3000);
      
      //lcd.setCursor(0, 0);
      //lcd.print("Scan a QR Code  ");
    }
    else {
      Serial.println("Invalid key!");
      //lcd.print("Invalid key!    ");    
      //delay(3000);
      //lcd.setCursor(0, 0);
      //lcd.print("Scan a QR Code  ");
    }
  }  
}
