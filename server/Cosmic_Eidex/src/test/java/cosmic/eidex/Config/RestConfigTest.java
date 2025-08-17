package cosmic.eidex.Config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = RestConfig.class)
class RestConfigTest {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void restTemplate() {
        assertNotNull(restTemplate);
    }
}