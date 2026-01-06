package tn.demo.jpa.team.view;

import java.util.UUID;

public record MemberView(
        UUID id,
        String name,
        String profession
) {}