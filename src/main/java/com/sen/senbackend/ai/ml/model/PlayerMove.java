package com.sen.senbackend.ai.ml.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "player_moves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PlayerMove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    private Integer roundNumber;

    private Integer weakestCard;

    private Integer discardTop;

    private Double averageCardValue;

    private Integer differenceWeakestToDiscardTop;

    private Boolean goodDecision;

    private LocalDateTime createdAt;
}
