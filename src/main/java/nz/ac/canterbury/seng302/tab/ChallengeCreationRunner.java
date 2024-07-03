package nz.ac.canterbury.seng302.tab;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.challenge.GeneralChallengeTemplate;
import nz.ac.canterbury.seng302.tab.entity.challenge.QuantifiableChallengeFactory;
import nz.ac.canterbury.seng302.tab.repository.challenge.GeneralChallengeTemplateRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.QuantifiableChallengeFactoryRepository;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.challenge.ChallengeGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ChallengeCreationRunner implements ApplicationRunner {

    @Autowired
    private GeneralChallengeTemplateRepository generalChallengeTemplateRepository;

    @Autowired
    private QuantifiableChallengeFactoryRepository quantifiableChallengeFactoryRepository;

    @Autowired
    private SportService sportService;

    private int getSportID(Sport[] sportList,String sportName){
        int index = -1; // Initialize the index to -1 (not found)

        // Iterate through the array
        for (int i = 0; i < sportList.length; i++) {
            if (sportList[i] != null) {
                if (sportList[i].getName().equals(sportName)) {
                    // If the current element is equal to the target, set the index and break the loop
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    @Override
    public void run(ApplicationArguments args) {
        Sport[] sportList = {
                new Sport("Basketball"),
                new Sport("Cricket"),
                new Sport("Tennis"),
                new Sport("Volleyball"),
                new Sport("Table Tennis"),
                new Sport("Baseball"),
                new Sport("Golf"),
                new Sport("American Football"),
                new Sport("Rugby"),
                new Sport("Hockey"),
                new Sport("Cycling"),
                new Sport("Swimming"),
                new Sport("Badminton"),
                new Sport("Boxing"),
                new Sport("Gymnastics"),
                new Sport("Martial Arts"),
                new Sport("Wrestling"),
                new Sport("Ice Hockey"),
                new Sport("Running"),
                new Sport("Track and Field"),
                new Sport("Fishing"),
                new Sport("Rowing"),
                new Sport("Skiing"),
                new Sport("Snowboarding"),
                new Sport("Surfing"),
                new Sport("Skateboarding"),
                new Sport("Handball"),
                new Sport("Netball"),
                new Sport("Water Polo"),
                new Sport("Field Hockey"),
                new Sport("Archery"),
                new Sport("Billiards"),
                new Sport("Bowling"),
                new Sport("Canoeing"),
                new Sport("Curling"),
                new Sport("Equestrian"),
                new Sport("Fencing"),
                new Sport("Sailing"),
                new Sport("Shooting"),
                new Sport("Snooker"),
                new Sport("Squash"),
                new Sport("Triathlon"),
                new Sport("Weightlifting"),
                new Sport("Polo"),
                new Sport("Lacrosse"),
                new Sport("Beach Volleyball"),
                new Sport("Darts"),
                new Sport("Kabaddi"),
                new Sport("Sepak Takraw"),
                new Sport("Hurling"),
                new Sport("Gaelic Football"),
                new Sport("Pickleball"),
                new Sport("Bocce"),
                new Sport("Inline Skating"),
                new Sport("Ultimate Frisbee"),
                new Sport("CrossFit"),
                new Sport("Orienteering"),
                new Sport("Pétanque"),
                new Sport("Softball"),
                new Sport("Aussie Rules"),
                new Sport("Chess Boxing"),
                new Sport("MuseSwipr"),
                new Sport("Raptor Rugby"),
                new Sport("Goose Pulling"),
                new Sport("Competitive Eating")
        };



        for (int i = 0; i < sportList.length; i++) {
            try {
                sportList[i] = sportService.getSportByName(sportList[i].getName());
            } catch (Exception e) {
                sportList[i] = sportService.addSport(sportList[i]);
            }
        }

            if (generalChallengeTemplateRepository.isEmpty()) {
                // Create and save general challenges
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.MEDIUM_HOPS_REWARD, "Read a book", "Read any book for 30 minutes"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.MEDIUM_HOPS_REWARD, "Meditate", "Meditate for 10 minutes"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.MEDIUM_HOPS_REWARD, "Yoga", "Do yoga for 30 minutes"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.MEDIUM_HOPS_REWARD, "Puzzle Time", "Complete a 1000 piece puzzle"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.LOW_HOPS_REWARD, "Puzzle Time", "Complete a 100 piece puzzle"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.HIGH_HOPS_REWARD, "Puzzle Time", "Complete a 2000 piece puzzle"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.HIGH_HOPS_REWARD, "Rock Climbing", "Try out rock climbing"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.LOW_HOPS_REWARD, "Stretch", "Stretch for 15 minutes"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.MEDIUM_HOPS_REWARD, "Cook a Healthy Meal", "Prepare a balanced meal at home"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.LOW_HOPS_REWARD, "Visit a Museum", "Visit a local museum or art gallery"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.MEDIUM_HOPS_REWARD, "Plant a Tree", "Plant a tree or some flowers"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.MEDIUM_HOPS_REWARD, "Volunteer", "Volunteer at a local organization"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.HIGH_HOPS_REWARD, "Digital Detox", "Stay off social media for a day"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.LOW_HOPS_REWARD, "Watch a Documentary", "Watch an educational documentary"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.HIGH_HOPS_REWARD, "Try a New Hobby", "Spend an hour on a hobby you've never tried before"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.LOW_HOPS_REWARD, "Write a Journal Entry", "Write a page in your journal"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.MEDIUM_HOPS_REWARD, "Photography", "Take photos of five different kinds of flowers or animals"));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.HIGH_HOPS_REWARD, "Ouch!", "Break your leg."));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.HIGH_HOPS_REWARD, "Ouch!", "Break or sprain your ankle."));
                generalChallengeTemplateRepository.save(new GeneralChallengeTemplate(ChallengeGeneratorService.HIGH_HOPS_REWARD, "Ouch!", "Break or sprain your wrist."));


            }

            if (!quantifiableChallengeFactoryRepository.isEmpty()) {
                quantifiableChallengeFactoryRepository.deleteAll();
            }
            if (quantifiableChallengeFactoryRepository.isEmpty()) {
                // Create and save quantifiable challenge factories
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Run", "Run for %d kilometers"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Walk", "Walk for %d kilometers"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(3.0, "Swim", "Swim %d laps"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(3.0, "Cycle", "Cycle for %d kilometers"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Push-Ups", "Do %d push-ups"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Sit-Ups", "Do %d sit-ups"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Squats", "Do %d squats"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Lunges", "Do %d lunges"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Burpees", "Do %d burpees"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(3.0, "Pull-Ups", "Do %d pull-ups"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(3.0, "Chin-Ups", "Do %d chin-ups"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Dips", "Do %d dips"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Crunches", "Do %d crunches"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Jumping Jacks", "Do %d jumping jacks"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Kettlebell Swings", "Do %d kettlebell swings"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(10.0, "Plank", "Hold a plank for %d seconds"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Rowing Machine", "Row for %d kilometers"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(4.0, "Treadmill", "Run or walk on a treadmill for %d minutes"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Mountain Climbers", "Do %d mountain climbers"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Calf Raises", "Do %d calf raises"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Box Jumps", "Do %d box jumps"));
                quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Skipping", "Skip rope %d times"));
                //Soccer
                int sportIndex = getSportID(sportList, "Soccer");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(5.0, "Dribble", "Dribble the ball for %d meters", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Shoot", "Take %d shots on goal", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Passing", "Complete %d successful passes", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Juggling", "Juggle the ball %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Headers", "Score %d goals with headers", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Free Kicks", "Score %d goals from free kicks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Assists", "Provide %d assists to teammates", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Dribble Around Cones", "Successfully dribble around %d cones", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Penalty Kicks", "Score %d goals from penalty kicks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Tackles", "Execute %d successful tackles", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Long Passes", "Complete %d accurate long passes", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Crosses", "Deliver %d accurate crosses into the box", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Keep-Ups", "Keep the ball in the air for %d seconds", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Solo Runs", "Complete %d successful solo runs past opponents", sportList[sportIndex]));
                }
                //Rugby
                sportIndex = getSportID(sportList, "Rugby");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Tackles", "Execute %d successful tackles", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Lineouts", "Win %d lineouts", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Scrum Engagements", "Engage in %d scrums", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Try Scoring", "Score %d tries", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Kicking Accuracy", "Achieve %d successful kicks at goal", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Ruck Success", "Win %d rucks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Conversions", "Successfully convert %d tries", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Offloads", "Complete %d successful offloads", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Meters Gained", "Gain %d meters with ball in hand", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Break Tackles", "Break %d tackles", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Clean Breaks", "Make %d clean breaks through the defense", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Steals", "Steal the ball %d times in rucks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Interceptions", "Make %d interceptions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Side Steps", "Execute %d successful side steps", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Drop Goals", "Score %d drop goals", sportList[sportIndex]));
                }
                //Cricket
                sportIndex = getSportID(sportList, "Cricket");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Wickets Taken", "Take %d wickets", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Runs Scored", "Score %d runs", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Sixes Hit", "Hit %d sixes", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Centuries", "Score %d centuries", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Catches Taken", "Take %d catches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Fifties Scored", "Score %d fifties", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Maiden Overs", "Bowl %d maiden overs", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Dot Balls Bowled", "Bowl %d dot balls", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Run Outs", "Execute %d run outs", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Hat-Tricks", "Achieve %d hat-tricks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Duck Avoidance", "Avoid getting out for %d ducks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Boundaries Hit", "Hit %d boundaries (4s and 6s)", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Partnership Runs", "Participate in %d partnerships totaling 100 runs or more", sportList[sportIndex]));
                }
                //Table Tennis
                sportIndex = getSportID(sportList, "Table Tennis");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Matches Won", "Win %d matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Points Scored", "Score %d points", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Aces Served", "Serve %d aces", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Games Won", "Win %d games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Service Errors", "Commit %d service errors", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Rally Length", "Participate in %d rallies of 30 shots or more", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Shots Played", "Play %d shots in a single rally", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Topspin Shots", "Execute %d successful topspin shots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Smash Winners", "Win points with %d smash winners", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Spin Shots", "Execute %d successful spin shots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Forehand Winners", "Win points with %d forehand winners", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Backhand Winners", "Win points with %d backhand winners", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Drop Shots", "Execute %d successful drop shots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Long Rallies", "Participate in %d rallies lasting 5 minutes or more", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Service Returns", "Successfully return %d serves", sportList[sportIndex]));
                }
                // Basketball
                sportIndex = getSportID(sportList, "Basketball");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Three-Pointers", "Make %d three-pointers", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Free Throws", "Score %d free throws", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Assists", "Provide %d assists to teammates", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Dribbling", "Dribble the ball for %d meters", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Rebounds", "Grab %d rebounds", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Steals", "Steal the ball %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Blocks", "Block %d shots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Points Scored", "Score %d points", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Fast Breaks", "Score %d points on fast breaks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Assist-to-Turnover Ratio", "Achieve a %d:1 assist-to-turnover ratio", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Double-Doubles", "Record %d double-doubles (points and rebounds or assists)", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Triple-Doubles", "Record %d triple-doubles (points, rebounds, and assists)", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Minutes Played", "Play %d minutes in a single game", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Crossovers", "Successfully execute %d crossovers", sportList[sportIndex]));
                }

                // Tennis
                sportIndex = getSportID(sportList, "Tennis");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Matches Won", "Win %d matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Points Scored", "Score %d points", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Aces Served", "Serve %d aces", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Sets Won", "Win %d sets", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Double Faults", "Commit %d double faults", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Longest Rally", "Participate in %d rallies of 30 shots or more", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Winning Streak", "Achieve a %d-game winning streak", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Forehand Winners", "Win points with %d forehand winners", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Smash Winners", "Win points with %d smash winners", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Backhand Winners", "Win points with %d backhand winners", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Service Returns", "Successfully return %d serves", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Net Approaches", "Execute %d successful net approaches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Drop Shots", "Execute %d successful drop shots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Long Matches", "Participate in %d matches lasting 3 hours or more", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Tiebreaks Won", "Win %d tiebreaks", sportList[sportIndex]));
                }
                // Volleyball
                sportIndex = getSportID(sportList, "Volleyball");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Sets Won", "Win %d sets", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Matches Won", "Win %d matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Aces Served", "Serve %d aces", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Blocks", "Block %d spikes", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Receives", "Receive %d serves successfully", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Attacks", "Make %d successful attacks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Digs", "Dig %d balls successfully", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Sets Assists", "Provide %d assists to teammates", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Errors Avoided", "Avoid %d errors in the game", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Spikes", "Score %d points with spikes", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Service Receives", "Receive %d serves successfully", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Block Points", "Score %d points with successful blocks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Defensive Plays", "Make %d successful defensive plays", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Long Rallies", "Participate in %d rallies lasting 5 minutes or more", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Winning Streak", "Achieve a %d-game winning streak", sportList[sportIndex]));
                }
                // Baseball
                sportIndex = getSportID(sportList, "Baseball");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Home Runs", "Hit %d home runs", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "RBIs", "Drive in %d runs (RBIs)", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Stolen Bases", "Steal %d bases", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Strikeouts", "Record %d strikeouts as a pitcher", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Double Plays", "Turn %d double plays", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Triples", "Hit %d triples", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Runs Scored", "Score %d runs", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Base Hits", "Collect %d base hits", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Errors Avoided", "Avoid %d errors in the game", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Pitching Wins", "Record %d wins as a pitcher", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Perfect Games", "Pitch %d perfect games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "No-Hitters", "Pitch %d no-hitters", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Complete Games", "Pitch %d complete games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Extra Innings", "Participate in %d extra-inning games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Championships", "Win %d championships", sportList[sportIndex]));
                }
                // Golf
                sportIndex = getSportID(sportList, "Golf");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Birdies", "Score %d birdies", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Eagles", "Score %d eagles", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Hole-in-Ones", "Achieve %d hole-in-ones", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Fairways Hit", "Hit %d fairways accurately", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Greens in Regulation", "Reach %d greens in regulation", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Longest Drive", "Hit the longest drive of %d yards", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Putts Per Hole", "Average %d putts per hole", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Par Rounds", "Achieve %d rounds at par or better", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Bunkers Avoided", "Avoid %d bunkers in the game", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Tournament Wins", "Win %d golf tournaments", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Majors Won", "Win %d major championships", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Course Records", "Set %d course records", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Hole Records", "Set %d hole records", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Career Grand Slam", "Achieve a %d career Grand Slam", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Hole-in-One Challenges", "Complete %d hole-in-one challenges", sportList[sportIndex]));
                }
                // American Football
                sportIndex = getSportID(sportList, "American Football");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Touchdowns", "Score %d touchdowns", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Passing Yards", "Throw for %d passing yards", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Rushing Yards", "Gain %d rushing yards", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Passing Touchdowns", "Throw %d passing touchdowns", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Interceptions", "Intercept %d passes", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Sacks", "Record %d sacks as a defender", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Field Goals", "Kick %d field goals", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Extra Points", "Score %d extra points", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Fumbles Recovered", "Recover %d fumbles", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Game-Winning Drives", "Lead %d game-winning drives", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Game Wins", "Win %d games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Tournament Wins", "Win %d Tournaments", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Passing Accuracy", "Achieve %d passing accuracy", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Kickoff Returns", "Return %d kickoffs for touchdowns", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Rushing Touchdowns", "Score %d rushing touchdowns", sportList[sportIndex]));
                }
                // Hockey
                sportIndex = getSportID(sportList, "Hockey");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Goals Scored", "Score %d goals", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Assists", "Record %d assists", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Shots on Goal", "Take %d shots on goal", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Power Play Goals", "Score %d power play goals", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Penalties Drawn", "Draw %d penalties", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Blocked Shots", "Block %d shots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Hits", "Deliver %d hits", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Faceoffs Won", "Win %d faceoffs", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Minutes Played", "Play %d minutes on the ice", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Hat Tricks", "Score %d hat tricks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Shutouts", "Achieve %d shutouts as a goalie", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Game-Winning Goals", "Score %d game-winning goals", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Overtime Goals", "Score %d overtime goals", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Hat Trick Challenges", "Complete %d hat trick challenges", sportList[sportIndex]));
                }
                // Cycling
                sportIndex = getSportID(sportList, "Cycling");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Distance Ridden", "Ride %d kilometers", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Elevation Gain", "Climb %d meters in elevation", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Sprints", "Complete %d sprints", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Time Trials", "Achieve %d personal bests in time trials", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Group Rides", "Participate in %d group rides", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Gran Fondos", "Complete %d Gran Fondo events", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Century Rides", "Complete %d century (100-mile) rides", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Hill Climbs", "Conquer %d challenging hill climbs", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Time in the Saddle", "Accumulate %d hours of riding time", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Race Wins", "Win %d cycling races", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Bike Upgrades", "Upgrade your bike %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Strava Achievements", "Achieve %d Strava achievements", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Bikepacking Adventures", "Complete %d bikepacking adventures", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Charity Rides", "Participate in %d charity rides", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Bike Maintenance", "Perform %d bike maintenance tasks", sportList[sportIndex]));
                }
                // Swimming
                sportIndex = getSportID(sportList, "Swimming");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Distance Swam", "Swim %d kilometers", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Laps Completed", "Complete %d laps in a pool", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Open Water Swims", "Complete %d open water swims", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Time Trials", "Achieve %d personal bests in time trials", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Butterfly Strokes", "Swim %d meters using butterfly stroke", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Medley Challenges", "Complete %d medley (IM) challenges", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Diving Practice", "Perform %d successful dives", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Water Polo Matches", "Participate in %d water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Relay Races", "Participate in %d relay races", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Race Wins", "Win %d swimming races", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Distance Challenges", "Complete %d long-distance swimming challenges", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Water Aerobics", "Attend %d water aerobics sessions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Scuba Diving", "Go on %d scuba diving trips", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Pool Cleanliness", "Contribute to %d pool cleaning sessions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Lifeguard Duty", "Serve %d hours of lifeguard duty", sportList[sportIndex]));
                }
                // Badminton
                sportIndex = getSportID(sportList, "Badminton");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Matches Won", "Win %d badminton matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Points Scored", "Score %d points in badminton", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Shuttlecock Serves", "Serve %d shuttlecocks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Games Won", "Win %d badminton games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Smash Winners", "Win points with %d smash winners", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Rally Length", "Participate in %d long rallies", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Drop Shots", "Execute %d successful drop shots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Backhand Winners", "Win points with %d backhand winners", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Service Errors", "Commit %d service errors", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Spin Shots", "Execute %d successful spin shots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Forehand Winners", "Win points with %d forehand winners", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Mixed Doubles", "Win %d mixed doubles matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Long Matches", "Participate in %d badminton matches lasting over an hour", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Shuttlecock Retrieval", "Retrieve %d shuttlecocks from difficult positions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Doubles Play", "Play %d doubles matches", sportList[sportIndex]));
                }
                // Boxing
                sportIndex = getSportID(sportList, "Boxing");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Matches Won", "Win %d boxing matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Knockouts", "Achieve %d knockouts in boxing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Rounds Sparred", "Spar %d rounds with opponents", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Titles Won", "Win %d boxing titles", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Jab Strikes", "Land %d jab strikes", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Hooks Landed", "Land %d hook punches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Uppercuts Landed", "Land %d uppercut punches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Body Blows", "Land %d body blows", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Dodge Mastery", "Successfully dodge %d opponent punches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Counterpunches", "Land %d counterpunches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Sparring Partners", "Spar with %d different opponents", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Training Sessions", "Attend %d boxing training sessions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Golden Gloves", "Participate in %d Golden Gloves tournaments", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Boxing Championships", "Win %d national boxing championships", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Punching Bags", "Punch %d punching bags during training", sportList[sportIndex]));
                }
                // Gymnastics
                sportIndex = getSportID(sportList, "Gymnastics");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Routines Perfected", "Perform %d gymnastic routines flawlessly", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Gold Medals Earned", "Earn %d gold medals in gymnastic competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Balance Beam Mastery", "Master %d balance beam routines", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Vault Excellence", "Excel in %d vault exercises", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Floor Routines", "Perfect %d floor exercise routines", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Parallel Bars Dominance", "Dominate in %d parallel bars routines", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Pommel Horse Skills", "Master %d pommel horse routines", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "High Bar Mastery", "Achieve %d high bar routines with excellence", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Uneven Bars Excellence", "Excel in %d uneven bars routines", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Ring Domination", "Dominate in %d ring exercises", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Gymnastic Championships", "Win %d gymnastic championships", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "National Representation", "Represent your nation in %d international gymnastic events", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Aerial Skills", "Master %d aerial gymnastic skills", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Acrobatic Excellence", "Achieve %d acrobatic feats of excellence", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Gymnastic Hall of Fame", "Be inducted into the gymnastic hall of fame %d times", sportList[sportIndex]));
                }
                // Karate
                sportIndex = getSportID(sportList, "Karate");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Katas Perfected", "Perform %d Karate katas flawlessly", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Sparring Matches Won", "Win %d sparring matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Breaking Boards", "Successfully break %d boards in Karate", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Kick Techniques", "Execute %d powerful kick techniques", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Punching Accuracy", "Achieve %d accurate punches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Spinning Kicks", "Perform %d spinning kick maneuvers", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Belt Progression", "Advance through %d Karate belt levels", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Tournament Victories", "Win %d Karate tournaments", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Karate Demonstrations", "Participate in %d Karate demonstrations", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Instructor Certification", "Become a certified Karate instructor %d times", sportList[sportIndex]));
                }
                // Ice Hockey
                sportIndex = getSportID(sportList, "Ice Hockey");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Goals Scored", "Score %d goals in ice hockey", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Assists", "Record %d assists in ice hockey", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Shots on Goal", "Take %d shots on goal in ice hockey", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Power Play Goals", "Score %d power play goals in ice hockey", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Penalties Drawn", "Draw %d penalties in ice hockey", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Blocked Shots", "Block %d shots in ice hockey", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Hits", "Deliver %d hits in ice hockey", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Faceoffs Won", "Win %d faceoffs in ice hockey", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Minutes Played", "Play %d minutes on the ice in ice hockey", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Hat Tricks", "Score %d hat tricks in ice hockey", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Shutouts", "Achieve %d shutouts as a goalie in ice hockey", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Game-Winning Goals", "Score %d game-winning goals in ice hockey", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Overtime Goals", "Score %d overtime goals in ice hockey", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Hat Trick Challenges", "Complete %d hat trick challenges in ice hockey", sportList[sportIndex]));
                }
                // Running
                sportIndex = getSportID(sportList, "Running");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Miles Run", "Run %d miles", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Marathons Completed", "Complete %d marathons", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Sprints", "Complete %d sprints", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Personal Bests", "Achieve %d personal best times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Trail Runs", "Participate in %d trail runs", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Ultra Marathons", "Complete %d ultra marathons", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Half Marathons", "Complete %d half marathons", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Hill Climbs", "Conquer %d challenging hill climbs", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Time on the Track", "Accumulate %d hours of track running", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Race Wins", "Win %d running races", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Achievement Unlocked", "Earn %d running achievements", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Running Clubs", "Join %d running clubs", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Charity Runs", "Participate in %d charity runs", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Interval Training", "Complete %d interval training sessions", sportList[sportIndex]));
                }
                // Track and Field
                sportIndex = getSportID(sportList, "Track and Field");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Medals Earned", "Earn %d medals in track and field events", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Records Set", "Set %d records in track and field events", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Personal Bests", "Achieve %d personal best performances", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Gold Medals", "Win %d gold medals in track and field events", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Multi-Event Mastery", "Excel in %d multi-event competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Relay Dominance", "Lead %d relay teams to victory", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Pole Vault Heights", "Clear %d heights in pole vault", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Throws Accuracy", "Achieve %d accurate throws in field events", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Decathlon Excellence", "Complete %d decathlons with excellence", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Steplechase Triumphs", "Triumph in %d steeplechase races", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Hammer Throws", "Throw the hammer %d meters", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "High Jump Heights", "Clear %d heights in high jump", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Long Jump Distances", "Jump %d meters in long jump", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Cross Country Races", "Complete %d cross country races", sportList[sportIndex]));}
                // Fishing
                sportIndex = getSportID(sportList, "Fishing");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Fish Caught", "Catch %d fish while fishing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Biggest Catch", "Catch the biggest fish weighing %d pounds", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Different Species", "Catch %d different fish species", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Tournaments Won", "Win %d fishing tournaments", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Fly Fishing Mastery", "Master the art of fly fishing with %d successful catches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Deep Sea Adventures", "Embark on %d deep-sea fishing adventures", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Fish Tales", "Tell %d interesting fish stories", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Casting Accuracy", "Achieve %d accurate casts while fishing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Catch and Release", "Release %d caught fish back into the water", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Golden Reel Awards", "Earn %d Golden Reel awards", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Ice Fishing", "Brave the cold and go ice fishing %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Fishing Competitions", "Participate in %d fishing competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "River Expeditions", "Explore %d different rivers for fishing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Catch and Cook", "Cook %d of your caught fish", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Underwater Photography", "Take photos of %d fish underwater", sportList[sportIndex]));
                }
                // Rowing
                sportIndex = getSportID(sportList, "Rowing");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Distance Rowed", "Row %d kilometers", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Regatta Wins", "Win %d rowing regattas", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Crew Size", "Row with %d different crew members", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Time Trials", "Achieve %d personal best times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Sculling Excellence", "Excel in %d sculling events", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Coxswain Skills", "Serve as a coxswain for %d races", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Single Sculls Mastery", "Master %d single sculls races", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Ergometer Sessions", "Complete %d ergometer training sessions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Rowing Camps", "Attend %d rowing training camps", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Head of the River", "Participate in %d Head of the River races", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Boat Upgrades", "Upgrade your rowing boat %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Rowing Associations", "Join %d rowing associations", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Coastal Rowing", "Experience %d coastal rowing adventures", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Community Rowing", "Participate in %d community rowing events", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Coxswain Leadership", "Lead as a coxswain for %d successful races", sportList[sportIndex]));
                }
                // Skiing
                sportIndex = getSportID(sportList, "Skiing");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Slopes Conquered", "Conquer %d ski slopes", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Mountain Peaks", "Reach %d mountain peaks on skis", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Ski Styles", "Master %d different skiing styles", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Ski Races Won", "Win %d ski races", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Freestyle Tricks", "Perform %d freestyle skiing tricks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Backcountry Adventures", "Embark on %d backcountry skiing adventures", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Terrain Park Skills", "Showcase %d skills in terrain parks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Snowboarding Crossovers", "Cross over to snowboarding %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Skiing Endurance", "Accumulate %d hours of skiing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Avalanche Safety", "Complete %d avalanche safety courses", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Ski Gear Upgrades", "Upgrade your skiing gear %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Skiing Competitions", "Participate in %d skiing competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Heli-Skiing Adventures", "Experience %d heli-skiing adventures", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Ski Instruction", "Become a certified ski instructor %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Ski Resorts Visited", "Visit %d different ski resorts", sportList[sportIndex]));
                }
                // Snowboarding
                sportIndex = getSportID(sportList, "Snowboarding");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Mountain Slopes Conquered", "Conquer %d mountain slopes on a snowboard", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Halfpipe Tricks", "Perform %d impressive tricks in a halfpipe", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Terrain Park Mastery", "Master %d terrain park features on a snowboard", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Snowboard Races Won", "Win %d snowboarding races", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Freeride Adventures", "Embark on %d freeride snowboarding adventures", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Backcountry Snowboarding", "Experience %d backcountry snowboarding trips", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Snowboard Styles", "Master %d different snowboarding styles", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Big Air Jumps", "Execute %d big air jumps on a snowboard", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Snowboarding Endurance", "Accumulate %d hours of snowboarding", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Avalanche Safety", "Complete %d avalanche safety courses for snowboarding", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Snowboard Gear Upgrades", "Upgrade your snowboarding gear %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Snowboarding Competitions", "Participate in %d snowboarding competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Heli-Snowboarding Adventures", "Experience %d heli-snowboarding adventures", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Snowboarding Instruction", "Become a certified snowboarding instructor %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Snowboarding Resorts Visited", "Visit %d different snowboarding resorts", sportList[sportIndex]));
                }
                // Surfing
                sportIndex = getSportID(sportList, "Surfing");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Waves Ridden", "Ride %d waves while surfing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Barrel Riding", "Successfully ride %d barrels", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Surfboard Collection", "Collect %d different surfboards", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Surfing Competitions Won", "Win %d surfing competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Tricks and Maneuvers", "Master %d surfing tricks and maneuvers", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Big Wave Riding", "Conquer %d big waves while surfing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Surfing Styles", "Excel in %d different surfing styles", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Surfing Spots Explored", "Explore %d different surfing spots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Surfing Endurance", "Accumulate %d hours of surfing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Environmental Surfing Initiatives", "Participate in %d environmental surfing initiatives", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Surfboard Customization", "Customize your surfboard %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Surfing Associations", "Join %d surfing associations", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Surfing Adventures", "Embark on %d epic surfing adventures", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Surfing Instruction", "Become a certified surfing instructor %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Surfing Competitions Judged", "Judge %d surfing competitions", sportList[sportIndex]));
                }
                // Skateboarding
                sportIndex = getSportID(sportList, "Skateboarding");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Tricks Mastered", "Master %d skateboarding tricks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Skate Park Dominance", "Dominate %d skate park competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Skateboard Collection", "Collect %d different skateboards", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Street Skate Challenges", "Complete %d street skate challenges", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Ramp Skills", "Showcase %d skills on ramps and halfpipes", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Grind Mastery", "Master %d grind tricks", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Big Air Jumps", "Execute %d big air jumps on a skateboard", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Skateboarding Styles", "Excel in %d different skateboarding styles", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Skateboarding Endurance", "Accumulate %d hours of skateboarding", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Skate Park Construction", "Participate in %d skate park construction projects", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Skateboard Customization", "Customize your skateboard %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Skateboarding Associations", "Join %d skateboarding associations", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Skateboarding Competitions Organized", "Organize %d skateboarding competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Skateboarding Filmography", "Produce %d skateboarding films", sportList[sportIndex]));
                }
                // Handball
                sportIndex = getSportID(sportList, "Handball");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Goals Scored", "Score %d goals in handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Assists", "Record %d assists in handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Shots on Target", "Take %d shots on target in handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Steals", "Achieve %d steals in handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Penalties Scored", "Score %d penalties in handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Blocks", "Make %d blocks in handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Interceptions", "Achieve %d interceptions in handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Saves (Goalkeepers)", "Make %d saves as a goalkeeper in handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Playing Time", "Play %d minutes in handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Hat Tricks", "Score %d hat tricks in handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Assist Streak", "Achieve a streak of %d consecutive assists in handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Winning Streak", "Achieve a streak of %d consecutive wins in handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Fair Play", "Receive no penalties in %d handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Captaincy", "Captain the team in %d handball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "MVP Awards", "Receive %d MVP awards in handball matches", sportList[sportIndex]));
                }
                // Netball
                sportIndex = getSportID(sportList, "Netball");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Goals Scored", "Score %d goals in netball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Assists", "Record %d assists in netball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Shots on Target", "Take %d shots on target in netball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Interceptions", "Achieve %d interceptions in netball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Penalties Scored", "Score %d penalties in netball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Blocks", "Make %d blocks in netball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Steals", "Achieve %d steals in netball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Saves (Goalkeepers)", "Make %d saves as a goalkeeper in netball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Playing Time", "Play %d minutes in netball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Hat Tricks", "Score %d hat tricks in netball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Assist Streak", "Achieve a streak of %d consecutive assists in netball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Winning Streak", "Achieve a streak of %d consecutive wins in netball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Fair Play", "Receive no penalties in %d netball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "MVP Awards", "Receive %d MVP awards in netball matches", sportList[sportIndex]));
                }
                // Water Polo
                sportIndex = getSportID(sportList, "Water Polo");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Goals Scored", "Score %d goals in water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Assists", "Record %d assists in water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Shots on Goal", "Take %d shots on goal in water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Steals", "Achieve %d steals in water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Penalties Scored", "Score %d penalty goals in water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Blocks", "Make %d blocks as a goalkeeper in water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Interceptions", "Achieve %d interceptions in water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Saves (Goalkeepers)", "Make %d saves as a goalkeeper in water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Playing Time", "Play %d minutes in water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Hat Tricks", "Score %d hat tricks in water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Assist Streak", "Achieve a streak of %d consecutive assists in water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Winning Streak", "Achieve a streak of %d consecutive wins in water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Fair Play", "Receive no penalties in %d water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Captaincy", "Captain the team in %d water polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "MVP Awards", "Receive %d MVP awards in water polo matches", sportList[sportIndex]));
                }
                // Archery
                sportIndex = getSportID(sportList, "Archery");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Bullseyes", "Hit %d bullseyes in archery competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Gold Medals Earned", "Earn %d gold medals in archery tournaments", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Distance Shots", "Successfully hit targets at %d meters in archery", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Consistent Accuracy", "Achieve %d consecutive accurate shots in archery", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Head-to-Head Wins", "Win %d head-to-head archery duels", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Long Shots", "Hit %d targets from long distances in archery", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Precision Shooting", "Achieve %d precise shots in archery competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Unique Targets", "Successfully hit %d unique targets in archery", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Equipment Upgrades", "Upgrade your archery equipment %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Champion Titles", "Win %d champion titles in archery", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.6, "Archery Mastery", "Become a master archer %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Archery Coaching", "Coach other archers for %d hours", sportList[sportIndex]));
                }
                // Bowling
                sportIndex = getSportID(sportList, "Bowling");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Strikes", "Score %d strikes in bowling games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Turkeys", "Achieve %d turkeys in consecutive frames", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Spares", "Convert %d spares in bowling games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "High Scores", "Achieve %d high scores in bowling games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Split Conversions", "Successfully convert %d split pin setups", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Perfect Game", "Bowl a perfect game (300) %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Bowling Competitions", "Participate in %d bowling competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Tournaments Won", "Win %d bowling tournaments", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "League Play", "Play %d seasons in a bowling league", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Pro Bowler", "Achieve a pro bowler status %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Perfect Strikes", "Score %d perfect strikes in a row", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Bowling Average", "Maintain an average score of %d or higher in bowling", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Bowl with Style", "Achieve %d stylish bowling moves", sportList[sportIndex]));
                }
                // Fencing
                sportIndex = getSportID(sportList, "Fencing");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Victorious Duels", "Win %d fencing duels", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Gold Medals Earned", "Earn %d gold medals in fencing tournaments", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Parries and Ripostes", "Successfully execute %d parries and ripostes", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Perfect Strikes", "Score %d perfect strikes in fencing matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Distance Control", "Maintain %d precise distance control in fencing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Mastering Weapon Types", "Excel in %d different weapon types in fencing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Counterattacks", "Execute %d successful counterattacks in fencing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Epee Accuracy", "Achieve %d accurate epee strikes in fencing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "No Touches Received", "Avoid receiving touches for %d consecutive matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Fencing Records", "Set %d fencing records in competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Equipment Mastery", "Upgrade your fencing equipment %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Fencing Championships", "Win %d fencing championships", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Fencing Styles", "Master %d fencing styles", sportList[sportIndex]));
                }
                // Sailing
                sportIndex = getSportID(sportList, "Sailing");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Regatta Wins", "Win %d regattas in sailing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Navigation Skills", "Demonstrate %d excellent navigation skills", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Knot Tying", "Tie %d different types of sailing knots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Smooth Tacking", "Execute %d smooth tacking maneuvers", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Spinnaker Handling", "Master %d spinnaker handling techniques", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Race Courses Completed", "Complete %d different race courses", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Anchoring Skills", "Perfect %d anchoring and mooring skills", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Offshore Voyages", "Embark on %d offshore voyages", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Safety at Sea", "Ensure safety at sea for %d sailing trips", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Transoceanic Crossing", "Complete %d transoceanic crossings", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Sailing Styles", "Master %d sailing styles", sportList[sportIndex]));
                }
                // Shooting
                sportIndex = getSportID(sportList, "Shooting");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Bullseye Hits", "Score %d bullseye hits in shooting competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Target Accuracy", "Achieve %d accurate shots on target", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Quick Draws", "Perform %d quick draws and shots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Clay Pigeon Shooting", "Hit %d clay pigeons in skeet shooting", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Long-Range Shots", "Make %d successful long-range shots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Precision Shooting", "Demonstrate %d precise shooting skills", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Shooting Drills", "Complete %d shooting drills with accuracy", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Safety Protocols", "Follow safety protocols for %d shooting sessions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Shooting Records", "Set %d shooting records in competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Firearm Maintenance", "Maintain your firearm %d times", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Shooting Championships", "Win %d shooting championships", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "National Representation", "Represent your country in %d international shooting events", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Shooting Styles", "Master %d shooting styles", sportList[sportIndex]));
                }
                // Snooker
                sportIndex = getSportID(sportList, "Snooker");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Century Breaks", "Score %d century breaks in snooker matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Maximum Breaks", "Achieve %d maximum (147) breaks in snooker", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Frame Wins", "Win %d frames in snooker games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Potting Skills", "Demonstrate %d accurate potting skills", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Safety Play", "Excel in %d safety shots and snookers", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Snooker Championships", "Win %d snooker championships", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Break Building", "Build %d high-scoring breaks in snooker", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Cue Control", "Demonstrate %d precise cue control", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Safety Battles", "Win %d safety battles in snooker", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Snooker Records", "Set %d snooker records in competitions", sportList[sportIndex]));
                }
                // Squash
                sportIndex = getSportID(sportList, "Squash");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Match Wins", "Win %d squash matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Games Won", "Win %d games in squash", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Rallies Won", "Win %d squash rallies", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Drop Shots", "Execute %d successful drop shots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Lob Shots", "Perfect %d lob shots in squash", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Front Court Dominance", "Dominate %d rallies in the front court", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Backhand Mastery", "Achieve %d precise backhand shots", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Fitness and Speed", "Improve your fitness and speed for %d squash matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Squash Drills", "Complete %d squash drills with accuracy", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Squash Records", "Set %d squash records in competitions", sportList[sportIndex]));
                }
                // Weightlifting
                sportIndex = getSportID(sportList, "Weightlifting");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Maximum Bench Press", "Achieve a %d kg maximum bench press", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Clean and Jerk Record", "Set a %d kg clean and jerk record", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Snatch Record", "Set a %d kg snatch record", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Squat Strength", "Squat %d kg with proper form", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Deadlift Record", "Set a %d kg deadlift record", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Weightlifting Competitions", "Participate in %d weightlifting competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Weightlifting Technique", "Improve your weightlifting technique over %d training sessions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Strength and Conditioning", "Complete %d strength and conditioning workouts", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Weightlifting Records", "Set %d weightlifting records in competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Weightlifting Gear", "Upgrade your weightlifting gear %d times", sportList[sportIndex]));
                }
                // Polo
                sportIndex = getSportID(sportList, "Polo");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Polo Matches Won", "Win %d polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Goals Scored", "Score %d goals in polo", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Chukkers Played", "Participate in %d chukkers in polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Mallet Swing Accuracy", "Demonstrate %d accurate mallet swings", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Horse Riding Skills", "Improve your horse riding skills over %d polo games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Team Coordination", "Demonstrate effective team coordination in %d polo matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Polo Strategy", "Develop and implement winning polo strategies in %d matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Polo Practices", "Attend %d polo practice sessions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Polo Records", "Set %d polo records in competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Horse Care", "Take care of your polo horses %d times", sportList[sportIndex]));
                }
                // Lacrosse
                sportIndex = getSportID(sportList, "Lacrosse");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Lacrosse Matches Won", "Win %d lacrosse matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Goals Scored", "Score %d goals in lacrosse", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Assists", "Record %d assists in lacrosse", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Shots on Goal", "Take %d shots on goal in lacrosse", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Ground Balls Recovered", "Recover %d ground balls in lacrosse", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Checks Landed", "Land %d successful checks in lacrosse", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Faceoffs Won", "Win %d faceoffs in lacrosse", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Minutes Played", "Play %d minutes in lacrosse matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Penalties Drawn", "Draw %d penalties in lacrosse", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Hat Tricks", "Score %d hat tricks in lacrosse", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Goalie Saves", "Make %d saves as a lacrosse goalie", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Cleared Passes", "Successfully clear %d passes in lacrosse", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Ground Ball Pickups", "Pick up %d ground balls in lacrosse matches", sportList[sportIndex]));
                }
                // Darts
                sportIndex = getSportID(sportList, "Darts");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Darts Matches Won", "Win %d darts matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Bullseyes Hit", "Hit %d bullseyes in darts", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "180s Scored", "Score %d 180s (maximum score) in darts", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Checkout Percentage", "Achieve a %d% checkout percentage in darts", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Double Trouble", "Score %d points with double hits in darts", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "High Checkouts", "Achieve %d high checkouts in darts", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Darts Accuracy", "Improve your darts accuracy over %d games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Leg Wins", "Win %d legs (games) in darts matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Darts Tournaments", "Participate in %d darts tournaments", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Darts Records", "Set %d darts records in competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Bullseye Mastery", "Achieve %d consecutive bullseyes in darts", sportList[sportIndex]));
                }
                // Kabaddi
                sportIndex = getSportID(sportList, "Kabaddi");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Kabaddi Matches Won", "Win %d Kabaddi matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Raids Successful", "Complete %d successful raids in Kabaddi", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Touches Scored", "Score %d touches in Kabaddi", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Super Raids", "Achieve %d super raids in Kabaddi", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Super Tackles", "Perform %d super tackles in Kabaddi", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Defensive Holds", "Hold %d opponents defensively in Kabaddi", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Raid Points", "Score %d raid points in Kabaddi matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Tackle Points", "Score %d tackle points in Kabaddi matches", sportList[sportIndex]));quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Kabaddi Records", "Set %d Kabaddi records in competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Raid Timings", "Achieve %d successful raids with precise timings", sportList[sportIndex]));
                }
                // Sepak Takraw
                sportIndex = getSportID(sportList, "Sepak Takraw");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Sepak Takraw Matches Won", "Win %d Sepak Takraw matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Spike Points", "Score %d spike points in Sepak Takraw", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Service Aces", "Achieve %d service aces in Sepak Takraw", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Net Saves", "Make %d net saves in Sepak Takraw", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Block Points", "Score %d block points in Sepak Takraw", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Diving Saves", "Perform %d diving saves in Sepak Takraw", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Rallies Won", "Win %d rallies in Sepak Takraw matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Takraw Skills", "Master %d Sepak Takraw skills", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Sepak Takraw Tournaments", "Participate in %d Sepak Takraw tournaments", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Sepak Takraw Records", "Set %d Sepak Takraw records in competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Sepak Takraw Techniques", "Execute %d advanced Sepak Takraw techniques", sportList[sportIndex]));
                }
                // Hurling
                sportIndex = getSportID(sportList, "Hurling");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Hurling Matches Won", "Win %d Hurling matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Goals Scored", "Score %d goals in Hurling", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Points Scored", "Score %d points in Hurling", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Saves Made", "Make %d saves in Hurling", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Hook Blocks", "Perform %d hook blocks in Hurling", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Puck Outs", "Successfully complete %d puck outs in Hurling", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Sideline Cuts", "Score %d points from sideline cuts in Hurling", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Hurling Skills", "Master %d Hurling skills", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Hurling Tournaments", "Participate in %d Hurling tournaments", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Hurling Records", "Set %d Hurling records in competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Hurling Techniques", "Execute %d advanced Hurling techniques", sportList[sportIndex]));
                }
                // Gaelic Football
                sportIndex = getSportID(sportList, "Gaelic Football");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Gaelic Football Matches Won", "Win %d Gaelic Football matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Goals Scored", "Score %d goals in Gaelic Football", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Points Scored", "Score %d points in Gaelic Football", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Saves Made", "Make %d saves in Gaelic Football", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Kick Outs", "Successfully complete %d kick outs in Gaelic Football", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Tackles Completed", "Complete %d successful tackles in Gaelic Football", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Solo Runs", "Perform %d solo runs in Gaelic Football", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Gaelic Football Skills", "Master %d Gaelic Football skills", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Gaelic Football Records", "Set %d Gaelic Football records in competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Gaelic Football Techniques", "Execute %d advanced Gaelic Football techniques", sportList[sportIndex]));
                }
                // Pickleball
                sportIndex = getSportID(sportList, "Pickleball");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Pickleball Matches Won", "Win %d Pickleball matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Points Scored", "Score %d points in Pickleball", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Aces Served", "Serve %d aces in Pickleball", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Volley Shots", "Hit %d successful volley shots in Pickleball", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Dink Shots", "Execute %d precise dink shots in Pickleball", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Smash Winners", "Win points with %d smash winners in Pickleball", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Rally Length", "Participate in %d long rallies in Pickleball", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Pickleball Skills", "Master %d Pickleball skills", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Pickleball Records", "Set %d Pickleball records in competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Pickleball Techniques", "Execute %d advanced Pickleball techniques", sportList[sportIndex]));
                }
                // Bocce
                sportIndex = getSportID(sportList, "Bocce");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Bocce Matches Won", "Win %d Bocce matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Points Scored", "Score %d points in Bocce", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Perfect Throws", "Make %d perfect throws in Bocce", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Bocce Precision", "Demonstrate %d precise throws in Bocce", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Strategy Moves", "Execute %d strategic moves in Bocce", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Bocce Showdowns", "Participate in %d intense Bocce showdowns", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Bocce Skills", "Master %d Bocce skills", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Bocce Championships", "Win %d Bocce championships", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Bocce Records", "Set %d Bocce records in competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Bocce Techniques", "Execute %d advanced Bocce techniques", sportList[sportIndex]));
                }
                // Inline Skating
                sportIndex = getSportID(sportList, "Inline Skating");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Distance Skated", "Skate %d kilometers in Inline Skating", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Speed Achievements", "Achieve %d speed milestones in Inline Skating", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Tricks and Stunts", "Perform %d tricks and stunts in Inline Skating", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Marathon Skates", "Complete %d marathon-distance skates in Inline Skating", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Slalom Skills", "Master %d slalom skating techniques in Inline Skating", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Freestyle Competitions", "Compete in %d freestyle Inline Skating competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Aggressive Skating", "Excel in %d aggressive skating events in Inline Skating", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Racing Victories", "Win %d racing events in Inline Skating", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Inline Skating Endurance", "Build %d hours of endurance in Inline Skating", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Inline Skating Records", "Set %d Inline Skating records in competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Inline Skating Techniques", "Master %d advanced Inline Skating techniques", sportList[sportIndex]));
                }
                // Ultimate Frisbee
                sportIndex = getSportID(sportList, "Ultimate Frisbee");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Games Won", "Win %d Ultimate Frisbee games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Points Scored", "Score %d points in Ultimate Frisbee", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Assists", "Record %d assists in Ultimate Frisbee", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Diving Catches", "Make %d spectacular diving catches in Ultimate Frisbee", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Frisbee Throws", "Complete %d precise Frisbee throws", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Layout Blocks", "Execute %d layout blocks in Ultimate Frisbee", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "End Zone Dominance", "Score %d points from the end zone in Ultimate Frisbee", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Spirit of the Game", "Exhibit excellent sportsmanship in %d Ultimate Frisbee matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Advanced Techniques", "Master %d advanced techniques in Ultimate Frisbee", sportList[sportIndex]));
                }
                // Softball
                sportIndex = getSportID(sportList, "Softball");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Games Won", "Win %d Softball games", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Runs Scored", "Score %d runs in Softball", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Home Runs", "Hit %d home runs in Softball", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Pitching Strikes", "Throw %d accurate strikes in Softball", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Catcher Skills", "Catch %d runners stealing bases in Softball", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Double Plays", "Execute %d double plays in Softball", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Fielding Excellence", "Make %d outstanding fielding plays in Softball", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Sliding Techniques", "Master %d sliding techniques in Softball", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Softball Endurance", "Build %d hours of endurance in Softball", sportList[sportIndex]));
                }
                // Aussie Rules (Australian Rules Football)
                sportIndex = getSportID(sportList, "Aussie Rules");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Matches Won", "Win %d Aussie Rules matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Goals Scored", "Kick %d goals in Aussie Rules", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Marks Taken", "Take %d marks in Aussie Rules", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Tackles Made", "Make %d tackles in Aussie Rules", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Handballs Completed", "Complete %d handballs in Aussie Rules", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Running Bounces", "Execute %d running bounces in Aussie Rules", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Clearances", "Achieve %d clearances in Aussie Rules", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Goal Assists", "Provide %d goal assists in Aussie Rules", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Kicks Accuracy", "Maintain %d% kicking accuracy in Aussie Rules", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Advanced Techniques", "Master %d advanced techniques in Aussie Rules", sportList[sportIndex]));
                }
                // Chess Boxing
                sportIndex = getSportID(sportList, "Chess Boxing");
                if (sportIndex > -1) {
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.2, "Chess Matches Won", "Win %d chess matches in Chess Boxing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.0, "Boxing Matches Won", "Win %d boxing matches in Chess Boxing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Checkmates Achieved", "Achieve %d checkmates in chess matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.3, "Knockouts Achieved", "Achieve %d knockouts in boxing matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.8, "Chess Rating Improvement", "Improve your chess rating by %d points", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.4, "Boxing Knockdowns", "Score %d knockdowns in boxing matches", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.1, "Simultaneous Wins", "Win both chess and boxing matches in %d Chess Boxing events", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.7, "Endurance Training", "Complete %d hours of endurance training for Chess Boxing", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(1.5, "Chess Boxing Records", "Set %d Chess Boxing records in competitions", sportList[sportIndex]));
                    quantifiableChallengeFactoryRepository.save(new QuantifiableChallengeFactory(0.9, "Advanced Chess Openings", "Master %d advanced chess openings in Chess Boxing", sportList[sportIndex]));
                }










            }

        }
    }
