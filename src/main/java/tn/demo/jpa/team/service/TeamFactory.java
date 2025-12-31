package tn.demo.jpa.team.service;

import org.springframework.stereotype.Component;
import tn.demo.jpa.team.domain.Team;
import tn.demo.jpa.team.domain.TeamId;

@Component
class TeamFactory {
    Team createNew(TeamId id, String name){
        return Team.createNew(id, name);
    }
}
