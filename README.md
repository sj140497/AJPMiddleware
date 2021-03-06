# AJPMiddleware

The aim of this project was to create a thin middleware layer in Java based on the concept of actors and meta-agents, and allow for each to work concurrently. Using the meta-agents, portals were built which are able to manage agent to agent communication. Each user agent is able to be scoped locally or globally. They also allow for NodeMonitors to be attached to track the messages sent and received.

# Running the project

The main method resides in the "MiddlewareProject" java file. This method creates multiple Portals, each with multiple UserAgents attached.

# Testing the project

Each test is in the file "Testing" inside Test Packages. These tests:
- Create Portals with UserAgents attached
- Add NodeMonitors
- Pass messages from one UserAgent to another locally
- Introduce 2 Portals together
- Pass messages from one UserAgent to another globally
- Pass messages to non-existant UserAgents
- Pass messages to a UserAgent out of scope

*****

Initial Middleware Group Checklist (deadlines can be added)

## Middleware Group Checklist

*****
### James
*****  
**Task**  
Node Monitors

**Description**  
Node Monitors can be added to a portal at any time, but will be added to the portals blocking queue and will actually be added when the previous tasks are completed. Node Monitors can track the path of a message but only receive the local messages from a portal (otherwise duplication would be an issue). The Node Monitor will run on seperate threads and have it's own blocking queue, eliminating synchronisation and interleaving problems.

**TODO**  
Basic Functionality  
GUI

**Deadlines**  

Basics - 25th December
GUI    - 1st Jan

*****
**Task**  
User Agent

**Description**   
?

**TODO**  
_Constructor_  
_Send Message_ method  
_Handle Message_ method  
_Update Node_ method  

**Deadlines**  

Constructor , Send, Handle 25th December
Update node  - 1st Jan

*****
### Sean  
*****  
**Task**  
Meta Agent

**Description**  
Super class that all agents will inherit  
??

**TODO**  
Basic functionality - Constructor

**Deadlines**  

19th December

*****
### Chris  
*****  
**Task**  
Portal

**Description**  
Portals inherit from Meta Agents and hold references for agents, they allow for messages to be sent between portals locally and globally.

**TODO**  
Constructor  
_Add Node_ method  
_Remove Node_ method    
_Handle Message_ method   
_Merge_ method  

**Deadlines**  

25th December

*****  
**Task**  
Putting it all together

**Description**  
Adding all of the seperate files into 1 main project, making sure they all function correctly together

**TODO**  
Put it all together

**Deadlines**  

10th January

*****
### Matt
*****
**Task**  
Message wrapping/unpacking

**Description**  
There are 3 major types of message: System, U2U (user to user) and Error. Each of these types have sub-types, for example, different error types. When a message is sent, the message (input) will need to be adapted to the same format all the other messages will use.

**TODO**  
Basic functionality  
System message  
U2U message  
Error message  

**Deadlines**  

Basics - 25th December
System, U2U, Error - 1st January

*****



