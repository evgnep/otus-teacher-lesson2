# Демо к уроку по JWT

Запуск в IDEA - выбираете в списке конфигураций 
ClientApplication и запускаете, далее (не останавливая ClientApplication) повторяете
с AuthApplication и BusinessApplication.

Запуск из командной строки - запускаете три консоли
* в первой - gradlew auth:bootRun
* во второй - gradlew client:bootRun
* в третьей gradlew business:bootRun

После запуска 
* ClientApplication доступна на localhost:8080
* AuthApplication - localhost:8095
* BusinessApplication - localhost:8090