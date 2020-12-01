package example.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.StreamObserver;
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

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;


// Implement the calc service. It has four sevices: add, subtract, multiply and divide
class CalcImpl extends CalcGrpc.CalcImplBase{
    //super();
    int[] input;

    public CalcImpl(){
    }

    // Add a set of numbers
    public void add(CalcRequest req, StreamObserver<CalcResponse> responseObserver) {
        try {
            System.out.println("Received from client: " + req.getNumList());
            CalcResponse.Builder response = CalcResponse.newBuilder();
            double sum = 0;
            for (double num : req.getNumList())
                sum += num;
            response.setSolution(sum);
            response.setIsSuccess(true);
            CalcResponse resp = response.build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
        catch (Exception e){
            CalcResponse.Builder response = CalcResponse.newBuilder();
            response.setIsSuccess(false);
            response.setError("Error: " + e);
            CalcResponse resp = response.build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }

    // Subtract a set of numbers
    public void subtract(CalcRequest req, StreamObserver<CalcResponse> responseObserver) {
        try {
            System.out.println("Received from client: " + req.getNumList());
            CalcResponse.Builder response = CalcResponse.newBuilder();
            double difference = req.getNumList().get(0);
            for (int i = 1; i < req.getNumList().size(); i++)
                difference -= req.getNumList().get(i);
            response.setSolution(difference);
            response.setIsSuccess(true);
            CalcResponse resp = response.build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
        catch (Exception e){
            CalcResponse.Builder response = CalcResponse.newBuilder();
            response.setIsSuccess(false);
            response.setError("Error: " + e);
            CalcResponse resp = response.build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }

    // Multiply a set of numbers
    public void multiply(CalcRequest req, StreamObserver<CalcResponse> responseObserver) {
        try {
            System.out.println("Received from client: " + req.getNumList());
            CalcResponse.Builder response = CalcResponse.newBuilder();
            double product = 1;
            for (double num : req.getNumList())
                product *= num;
            response.setSolution(product);
            response.setIsSuccess(true);
            CalcResponse resp = response.build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
        catch (Exception e){
            CalcResponse.Builder response = CalcResponse.newBuilder();
            response.setIsSuccess(false);
            response.setError("Error: " + e);
            CalcResponse resp = response.build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }

    // Divide a set of numbers
    public void divide(CalcRequest req, StreamObserver<CalcResponse> responseObserver) {
        try {
            System.out.println("Received from client: " + req.getNumList());
            CalcResponse.Builder response = CalcResponse.newBuilder();
            double dividend = req.getNumList().get(0);
            double divisor = 0;
            double quotient;
            for (int i = 1; i < req.getNumList().size(); i++)
                divisor += req.getNumList().get(i);
            quotient = dividend/divisor;
            response.setSolution(quotient);
            response.setIsSuccess(true);
            CalcResponse resp = response.build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
        catch (Exception e){
            CalcResponse.Builder response = CalcResponse.newBuilder();
            response.setIsSuccess(false);
            response.setError("Error: " + e);
            CalcResponse resp = response.build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }
}