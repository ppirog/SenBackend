package com.sen.senbackend.gamelogic.controller;

import com.sen.senbackend.gamelogic.dto.responses.FrontendGameStateDTO;
import com.sen.senbackend.gamelogic.dto.responses.RoundHistoryDto;
import com.sen.senbackend.gamelogic.dto.responses.WakeUpResponseDto;
import com.sen.senbackend.gamelogic.dto.requests.SwapCardRequest;
import com.sen.senbackend.gamelogic.wrapper.FrontendGameWrapper;
import com.sen.senbackend.login.loginandregister.User;
import com.sen.senbackend.login.loginandregister.UserRepository;
import com.sen.senbackend.gamelogic.wrapper.GameWrapper;
import com.sen.senbackend.gamelogic.model.GameSession;
import com.sen.senbackend.gamelogic.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/frontend/game")
@RequiredArgsConstructor
public class GameFrontendController {

    private final GameService gameService;
    private final GameWrapper gameWrapper;
    private final FrontendGameWrapper frontendGameWrapper;
    private final UserRepository userRepository;

    @GetMapping("/{id}/state")
    public ResponseEntity<FrontendGameStateDTO> getGameState(@PathVariable Long id, Principal principal) {
        GameSession session = gameService.getGameSessionByIdAndPlayer(id, principal.getName());
        return ResponseEntity.ok(frontendGameWrapper.build(gameWrapper.build(session)));
    }

    @PostMapping("/start")
    public ResponseEntity<FrontendGameStateDTO> startGame(
            Principal principal,
            @RequestParam(defaultValue = "random") String strategy
    ) {
        String login = principal.getName();
        Long userId = getUserIdByLogin(login);
        GameSession session = gameService.createNewSession(userId, strategy);
        return ResponseEntity.ok(frontendGameWrapper.build(gameWrapper.build(session)));
    }

    @PatchMapping("/{id}/swap")
    public ResponseEntity<FrontendGameStateDTO> swapCardWithDiscard(
            @PathVariable Long id,
            @RequestBody SwapCardRequest request,
            Principal principal
    ) {
        GameSession session = gameService.swapCardWithDiscard(id, principal.getName(), request.getCardIndex());
        return ResponseEntity.ok(frontendGameWrapper.build(gameWrapper.build(session)));
    }

    @PostMapping("/{id}/skip-swap")
    public ResponseEntity<FrontendGameStateDTO> skipSwap(
            @PathVariable Long id,
            Principal principal
    ) {
        GameSession session = gameService.skipSwap(id, principal.getName());
        return ResponseEntity.ok(frontendGameWrapper.build(gameWrapper.build(session)));
    }

    @PostMapping("/{id}/draw")
    public ResponseEntity<FrontendGameStateDTO> drawCard(
            @PathVariable Long id,
            Principal principal
    ) {
        GameSession session = gameService.drawCardFromDeck(id, principal.getName());
        return ResponseEntity.ok(frontendGameWrapper.build(gameWrapper.build(session)));
    }

    @PostMapping("/{id}/wake-up")
    public ResponseEntity<WakeUpResponseDto> wakeUp(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(gameService.wakeUp(id, principal.getName()));
    }

    @GetMapping("/{id}/rounds")
    public ResponseEntity<List<RoundHistoryDto>> getRoundHistory(
            @PathVariable Long id,
            Principal principal
    ) {
        return ResponseEntity.ok(gameService.getRoundHistory(id, principal.getName()));
    }

    private Long getUserIdByLogin(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + login));
        return user.getId();
    }
}
