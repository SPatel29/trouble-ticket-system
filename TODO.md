# To Do List

This md file contains all task lists needed for completion

## Create Database Tables

### Uncompleted Table Tasks

- [X] ~~Login Table~~
  - userID (Int, Primary Key, Not Null, autoIncrement)
  - userName (varChar)
  - passWord (varChar)
  - adminStatus (Int)

- [X] ~~Open Ticket Table~~
  - ticketID (Int, Primary key, autoIncrement)
  - userID (Int, Foreign key references LoginTable(userID), Not Null)
  - Start Time (Date type)
  - Description (varChar 32)
- [X] ~~Closed Ticket Table~~
  - ticketID (Int, Foreign key references OpenTicketTable(ticketID), Not Null)
  - Start Time (DateTime )
  - Closed Time (DateTime)

### Completed Login Tasks ✓

- Login Table
- Open Ticket Table
- Closed Ticket Table

---

## Login Screen

Below are all tasks regular employee can do:

### Uncompleted Login Tasks

- [ ] Login the system given record in login table
- [ ] Display Login attempts
- [ ] Close Application after 3 unsuccesful attempts
- [ ] redirect user to associated page, given adminStatus

---

## Delete Operation

- [ ] Fetch all tickets given userID
- [ ] User Input decides which ticket to delete via ticket ID
- [ ] Popup should prompt user to confirm they want to delete it
- [ ] Remove entry from table
- [ ] Should display a toaste message confirming deletion
- [ ] if admin, can delete any ticket since they can view all tickets

---

## Update Operation

- [ ] Fetch all tickets given userID
- [ ] User input decides which ticket to update via ticket ID
- [ ] User input will change the description
- [ ] Popup should prompt user to confirm they want to update it
- [ ] Should display a toaste message confirming modification
- [ ] if admin, can update any ticket since they can view all tickets

---

## Close Tickets Operation

- [ ] Fetch all tickets given userID
- [ ] User Input decides which ticket to close via ticket ID
- [ ] Popup should prompt user to confirm they want to close it
- [ ] Ticket should then be added to close tickets table with neccessary info
- [ ] if admin, can close any ticket since they can view all tickets

---

## View tickets Operations

- [ ] View all tickets given userID
- [ ] if admin, can view all tickets
