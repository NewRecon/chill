package ru.ivamly.chill.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.ivamly.chill.dto.CreateChillRq;
import ru.ivamly.chill.dto.CreateChillRs;
import ru.ivamly.chill.dto.GetChillRs;
import ru.ivamly.chill.dto.UpdateChillRq;
import ru.ivamly.chill.dto.UpdateChillRs;
import ru.ivamly.chill.mapper.ChillMapper;
import ru.ivamly.chill.service.ChillService;

import java.util.UUID;

@RestController
@RequestMapping("/chills")
@RequiredArgsConstructor
public class ChillController { // TODO добавить сваггер

    private final ChillService chillService;
    private final ChillMapper chillMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateChillRs create(@RequestBody @Valid CreateChillRq request) {
        return chillMapper.mapToCreateChillRs(
                chillService.create(
                        chillMapper.map(request)
                )
        );
    }

    @PutMapping("/{id}")
    public UpdateChillRs update(@PathVariable UUID id, @RequestBody @Valid UpdateChillRq request) {
        return chillMapper.mapToUpdateChillRs(
                chillService.update(
                        id,
                        chillMapper.map(request)
                )
        );
    }

    @GetMapping("/{id}")
    public GetChillRs get(@PathVariable UUID id) {
        return chillMapper.mapToGetChillRs(
                chillService.get(id)
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        chillService.delete(id);
    }
}
