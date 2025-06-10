package com.epam.capstone.model.form;

import com.epam.capstone.dto.BookingDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingForm {

    public interface Step1 {}
    @NotNull(message = "{booking.step1.error}", groups = Step1.class)
    private Long locationId;

    public interface Step2Type {}
    @NotNull(message = "{booking.step2.errorType}", groups = Step2Type.class)
    private Integer roomTypeId;

    public interface Step2Room {}
    @NotNull(message = "{booking.step2.errorRoom}", groups = Step2Room.class)
    private Long roomId;

    public interface Step3 {}

    @NotBlank(groups = Step3.class)
    @Size(
            max = 255,
            message = "{booking.step3.errorPurposeLength}",
            groups = Step3.class
    )
    private String purpose;

    @NotNull(
            message = "{booking.step3.errorDateRequired}",
            groups  = Step3.class
    )
    private LocalDateTime startDateTime;

    @NotNull(
            message = "{booking.step3.errorDateRequired}",
            groups  = Step3.class
    )
    private LocalDateTime endDateTime;

    /**
     * Convert this form into a BookingDto.
     *
     * @param statusId the initial status (e.g. PENDING)
     * @return a BookingDto ready to be passed to BookingService
     */
    public BookingDto toBookingDto(Short statusId) {
        return new BookingDto(
                /* bookingId */   null,
                /* userId */      null,
                /* roomId */      this.roomId,
                /* statusId */    statusId,
                /* startTime */   this.startDateTime,
                /* endTime */     this.endDateTime,
                /* purpose */     this.purpose,
                /* createdAt */   null,
                /* updatedAt */   null
        );
    }
}