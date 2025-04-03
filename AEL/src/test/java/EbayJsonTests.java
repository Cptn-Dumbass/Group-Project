import com.example.ael.AmazonOrder;
import com.example.ael.EbayJsonHandler;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EbayJsonTests {

    @Test
    void whenValidResponseIsGiven_ThenItIsPutIntoAnOrderObject() throws IOException {

        String json = Files.readString(Path.of("src/test/resources/EbayOrder.json"));

        EbayJsonHandler handler = new EbayJsonHandler();

        AmazonOrder[] orders =
                handler.handleEbayResponse(json);

        assertEquals(orders[0].getAmazonOrderId(), "2*********9");
    }


}
