package example.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.StreamObserver;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import service.*;
import java.util.Stack;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;

import org.json.*;

// Implement the story service. It has two sevices read and write
class StoryImpl extends StoryGrpc.StoryImplBase {

    // have a global story
    JSONArray story = new JSONArray();
    String sentence;

    public StoryImpl(){
        super();
        // Start a story
        sentence = "Once upon a time...\n";
        story.put(sentence);
        // Write JSON file
        try (FileWriter file = new FileWriter("story.json")) {
            file.write(story.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read the story so far
    @Override
    public void read(Empty req, StreamObserver<ReadResponse> responseObserver) {
        System.out.println("Received from client: Read request");
        try{
            ReadResponse.Builder response = ReadResponse.newBuilder();
            response.setSentence(sentence);
            response.setIsSuccess(true);
            ReadResponse resp = response.build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        } catch (Exception e){
            ReadResponse.Builder response = ReadResponse.newBuilder();
            response.setIsSuccess(false);
            ReadResponse resp = response.build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }

    // Add a sentence to the story
    @Override
    public void write(WriteRequest req, StreamObserver<WriteResponse> responseObserver) {
        System.out.println("Received from client: " + req.getNewSentence());
        sentence = req.getNewSentence();
        try(FileInputStream in = new FileInputStream("story.json")){
            story = new JSONArray(in.toString());

            // Add sentence to story
            story.put(sentence);

            // Converty JSONArray to story string
            StringBuilder sb = new StringBuilder();
            story.iterator().forEachRemaining(element -> {
                sb.append(element + " ");
            });
            WriteResponse.Builder response = WriteResponse.newBuilder();
            response.setStory(sb.toString());
            response.setIsSuccess(true);
            WriteResponse resp = response.build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        } catch (Exception e) {
            WriteResponse.Builder response = WriteResponse.newBuilder();
            response.setIsSuccess(false);
            WriteResponse resp = response.build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }
}