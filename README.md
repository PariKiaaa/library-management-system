# Library Management System

A desktop-based **Library Management System** developed in **Java** using **Swing** for the graphical user interface.

This project was developed to practice object-oriented programming concepts, layered architecture, GUI development, and data persistence using Java.

---

## Features

### Authentication

- User registration
- Secure login
- Role-based access
- Student and Librarian accounts

### Book Management

- Add new books
- Edit book information
- Delete books
- Search books
- View all available books

### Borrowing System

- Borrow books
- Return borrowed books
- Prevent borrowing unavailable books
- Track borrowed books

### Data Persistence

- Store application data using serialized files
- Automatically load saved data on startup

### Validation & Error Handling

- Input validation
- Custom exception classes
- User-friendly error messages

---

## Technologies Used

- Java
- Java Swing
- Object-Oriented Programming (OOP)
- File Serialization
- Collections Framework

---

## Project Structure

```
src/
│
├── model/
├── repository/
├── service/
├── ui/
├── util/
├── exception/
└── Main.java
```

The project follows a layered architecture:

- **Model** → Data classes
- **Repository** → Data access and storage
- **Service** → Business logic
- **UI** → Swing graphical interface
- **Exception** → Custom exceptions
- **Util** → Helper utilities

---

## Screenshots


<img width="497" height="408" alt="image" src="https://github.com/user-attachments/assets/0688433e-5598-40b4-98ef-33ec0991131d" />
<img width="1002" height="614" alt="image" src="https://github.com/user-attachments/assets/a4b17c32-1d80-4b28-b1f4-f01fbd198d77" />
<img width="1195" height="705" alt="image" src="https://github.com/user-attachments/assets/cff0aed7-5e8d-4328-bcd9-17361f0265a0" />
<img width="620" height="828" alt="image" src="https://github.com/user-attachments/assets/f73e0ef0-db1c-4350-a44f-47c6e4f3b17f" />

<img width="1195" height="706" alt="image" src="https://github.com/user-attachments/assets/2f82ae0b-b8fe-4d1a-908e-d5f14ab9304b" />



---

## Future Improvements

- Database integration (MySQL / SQLite)
- Password hashing
- Export data to PDF or Excel
