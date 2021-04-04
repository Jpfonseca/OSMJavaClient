import Requests.AsyncRequests;
import Requests.Headers;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AsyncRequestsTest {
    @Test
    public void test(){
        String uri= "http://httpbin.org/";
        AsyncRequests test =new AsyncRequests(uri);
        String answer;
        JSONObject response=test.response(test.patch("patch","text","tokenid"));
        answer=response.get("message").toString();
        System.out.println(answer);

        response=test.response(test.put("put","/home/planck/put","tokenized"));
        answer=response.get("message").toString();
        System.out.println(answer);
        String data="";
        try {
            data=new String( Files.readAllBytes(Paths.get("/home/planck/put")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file=new File("/home/planck/put");

        System.out.println(data);
        response=test.response(test.put("put",file,"tokenized", Headers.JSON,Headers.ZIP));
        answer=response.get("message").toString();
        System.out.println(answer);


    }

}
