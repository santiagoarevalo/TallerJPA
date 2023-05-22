package co.com.icesi.icesiAccountSystem.integrationTests;

import co.com.icesi.icesiAccountSystem.dto.LoginDTO;
import co.com.icesi.icesiAccountSystem.dto.RequestAccountDTO;
import co.com.icesi.icesiAccountSystem.dto.TokenDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestConfigurationData.class)
@ActiveProfiles(profiles="test")

public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    @Order(1)
    @Test
    public void testCreateAnAccountWhenLoggedUserIsAdmin() throws Exception {
        var resultToken = mockMvc.perform(MockMvcRequestBuilders.post("/login").content(
                                objectMapper.writeValueAsString(new LoginDTO("johndoe@email.com", "password"))
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        TokenDTO token = objectMapper.readValue(resultToken.getResponse().getContentAsString(),TokenDTO.class);
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/accounts/create").content(
                                objectMapper.writeValueAsString(
                                        RequestAccountDTO.builder()
                                                .userEmail("keren@email.com")
                                                .balance(50000000)
                                                .type("deposit only")
                                                .build()
                                ))
                        .header("Authorization", "Bearer "+token.getToken())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Order(2)
    @Test
    public void testCreateAnAccountWhenUserLoggedIsABankUser() throws Exception {
        var resultToken = mockMvc.perform(MockMvcRequestBuilders.post("/login").content(
                                objectMapper.writeValueAsString(new LoginDTO("ethan@email.com", "password"))
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        TokenDTO token = objectMapper.readValue(resultToken.getResponse().getContentAsString(),TokenDTO.class);
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/accounts/create").content(
                                objectMapper.writeValueAsString(
                                        RequestAccountDTO.builder()
                                                .userEmail("keren@email.com")
                                                .balance(50000000)
                                                .type("deposit only")
                                                .build()
                                ))
                        .header("Authorization",  "Bearer "+token.getToken())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }
    @Order(3)
    @Test
    public void testAUserCanCreateAccountsByHimself() throws Exception {
        var resultToken = mockMvc.perform(MockMvcRequestBuilders.post("/login").content(
                                objectMapper.writeValueAsString(new LoginDTO("keren@email.com", "password"))
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        TokenDTO token = objectMapper.readValue(resultToken.getResponse().getContentAsString(),TokenDTO.class);
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/accounts/create").content(
                                objectMapper.writeValueAsString(
                                        RequestAccountDTO.builder()
                                                .userEmail("keren@email.com")
                                                .balance(1800000)
                                                .type("deposit only")
                                                .build()
                                ))
                        .header("Authorization",  "Bearer "+token.getToken())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Order(4)
    @Test
    public void testAUserCannotCreateAccountsForOtherUsers() throws Exception {
        var resultToken = mockMvc.perform(MockMvcRequestBuilders.post("/login").content(
                                objectMapper.writeValueAsString(new LoginDTO("keren@email.com", "password"))
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        TokenDTO token = objectMapper.readValue(resultToken.getResponse().getContentAsString(),TokenDTO.class);
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/accounts/create").content(
                                objectMapper.writeValueAsString(
                                        RequestAccountDTO.builder()
                                                .userEmail("ethan@email.com")
                                                .balance(1800000)
                                                .type("deposit only")
                                                .build()
                                ))
                        .header("Authorization",  "Bearer "+token.getToken())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

}
