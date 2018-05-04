#Ticket Service Implementation

##Overview
 This project implements a TicketService that handles holding and reserving tickets for a venue. One design issue came from dealing with giving a user the best available seats. The simplest approach would be to let the user select their own seats. Automated seat assingments can be done easily for single buyers, but groups are the problem. It's definitely possible that a group cannot all sit toghether. When that happens it gets a little messy and turns into a clustering problem. K-means clustering seams to be a common approach for keeping a group close together while maximizing values, such as seat quality. The downside is that a K-means clustering algorithm is superpolynomial if unoptimized, so this method isn't exactly practical.

I went for a simpler approach. I decided to deal with the arena as a group of seat sections. I decided to assume that each seat in one section would be the same quality. If a group can fit in one section then they sit together, and I don't need to worry about group splintering. Otherwise if there is no section large enough to fit in one group then it splinters and the highest quality seats are filled up. Another assumption is that seats nearer the stage are higher quality, and that seat quality is highly dependant on location. If this is true splintered groups are kept closer together, but it's still not honestly great. Both group size and the lateness of purchasing tickets should increase splintering and the severity of splintering. One minor approvement would be to check if a large group could be treated as two separate groups of about equal size.


##Setup
First run 'gradle build' to build the project and download dependencies.

Run 'gradle check' or 'gradle test' to run unit tests without rebuilding the project.

