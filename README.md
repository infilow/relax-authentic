# Relax Authentic

JWT based authentication & ABAC based authorization with spring-boot & pac4j.

## Mark Controller

```java
@HasRole({"root", "admin"})
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RootOrAdmin {
}

@RestController
class BizController {
    
    @HasRole("root")
    public void api_1() {
    }

    @HasPermit("edit")
    public void api_3() {
    }

    @RootOrAdmin
    public void api_3() {
    }

    @HasAuthorize(role = @HasRole("admin"),permit = @HasPermit("edit"))
    public void api_4() {
    }
}
```

## Release 

1. update version

```bash
mvn versions:set -DnewVersion=0.6.0 -DprocessAllModules -DgenerateBackupPoms=false versions:commit
```

2. publish artifact

