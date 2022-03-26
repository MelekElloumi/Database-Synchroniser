# Database Synchroniser with RabbitMQ

- This is a Database Synchroniser that can send data from derived databases to their parent database.
- Here I have implemented an exemple of a Head Office (HO) with 2 Branch Offices (BO)
- The interface was made with Java AWT and Jtables.
- Activate `RabbitMQ`, then run the server then the clients.
- In order to use RabbitMQ you need to import [these](https://drive.google.com/file/d/130ypLW4P2nQa16yB5pSjb83MNk6fFFLW/view?usp=sharing) jar to the java project. 


- New data that is added to the branch offices is labeled red.
- Branch office 1 sends it manually with the button.
- Branch office 2 sends it periodically every minute.
- Head office keep listening to RabitMQ Queue and adds the data to its database upon its reception and adds from where it recieved it.

### Exécution:

![Imgur](https://i.imgur.com/2PFujrB.png)