package com.yourname.zerotrust.policy;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.yourname.zerotrust.dto.AccessDecisionResponse;
import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.entity.User;

@Component
public class TraditionalAccessEvaluator {

    public AccessDecisionResponse evaluate(User user, String resource, String action) {
        AccessDecisionResponse response = new AccessDecisionResponse();
        response.setModel("TRADITIONAL");

        if (user == null) {
            response.setAllowed(false);
            response.setDecision("DENY");
            response.setReason("User not found");
            return response;
        }

        Set<String> roles = user.getRoles() == null ? Set.of() :
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        if ("admin".equalsIgnoreCase(resource)) {
            if (roles.contains("ADMIN")) {
                response.setAllowed(true);
                response.setDecision("ALLOW");
                response.setReason("Static RBAC: user has ADMIN role");
            } else {
                response.setAllowed(false);
                response.setDecision("DENY");
                response.setReason("Static RBAC: admin resource requires ADMIN role");
            }
            return response;
        }

        if (roles.contains("USER") || roles.contains("ADMIN")) {
            response.setAllowed(true);
            response.setDecision("ALLOW");
            response.setReason("Static RBAC: user has " + String.join("/", roles) + " role — no risk check");
        } else {
            response.setAllowed(false);
            response.setDecision("DENY");
            response.setReason("Static RBAC: no valid role assigned");
        }
        return response;
    }
}
