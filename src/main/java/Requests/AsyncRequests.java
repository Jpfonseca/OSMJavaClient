package Requests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static java.util.logging.Level.INFO;


public class AsyncRequests {
    private final String URI;
    private final String userAgent;
    private static final Logger logger = Logger.getLogger(AsyncRequests.class.getName());

    public AsyncRequests(String uri){

        this.URI=uri;
        PropertiesHandler propertiesHandler=new PropertiesHandler();
        this.userAgent=propertiesHandler.readUserAgent();

    }
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    public HttpRequest get(String endpoint, String token_id){
        URI test=null;
        try {
            test=new URI(getURI()+endpoint);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpRequest request;
        if(token_id.length()<1) {
            request = HttpRequest.newBuilder()
                    .GET().uri(test)
                    .headers("Accept", "application/json")
                    .headers("Content-Type", "application/json")
                    .setHeader("User-Agent", getUserAgent())
                    .build();
        }
        else {
            request = HttpRequest.newBuilder()
                    .GET().uri(test)
                    .headers("Accept", "application/json")
                    .headers("Content-Type", "application/json")
                    .headers("Authorization","Bearer "+token_id)
                    .setHeader("User-Agent", getUserAgent())
                    .build();
        }
        return request;
    }

    public HttpRequest post(String endpoint, Object payload, String token_id){
        URI test=null;
        try {
            test=new URI(getURI()+endpoint);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpRequest request=null;
        
        if (payload instanceof String){
            if (token_id.length()<1){
                request= HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString((String) payload)).uri(test)
                        .setHeader("User-Agent",getUserAgent() )
                        .headers("Accept", "application/json")
                        .headers("Content-Type", "application/json")
                        .build();
            }
            else {
                request= HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString((String) payload)).uri(test)
                        .setHeader("User-Agent", getUserAgent())
                        .headers("Accept", "application/json")
                        .headers("Content-Type", "application/json")
                        .headers("Authorization","Bearer "+token_id)
                        .build();
            }
        }
        else if (payload instanceof File){
            try {
                request= HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofFile(((File) payload).toPath())).uri(test)
                        .setHeader("User-Agent", "Java 11 HttpClient Bot")
                        .headers("Accept", "application/json")
                        .headers("Content-Type", "application/json")
                        .headers("Authorization","Bearer "+token_id)
                        .build();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        

        return request;
    }

    public HttpRequest delete(String endpoint, String token_id){
        URI test=null;
        try {
            test=new URI(getURI()+endpoint);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpRequest request;

        if (token_id.length()<1){
            request= HttpRequest.newBuilder()
                    .DELETE().uri(test)
                    .setHeader("User-Agent", getUserAgent())
                    .headers("Accept", "application/json")
                    .headers("Content-Type", "application/json")
                    .build();
        }
        else {
            request= HttpRequest.newBuilder()
                    .DELETE().uri(test)
                    .setHeader("User-Agent", getUserAgent())
                    .headers("Accept", "application/json")
                    .headers("Content-Type", "application/json")
                    .headers("Authorization","Bearer "+token_id)
                    .build();
        }



        return request;
    }

    public HttpRequest patch(String endpoint, String payload, String token_id){
        URI test=null;
        try {
            test=new URI(getURI()+endpoint);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpRequest request;

        request=HttpRequest.newBuilder()
                .method("PATCH",HttpRequest.BodyPublishers.ofString(payload)).uri(test)
                .setHeader("User-Agent", getUserAgent())
                .headers("Accept", "application/json")
                .headers("Content-Type", "application/json")
                .headers("Authorization","Bearer "+token_id)
                .build();

        return request;
    }

    public HttpRequest put(String endpoint,Object payload, String token_id){
        URI test=null;
        try {
            test=new URI(getURI()+endpoint);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpRequest request=null;
        if(payload instanceof String){
            request=HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString((String)payload)).uri(test)
                    .setHeader("User-Agent", getUserAgent())
                    .headers("Accept", "application/json")
                    .headers("Content-Type", "application/json")
                    .headers("Authorization","Bearer "+token_id)
                    .build();
        }
        else if(payload instanceof JSONObject){
            request=HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString(((JSONObject) payload).toJSONString())).uri(test)
                    .setHeader("User-Agent", getUserAgent())
                    .headers("Accept", "application/json")
                    .headers("Content-Type", "application/json")
                    .headers("Authorization","Bearer "+token_id)
                    .build();
        }
        else if (payload instanceof File){
            try {
                HttpRequest.BodyPublisher bodyPublisher=HttpRequest.BodyPublishers.ofFile(((File) payload).toPath());
                request=HttpRequest.newBuilder().PUT(bodyPublisher).uri(test)
                        .setHeader("User-Agent", getUserAgent())
                        .headers("Accept", "application/json")
                        .headers("Content-Type", "application/json")
                        .headers("Authorization","Bearer "+token_id)
                        .build();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return request;
    }

    public HttpRequest get(String endpoint, String token_id,Headers accept, Headers contentType){
        URI test=null;
        try {
            test=new URI(getURI()+endpoint);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpRequest request;
        if(token_id.length()<1) {
            request = HttpRequest.newBuilder()
                    .GET().uri(test)
                    .headers("Accept", accept.getHeaderInfo())
                    .headers("Content-Type", contentType.getHeaderInfo())
                    .setHeader("User-Agent", getUserAgent())
                    .build();
        }
        else {
            request = HttpRequest.newBuilder()
                    .GET().uri(test)
                    .headers("Accept", accept.getHeaderInfo())
                    .headers("Content-Type", accept.getHeaderInfo())
                    .headers("Authorization","Bearer "+token_id)
                    .setHeader("User-Agent", getUserAgent())
                    .build();
        }
        return request;
    }
    public HttpRequest post(String endpoint, Object payload, String token_id,Headers accept,Headers content){
        URI test=null;
        try {
            test=new URI(getURI()+endpoint);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpRequest request=null;

        if (payload instanceof String){
            if (token_id.length()<1){
                request= HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString((String) payload)).uri(test)
                        .setHeader("User-Agent",getUserAgent() )
                        .headers("Accept", accept.getHeaderInfo())
                        .headers("Content-Type",content.getHeaderInfo() )
                        .build();
            }
            else {
                request= HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString((String) payload)).uri(test)
                        .setHeader("User-Agent", getUserAgent())
                        .headers("Accept", accept.getHeaderInfo())
                        .headers("Content-Type", content.getHeaderInfo())
                        .headers("Authorization","Bearer "+token_id)
                        .build();
            }
        }
        else if (payload instanceof File){
            try {
                request= HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofFile(((File) payload).toPath())).uri(test)
                        .setHeader("User-Agent", "Java 11 HttpClient Bot")
                        .headers("Accept", accept.getHeaderInfo())
                        .headers("Content-Type",content.getHeaderInfo() )
                        .headers("Authorization","Bearer "+token_id)
                        .build();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }



        return request;
    }
    public HttpRequest put(String endpoint,Object payload, String token_id,Headers accept,Headers content){
        URI test=null;
        try {
            test=new URI(getURI()+endpoint);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpRequest request=null;
        if(payload instanceof String){
            request=HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString((String)payload)).uri(test)
                    .setHeader("User-Agent", getUserAgent())
                    .headers("Accept", accept.getHeaderInfo())
                    .headers("Content-Type", content.getHeaderInfo())
                    .headers("Authorization","Bearer "+token_id)
                    .build();
        }
        else if (payload instanceof File){
            try {
                HttpRequest.BodyPublisher bodyPublisher=HttpRequest.BodyPublishers.ofFile(Paths.get(((File) payload).getCanonicalPath()));

                request=HttpRequest.newBuilder().PUT(bodyPublisher).uri(test)
                        .setHeader("User-Agent", getUserAgent())
                        .headers("Accept", accept.getHeaderInfo())
                        .headers("Content-Type", content.getHeaderInfo())
                        .headers("Authorization","Bearer "+token_id)
                        .build();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return request;
    }



    public JSONObject response(HttpRequest request){
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        int status_code=0;
        String inforequest = null,result = null;
        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            inforequest = response.get().toString();
            status_code = response.thenApply(HttpResponse::statusCode).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        JSONParser parser = new JSONParser();
        Object obj = null;

        if(result.length()>2){
            try {

                obj =parser.parse(result);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonResponse= new JSONObject();
        jsonResponse.put("status_code",status_code);
        jsonResponse.put("message", obj);
        if(!inforequest.contains("/admin/")) {
            logger.info("Request:\n" + inforequest);
            logger.info("Message:\n" + obj + "\n\n");

            //System.out.println(result);
        }
        return jsonResponse;
    }

    public JSONObject response(HttpRequest request,Headers acceptType){
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        int status_code=0;
        String inforequest = null,result = null;
        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            inforequest = response.get().toString();
            status_code = response.thenApply(HttpResponse::statusCode).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }



        Object obj = null;
        switch (acceptType){
            case JSON:
                JSONParser parser = new JSONParser();

                if(result.length()>2){
                    try {
                        obj =parser.parse(result);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case TEXT:
                obj=result;
                break;
            default:
                obj=result;
                break;
        }

        JSONObject jsonResponse= new JSONObject();
        jsonResponse.put("status_code",status_code);
        jsonResponse.put("message", obj);
        if(!inforequest.contains("/admin/")) {
            logger.info("Request:\n" + inforequest);
            logger.info("Message:\n" + obj + "\n\n");

            //System.out.println(result);
        }
        return jsonResponse;
    }


    private String getURI(){
        return this.URI;
    }
    private String getUserAgent(){
        return this.userAgent;
    }
}
