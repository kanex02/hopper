package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Token;
import nz.ac.canterbury.seng302.tab.entity.TokenType;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;


    /**
     * Finds the token with the corresponding ID, as a {@link UUID}
     *
     * @param id The UUID of the token. This is probably encoded with the link.
     * @return Returns an optional that may contain the token associated with the id
     */
    public Optional<Token> getTokenById(UUID id) {
        return this.tokenRepository.findById(id);
    }

    public Optional<Token> getTokenByUser(UserEntity user, TokenType type) {
        return this.tokenRepository.findByUserAndType(user, type);
    }

    public Token createToken(UserEntity user, TokenType type) {
        return this.tokenRepository.save(new Token(user, type));
    }

    public void deleteToken(Token token) {
        this.tokenRepository.delete(token);
    }
}
