## SER321-Module6
# Activity 2
##Task 1
1. This project uses gRPC to run custom services. The services Echo and Joke were provided.  I implemented Calc which computes basic calculations such as Add, Subtract, Multiply and Divide (press enter after inputting each number, press enter a second time inputting after last number).  Each Calc service can handle any number of inputs. I also implemented Story which begins a story with an opening and allows users to check the most recent sentence added and add their own sentence.  When adding a sentence, the entire story is read back to the user.  Sentences are stored in JSON file for durability (story.json).

2. Run the default program with:
```
gradle runClientJava -q --console=plain
```
You will see the following menu:
```
***Welcome to SER 321 gRPC***
Please select a service below:
1. Echo  // echos given string to server   use cammand line argument -Pmessage=<message> to echo non-gradle defined value. Default value is "Hello you"
2. Joke  // get number of jokes from or add joke to server
3. Calc  // do add, subtract, multiply or divide operations
4. Story // read last sentence or add to and read story
5. Exit  // exit program
```
Run the demonstration with:
```
gradle runClientJava -Pauto=1 -q --console=plain
```
You will see the following output:
```
***RUNNING DEMO***
Echoing Command Line message...
Received from server: Hello You 											// echo default message from build.gradle
Getting a couple jokes... 
Your jokes:	 																// get 2 jokes
--- Did you hear the rumor about butter? Well, I'm not going to spread it!
--- What do you call someone with no body and no nose? Nobody knows.
Adding joke: 'I made a pencil with two erasers. It was pointless.'			// add 1 joke
set joke ok?: true															// joke verified as added
Adding: 2 + 2...															// add some numbers
OP: +
add ok?: true
sum: 4.0																	// sum is here
Subtracting: 4.5 - 2 - 2...													// other arithmetic operations...
OP: -
subtract ok?: true
difference: 0.5																// ...and their solutions
Multiplying: 4 * 4 * 4 * 4...
OP: *
multiply ok?: true
product: 256.0
Dividing: 3 / (1 + 1)...
OP: /
divide ok?: true
quotient: 1.5
Starting a story...
request ok?: true
sentence: Once upon a time...												// read back initial story sentence from constructor

That's not very good. Let's add to it.										// sentences are added...
request ok?: true
The story so far...
Once upon a time...										
 There was a course called SER 321.											// ...and read back
request ok?: true
The story so far...
Once upon a time...
 There was a course called SER 321. It was very difficult but also very rewarding and fun.
request ok?: true
The story so far...
Once upon a time...
 There was a course called SER 321. It was very difficult but also very rewarding and fun. It aslo has a boss instructor named Dr. M.
request ok?: true
The story so far...
Once upon a time...
 There was a course called SER 321. It was very difficult but also very rewarding and fun. It aslo has a boss instructor named Dr. M. THE END.
***DEMO OVER***

```

3. Example input and output:
Echo input:
```
***ECHO SERVICE***
Please enter text that you'd like to echo to the server:
SER 321 kicks butt!
```
Echo output:
```
Received from server: SER 321 kicks butt!
```

Joke menu:
```
***JOKE SERVICE***
What do you want to do?
1. Get jokes
2. Create joke
```
Joke Get input:
```
***GET JOKES***
How many jokes would you like?
4
```
Joke Get output:
```
Your jokes:
--- I made a pencil with two erasers. It was pointless.
--- I don't trust stairs. They're always up to something.
--- How do you get a squirrel to like you? Act like a nut.
--- I am out of jokes...

```
Joke Set input:
```
***CREATE JOKE***
Enter a joke to store on server:
SER 321 is a piece of cake!
```
Joke Set output:
```
set joke ok?: true
```

Calc Menu:
```
***CALC SERVICE***
Would you like to add, subtract, multiply or divide (enter choice as +, -, * or /)?
```
Calc Add input:
```
Enter numbers to add one at time. Press enter twice after last number.
1
2
3
4
5
6
7
```
Calc Add output:
```
OP: +
add ok?: true
sum: 28.0
```
Calc Subtract input:
```
Enter numbers to subtract one at time. Numbers will be subtracted from in the order entered.
Press enter twice after last number.
-100
0
35
```
Calc Subtract output:
```
OP: -
subtract ok?: true
difference: -135.0
```
Calc Multiply input:
```
Enter numbers to multiply one at time. Press enter twice after last number.
2
2
2
2
2
2
2
2
```
Calc Multiply output:
```
OP: *
multiply ok?: true
product: 256.0
```
Calc Divide input:
```
Enter numbers to divide one at time. The first number will be divided by the sum of the remaining numbers.
Press enter twice after last number.
100
3.14
```
Calc Divide output:
```
OP: /
divide ok?: true
quotient: 31.84713375796178
```

Story menu:
```
***STORY SERVICE***
What do you want to do?
1. Read latest addition to story
2. Add to story
```
Story Read Latest output:
```
***READ LAST SENTENCE***
The last sentence added to story is:
request ok?: true
sentence: This is going to be epic.
```
Story Add Sentence input:
```
***ADD TO THE STORY***
Enter a single sentence to add the story (don't forget punctuation!):
Just kidding!
```
Story Add Sentence output:
```
request ok?: true
The story so far...
Once upon a time...
 This is going to be epic. Just kidding!
```

##Task 2
1. Client.java and NodeService.java add registry services to the program.  Run NodeService.java with:
```
gradle registerServiceNode
```
Run Client.java with:
```
gradle runClient -q --console=plain

```
The system runs fine locally and gives the following example output:
```
***REGISTRY SERVICES***
Please select a service below:
 1. services.Echo/parrot
 2. services.Joke/setJoke
 3. services.Joke/getJoke
 4. services.Calc/subtract
 5. services.Calc/divide
 6. services.Calc/add
 7. services.Calc/multiply
 8. services.Story/read
 9. services.Story/write
10. services.Registry/getServices
11. Exit
```
However, running over a network, I am getting address bind problems.