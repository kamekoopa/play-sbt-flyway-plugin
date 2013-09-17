# Flyway Sbt Plugin for Play2 console

This is the plugin that execute the Flyway from play console using the play's configuration file.

You can specify a configuration file by system property and plugin setting.
Yhe priority order follows below.

1. `config.resource`
2. `config.file`
3. plugin setting

You can specify the location of migration script.

The default setting of each setting key is the below.

`flyway.configFile in flyway.Config := "conf/applicatione.conf"`
`flyway.scriptLocationin flyway.Config := "db/migration"`


For example of the db setting that is in application.conf is the below.

```
db.default.url="jdbc:h2:mem:play"
db.default.user=sa
db.default.password=""
db.default.initOnMigrate=true
```
