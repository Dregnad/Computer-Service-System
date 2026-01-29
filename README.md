1. Wymagania Techniczne:

JDK 23 (OpenJDK 23.0.1).

Maven (zarządzanie zależnościami).

Camunda 8 (SaaS/Self-Managed).

PostgreSQL 14+.


2. Konfiguracja Bazy Danych:

Utwórz bazę danych PostgreSQL i zaimportuj schemat oraz dane testowe z pliku: db/serwis.sql


3. Konfiguracja Camunda 8:

Zdeployuj modele procesów (.bpmn) oraz formularze (.form) znajdujące się w folderze src/main/resources/model/ na swój klaster Camunda Cloud.


4. Ustawienia Aplikacji

W pliku src/main/resources/application.properties uzupełnij:

Dane połączenia z klastrem Camunda.

Dane dostępowe do bazy PostgreSQL.

Konfigurację serwera SMTP dla powiadomień e-mail.



5. Uruchomienie:

Start Procesu

Panel zgłoszeniowy: http://localhost:8080/index.html
