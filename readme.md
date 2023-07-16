## Notification Book (in progress)
The user creates an account with email confirmation. It is possible to create notes (title + text) and add a time to 
send a reminder to Telegram, if the user has a Telegram ID. Notes can be edited and deleted, and there is a search 
for notes: priority is the title and then the content of the note.

There are two user roles: admin and user. Admin sees all notes and can add and delete users. User can only see their 
own notes.

### ToDo:
1. Add Exceptions
2. Add validation
3. Separate user self-registration and user creation for admin

### Technologies used:
- Java 17
- PostrgeSQL
- Spring Boot
- Spring Data JPA and Hibernate
- Spring Security
- Telegram API
- Thymeleaf templates
- Lombok
- Maven

