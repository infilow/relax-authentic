# relax-auth-token

Quickly implement JWT with spring-boot & pac4j.

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
mvn versions:set -DnewVersion=0.1.0 -DprocessAllModules -DgenerateBackupPoms=false versions:commit
```

2. publish artifact

## RBAC

1. 从 Spring 上下文获得所有接口定义，method + path + params，得到 Resource 列表
2. 
