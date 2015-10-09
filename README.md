Kairus' UoA Soccer Robots simulator/controller

Why?
Easy testing, development via simulation, higher level language, on-screen representation, a gui to help manage robots, event playback, all kinda of reasons. It's actually got an extremely effective strategy built into it currently. The only reason we didn't win last season was because two of our robots were physically incorrectly configured, and we worked it out when it was too late :c

Why not?
Close to competition? Not good at Java? Don't want to look into a program that requires a bit of setup, especially when it comes to actually making the robots work? If so, then this probably isn't for you - unless someone fixes it via a pull request.

How to set up:
Get eclipse, right click in package explorer, import, general, existing projects into workspace, browse to this folder.

Testing:
1) Run RunSimulation.java, then one can simply modify settings to make the physics more realistic, and change strategies, using the positions etc of everything.
2) Simulate strategies go head to head, hundreds of times faster than real-time.
3) Replay and re-watch plays, to make decisions on what parts your strategy should improve on, go to pre-defined spots, automatically swap robots, etc.

Actually using it on the physical robots:
Note: This is going to need some work to get working again, but essentially, in server.java, make sure coms are opening, and data is being recieved from the stock software.

1) Run the modified stock software (https://mega.nz/#!6MhhCZTB!fO45vUvs5XjwsV99-DvzGspERH_9POid-cD3eTZw650) along-side.
2) Run server.java, make sure coms are opening.
3) When the actual software is in game mode, it will start sending data to the server program, which then runs a frame of it's simulation, deciding what to do with the robots, and controlling them (assuming you are green).


What is different about the modified software?
The modified software does NOT use the com port, and instead, relays positional information about the robots and ball to the server. This java program then controls the robots itself.

What kinds of delay are we having to deal with because of all this? It runs at 60fps, so, <17ms.