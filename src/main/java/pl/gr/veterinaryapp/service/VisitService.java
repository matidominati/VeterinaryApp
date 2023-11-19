package pl.gr.veterinaryapp.service;

import org.springframework.security.core.userdetails.User;
import pl.gr.veterinaryapp.model.dto.*;
import pl.gr.veterinaryapp.model.entity.Visit;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

public interface VisitService {

    void deleteVisit(Long id);

    List<VisitResponseDto> getAllVisits(UserDto user);

    VisitResponseDto createVisit(UserDto user, VisitRequestDto visitRequestDto);

    VisitResponseDto finalizeVisit(VisitEditDto visitEditDto);

    VisitResponseDto getVisitById(UserDto user, Long id);

    List<AvailableVisitDto> getAvailableVisits(
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime,
            Collection<Long> vetIds);
}
