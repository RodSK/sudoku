This is typical Sudoku game.
Player is provided with options to select new game or continue previous game.
Player is provided with five slots to save checkpoints.
Application saves checkpoints in to SQLlite database.
If player selects new game, the checkpoints from previous game will be deleted. 
Application display best score of finished games only.
To control game, player has to press/select targeted cell on the grid.
Once cell is selected, player can press control buttons which will indicate legal move and game score will be adjusted accordingly.
One submitted number is one point. One deleted number results in deduction of one point.
The major application components are Fragments, Threads, Database and GrisView.  
Application has only two Activities that control multiple Fragments. 
Threads are used to call queries on SQLlite database.
Despite of the small size, application has very complex process flow. 
