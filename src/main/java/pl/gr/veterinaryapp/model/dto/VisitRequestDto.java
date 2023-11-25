package pl.gr.veterinaryapp.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.gr.veterinaryapp.common.OperationType;
import pl.gr.veterinaryapp.common.VisitType;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;

@Data
@Builder(builderClassName = "VisitRequestDtoBuilder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class VisitRequestDto {

    private Long vetId;
    private Long petId;
    private OffsetDateTime startDateTime;
    private Duration duration;
    private BigDecimal price;
    private VisitType visitType;
    private OperationType operationType;
}
