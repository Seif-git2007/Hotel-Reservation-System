# Hotel Reservation System

A full-featured desktop hotel management application built with Java and JavaFX. The system simulates real hotel operations from booking to checkout, supporting multiple user roles and multiple connected machines simultaneously.

---

## Features

- **Three user roles** — Guest, Receptionist, and Admin each have access to different parts of the system
- **Real-time sync** — any action (booking, cancellation, check-in, check-out) updates instantly across all connected machines
- **Live chat** — guests and receptionists can communicate in real time across different machines using Java Sockets
- **Multithreading** — database loading and heavy background tasks run on separate threads to keep the UI responsive
- **Payment handling** — supports multiple payment methods at checkout
- **Persistent storage** — all data is saved to a MySQL database and restored when the application starts
- **LAN support** — multiple machines on the same network can connect through the shared database

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| GUI | JavaFX |
| Networking | Java Sockets |
| Concurrency | Java Multithreading |
| Database | MySQL + JDBC |

---

## How to Run

### Requirements
- Java 11 or higher
- JavaFX SDK
- MySQL Server

### Steps

1. Clone the repository
   ```
   git clone https://github.com/Seif-git2007/Hotel-Reservation-System.git
   ```

2. Set up the database
    - Open MySQL and create a new database
    - Run the provided SQL file to create the tables

3. Configure the connection
    - Open `DataBaseManager.java`
    - Update these three lines with your own MySQL details:
      ```java
      private static final String URL  = "jdbc:mysql://localhost:3306/your_database_name";
      private static final String USER = "your_mysql_username";
      private static final String PASS = "your_mysql_password";
      ```

4. Run the application
    - Open the project in your IDE (IntelliJ or Eclipse)
    - Add JavaFX to your module path
    - Run `Main.java`

---

## User Roles

| Role | Access |
|---|---|
| Guest | Browse rooms, make bookings, chat with receptionist, checkout |
| Receptionist | Manage check-ins and check-outs, chat with guests, view bookings |
| Admin | Full access to all system operations and user management |

---

## Project Structure

```
HotelReservationSystem/
├── src/
│   ├── main/          # Entry point
│   ├── models/        # Data models (Room, Guest, Booking...)
│   ├── controllers/   # UI controllers
│   ├── database/      # MySQL connection and queries
│   ├── network/       # Socket server and client
│   └── views/         # JavaFX FXML files
└── db/
    └── schema.sql     # Database setup script
```

---

## Author

**Seif Ahmed Mahrous**
- GitHub: [@Seif-git2007](https://github.com/Seif-git2007)
- LinkedIn: [seif-mahrous-a1885b31b](https://linkedin.com/in/seif-mahrous-a1885b31b)