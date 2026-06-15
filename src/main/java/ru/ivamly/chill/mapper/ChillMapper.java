package ru.ivamly.chill.mapper;

import java.util.Collection;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.ivamly.chill.config.MapstructConfig;
import ru.ivamly.chill.dto.ChillInfo;
import ru.ivamly.chill.dto.CreateChillRq;
import ru.ivamly.chill.dto.CreateChillRs;
import ru.ivamly.chill.dto.GetChillRs;
import ru.ivamly.chill.dto.UpdateChillRq;
import ru.ivamly.chill.dto.UpdateChillRs;
import ru.ivamly.chill.entity.Chill;

@Mapper(config = MapstructConfig.class)
public interface ChillMapper {

    @Mapping(target = "id", ignore = true)
    Chill map(CreateChillRq source, UUID userId);

    ChillInfo map(Chill source);

    Collection<ChillInfo> map(Collection<Chill> source);

    @Mapping(target = "id", ignore = true)
    Chill map(UpdateChillRq source);

    @Mapping(target = "id", ignore = true)
    Chill map(UpdateChillRq source, UUID userId);

    CreateChillRs mapToCreateChillRs(Chill source);

    GetChillRs mapToGetChillRs(Chill source);

    UpdateChillRs mapToUpdateChillRs(Chill source);
}
