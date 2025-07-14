# Travel Agency Database Project

This project was developed as part of the Database Lab course. The goal was to design and implement
a relational database system for a travel agency and to create a GUI-based application using Java and JDBC that interacts with the database.

## Objectives

- Gain hands-on experience with relational database design and implementation
- Create stored procedures and triggers to enforce business logic
- Populate the database with realistic data, including bulk data insertion
- Develop a desktop application with authentication and CRUD capabilities
- Demonstrate the full functionality of the system with screenshots and example use cases

### Key Features

- Extended schema with support for travel offers and IT personnel
- Massive data insertion (60,000+ reservations for offers)
- Efficient indexing for high-performance querying
- Multiple stored procedures to:
  - Insert new drivers into the least-loaded branch
  - Display trip summaries within a date range
  - Conditionally delete employees
  - Query reservations based on deposit range or surname
- Triggers for:
  - Logging INSERT/UPDATE/DELETE actions
  - Preventing changes to booked trips
  - Blocking salary reductions
- **Login screen**: Only IT Managers can access the system
- **Main dashboard** capabilities:
  - View and manage all entities (employees, trips, destinations, offers)
  - Prevent illegal operations (e.g., modifying booked trips or reducing salaries)
  - View logs of all critical actions
  - Insert and assign IT managers
  - Generate summaries of income and employee salaries per branch
- **UX Considerations**:
  - Dropdowns and menus used for safe and quick data entry
  - Validations for data consistency
- Financial dashboard displaying revenue and expenses per branch
- Extended GUI for administrative staff


## Technologies Used

- Java with Swing for GUI
- JDBC for database connectivity
