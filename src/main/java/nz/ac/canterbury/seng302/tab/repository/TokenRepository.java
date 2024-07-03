package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Token;
import nz.ac.canterbury.seng302.tab.entity.TokenType;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends CrudRepository<Token, UUID> {


    /**
     * Gets all tokens of the specified type
     *
     * @param type The type to get tokens by
     * @return Returns an iterable of tokens with the matching type
     */
    Iterable<Token> findAllByType(TokenType type);

    Optional<Token> findByUserAndType(UserEntity user, TokenType type);

}
