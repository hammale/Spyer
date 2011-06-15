Building Spyer from source
====================
The easiest but the most inconvenient way of doing it is just compiling this in eclipse and zipping the contents of the bin folder and config folder together in all projects, but a better way is to write ant buildfiles.
Now, version 1.4 introduced modularity of the SpyerAdmin package. That means that if you want SpyerAdmin to work, you will need the two modules. The buildfile should make a jar for each project and copy them into a single plugins directory, where they can be tested. Same buildfile can be reused for different command module versions thanks to eclipse's launch configuration manager (overwriting variables).

Finding your way around the projects
====================
SpyerAdmin – main SpyerAdmin module.
SpyerAdminCommands – SpyerAdmin commands. By default forces who, message and reply, but can be extended to work with more. Needs rhino.jar in JRE lib directory.
SpyerAdminCommandBookCommands – SpyerAdmin commands for CommandBook.
SpyerFun – SpyerFun.
Each project contains two directories: src (the source files) and config(manifests and other).

How can you help
====================
I would appreciate some cleaning up. Why can't I do it myself? Because a clean mind can do it better – there are no “can't be done”s that I have because of running into numerous problems during development which was done in quite a rush. Thanks in advance!
