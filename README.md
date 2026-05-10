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
  ###Requirement c: Admin functionalities
  The admin can perform CRUD operations for trains, stations and bookings. They can also notiy customers about delays. 
  #### View Bookings for a speciic train
  When an admin enters the train ID into the text box, they will see the bookings for a train as a table when they press the "Refresh Bookings" button. 
  <img width="976" height="201" alt="image" src="https://github.com/user-attachments/assets/510f5f65-6bb9-41c3-a9e7-b0576342920b" />
  ####Signaling a delay
  When the admin wants to signal a delay, the delay will be signaled for the train currently watched in the bookings window. A dialog box pops up for the admin to enter a delay time in minutes
  <img width="284" height="121" alt="image" src="https://github.com/user-attachments/assets/4538622a-2632-41fe-9691-39dadb11603e" />
  Customers get notified about the delay once it has been signaled
  <img width="284" height="121" alt="image" src="https://github.com/user-attachments/assets/65ce7a61-2446-426f-ba80-8ef2a6a248af" />
  <img width="836" height="123" alt="image" src="https://github.com/user-attachments/assets/d448c71d-3ade-4104-a3eb-a4d45022afa5" />
  #### Adding a train
  When the admin adds a train, 2 dialog boxes prompting for the train ID and capacity pop up. 
  <img width="287" height="124" alt="image" src="https://github.com/user-attachments/assets/88e16f74-cd9d-4484-8d71-65dff7e167df" />
  <img width="292" height="123" alt="image" src="https://github.com/user-attachments/assets/e858c584-540c-4cf7-b469-c9d736429c59" />
  The new entry now appears in the dropdown and customers are notified
  <img width="470" height="134" alt="image" src="https://github.com/user-attachments/assets/cf38a245-6185-4421-abac-6e1262f82685" />
  <img width="350" height="31" alt="image" src="https://github.com/user-attachments/assets/7f8c3a9b-384d-4a65-b71b-74a21da3e7e0" />
  #### Adding a new station
  Same idea, but the pop-up prompts for the station name.
  <img width="286" height="122" alt="image" src="https://github.com/user-attachments/assets/4c719d75-01db-43c8-9285-fd5e78129a2f" />
  <img width="315" height="25" alt="image" src="https://github.com/user-attachments/assets/a8fb512d-6b7d-4c74-97f0-97c7abdc0cc4" />
  <img width="463" height="150" alt="image" src="https://github.com/user-attachments/assets/bc98c28e-52ab-4f10-bc89-7aa0a0815234" />
  #### Deleting a train
  An admin can only delete a specific train if it has no bookings left. When we try to delete a booked train, we get an error pop-up
  <img width="540" height="25" alt="image" src="https://github.com/user-attachments/assets/48cb7959-d734-4c97-a394-53fa9a699f37" />
  Here's a successful train deletion: 
  <img width="980" height="391" alt="image" src="https://github.com/user-attachments/assets/ba091a72-f802-4c7f-a5e8-aca6ffb89508" />
  <img width="255" height="116" alt="image" src="https://github.com/user-attachments/assets/b76859b7-5f4e-4fee-b5d1-c2f226d0d022" />
  <img width="492" height="20" alt="image" src="https://github.com/user-attachments/assets/d1332fcf-d982-45fc-bbb2-7fae5f3e084f" />  
  #### Updating Train Capacity
  The train capacity can be updated for the currently viewing train. The admin will be prompted to enter a new capacity. 
  <img width="975" height="377" alt="image" src="https://github.com/user-attachments/assets/ca4c5121-c9ae-4055-b96f-6eb4c93c6ced" />
  <img width="461" height="33" alt="image" src="https://github.com/user-attachments/assets/a094fd23-1b7f-4d98-ac14-2566cead7ea5" />
  #### Updating Schedule Time
  The admin can also update the departure time for a train. They will be prompted to enter the route ID and new departure time. Arrival time is calculated based on number of stations and is adjusted accordingly. 
  <img width="290" height="124" alt="image" src="https://github.com/user-attachments/assets/eacc5134-cc97-4a86-9cb6-1f1ca8f6c54d" />
  <img width="291" height="126" alt="image" src="https://github.com/user-attachments/assets/4c02665e-4401-4ac7-ac92-7761bba266e6" />
  <img width="256" height="114" alt="image" src="https://github.com/user-attachments/assets/c0ac1911-7997-4bac-a67b-e3e4909eefa3" />
  <img width="513" height="30" alt="image" src="https://github.com/user-attachments/assets/4d94a024-aca1-4e9e-a834-c468ee96a842" />
  #### Updating and deleting bookings
  An Update and Delete Button is present for every booking entry. The Update button will update the number of seats for a specific booking while the Delete button will delete a speciic booking.
  <img width="971" height="295" alt="image" src="https://github.com/user-attachments/assets/3994bb9f-5312-4da6-91b9-2fbba57f56f3" />
  <img width="973" height="39" alt="image" src="https://github.com/user-attachments/assets/7ccc8255-f524-48f4-9dc6-c968a355ebad" />
  <img width="543" height="24" alt="image" src="https://github.com/user-attachments/assets/eca1fb13-d67f-4a92-b7ac-3af495b85c37" />
  <img width="994" height="367" alt="image" src="https://github.com/user-attachments/assets/ead66d70-f080-4d40-b787-36cdc7073a73" />
  <img width="985" height="212" alt="image" src="https://github.com/user-attachments/assets/d4fc6a18-1754-4c1e-9189-ea1f6596d370" />
  <img width="574" height="25" alt="image" src="https://github.com/user-attachments/assets/c5f00df0-a86d-427b-b96b-32c58e557f43" />








  














