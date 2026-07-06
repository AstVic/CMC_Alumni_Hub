package ru.msu.cmc.alumnihub.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;
import ru.msu.cmc.alumnihub.security.CustomUserDetailsService;
import ru.msu.cmc.alumnihub.security.JwtAuthFilter;
import ru.msu.cmc.alumnihub.security.JwtService;
import ru.msu.cmc.alumnihub.security.RestAccessDeniedHandler;
import ru.msu.cmc.alumnihub.security.RestAuthenticationEntryPoint;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityTestController.class)
@Import({SecurityConfig.class, RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class, SecurityConfigProdTest.Beans.class})
@ActiveProfiles("prod")
class SecurityConfigProdTest {

    @Autowired MockMvc mockMvc;

    @Test
    void healthIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    void swaggerIsNotPublicInProduction() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpointRequiresAdminRole() throws Exception {
        mockMvc.perform(get("/api/admin/test")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ALUMNI")
    void alumniCannotUseAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/test")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanUseAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/test")).andExpect(status().isOk());
    }

    @TestConfiguration
    static class Beans {
        @Bean
        AppProperties appProperties() {
            return new AppProperties(
                    "https://alumni.example.com",
                    false,
                    new AppProperties.Admin("admin@example.com", "Strong-production-password-42"),
                    new AppProperties.Jwt("s".repeat(64), 60, 7),
                    new AppProperties.Invite(7),
                    new AppProperties.Mail("no-reply@example.com"),
                    new AppProperties.Storage("./target/test-uploads"),
                    new AppProperties.Moderation("rule-based", "", ""));
        }

        @Bean
        JwtAuthFilter jwtAuthFilter() {
            return new JwtAuthFilter(mock(JwtService.class), mock(CustomUserDetailsService.class));
        }

        @Bean
        UserDetailsService userDetailsService() {
            return username -> {
                throw new IllegalArgumentException("Not used in this test");
            };
        }
    }
}
