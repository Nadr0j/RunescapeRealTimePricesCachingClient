import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.jws.client.OsrsApiClient;
import org.jws.model.Timestep;

import java.io.IOException;

public class OsrsApiClientTest {
    @Test
    public void test() throws IOException {
        final OsrsApiClient client = new OsrsApiClient("@Exadre - testing", new ObjectMapper());
        System.out.println(client.getTimeSeries(2, Timestep.TWENTY_FOUR_HOUR));

    }
}
