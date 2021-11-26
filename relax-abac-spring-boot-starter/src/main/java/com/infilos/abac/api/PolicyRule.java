package com.infilos.abac.api;

import lombok.*;
import org.springframework.expression.Expression;

@Data
@Builder
@NoArgsConstructor
public class PolicyRule {
    private String name;
    private String descr;
    private Expression matcher;     // If true, this rule is applied to the request access context.
    private Expression verifier;    // If true, access allowed.
    
    public PolicyRule(Expression matcher, Expression verifier) {
        super();
        this.matcher = matcher;
        this.verifier = verifier;
    }

    public PolicyRule(String name, String descr, Expression matcher, Expression verifier) {
        super();
        this.name = name;
        this.descr = descr;
        this.matcher = matcher;
        this.verifier = verifier;
    }
}
