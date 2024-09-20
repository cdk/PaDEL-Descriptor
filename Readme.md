Readme.md

In order to run PaDEL from the terminal follow the steps below.

Make a PaDEL-Descriptor repository from GitHub, follow the steps below:

Perform a build (ant) in the folders, in the following order:
     ~/PaDEL-Descriptor/libPaDEL
     ~/PaDEL-Descriptor/libPaDEL-Jobs
     ~/PaDEL-Descriptor/libPaDEL-Descriptor
     ~/PaDEL-Descriptor/

After running the build in the last folder ~/PaDEL-Descriptor, go through the output in the terminal and look for -do-jar-with-mainclass:. 
Underneath ‘-do-jar-with-mainclass:’ two [echo] lines are visible. In the second [echo] line you will find 

[echo] java cp “…” padeldescriptor.PaDELDescriptorApp

The dots (…) give the specified locations of jar files on your computer.

Copy the complete line and run it on the command line.

A PaDEL window should now appear!!


Note:
Running PaDEL was tested using:
cdk.1.4.15
openjdk 21.0.3
