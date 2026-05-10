# Train Ticketing System
This project is an implementation for the proposed challenge by Siemens. The task is to implement a java application for a train ticketing system. The task is described as below:
<img width="940" height="738" alt="image" src="https://github.com/user-attachments/assets/2229f5e9-0570-4aed-ad32-d31f79814208" />
## Implementation of the solution
To implement this task, a database was created in MySQL to withhold all the information about trains, bookings, routes and schedules. Below is the ERD for the database: 
<img width="798" height="731" alt="image" src="https://github.com/user-attachments/assets/a87c6a80-fae2-4fb4-9cf2-763b999e6ba3" />
The whole application was done in Java Spring Boot using Gradle and Hibernate technologies to communicate with the database. The application uses 3-layer architecture where the entities make up the data layer, the repositories and service make up the business logic layer and the Swing GUI makes the client layer. While I could've implemented a full front-end in HTML/CSS/JS and used web routings, due to the lack of time I had, I simply resolved to a Swing UI. The mailing part was not fully implemented but rather simulated, since that would require me to implement a full SMTP server, which I did not have the time to do and also because I am not using real data, but rather dummy data I generated. 
## How to integrate the DB and run the project
To ensure the project runs properly on your personal device, open your local connection on MySQL workbench and run the script found in /src/resources to have the database set up. Then in the same folder, modify the application.properties file according to the credentials for your local MySQL connection. To run the project, run TrainApp.java found in the /src/main/java folder as a Java Application.
## Customer and Admin User interaces
Below is the UI for both the customer and admin panel: 
<img width="992" height="709" alt="image" src="https://github.com/user-attachments/assets/90cf5235-d0ea-4961-a846-cf613fc43868" />
<img width="985" height="712" alt="image" src="https://github.com/user-attachments/assets/b5c995b3-933f-412e-a0a9-9fe342051136" />
I did not style them to look nice since I mainly focused on the application's functionalities rather than the aestethical aspect. 
## Main Functionalities
  ### Requirements a and b: Booking a train and check for routes/schedules
  The customer is able to check routes and trains based on their departure and arrival point. They can choose their departure and arrival stations based on a dropdown which lists all the stations ffrom the database. 
  <img width="489" height="175" alt="image" src="https://github.com/user-attachments/assets/874bc149-2cb2-4097-af2c-39708584f4a9" />
  Once the customer has selected their departure and arrival stations, they can click the Find Route Button which will tell them their train and route to take. Here are some example outputs for all different cases: 
  #### Changeover
  <img width="983" height="62" alt="image" src="https://github.com/user-attachments/assets/f6263b6c-5a89-494c-8c64-2a34f4acce32" />
  <img width="967" height="405" alt="image" src="https://github.com/user-attachments/assets/3c719902-58cc-4f68-a86c-c92a8b6d18d3" />
  #### Direct route
  <img width="616" height="66" alt="image" src="https://github.com/user-attachments/assets/bc337f41-5f8e-40d4-afc9-a91ae5dcda7c" />
  #### No existent route
  <img width="976" height="68" alt="image" src="https://github.com/user-attachments/assets/9042acaa-0d3c-45ad-9992-56ec6afb396c" />
  <img width="382" height="57" alt="image" src="https://github.com/user-attachments/assets/720aca3a-b4da-49cc-9c84-5997a7cc5724" />
  Once the train(s) and routes have been found, the customer can choose their train from a dropdown and the number of seats to book. To book the train, the Confirm Booking button must be pressed. An error message will be displayed should the customer overbook. Here are the inputs and outputs for the booking
  <img width="976" height="276" alt="image" src="https://github.com/user-attachments/assets/353ca497-c580-4ae9-97ac-8c3e7be3dce3" />
  <img width="580" height="53" alt="image" src="https://github.com/user-attachments/assets/88b2a579-d1be-4e50-92e1-b8b75a8e10d1" />
  Here is the overbooking case. This would happen if the number of seats booked is greater than the available seats: 
  <img width="774" height="30" alt="image" src="https://github.com/user-attachments/assets/29568d67-96d0-4cc6-bd70-e568938bbb28" />











