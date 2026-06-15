package ru.ivamly.chill.it;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import lombok.SneakyThrows;
import ru.ivamly.chill.config.MockMvcConfig;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(classes = MockMvcConfig.class)
@AutoConfigureMockMvc
abstract class BaseIntegrationTest { // TODO добавить конфигурацию api versioning

    private static boolean isDbInitialized = false;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @BeforeAll
    static void setupDb(@Autowired DataSource dataSource) throws Exception { // TODO переписать на миграции
        if (!isDbInitialized) {
            try (Connection conn = dataSource.getConnection()) {
                ScriptUtils.executeSqlScript(conn, new ClassPathResource("add-admin-to-users.sql"));
            }
            isDbInitialized = true;
        }
    }

    protected <T> String getContent(T value) {
        return jsonMapper.writeValueAsString(value);
    }

    @SneakyThrows
    protected <T> T getResponse(ResultActions resultAction, Class<T> type) {
        return jsonMapper.readValue(resultAction.andReturn().getResponse().getContentAsString(), type);
    }
}
