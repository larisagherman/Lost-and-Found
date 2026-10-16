package backend.service;

import backend.repository.MatchItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchItemService {
    private final MatchItemRepository matchItemRepository;

}
