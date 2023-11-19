package pl.gr.veterinaryapp.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import pl.gr.veterinaryapp.common.OperationType;
import pl.gr.veterinaryapp.common.VisitStatus;
import pl.gr.veterinaryapp.common.VisitType;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder(builderClassName = "VisitResponseDtoBuilder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class VisitResponseDto extends RepresentationModel<VisitResponseDto> {

    private Long id;
    private Long vetId;
    private Long petId;
    private Long treatmentRoomId;
    private OffsetDateTime startDateTime;
    private Duration duration;
    private BigDecimal price;
    private VisitType visitType;
    private String visitDescription;
    private VisitStatus visitStatus;
    private OperationType operationType;

    public static class VisitResponseDtoBuilder {

        private Long id = 1L;
        private Long vetId = 1L;
        private Long petId = 1L;
        private Long treatmentRoomId = 1L;
        private OffsetDateTime startDateTime = OffsetDateTime.MIN;
        private Duration duration = Duration.ZERO;
        private BigDecimal price = BigDecimal.ZERO;
    }
}
