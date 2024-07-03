package nz.ac.canterbury.seng302.tab.service.club;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.service.SportService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClubMockServiceTest {
    
    @Mock
    private SportService sportService;
    
    @Mock
    private Model model;
    
    @InjectMocks
    private ClubService clubService;
    
    private Club club;
    
    @BeforeEach
    void setup() {
        club = new Club("Test club", "Test description");
    }
    
    @Test
    void validateSport_noSportId_noSportReturned() {
        Sport result = clubService.validateSport(null, club, model);
        Assertions.assertNull(result);
    }
    
    @Test
    void validateSport_validSportId_sportReturned() {
        Mockito.when(sportService.findById(anyLong())).thenReturn(new Sport());
        Sport result = clubService.validateSport("1", club, model);
        Assertions.assertNotNull(club.getAssociatedSport());
        Assertions.assertNotNull(result);
    }
    
    @Test
    void validateSport_invalidSportId_sportReturned() {
        Sport result = clubService.validateSport("test", club, model);
        verify(model).addAttribute(eq("sportError"), anyString());
        Assertions.assertNull(result);
    }
    
    @Test
    void validateSport_noClub_noSportReturned() {
        Sport result = clubService.validateSport("1", null, model);
        Assertions.assertNull(result);
    }
    
    @Test
    void validateSport_noModel_noSportReturned() {
        Sport result = clubService.validateSport("1", club, null);
        Assertions.assertNull(result);
    }
    
}
