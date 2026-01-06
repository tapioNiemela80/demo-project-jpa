package tn.demo.jpa.team.view;

import java.util.UUID;

record TeamsViewRow(
        UUID teamId,
        String teamName
) {}