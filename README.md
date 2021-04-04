# Школа 1576


### Создание миграций
```bash
./create_migration.sh migration description
```


### Доступ к maven репозиторию

1. создать персональный токен в личном кабинете Gitlab c разрешением api
1. создать файл в домашней директории `$HOME/.gradle/gradle.properties` и содержимым вида
```
gitLabPrivateToken=REPLACE_WITH_YOUR_PERSONAL_ACCESS_TOKEN
```
