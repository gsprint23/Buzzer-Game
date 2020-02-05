Buzzer! Game
------------

Buzzer! app for playing live Jeopardy like games

Compare to hardware implementations such as
* https://www.buzzersystems.com/
* https://www.quizgamebuzzers.com

### Screenshots
Server  
<img src="" width="500"/>

Client (Mac)
<img src="" width="300"/>
Client (Linux)  
<img src="" width="300"/>

### Server MVC Design (com.ginasprint.buzzer.server)
* Model: Server
* View: ServerGUI
* Controller: ServerController

### Client MVC Design (com.ginasprint.buzzer.client)
* Model: Client
* View: ClientGUI
* Controller: ClientController
 
### To Compile and Run via in IntelliJ IDEA
* Open the project in IntelliJ IDEA
* Choose the MainServer run configuration and run
* Then choose the MainClient run configuration and run
* Set up command line arguments for each run configuration as follows
    * java MainServer portNumber
    * java MainClient serverHostName portNumber

Default values are
* portNumber 8080
* serverHostName localhost

### To Compile and Run from Command Line
* cd into src/
* Compile and run server
    * javac ./com/ginasprint/buzzer/server/*.java
    * java com.ginasprint.buzzer.server.MainServer portNumber
* Compile and run client
    * javac ./com/ginasprint/buzzer/client/*.java
    * java com.ginasprint.buzzer.client.MainClient serverHostName portNumber

### Dependencies
* Java version >= 8
* Java Swing
