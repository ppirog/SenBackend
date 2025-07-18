package com.sen.senbackend.gamelogic.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WakeUpResponseDto {
    private int gameRound;
    private int movesInRound;
    private int playerPoints;
    private int aiPoints;
}
