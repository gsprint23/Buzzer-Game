Buzzer! Game
------------

Buzzer! app for playing live Jeopardy like games

Compare to hardware implementations such as
* https://www.buzzersystems.com/
* https://www.quizgamebuzzers.com

### Server MVC Design (com.ginasprint.buzzer.server)
* Model: Server
* View: ServerGUI
* Controller: ServerController

### Client MVC Design (com.ginasprint.buzzer.client)
* Model: Client
* View: ClientGUI
* Controller: ClientController
 
### To Run
* java MainServer portNumber
* java MainClient serverHostName portNumber

Default values are
* portNumber 8080
* serverHostName localhost

### Dependencies
* Java version >= 8
* Java Swing
