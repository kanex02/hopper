package nz.ac.canterbury.seng302.tab.controller.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;

import java.time.LocalDateTime;
import java.util.List;

public class JsonChallenge {
    @JsonProperty
    private long id;

    @JsonProperty
    private List<JsonUser> invitedUsers;

    @JsonProperty
    private String title;

    @JsonProperty
    private String goal;

    @JsonProperty
    private int hops;

    @JsonProperty
    private boolean isWaitingOnUsers;

    @JsonProperty
    private boolean isShared;

    private final JsonUser userGeneratedFor;

    @JsonIgnore
    private final LocalDateTime endDate;

    public JsonChallenge(Challenge challenge, UserEntity user) {
        this.id = challenge.getId() == null ? -1 : challenge.getId();
        this.invitedUsers = challenge.getInvitedUsers().stream().map(JsonUser::new).toList();
        this.title = challenge.getTitle();
        this.goal = challenge.getGoal();
        this.hops = challenge.getHops();
        this.userGeneratedFor = new JsonUser(challenge.getUserGeneratedFor());
        this.endDate = challenge.getEndDate();
        this.isShared = challenge.isShared();
        this.isWaitingOnUsers = challenge.isWaitingOnOtherUsers(user);
    }

    public JsonChallenge(Challenge challenge) {
        this.id = challenge.getId() == null ? -1 : challenge.getId();
        this.invitedUsers = challenge.getInvitedUsers().stream().map(JsonUser::new).toList();
        this.title = challenge.getTitle();
        this.goal = challenge.getGoal();
        this.hops = challenge.getHops();
        this.userGeneratedFor = new JsonUser(challenge.getUserGeneratedFor());
        this.endDate = challenge.getEndDate();
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGoal() {
        return goal;
    }

    public int getHops() {
        return hops;
    }

    public JsonUser getUserGeneratedFor() {
        return userGeneratedFor;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public List<JsonUser> getInvitedUsers() {
        return invitedUsers;
    }

    public boolean isWaitingOnUsers() {
        return isWaitingOnUsers;
    }

    public boolean isShared() {
        return isShared;
    }
}
