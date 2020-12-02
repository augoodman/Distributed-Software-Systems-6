package example.grpcclient;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import service.*;
import test.TestProtobuf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Client that requests `parrot` method from the `EchoServer`.
 */
public class EchoClient {
  private final EchoGrpc.EchoBlockingStub blockingStub;
  private final JokeGrpc.JokeBlockingStub blockingStub2;
  private final RegistryGrpc.RegistryBlockingStub blockingStub3;
  private final CalcGrpc.CalcBlockingStub blockingStub4;
  private final StoryGrpc.StoryBlockingStub blockingStub5;

  /** Construct client for accessing server using the existing channel. */
  public EchoClient(Channel channel, Channel regChannel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's
    // responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to
    // reuse Channels.
    blockingStub = EchoGrpc.newBlockingStub(channel);
    blockingStub2 = JokeGrpc.newBlockingStub(channel);
    blockingStub3 = RegistryGrpc.newBlockingStub(regChannel);
    blockingStub4 = CalcGrpc.newBlockingStub(channel);
    blockingStub5 = StoryGrpc.newBlockingStub(channel);
  }

  public void askServerToParrot(String message) {
    ClientRequest request = ClientRequest.newBuilder().setMessage(message).build();
    ServerResponse response;
    try {
      response = blockingStub.parrot(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e.getMessage());
      return;
    }
    System.out.println("Received from server: " + response.getMessage());
  }

