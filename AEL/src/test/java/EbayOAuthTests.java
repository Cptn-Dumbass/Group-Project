import com.ebay.api.client.auth.oauth2.CredentialUtil;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import com.ebay.api.client.auth.oauth2.model.AccessToken;
import com.ebay.api.client.auth.oauth2.model.Environment;
import com.ebay.api.client.auth.oauth2.model.OAuthResponse;
import com.ebay.api.client.auth.oauth2.model.RefreshToken;
import com.example.ael.EbayRequestHandler;
import com.example.ael.EbayJsonHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.text.html.Option;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class EbayOAuthTests {


    // Password: testAEL123!
    @Test
    void getEbayURI() throws IOException, URISyntaxException {

        EbayRequestHandler handler = new EbayRequestHandler();

        System.out.println(handler.getAuthUri());

    }

    @Test
    void getAccessAndRefreshToken() throws IOException {
        EbayRequestHandler handler = new EbayRequestHandler();

        OAuthResponse response = handler.getAccessAndRefreshTokens(
"https://auth.ebay.com/oauth2/ThirdPartyAuthSucessFailure?isAuthSuccessful=true&code=v%5E1.1%23i%5E1%23I%5E3%23f%5E0%23p%5E3%23r%5E1%23t%5EUl41Xzg6RjJDMzg1ODZBQjAwNUMwOUY3NzRCNjhEOTMyNjQ1MDNfMV8xI0VeMTI4NA%3D%3D&expires_in=299"
        );

        System.out.println(response.getRefreshToken().get().getToken());
        System.out.println(response.getRefreshToken().get().getExpiresOn());
        System.out.println(response.getRefreshToken());
        assertNull(response.getErrorMessage());
        handler.createOauthProperties(response, "test");
    }

    @Test
    void getTokensFromRefreshToken() throws IOException {
        EbayRequestHandler handler = new EbayRequestHandler();

        RefreshToken token = new RefreshToken();
        token.setToken("v^1.1#i^1#p^3#f^0#r^1#I^3#t^Ul4xMF81OjQ4NkYxRkExRDY3RUVBQzExM0Y0OEY3M0I0Q0JFRjY1XzBfMSNFXjEyODQ=");
        OAuthResponse response1 = handler.getAccessTokenFromRefresh(token);

        assertNull(response1.getRefreshToken().get().getToken());
        assertNotNull(response1.getAccessToken().get().getToken());
        System.out.println(response1);
    }

    @Test
    void writeToPropertiesTest() throws IOException {
        EbayRequestHandler handler = new EbayRequestHandler();
        RefreshToken token = new RefreshToken();
        token.setToken("v^1.1#i^1#p^3#f^0#r^1#I^3#t^Ul4xMF81OjQ4NkYxRkExRDY3RUVBQzExM0Y0OEY3M0I0Q0JFRjY1XzBfMSNFXjEyODQ=\n");
        OAuthResponse response1 = handler.getAccessTokenFromRefresh(token);

        handler.createOauthProperties(response1, "test");
    }

}
