# trouble-ticket-system

This project is a trouble ticket system. This application allows users to use a Graphical User Interphase to make, submit, view, delete or modify tickets. This application also uses the MYSQL jdbc driver to interact with a database. This database stores information such as login info, and ticket information. You can view the table design in the database Design section.

The applicatoin then uses the database to help users login the application. According to the database, each user is grouped into 2 scenarios. An admin, and a regular user. Both types of users have different funtionalities. An admin will have more privillages than a regular user.

## Database Design

### Login Information

### Ticket Information

## Regular User

 Regular users can Open tickets, View tickets, or close tickets.

### Open Tickets

 Regular users can only open tickets for themselves. They cannot assign a ticket to another user. Each time a ticket is created, their userID gets associated with the ticketID in the database. This signifies that the newly created ticket belongs to that user.

 Each time a ticket is created a ticketID is generated. This ticketID is the primary key, which unqiuely identifies the ticket. The ticket also automatically gets the current timestamp and inserts it onto the ticket.

 You can check out what information the ticket has in the Ticket Information Section above.

### View Tickets

Regular users can only view their own tickets. They can view a particular ticket by ID, or they can view all of the tickets assigned to them. Ticket information is displayed in a tabular layout.

### Close Tickets

Regular users can close their own tickets. They can close a ticket by speciying a ticket ID. In the background the java code will communicate with the database and check if the user inputted ticket ID is assigned to them or not. If it assigned to them the user will have succesfully closed it, otherwise the closing will be unsucessful. If the closing of the ticket is succesful, it will capture a timestamp of when user closed ticket and it will change the status of the ticket to bein closed.

## Admin User

### Open Tickets

An admin can can open tickets. When admins open tickets, they get the option to assign the newly created ticket to another user, or they can assign it to themselves.

### View Tickets

When Admins view tickets they can view a ticket by ID or view all the tickets, regardless of if the tickets are assigned to them. Tickets are displayed in a tabular layout.

### Close tickets

When admins close tickets they can close any ticket, regardless of if the ticket is assigned to them. Current timestamp is attached to ticket as soon as ticket is closed. Ticket status also changes to being closed.

### Update Ticket

 Admins can update a ticket by giving it another ticket description. The tickets do not need to be assigned to the admin to be able to get their descriptions changed.

### Delete Ticket

Admins can also delete tickets. Admins can delete a ticket by providing the tifcket ID. Admins do not need to be assigned the ticket to delete them
