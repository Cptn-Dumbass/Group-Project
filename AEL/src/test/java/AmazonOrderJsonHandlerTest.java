import com.example.ael.AmazonOrder;
import com.example.ael.AmazonOrderItem;
import com.example.ael.AmazonOrderJSONHandler;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AmazonOrderJsonHandlerTest {


    @Test
    void whenMultipleJsonObjectsArePassedIn_ThenTheyWillBeReturnedAsAnArray() throws IOException {
        //when
        AmazonOrderJSONHandler handler = new AmazonOrderJSONHandler();
        String json = Files.readString(Path.of("src/test/resources/AmazonOrderArray.json"));
        AmazonOrder[] order = handler.getObjectArray(json);

        //then
        assertEquals(order[0].getAmazonOrderId(), "902-1845936-5435065");
        assertEquals(order[1].getAmazonOrderId(), "902-8745147-1934268");


    }

    @Test
    void whenOrderItemIsPasseIn_ThenPrintOffObjects() throws IOException {

        AmazonOrderJSONHandler handler = new AmazonOrderJSONHandler();
        String json = Files.readString(Path.of("src/test/resources/OrderItemsResponse.json"));
        AmazonOrderItem[] items = handler.getOrderItemArrayForTest(json);


        assertEquals("CBA_OTF_1", items[0].getSku());
    }
}
