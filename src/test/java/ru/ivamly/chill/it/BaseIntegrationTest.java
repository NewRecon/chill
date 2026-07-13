package ru.ivamly.chill.it;

import lombok.SneakyThrows;
import ru.ivamly.chill.config.MockMvcConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(classes = MockMvcConfig.class)
@AutoConfigureMockMvc
abstract class BaseIntegrationTest { // TODO добавить конфигурацию api versioning

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    protected <T> String getContent(T value) {
        return jsonMapper.writeValueAsString(value);
    }

    @SneakyThrows
    protected <T> T getResponse(ResultActions resultAction, Class<T> type) {
        return jsonMapper.readValue(resultAction.andReturn().getResponse().getContentAsString(), type);
    }
}
