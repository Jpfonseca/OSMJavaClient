/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.Requests;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class AsyncRequests {
    private final String URI;
    private final String userAgent;
    private static final Logger logger = Logger.getLogger(AsyncRequests.class.getName());
    
    private CloseableHttpClient httpClient;


    public AsyncRequests(String uri){

        this.URI=uri;
        PropertiesHandler propertiesHandler=new PropertiesHandler();
        this.userAgent=propertiesHandler.readUserAgent();
        
        SSLContextBuilder builder = new SSLContextBuilder();
        SSLConnectionSocketFactory sslsf = null;
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            sslsf = new SSLConnectionSocketFactory(builder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AsyncRequests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(AsyncRequests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(AsyncRequests.class.getName()).log(Level.SEVERE, null, ex);
        }

        Registry<ConnectionSocketFactory> registry = RegistryBuilder. 
                         <ConnectionSocketFactory> create()
                        .register("http", new PlainConnectionSocketFactory())
                        .register("https", sslsf)
                        .build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(2000);
        
        this.httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(cm).build();
    }
    
    public HttpRequestBase get(String endpoint, String token_id){
//        URI test=null;
//        try {
//            test=new URI(getURI()+endpoint);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
        HttpGet request = new HttpGet(getURI()+endpoint);
        
//        HttpRequest request;
        if(token_id.length()<1) {
//            request = HttpRequest.newBuilder()
//                    .GET().uri(test)
//                    .headers("Accept", "application/json")
//                    .headers("Content-Type", "application/json")
//                    .setHeader("User-Agent", getUserAgent())
//                    .build();
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");
            request.setHeader("User-Agent", getUserAgent());
        }
        else {
//            request = HttpRequest.newBuilder()
//                    .GET().uri(test)
//                    .headers("Accept", "application/json")
//                    .headers("Content-Type", "application/json")
//                    .headers("Authorization","Bearer "+token_id)
//                    .setHeader("User-Agent", getUserAgent())
//                    .build();
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization","Bearer "+token_id);
            request.setHeader("User-Agent", getUserAgent());
        }
        return request;
    }

    public HttpRequestBase post(String endpoint, Object payload, String token_id){
//        URI test=null;
//        try {
//            test=new URI(getURI()+endpoint);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        HttpRequest request=null;
        
        HttpPost request = new HttpPost(getURI()+endpoint);
        if (payload instanceof String){
            if (token_id.length()<1){
//                request= HttpRequest.newBuilder()
//                        .POST(HttpRequest.BodyPublishers.ofString((String) payload)).uri(test)
//                        .setHeader("User-Agent",getUserAgent() )
//                        .headers("Accept", "application/json")
//                        .headers("Content-Type", "application/json")
//                        .build();
                request.setHeader("Accept", "application/json");
                request.setHeader("Content-Type", "application/json");
                request.setHeader("User-Agent", getUserAgent());
                try {
                    request.setEntity(new StringEntity(payload.toString()));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(AsyncRequests.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {
//                request= HttpRequest.newBuilder()
//                        .POST(HttpRequest.BodyPublishers.ofString((String) payload)).uri(test)
//                        .setHeader("User-Agent", getUserAgent())
//                        .headers("Accept", "application/json")
//                        .headers("Content-Type", "application/json")
//                        .headers("Authorization","Bearer "+token_id)
//                        .build();
                request.setHeader("Accept", "application/json");
                request.setHeader("Content-Type", "application/json");
                request.setHeader("Authorization","Bearer "+token_id);
                request.setHeader("User-Agent", getUserAgent());
                try {
                    request.setEntity(new StringEntity(payload.toString()));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(AsyncRequests.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if (payload instanceof File){
//            try {
//                request= HttpRequest.newBuilder()
//                        .POST(HttpRequest.BodyPublishers.ofFile(((File) payload).toPath())).uri(test)
//                        .setHeader("User-Agent", "Java 11 HttpClient Bot")
//                        .headers("Accept", "application/json")
//                        .headers("Content-Type", "application/json")
//                        .headers("Authorization","Bearer "+token_id)
//                        .build();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                return null;
//            }
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization","Bearer "+token_id);
            request.setHeader("User-Agent", "Java 11 HttpClient Bot");
            request.setEntity(new FileEntity((File) payload));
        }
        
        return request;
    }

    public HttpRequestBase delete(String endpoint, String token_id){
//        URI test=null;
//        try {
//            test=new URI(getURI()+endpoint);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        HttpRequest request;

        HttpDelete request = new HttpDelete(getURI()+endpoint);
        if (token_id.length()<1){
//            request= HttpRequest.newBuilder()
//                    .DELETE().uri(test)
//                    .setHeader("User-Agent", getUserAgent())
//                    .headers("Accept", "application/json")
//                    .headers("Content-Type", "application/json")
//                    .build();
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");
            request.setHeader("User-Agent", getUserAgent());
        }
        else {
//            request= HttpRequest.newBuilder()
//                    .DELETE().uri(test)
//                    .setHeader("User-Agent", getUserAgent())
//                    .headers("Accept", "application/json")
//                    .headers("Content-Type", "application/json")
//                    .headers("Authorization","Bearer "+token_id)
//                    .build();
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization","Bearer "+token_id);
            request.setHeader("User-Agent", getUserAgent());
        }

        return request;
    }

    public HttpRequestBase patch(String endpoint, String payload, String token_id){
//        URI test=null;
//        try {
//            test=new URI(getURI()+endpoint);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        HttpRequest request;

        HttpPatch request = new HttpPatch(getURI()+endpoint);
//        request=HttpRequest.newBuilder()
//                .method("PATCH",HttpRequest.BodyPublishers.ofString(payload)).uri(test)
//                .setHeader("User-Agent", getUserAgent())
//                .headers("Accept", "application/json")
//                .headers("Content-Type", "application/json")
//                .headers("Authorization","Bearer "+token_id)
//                .build();
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization","Bearer "+token_id);
        request.setHeader("User-Agent", getUserAgent());
        try {
            request.setEntity(new StringEntity(payload.toString()));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AsyncRequests.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        return request;
    }

    public HttpRequestBase put(String endpoint,Object payload, String token_id){
//        URI test=null;
//        try {
//            test=new URI(getURI()+endpoint);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        HttpRequest request=null;

        HttpPut request = new HttpPut(getURI()+endpoint);
        if(payload instanceof String){
//            request=HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString((String)payload)).uri(test)
//                    .setHeader("User-Agent", getUserAgent())
//                    .headers("Accept", "application/json")
//                    .headers("Content-Type", "application/json")
//                    .headers("Authorization","Bearer "+token_id)
//                    .build();
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization","Bearer "+token_id);
            request.setHeader("User-Agent", getUserAgent());
            try {
                request.setEntity(new StringEntity(payload.toString()));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(AsyncRequests.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(payload instanceof JSONObject){
//            request=HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString(((JSONObject) payload).toJSONString())).uri(test)
//                    .setHeader("User-Agent", getUserAgent())
//                    .headers("Accept", "application/json")
//                    .headers("Content-Type", "application/json")
//                    .headers("Authorization","Bearer "+token_id)
//                    .build();
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization","Bearer "+token_id);
            request.setHeader("User-Agent", getUserAgent());
            try {
                request.setEntity(new StringEntity(((JSONObject) payload).toJSONString()));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(AsyncRequests.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (payload instanceof File){
//            try {
//                HttpRequest.BodyPublisher bodyPublisher=HttpRequest.BodyPublishers.ofFile(((File) payload).toPath());
//                request=HttpRequest.newBuilder().PUT(bodyPublisher).uri(test)
//                        .setHeader("User-Agent", getUserAgent())
//                        .headers("Accept", "application/json")
//                        .headers("Content-Type", "application/json")
//                        .headers("Authorization","Bearer "+token_id)
//                        .build();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization","Bearer "+token_id);
            request.setHeader("User-Agent", getUserAgent());
            request.setEntity(new FileEntity((File)payload));
            
        }
        return request;
    }

    public HttpRequestBase get(String endpoint, String token_id,Headers accept, Headers contentType){
//        URI test=null;
//        try {
//            test=new URI(getURI()+endpoint);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        HttpRequest request;
        
        HttpPut request = new HttpPut(getURI()+endpoint);
        if(token_id.length()<1) {
//            request = HttpRequest.newBuilder()
//                    .GET().uri(test)
//                    .headers("Accept", accept.getHeaderInfo())
//                    .headers("Content-Type", contentType.getHeaderInfo())
//                    .setHeader("User-Agent", getUserAgent())
//                    .build();
            request.setHeader("Accept", accept.getHeaderInfo());
            request.setHeader("Content-Type", contentType.getHeaderInfo());
            request.setHeader("User-Agent", getUserAgent());
        }
        else {
//            request = HttpRequest.newBuilder()
//                    .GET().uri(test)
//                    .headers("Accept", accept.getHeaderInfo())
//                    .headers("Content-Type", accept.getHeaderInfo())
//                    .headers("Authorization","Bearer "+token_id)
//                    .setHeader("User-Agent", getUserAgent())
//                    .build();
            request.setHeader("Accept", accept.getHeaderInfo());
            request.setHeader("Content-Type", contentType.getHeaderInfo());
            request.setHeader("Authorization","Bearer "+token_id);
            request.setHeader("User-Agent", getUserAgent());
        }
        return request;
    }
    public HttpRequestBase post(String endpoint, Object payload, String token_id,Headers accept,Headers content){
//        URI test=null;
//        try {
//            test=new URI(getURI()+endpoint);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        HttpRequest request=null;

        HttpPost request = new HttpPost(getURI()+endpoint);
        if (payload instanceof String){
            if (token_id.length()<1){
//                request= HttpRequest.newBuilder()
//                        .POST(HttpRequest.BodyPublishers.ofString((String) payload)).uri(test)
//                        .setHeader("User-Agent",getUserAgent() )
//                        .headers("Accept", accept.getHeaderInfo())
//                        .headers("Content-Type",content.getHeaderInfo() )
//                        .build();
                request.setHeader("Accept", accept.getHeaderInfo());
                request.setHeader("Content-Type", content.getHeaderInfo());
                request.setHeader("User-Agent", getUserAgent());
                try {
                    request.setEntity(new StringEntity(payload.toString()));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(AsyncRequests.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {
//                request= HttpRequest.newBuilder()
//                        .POST(HttpRequest.BodyPublishers.ofString((String) payload)).uri(test)
//                        .setHeader("User-Agent", getUserAgent())
//                        .headers("Accept", accept.getHeaderInfo())
//                        .headers("Content-Type", content.getHeaderInfo())
//                        .headers("Authorization","Bearer "+token_id)
//                        .build();
                request.setHeader("Accept", accept.getHeaderInfo());
                request.setHeader("Content-Type", content.getHeaderInfo());
                request.setHeader("Authorization","Bearer "+token_id);
                request.setHeader("User-Agent", getUserAgent());
                try {
                    request.setEntity(new StringEntity(payload.toString()));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(AsyncRequests.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if (payload instanceof File){
//            try {
//                request= HttpRequest.newBuilder()
//                        .POST(HttpRequest.BodyPublishers.ofFile(((File) payload).toPath())).uri(test)
//                        .setHeader("User-Agent", "Java 11 HttpClient Bot")
//                        .headers("Accept", accept.getHeaderInfo())
//                        .headers("Content-Type",content.getHeaderInfo() )
//                        .headers("Authorization","Bearer "+token_id)
//                        .build();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                return null;
//            }
            request.setHeader("Accept", accept.getHeaderInfo());
            request.setHeader("Content-Type", content.getHeaderInfo());
            request.setHeader("Authorization","Bearer "+token_id);
            request.setHeader("User-Agent", "Java 11 HttpClient Bot");
            request.setEntity(new FileEntity((File) payload));
        }

        return request;
    }
    public HttpRequestBase put(String endpoint,Object payload, String token_id,Headers accept,Headers content){
//        URI test=null;
//        try {
//            test=new URI(getURI()+endpoint);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        HttpRequest request=null;
        
        HttpPut request = new HttpPut(getURI()+endpoint);
        if(payload instanceof String){
//            request=HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString((String)payload)).uri(test)
//                    .setHeader("User-Agent", getUserAgent())
//                    .headers("Accept", accept.getHeaderInfo())
//                    .headers("Content-Type", content.getHeaderInfo())
//                    .headers("Authorization","Bearer "+token_id)
//                    .build();
            request.setHeader("Accept", accept.getHeaderInfo());
            request.setHeader("Content-Type", content.getHeaderInfo());
            request.setHeader("Authorization","Bearer "+token_id);
            request.setHeader("User-Agent", getUserAgent());
            try {
                request.setEntity(new StringEntity(payload.toString()));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(AsyncRequests.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (payload instanceof File){
//            try {
//                HttpRequest.BodyPublisher bodyPublisher=HttpRequest.BodyPublishers.ofFile(Paths.get(((File) payload).getCanonicalPath()));

//                request=HttpRequest.newBuilder().PUT(bodyPublisher).uri(test)
//                        .setHeader("User-Agent", getUserAgent())
//                        .headers("Accept", accept.getHeaderInfo())
//                        .headers("Content-Type", content.getHeaderInfo())
//                        .headers("Authorization","Bearer "+token_id)
//                        .build();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            request.setHeader("Accept", accept.getHeaderInfo());
            request.setHeader("Content-Type", content.getHeaderInfo());
            request.setHeader("Authorization","Bearer "+token_id);
            request.setHeader("User-Agent", getUserAgent());
            request.setEntity(new FileEntity((File) payload));
        }
        return request;
    }



    public JSONObject response(HttpRequestBase request){
//        CompletableFuture<HttpResponse<String>> response =
//                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
//
//        int status_code=0;
//        String inforequest = null,result = null;
//        try {
//            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
//            inforequest = response.get().toString();
//            status_code = response.thenApply(HttpResponse::statusCode).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }



        int status_code=0;
        String inforequest = "/admin/",result = null;
        try {
            CloseableHttpResponse response = this.httpClient.execute(request);
            status_code=response.getStatusLine().getStatusCode();
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException ex) {
            Logger.getLogger(AsyncRequests.class.getName()).log(Level.SEVERE, null, ex);
        }

        JSONParser parser = new JSONParser();
        Object obj = null;

        try {
            obj = parser.parse(result);
        } catch (ParseException e) {
            e.printStackTrace();
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

    public JSONObject response(HttpRequestBase request,Headers acceptType){
//        CompletableFuture<HttpResponse<String>> response =
//                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
//
//        int status_code=0;
//        String inforequest = null,result = null;
//        try {
//            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
//            inforequest = response.get().toString();
//            status_code = response.thenApply(HttpResponse::statusCode).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }

        int status_code=0;
        String inforequest = "/admin/",result = null;
        try {
            CloseableHttpResponse response = this.httpClient.execute(request);
            status_code=response.getStatusLine().getStatusCode();
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException ex) {
            Logger.getLogger(AsyncRequests.class.getName()).log(Level.SEVERE, null, ex);
        }



        Object obj = null;
        switch (acceptType){
            case JSON:
                JSONParser parser = new JSONParser();

                try {
                    obj =parser.parse(result);
                } catch (ParseException e) {
                    e.printStackTrace();
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
