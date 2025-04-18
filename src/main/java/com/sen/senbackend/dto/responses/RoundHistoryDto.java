package com.sen.senbackend.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoundHistoryDto {
    private int roundNumber;
    private int playerPoints;
    private int aiPoints;
}
