import com.ebay.api.client.auth.oauth2.model.AccessToken;
import com.ebay.api.client.auth.oauth2.model.OAuthResponse;
import com.ebay.api.client.auth.oauth2.model.RefreshToken;
import com.example.ael.AmazonOrder;
import com.example.ael.EbayRequestHandler;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class EbayRequestTests {

    @Test
    void whenValidRequestIsSent_ThenReturnOrders() throws IOException, URISyntaxException, InterruptedException {

        EbayRequestHandler handler =new EbayRequestHandler();

        RefreshToken token = new RefreshToken();
        token.setToken("v^1.1#i^1#r^1#f^0#p^3#I^3#t^Ul4xMF80OjA4MDY5NUU5MThFRTNFNTFDOENGNEZCRTRFMUM1MDhBXzBfMSNFXjEyODQ=");
        OAuthResponse accessToken = handler.getAccessTokenFromRefresh(token);

       AmazonOrder[] response = handler.sendRequest(accessToken.getAccessToken().get().getToken(),
               "2022-05-01T15:05:43.026Z..");

       System.out.println(response);

       assertNull(accessToken.getErrorMessage());
    }
}
