# DevOps

## Executar localmente

Na pasta `devops`, use o Maven Wrapper. O ambiente padrão utiliza um banco H2 local e não exige configuração adicional:

```powershell
cd devops
.\mvnw.cmd spring-boot:run
```

Depois, acesse `http://localhost:8080/login`.

Para executar com PostgreSQL, configure a senha e habilite o perfil correspondente:

```powershell
$env:DB_PASSWORD="sua_senha"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=postgres"
```

Não execute `DevopsApplication.java` diretamente com `javac`, pois as dependências do Spring Boot são gerenciadas pelo Maven.
