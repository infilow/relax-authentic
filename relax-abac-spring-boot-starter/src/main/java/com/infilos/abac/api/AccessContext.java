package com.infilos.abac.api;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class AccessContext {
    private Object profile;
    private Object resource;
    private Object action;
    private Object environ;
}
