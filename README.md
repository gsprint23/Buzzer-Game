Buzzer! Game
------------

Buzzer! app for playing live Jeopardy like games

Compare to hardware implementations such as
* https://www.buzzersystems.com/
* https://www.quizgamebuzzers.com

### Model-View-Controller Design
* Model: Server
* View: ServerGUI
* Controller: ServerController
 
### To Run
* java MainServer portNumber
* java MainClient serverHostName portNumber

Default values are
* portNumber 8080
* serverHostName localhost

### Dependencies
* Java version >= 8
* Java Swing