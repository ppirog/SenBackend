package com.sen.senbackend.controller;

import com.sen.senbackend.dto.GameStateDTO;
import com.sen.senbackend.login.loginandregister.User;
import com.sen.senbackend.login.loginandregister.UserRepository;
import com.sen.senbackend.mapper.GameMapper;
import com.sen.senbackend.model.GameSession;
import com.sen.senbackend.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final GameMapper gameMapper;
    private final UserRepository userRepository;

    @GetMapping("/{id}/state")
    public ResponseEntity<GameStateDTO> getGameState(@PathVariable Long id, Principal principal) {
        GameSession session = gameService.getGameSessionByIdAndPlayer(id, principal.getName());
        return ResponseEntity.ok(gameMapper.toDto(session));
    }

    @PostMapping("/start")
    public ResponseEntity<GameStateDTO> startGame(Principal principal) {
        String login = principal.getName();
        Long userId = getUserIdByLogin(login);
        GameSession session = gameService.createNewSession(userId);
        return ResponseEntity.ok(gameMapper.toDto(session));
    }

    private Long getUserIdByLogin(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + login));
        return user.getId();
    }
}
