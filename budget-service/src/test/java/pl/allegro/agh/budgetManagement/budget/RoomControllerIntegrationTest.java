package pl.allegro.agh.budgetManagement.budget;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import pl.allegro.agh.budgetManagement.budget.dto.RoomDto;
import pl.allegro.agh.budgetManagement.budget.dto.RoomProductDto;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.security.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration"
    }
)
@ActiveProfiles("budget")
public class RoomControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private String baseUrl() {
        return "http://localhost:" + port + "/rooms";
    }

    @Test
    void createRoom_and_listRooms() {
        RoomDto request = new RoomDto(null, "Test Room");
        ResponseEntity<RoomDto> resp = restTemplate.postForEntity(baseUrl(), request, RoomDto.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        RoomDto created = resp.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getRoomId()).isNotNull();
        assertThat(created.getRoomName()).isEqualTo("Test Room");

        ResponseEntity<RoomDto[]> listResp = restTemplate.getForEntity(baseUrl(), RoomDto[].class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        RoomDto[] rooms = listResp.getBody();
        assertThat(rooms).isNotEmpty();
    }

    @Test
    void addProduct_and_listProducts() {
        // create room
        RoomDto request = new RoomDto(null, "Product Room");
        ResponseEntity<RoomDto> resp = restTemplate.postForEntity(baseUrl(), request, RoomDto.class);
        Long roomId = resp.getBody().getRoomId();

        // add product
        RoomProductDto prodReq = new RoomProductDto(null, null, "Milk", new BigDecimal("2.50"), false);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RoomProductDto> entity = new HttpEntity<>(prodReq, headers);
        ResponseEntity<RoomProductDto> prodResp = restTemplate.postForEntity(baseUrl() + "/" + roomId + "/products", entity, RoomProductDto.class);
        assertThat(prodResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        RoomProductDto created = prodResp.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getProductId()).isNotNull();
        assertThat(created.getProductName()).isEqualTo("Milk");

        // list products
        ResponseEntity<RoomProductDto[]> listResp = restTemplate.getForEntity(baseUrl() + "/" + roomId + "/products", RoomProductDto[].class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        RoomProductDto[] products = listResp.getBody();
        assertThat(products).isNotEmpty();
    }
}
