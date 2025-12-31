package tn.demo.jpa.team.domain;

import jakarta.persistence.*;
import tn.demo.jpa.common.domain.RootAware;

import java.util.Objects;
import java.util.UUID;

@Table(name = "team_members")
@Entity
class TeamMember implements RootAware<Team> {
    @Id
    private UUID id;

    private String name;

    private String profession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    protected TeamMember() {
        //for jpa
    }

    private TeamMember(UUID id, String name, String profession, Team team) {
        this.id = id;
        this.name = name;
        this.profession = profession;
        this.team = team;
    }

    static TeamMember createNew(TeamMemberId memberId, String name, String profession, Team team) {
        return new TeamMember(memberId.value(), name, profession, team);
    }

    void markTeamRemoved(){
        this.team = null;
    }

    boolean hasId(TeamMemberId expected) {
        return id.equals(expected.value());
    }

    boolean hasDetails(TeamMemberId memberId, String name, String profession) {
        return hasId(memberId) && Objects.equals(name, this.name) && Objects.equals(profession, this.profession);
    }

    @Override
    public Team root() {
        return team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMember other = (TeamMember) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
