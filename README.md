##Assignment 6
#Activity 1
1. This program uses multiple gradle tasks to run both a MusicThread publisher and a Subscriber.  The MusicThread is presented with a music player gui that allows the user to play .wav files.  Whenever a song is played, a message is sent to a JMS broker which distributes the message to any subscribers.  Each Subscriber takes in this message and adds it to a song ranking list.  The list then displays all songs played in order of number of times played.  The list is updated everytime a song is played.

2. To run the program, first ensure you are logged into ActiveMQ and monitor the Topics dashboard for activity.  For my development and testing, I run the binary using the command:
```
bin\activemq start
```
from the root ActiveMQ folder once unzipped.
To run the MusicThread, from the project folder, use the command:
```
gradle MusicThread -Pidentifier=<identifier>
```
where identifier is your chosen user name.
The music player GUI will spawn and from here you may select, play and stop songs.  In the terminal, there will be some output showing the messages sent and music player status.  Use file->exit to terminate the process and exit.
To run the Subscriber, use the command:
```
gradle Subscriber
```
Whenever a song is played, the updated rankings will be printed to terminal.  Enter 'exit' anytime to terminate process.