  public void askForJokes(int num) {
    JokeReq request = JokeReq.newBuilder().setNumber(num).build();
    JokeRes response;

    try {
      response = blockingStub2.getJoke(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    System.out.println("Your jokes: ");
    for (String joke : response.getJokeList()) {
      System.out.println("--- " + joke);
    }
  }

  public void setJoke(String joke) {
    JokeSetReq request = JokeSetReq.newBuilder().setJoke(joke).build();
    JokeSetRes response;

    try {
      response = blockingStub2.setJoke(request);
      System.out.println("set joke ok?: " + response.getOk());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void getServices() {
    GetServicesReq request = GetServicesReq.newBuilder().build();
    ServicesListRes response;
    try {
      response = blockingStub3.getServices(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void findServer(String name) {
    FindServerReq request = FindServerReq.newBuilder().setServiceName(name).build();
    SingleServerRes response;
    try {
      response = blockingStub3.findServer(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void findServers(String name) {
    FindServersReq request = FindServersReq.newBuilder().setServiceName(name).build();
    ServerListRes response;
    try {
      response = blockingStub3.findServers(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void calcNums(String op, double firstNum, double ... otherNums) {
    if(op.equals("-"));
      System.out.println("OP: " + op);
    CalcRequest request;
    List<Double> nums = new ArrayList<Double>();
    nums.add(firstNum);
    for(int i = 0; i < otherNums.length; i++)
      nums.add(otherNums[i]);
    request = CalcRequest.newBuilder().addAllNum(nums).build();
    CalcResponse response;
    try {
      if(op.equals("+")) {
        response = blockingStub4.add(request);
        System.out.println("add ok?: " + response.getIsSuccess());
        if(response.getIsSuccess())
          System.out.println("sum: " + response.getSolution());
      }
      else if(op.equals("-")) {
        response = blockingStub4.subtract(request);
        System.out.println("subtract ok?: " + response.getIsSuccess());
        if(response.getIsSuccess())
          System.out.println("difference: " + response.getSolution());
      }
      else if(op.equals("*")) {
        response = blockingStub4.multiply(request);
        System.out.println("multiply ok?: " + response.getIsSuccess());
        if(response.getIsSuccess())
          System.out.println("product: " + response.getSolution());
      }
      else if(op.equals("/")) {
        response = blockingStub4.divide(request);
        System.out.println("divide ok?: " + response.getIsSuccess());
        if(response.getIsSuccess())
          System.out.println("quotient: " + response.getSolution());
      }
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void readSentence(){
    Empty request = Empty.newBuilder().build();
    ReadResponse response;
    try{
      response = blockingStub5.read(request);
      System.out.println("request ok?: " + response.getIsSuccess());
      if(response.getIsSuccess())
        System.out.println("sentence: " + response.getSentence());
    } catch (Exception e){
      System.err.println("RPC failed: " + e);
    }
  }

  public void write(String sentence){
    WriteRequest request = WriteRequest.newBuilder().setNewSentence(sentence).build();

    WriteResponse response;
    try{
      response = blockingStub5.write(request);
      System.out.println("request ok?: " + response.getIsSuccess());
      if(response.getIsSuccess())
        System.out.println("the story so far: " + response.getStory());
    } catch (Exception e){
      System.err.println("RPC failed: " + e);
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 5) {
      System.out
          .println("Expected arguments: <host(String)> <port(int)> <regHost(string)> <regPort(int)> <message(String)>");
      System.exit(1);
    }
    int port = 9099;
    int regPort = 9003;
    String host = args[0];
    String regHost = args[2];
    String message = args[4];
    try {
      port = Integer.parseInt(args[1]);
      regPort = Integer.parseInt(args[3]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port] must be an integer");
      System.exit(2);
    }

    // Create a communication channel to the server, known as a Channel. Channels
    // are thread-safe
    // and reusable. It is common to create channels at the beginning of your
    // application and reuse
    // them until the application shuts down.
    String target = host + ":" + port;
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS
        // to avoid
        // needing certificates.
        .usePlaintext().build();

    String regTarget = regHost + ":" + regPort;
    ManagedChannel regChannel = ManagedChannelBuilder.forTarget(regTarget).usePlaintext().build();
    try {

      // ##############################################################################
      // ## Assume we know the port here from the service node it is basically set through Gradle
      // here.
      // In your version you should first contact the registry to check which services
      // are available and what the port
      // etc is.

      /**
       * Your client should start off with 
       * 1. contacting the Registry to check for the available services
       * 2. List the services in the terminal and the client can
       *    choose one (preferably through numbering) 
       * 3. Based on what the client chooses
       *    the terminal should ask for input, eg. a new sentence, a sorting array or
       *    whatever the request needs 
       * 4. The request should be sent to one of the
       *    available services (client should call the registry again and ask for a
       *    Server providing the chosen service) should send the request to this service and
       *    return the response in a good way to the client
       * 
       * You should make sure your client does not crash in case the service node
       * crashes or went offline.
       */

      // Just doing some hard coded calls to the service node without using the
      // registry
      // create client
      EchoClient client = new EchoClient(channel, regChannel);

      // call the parrot service on the server
      client.askServerToParrot(message);

      // ask the user for input how many jokes the user wants
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

      // Reading data using readLine
      System.out.println("How many jokes would you like?"); // NO ERROR handling of wrong input here.
      String num = reader.readLine();

      // calling the joked service from the server with num from user input
      client.askForJokes(Integer.valueOf(num));

      // adding a joke to the server
      client.setJoke("I made a pencil with two erasers. It was pointless.");

      // showing 6 joked
      client.askForJokes(Integer.valueOf(6));

      System.out.println("Would you like to add, subtract, multiply or divide (enter choice as +, -, * or /)?");
      String op = reader.readLine();
      switch (op) {
        case "+":
          System.out.println("Enter numbers to add one at time. Press enter twice after last number.");
          break;
        case "-":
          System.out.println("Enter numbers to subtract one at time. Numbers will be subtracted from in the order " +
                  "entered.\nPress enter twice after last number.");
          break;
        case "*":
          System.out.println("Enter numbers to multiply one at time. Press enter twice after last number.");
          break;
        case "/":
          System.out.println("Enter numbers to divide one at time. The first number will be divided by the sum of " +
                  "the remaining numbers.\nPress enter twice after last number.");
          break;
        default:
          System.out.println("Invalid operand.");
      }
      String nextEntry = reader.readLine();
      if(nextEntry.length()>0){
        double firstNum = Double.valueOf(nextEntry).doubleValue();
        List<Double> otherNums = new ArrayList<Double>();
        while(true){
          nextEntry = reader.readLine();
          if(nextEntry.length()>0)
            otherNums.add(Double.valueOf(nextEntry).doubleValue());
          else break;
        }
        double[] dArr = new double[otherNums.size()];
        for(int i = 0; i < otherNums.size(); i++)
          dArr[i] = otherNums.get(i);
        client.calcNums(op, firstNum, dArr);
      }
      else
        System.out.println("You didn't enter anything.");
      // test add
      client.calcNums("+", 10, 2, 3);

      System.out.println("Test read sentence");
      client.readSentence();
      System.out.println("Test write sentence");
      client.write("test, test, test");



      // ############### Contacting the registry just so you see how it can be done

      // Comment these last Service calls while in Activity 1 Task 1, they are not needed and wil throw issues without the Registry running
      // get thread's services
      //client.getServices();

      // get parrot
      //client.findServer("services.Echo/parrot");
      
      // get all setJoke
      //client.findServers("services.Joke/setJoke");

      // get getJoke
      //client.findServer("services.Joke/getJoke");

      // does not exist
      //client.findServer("random");


    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent
      // leaking these
      // resources the channel should be shut down when it will no longer be used. If
      // it may be used
      // again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
      regChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
