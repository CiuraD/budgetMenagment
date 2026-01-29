package pl.allegro.agh.budgetManagement.budget;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.allegro.agh.budgetManagement.budget.dto.RoomDto;
import pl.allegro.agh.budgetManagement.budget.dto.RoomProductDto;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.security.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:budgetdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@ActiveProfiles("budget")
public class RoomControllerIntegrationTest {

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    // use a local ObjectMapper to avoid relying on autoconfiguration in the test context
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void createRoom_and_listRooms() throws Exception {
        RoomDto request = new RoomDto(null, "Test Room");

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId").exists())
                .andExpect(jsonPath("$.roomName").value("Test Room"));

        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void addProduct_and_listProducts() throws Exception {
        // create room
        RoomDto request = new RoomDto(null, "Product Room");
        String createResp = mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        RoomDto created = objectMapper.readValue(createResp, RoomDto.class);
        Long roomId = created.getRoomId();

        // add product
        RoomProductDto prodReq = new RoomProductDto(null, null, "Milk", new BigDecimal("2.50"), false);
        mockMvc.perform(post("/rooms/" + roomId + "/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prodReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").exists())
                .andExpect(jsonPath("$.productName").value("Milk"));

        mockMvc.perform(get("/rooms/" + roomId + "/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void createRoom_listRooms_andAddProduct_overHttp() throws Exception {
        String roomName = "integ-room-" + System.currentTimeMillis();
        RoomDto req = new RoomDto(null, roomName);

        String createResp = mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        RoomDto created = objectMapper.readValue(createResp, RoomDto.class);
        Long roomId = created.getRoomId();

        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        RoomProductDto pReq = new RoomProductDto(null, null, "Bread", BigDecimal.valueOf(3.5), false);
        mockMvc.perform(post("/rooms/" + roomId + "/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").exists());

        mockMvc.perform(get("/rooms/" + roomId + "/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void createRoom_andAddProducts_thenGetTotalUnpaidAmount() throws Exception {
        String roomName = "total-unpaid-room-" + System.currentTimeMillis();
        RoomDto req = new RoomDto(null, roomName);

        String createResp = mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        RoomDto created = objectMapper.readValue(createResp, RoomDto.class);
        Long roomId = created.getRoomId();

        RoomProductDto p1 = new RoomProductDto(null, null, "Item1", BigDecimal.valueOf(10.0), false);
        RoomProductDto p2 = new RoomProductDto(null, null, "Item2", BigDecimal.valueOf(15.5), false);
        RoomProductDto p3 = new RoomProductDto(null, null, "Item3", BigDecimal.valueOf(100), false);

        mockMvc.perform(post("/rooms/" + roomId + "/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/rooms/" + roomId + "/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p2)))
                .andExpect(status().isCreated());

        String p3Resp = mockMvc.perform(post("/rooms/" + roomId + "/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p3)))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString();

        RoomProductDto createdP3 = objectMapper.readValue(p3Resp, RoomProductDto.class);
        Long p3Id = createdP3.getProductId();

        mockMvc.perform(patch("/rooms/" + roomId + "/products/"+ p3Id +"/pay"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rooms/" + roomId + "/products/unpaid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(25.5));
    }
}
