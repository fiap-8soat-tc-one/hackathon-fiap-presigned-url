//package presignedUrl;
//
//import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class LambdaHandlerTest {
//    @Test
//    public void successfulResponse() {
//        LambdaHandler app = new LambdaHandler();
//        APIGatewayProxyResponseEvent result = app.handleRequest(null, null);
//        assertEquals(200, result.getStatusCode().intValue());
//        assertEquals("application/json", result.getHeaders().get("Content-Type"));
//        String content = result.getBody();
//        assertNotNull(content);
//    }
//}
