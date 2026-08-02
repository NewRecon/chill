package ru.ivamly.chill.it;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.ResultActions;
import ru.ivamly.chill.dto.CreateChillRq;
import ru.ivamly.chill.dto.CreateChillRs;
import ru.ivamly.chill.entity.Chill;
import ru.ivamly.chill.entity.enums.ChillType;
import ru.ivamly.chill.repository.ChillRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Создание заявки на chill")
class CreateChillTest extends BaseIntegrationTest {

    @Autowired
    private ChillRepository chillRepository;

    static Stream<Arguments> provideOverlappingChills() {
        Chill earlierSickChill = new Chill();
        earlierSickChill.setUserId(UUID.randomUUID());
        earlierSickChill.setType(ChillType.SICK);
        earlierSickChill.setStartDate(LocalDate.now().minusDays(10));
        earlierSickChill.setEndDate(LocalDate.now());

        Chill laterSickChill = new Chill();
        laterSickChill.setUserId(UUID.randomUUID());
        laterSickChill.setType(ChillType.SICK);
        laterSickChill.setStartDate(LocalDate.now());
        laterSickChill.setEndDate(LocalDate.now().plusDays(10));

        Chill middleSickChill = new Chill();
        middleSickChill.setUserId(UUID.randomUUID());
        middleSickChill.setType(ChillType.SICK);
        middleSickChill.setStartDate(LocalDate.now().minusDays(10));
        middleSickChill.setEndDate(LocalDate.now().plusDays(10));

        Chill sickChill = new Chill();
        sickChill.setUserId(UUID.randomUUID());
        sickChill.setType(ChillType.SICK);
        sickChill.setStartDate(LocalDate.now());
        sickChill.setEndDate(LocalDate.now().plusDays(5));

        Chill dayOffChill = new Chill();
        dayOffChill.setUserId(UUID.randomUUID());
        dayOffChill.setType(ChillType.OFF);
        dayOffChill.setStartDate(LocalDate.now());
        dayOffChill.setEndDate(LocalDate.now());

        return Stream.of(
                Arguments.of(earlierSickChill),
                Arguments.of(laterSickChill),
                Arguments.of(middleSickChill),
                Arguments.of(sickChill),
                Arguments.of(dayOffChill)
        );
    }

    static Stream<Arguments> provideInvalidRequests() {
        return Stream.of(
                Arguments.of(new CreateChillRq(
                        UUID.randomUUID(),
                        ChillType.OFF,
                        null,
                        LocalDate.now(),
                        LocalDate.now().minusDays(1L)
                )),
                Arguments.of(new CreateChillRq(
                        UUID.randomUUID(),
                        ChillType.SICK,
                        null,
                        LocalDate.now(),
                        LocalDate.now().minusDays(1L)
                )),
                Arguments.of(new CreateChillRq(
                        UUID.randomUUID(),
                        ChillType.SICK,
                        null,
                        null,
                        LocalDate.now().plusDays(1L)
                )),
                Arguments.of(new CreateChillRq(
                        UUID.randomUUID(),
                        ChillType.SICK,
                        null,
                        LocalDate.now(),
                        null
                )),
                Arguments.of(new CreateChillRq(
                        UUID.randomUUID(),
                        ChillType.SICK,
                        "",
                        LocalDate.now(),
                        LocalDate.now().plusDays(1)
                )),
                Arguments.of(new CreateChillRq(
                        UUID.randomUUID(),
                        ChillType.SICK,
                        null,
                        LocalDate.now().minusDays(1),
                        LocalDate.now().plusDays(1)
                ))
        );
    }

    static Stream<Arguments> provideValidRequests() {
        return Stream.of(
                Arguments.of(new CreateChillRq(
                        UUID.randomUUID(),
                        ChillType.OFF,
                        "some comment",
                        LocalDate.now(),
                        LocalDate.now().plusDays(1L)
                )),
                Arguments.of(new CreateChillRq(
                        UUID.randomUUID(),
                        ChillType.SICK,
                        "some comment",
                        LocalDate.now(),
                        LocalDate.now().plusDays(5L)
                ))
        );
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideValidRequests")
    @DisplayName("Успешное создание заявки на chill")
    void shouldCreateChill(CreateChillRq request) {
        // when
        ResultActions resultAction = mockMvc.perform(post("/api/1/chills")
                .content(getContent(request)));

        // then
        resultAction.andExpect(status().isCreated());
        CreateChillRs response = getResponse(resultAction, CreateChillRs.class);
        assertThat(response.id())
                .isNotNull();
        Chill chill = chillRepository.findById(response.id()).orElseThrow();
        assertThat(response.id())
                .isEqualTo(chill.getId());
        assertThat(response.userId())
                .isEqualTo(chill.getUserId())
                .isEqualTo(request.userId());
        assertThat(response.type())
                .isEqualTo(chill.getType())
                .isEqualTo(request.type());
        assertThat(response.comment())
                .isEqualTo(chill.getComment())
                .isEqualTo(request.comment());
        assertThat(response.startDate())
                .isEqualTo(chill.getStartDate())
                .isEqualTo(request.startDate());
        assertThat(response.endDate())
                .isEqualTo(chill.getEndDate())
                .isEqualTo(request.endDate());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideOverlappingChills")
    @DisplayName("Ошибка создания при пересечении с существующим chill")
    void shouldReturnConflict(Chill chill) {
        // given
        Chill savedChill = chillRepository.save(chill);
        CreateChillRq request = new CreateChillRq(
                savedChill.getUserId(),
                ChillType.SICK,
                null,
                LocalDate.now(),
                LocalDate.now().plusDays(5)
        );

        // when
        ResultActions resultAction = mockMvc.perform(post("/api/1/chills")
                .content(getContent(request)));

        // then
        String timestamp = "timestamp";
        resultAction.andExpect(status().isConflict());
        ProblemDetail response = getResponse(resultAction, ProblemDetail.class);
        assertThat(response.getTitle())
                .isNotBlank();
        Map<String, Object> properties = response.getProperties();
        assertThat(properties)
                .usingRecursiveComparison()
                .ignoringFields(timestamp)
                .isEqualTo(
                        Map.of(
                                "userId", request.userId().toString(),
                                "startDate", request.startDate().toString(),
                                "endDate", request.endDate().toString()
                        )
                );
        assertThat(Instant.parse((String) response.getProperties().get(timestamp)))
                .isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideInvalidRequests")
    @DisplayName("Ошибка валидации")
    void shouldReturnBadRequest(CreateChillRq request) {
        // when
        ResultActions resultAction = mockMvc.perform(post("/api/1/chills")
                .content(getContent(request)));

        // then
        resultAction.andExpect(status().isBadRequest());
        ProblemDetail response = getResponse(resultAction, ProblemDetail.class);
        assertThat(response.getTitle())
                .isEqualTo("Bad Request");
    }
}
