# Train Ticketing System
This project is an implementation for the proposed challenge by Siemens. The task is to implement a java application for a train ticketing system. The task is described as below:
<img width="940" height="738" alt="image" src="https://github.com/user-attachments/assets/2229f5e9-0570-4aed-ad32-d31f79814208" />
## Implementation of the solution
To implement this task, a database was created in MySQL to withhold all the information about trains, bookings, routes and schedules. Below is the ERD for the database: 
<img width="798" height="731" alt="image" src="https://github.com/user-attachments/assets/a87c6a80-fae2-4fb4-9cf2-763b999e6ba3" />
The whole application was done in Java Spring Boot using Gradle and Hibernate technologies to communicate with the database. The application uses 3-layer architecture where the entities make up the data layer, the repositories and service make up the business logic layer and the Swing GUI makes the client layer. While I could've implemented a full front-end in HTML/CSS/JS and used web routings, due to the lack of time I had, I simply resolved to a Swing UI. The mailing part was not fully implemented but rather simulated, since that would require me to implement a full SMTP server, which I did not have the time to do and also because I am not using real data, but rather dummy data I generated. 


