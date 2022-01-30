package com.sus.utils;

import com.sus.error.ErrorMessage;
import com.sus.error.SusException;
import com.sus.model.SusRequest;
import com.sus.model.UsabilityResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {

    public static String calculateGrade(Double percentile) {

        if (percentile >= 80.3 && percentile < 100.0)
            return "A";
        else if (percentile > 68 && percentile < 80.3)
            return "B";
        else if (percentile == 68)
            return "C";
        else if (percentile >= 51 && percentile < 68)
            return "D";
        else if (percentile < 51)
            return "F";
        else
            return "Z";

    }

    public static void validateRequest(SusRequest request) {

        var numbers = request.getUsabilityResponses().stream()
                .map(UsabilityResponse::getQuestionNumber).toList();

        var unique = numbers.stream()
                .distinct().toList();

        if (unique.size() != 10) {
            throw new SusException(ErrorMessage.builder().
                    code("SUS003")
                    .message("duplicate questions received")
                    .build());
        }
    }

    public static void validateDates(LocalDate fromDate, LocalDate toDate){

        if(fromDate!=null && fromDate.isAfter(LocalDate.now())){
            throw new SusException(ErrorMessage.builder().message("From date cannot be after current date").code("SUS003").build());
        }

        if(toDate!=null && toDate.isBefore(LocalDate.now())){
            throw new SusException(ErrorMessage.builder().message("To date cannot be after current date").code("SUS004").build());
        }

        if((fromDate!=null && toDate!=null) && (fromDate.isAfter(toDate))){
            throw new SusException(ErrorMessage.builder().message("From date cannot be after To date").code("SUS005").build());
        }

    }

}
