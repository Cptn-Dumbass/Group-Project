import com.example.ael.AmazonRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AmazonApiTest {

    @Test
    void whenRequestIsSent_ThenReturnNoPermissionError(){
        //this test will be changed when I have the server up and running

        //when
        AmazonRequest amazonRequest =
                new AmazonRequest("/orders/v0/orders"
                        ,"ATVPDKIKX0DER");

        try{
            amazonRequest.sendRequest("TEST_CASE_200");
        }catch (IOException e){
            System.err.println(e);
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        //then
        assertEquals(403, amazonRequest.getStatus());

    }

    @Test
    void lastMinTest() throws URISyntaxException, IOException, InterruptedException {
        AmazonRequest amazonRequest = new AmazonRequest("/orders/v0/orders"
                ,"ATVPDKIKX0DER");

        amazonRequest.getAmazonAccessToken("udhfid", "dfhisd", "kjdfd");
    }
}
