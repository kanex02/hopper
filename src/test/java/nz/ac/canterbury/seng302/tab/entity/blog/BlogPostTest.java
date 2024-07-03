package nz.ac.canterbury.seng302.tab.entity.blog;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BlogPostTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "Sh",
            "ReallyLongTitleExceeding30Characters"
    })
    void testInvalidTitles(String title) {
        BlogPost post = new BlogPost();
        post.setTitle(title);
        Set<ConstraintViolation<BlogPost>> violations = validator.validate(post);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Valid Title",
            "Another One"
    })
    void whenCreateBlogPostWithValidTitle_onValidate_noViolations(String title) {
        BlogPost post = new BlogPost();
        post.setDescription("Valid Description");
        post.setTitle(title);
        Set<ConstraintViolation<BlogPost>> violations = validator.validate(post);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "Sh",
            """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque semper nunc non \
            neque bibendum, eu sagittis libero gravida. Donec faucibus risus nec ex placerat, \
            sed tincidunt metus lacinia. Ut arcu risus, tempus sed cursus et, facilisis eu nisl. \
            In hac habitasse platea dictumst. Nullam facilisis sapien nisi, sed blandit enim \
            dapibus volutpat. Suspendisse potenti. Suspendisse vitae blandit purus. Cras a \
            scelerisque ex, et efficitur urna. In gravida aliquam vestibulum. Sed vel leo in \
            leo volutpat iaculis. Ut lobortis vulputate tempor. Duis eu lectus rhoncus, \
            fermentum massa id, accumsan velit. Nulla at orci vitae eros condimentum pretium non \
            ultrices dolor.
            
            Curabitur tristique a mauris vel faucibus. Etiam ultricies rutrum eros, et pharetra \
            sem lobortis sed. Nunc sodales venenatis nulla in sagittis. Curabitur porta libero \
            vel augue lobortis, quis volutpat tortor elementum. Integer vehicula elementum augue \
            sed rutrum. Maecenas orci turpis, ultricies a felis eget, mattis sodales dui. \
            Phasellus tristique purus sit amet quam finibus efficitur. Vestibulum ornare justo \
            at enim accumsan, id varius est iaculis. Donec tellus sem, interdum at lectus in, \
            tempus mattis felis. Aliquam leo odio, suscipit eget posuere a, bibendum a purus. \
            Donec suscipit purus metus, eu cursus lacus pulvinar et.
            
            Nam aliquet tempus libero ac ornare. Morbi odio leo, blandit eu tortor et, finibus \
            maximus enim. Etiam tristique elit felis. Vestibulum ac metus id purus commodo \
            lobortis vitae vitae odio. Morbi malesuada gravida quam sit amet dapibus. Aenean \
            at turpis ligula. Integer iaculis blandit purus a venenatis. Sed viverra tortor ac \
            tellus mattis aliquam. Pellentesque dictum tellus eu nisi sodales maximus. Integer \
            sed eros nulla. Etiam rhoncus, leo ac rutrum sodales, urna elit dictum sem, a \
            rhoncus diam dui quis dui. Donec molestie libero vitae enim facilisis, nec porta ex \
            suscipit. Curabitur cursus tellus nec est maximus porttitor. Phasellus mauris ex, \
            sollicitudin placerat dolor vel, iaculis luctus leo. Integer consequat erat odio, \
            porttitor porttitor nibh vestibulum sed.
            
            Interdum et malesuada fames ac ante ipsum primis in faucibus. Nam vitae urna vel \
            felis dignissim hendrerit ut ac dui. Vestibulum nec finibus enim. Suspendisse potenti. \
            Proin purus dolor, efficitur eget dolor ac, ullamcorper lacinia leo. Nam vel rutrum \
            elit. Interdum et malesuada fames ac ante ipsum primis in faucibus. Sed convallis \
            congue turpis sit amet varius.
            
            In pretium ligula id mauris tristique, quis condimentum mauris fermentum. Vivamus \
            varius quam et lacus pharetra, non tempor metus accumsan. Etiam luctus, felis ut \
            congue posuere, nulla neque blandit purus, nec elementum tellus sapien vel augue. \
            Donec vestibulum enim id cursus sodales. Curabitur sed ex vel magna tempor \
            pellentesque. Aliquam sed sem a justo bibendum ultricies id eget felis. Aenean id \
            faucibus nibh. Maecenas sit amet magna sed neque tincidunt bibendum. Mauris in purus \
            purus.
            
            In suscipit at ligula sed facilisis. Integer in tincidunt diam. Vestibulum ante ipsum \
            primis in faucibus orci luctus et ultrices posuere cubilia curae; Mauris facilisis \
            arcu quam, eget consequat ante viverra at. Vivamus tempus massa venenatis leo sodales, \
            nec accumsan augue tincidunt. Ut eu gravida sem. Donec enim massa, gravida et \
            elementum vel, lobortis id orci.
            
            Mauris purus leo, blandit id est at, convallis malesuada diam. Quisque gravida nec \
            mauris in aliquet. Sed sed turpis ultricies, laoreet enim ut, placerat neque. \
            Vivamus eget viverra arcu. In orci aliquam.
            """
    })
    void whenCreateBlogPostWithInvalidDescription_onValidate_violationPresent(String description) {
        BlogPost post = new BlogPost();
        post.setTitle("A valid title");
        post.setDescription(description);
        Set<ConstraintViolation<BlogPost>> violations = validator.validate(post);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Good Description",
            "Another One"
    })
    void whenCreateBlogPostWithValidDescription_onValidate_noViolations(String description) {
        BlogPost post = new BlogPost();
        post.setTitle("A valid title");
        post.setDescription(description);
        Set<ConstraintViolation<BlogPost>> violations = validator.validate(post);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenCreateBlogPostWithInvalidBothClubAndTeamProxy_onValidate_throwException() {
        BlogPost post = new BlogPost();
        post.setClubProxy(new Club("ClubName", "ClubDescription"));
        post.setTeamProxy(Team.createTestTeam());
        assertThrows(IllegalArgumentException.class, post::validateProxiesAndTargets);
    }

    @Test
    void whenCreateBlogPostWithValidSingleProxyClub_onValidate_doesNotThrowException() {
        BlogPost post = new BlogPost();
        post.setClubProxy(new Club("ClubName", "ClubDescription"));
        assertDoesNotThrow(post::validateProxiesAndTargets);
    }

    @Test
    void whenCreateClubWithValidSingleProxyTeam_onValidate_doesNotThrowException() {
        BlogPost post = new BlogPost();
        post.setTeamProxy(Team.createTestTeam());
        assertDoesNotThrow(post::validateProxiesAndTargets);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test.png",
            "test.jpg",
            "test.jpeg",
            "test.svg"
    })
    void blogPostWithImage_whenGetType_returnsImageString(String filename) {
        BlogPost post = new BlogPost();
        post.setFileName(filename);
        assertEquals("IMAGE", post.getMediaType());
    }

    @Test
    void blogPostWithVideo_whenGetType_returnsVideoString() {
        BlogPost post = new BlogPost();
        post.setFileName("test.mp4");
        assertEquals("VIDEO", post.getMediaType());
    }

    @Test
    void blogPostWithNoMedia_whenGetType_returnsNull() {
        BlogPost post = new BlogPost();
        assertNull(post.getMediaType());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test.dge",
            "test.mte",
            "test.vxi",
            "test.plm"
    })
    void blogPostWithInvalidMedia_whenGetType_returnsNull(String filename) {
        BlogPost post = new BlogPost();
        post.setFileName(filename);
        assertNull(post.getMediaType());
    }

}