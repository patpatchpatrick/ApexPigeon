=======
# Apex Pigeon - Arcade "Runner" Game
![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/cover.png)

[<b>Play Online</b>](https://patpatchpatrick.itch.io/alpha-pigeon)

<b>[Download Android Version</b>](https://www.amazon.com/Patrick-Doyle-Alpha-Pigeon/dp/B07PJXBS25/ref=sr_1_1?keywords=alpha+pigeon&qid=1552340070&s=mobile-apps&sr=1-1)

* Disaster has struck.  While the other birds fly away, you (the Apex Pigeon) fly towards it.

* Fly as far as possible while dodging objects along the way.

* Earn global high scores.  Try and top the online leaderboard and beat your peers!

* This game is meant to be challenging.  Do not expect an easy victory!

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/gameplay1.gif)
![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/gameplay2.gif)

# The Code Behind the Game

The goal of the project was to create a non-stop "runner" game.  Essentially, a game in which the user-controlled sprite (Pigeon, in this case) continuously moves forward until it collides with another object.   

The game was built using the open-source Libgdx framework along with the Box2D physics library.  
The Libgdx framework comes with a "Core" module along with modules for Desktop, HTML/GWT, Android and IOS.  This framework allows for the game to be maintained in the "Core" module, while providing the option of creating module specific versions of the game.

## Screens

The game was built using only four main screens for simplicity sake.  A main menu screen, a game screen (primary screen which houses the gameplay), a high score screen and a settings screen.  The user can select which screen they want to access from the main menu.  Each screen also provides access to the main menu screen via a back arrow button. There is a fifth screen, game over screen, that is shown when the Pigeon crashes.  

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageOne.png)

## Game Screen / Gameplay

The primary function of the game is for the user to control a Pigeon and prevent it from colliding with enemy objects (named "Dodgeables" within the code).  The Pigeon flies with a positive velocity that slowly accelerates as time increases.   The score in the top right corner of the game represents the distance flown by the Pigeon (in meters), and stays in sync with the velocity of the Pigeon.  As time goes on, the game becomes more difficult and the Pigeon must overcome more difficult sequences of "Dodgeables" which appear.  The game ends when the Pigeon collides with a dodgeable, and the game over screen appears.  The game over screen shows if a new high score was attained, and if so, it will send it to the online leaderboard.

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageTwo.png)

## Bodies/Collisions/Box2D

All of the sprites in the game were created using the Box2d physics library.  All of the sprites except for the main sprite (Pigeon) were created/managed using a "Dodgeable" abstract class in the game and were spawned/controlled using a class titled "Dodgeables".  For example, a "Level Two Bird" enemy was created using the "LevelTwoBird" class which extends the "Dodgeable" abstract class. 


The creation process for a dodgeable object looked like so:

1. Draw object in photoshop (yes, I did all of the drawings myself :))
2. Use Physics Body Editor Tool to outline the collision skeleton of the object and generate a JSON Box2D object
3. Create a new class in the game to represent the sprite (LevelTwoBird, in this case)
4. Within the new dodgeable class, create a new Box2D body within constructor method.  The Box2D body is created using several Box2D tools (BodyDefs and Fixtures) which I won't go into detail on, and is generated using the JSON file from step 2.    
5. Create a sprite sheet image for the object (using Photoshop) for the animation of the object.  The animation is created using the Libgdx TextureRegion class which I won't get into detail on.    
The primary Pigeon object itself was created using the same process, but instead of extending the "Dodgeable" abstract class and being part of the "Dodgeables" class, the Pigeon has it's own class since it has special functionality being the primary user-controlled sprite in the game.

*Physics Body Editor Tool for "Level Two Bird"*

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageThree.png)
   
*Initializing Level Two Bird using JSON Object*

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageFour.png)

*Level Two Bird Sprite Sheet*

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageFive.png)

## User Input / Controls

User input for all screens in the game are controlled using an "InputProcessor" and/or the mobile device accelerometer (if the game is played on a mobile device).  An InputProcessor is essentially a Libgdx class which reads and provides the user input so that it can be used to manipulate objects in the game accordingly.  The accelerometer is the tool that reads which direction the mobile device is tilted and the angle that it is tilted.  An AccelerometerController class was created to help process accelerometer input for the game. 


The Apex Pigeon game uses very simple user controls.  The keyboard, mouse or accelerometer (if using a mobile device) can be used to control the direction that the Pigeon moves.  If a key is pressed, a constant force is applied to the center of the Pigeon object's body depending on which key was pressed.  If the mouse is clicked on the screen, a constant force is applied to the Pigeon object.  The force's magnitude and angle are calculated using a vector between the Pigeon's location and the click location.  If the mobile device is tilted and the accelerometer is being used, a force is applied to the Pigeon proportional to the tilt of the accelerometer.  The accelerometer input is adjusted based on the orientation of the particular mobile device.  (Note: there is a SettingsScreen which allows the user to enable/disable the accelerometer and touch input as well as adjust the input sensitivity)  Examples of the input control setup can be seen below:

*Input Processor (Keys)*

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageSix.png)

*Input Processor (Mouse)*
![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageSeven.png)

*Accelerometer Controls*
	
![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageEight.png)


## Gameplay


A GamePlay class is used in the game to manage the gameplay and how the levels and game progress over time.  


The gameplay in the game is divided into five levels.  Each level will spawn sequences/puzzles of different dodgeables that are more challenging than the prior level.  These sequences are called "Waves".  The final level in the game (named LevelFinal) is a continuous level which runs two random levels (between level 1 and 4) at the same time.  This allows the game to be continued forever until the user ultimately collides with an enemy dodgeable.

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageNine.png)

The GamePlay class uses an update() method, to update all of the objects on the screen using the current stateTime of  the game.  The stateTime of the game is the overall time that has passed since the game was initiated.   


The GamePlay class update() method works as follows:
1. Firstly, initiate the startTime of the game when the gameplay is initiated
2. Calculate the totalGameTime using the stateTime and the startTime
3. Update all of the levels
4. Update/accelerate the birds speed based on totalGameTime
5. Update all of the enemy dodgeables

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageTen.png)


Several of the gameplay items are expanded on below:

#### Levels
Each Level extends the "Level" abstract class.  The "Level" abstract class contains the start times and end times of each level in "seconds".  When the levels are updated, the appropriate level will be run based on whatever totalGameTime has passed.

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageEleven.png)


Most levels are similarly designed.  Each level has random waves of enemy dodgeables that spawn in some type of sequence or puzzle format.   For example, LevelThree has a "runRandomWaveMissiles" wave/method which will spawn rockets and alien missiles at certain intervals.  

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageTwelve.png)

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageThirteen.png)

A level can either be run randomly or manually.  If a level is run randomly, random waves will be continuously run until the level duration has passed and the next level begins.  If a level is run manually, then a level will run a specific (non-random) wave indicated via the method's inputs.  Levels are typically run randomly, but are run manually when the final level is run.   


When the level is started, if the level is being run randomly then a random wave number is generated.  That particular random wave is then run.   


For example, when LevelThree is first run (see run() method screenshot below), it will be run randomly (i.e. the runRandomWave input variable will be true).  Therefore, the waveToRun will be randomly chosen (line 55).  Afterwards, whichever corresponding waveToRun is randomly chosen will be run in lines 64-75.   The run() method is continuously run until the wave is complete.


As you can see in the runRandomWaveMissiles() class (in screenshot above), the wave will continuously be run, and birds, rockets, and alien missiles will continue to be spawned at certain intervals until the wave is over.  You'll notice that there is a checkIfRandomWaveIsComplete() method that is returned at the end of the wave.  What this essentially does, is check if the total duration of the wave has passed, and if so, resets the waveisInitiated variable back to false and ends the wave.  Then, when LevelThree is run again,  a new random wave will be selected and this process of selecting and running random waves will continue until the LevelThree duration is complete.  Afterwards, LevelFour will begin.

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageFourteen.png)

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageFifteen.png)

I'm not going to fully expand on how the final level works, but it works very similarly to the other Levels except that instead of selecting a random wave to run, it selects two different random levels (from levels one to four) and two random waves from those levels.  It will then run those waves simultaneously until both waves are complete.  Afterwards, two new levels and two new waves are run.  The process for selecting waves and levels is quite similar to the process of selecting random waves in Levels 1-4 as mentioned above.  Feel free to check out the "LevelFinal" class code for more details.


*Level Final Code Preview*

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageSixteen.png)

#### Pigeon Speed

The Pigeon will slowly accelerate over time as its speed is updated in the Gameplay class update() method.  A class called "GameVariables" is used to control some of the important  game variables that must be referenced by other classes as the game progresses.  One of these important variables is the Pigeon's speed.  The Pigeon starts the game with a speed of  9 m/s and has a MAX_SPEED of 300 m/s.  The pigeon will reach the max speed after approximately 20 mins (600 seconds).  

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageSeventeen.png)

#### Dodgeables

The last class that is updated via the Gameplay update method is the Dodgeables class.  The Dodgeables class is used to control all of the enemies that are spawned throughout the duration of the game.  The Dodgeables class controls the objects themselves via it's update() method and controls the rendering of the objects via the render() method.  

##### Rendering Dodgeables
I'm not going to discuss rendering in too much detail here because it is relatively straight forward.  Essentially, TextureRegions were created for all of the dodgeable enemy objects spritesheet images which I created using Photoshop.  TextureRegions break up spritesheets into individual images/frames.  When texture regions are rendered on the screen, they will show a particular frame of the animation spritesheet depending on the game time.   All of the TextureRegions are drawn using batches and are drawn at the location of the Box2D object so that the Box2D skeleton is  aligned with the animation that is being drawn on the screen.  Feel free to check out the render() method for each of the Dodgeables in the game for more details on how it works.  

##### Spawning Dodgeables
 
The dodgeables class is used to manage all enemy dodgeables in the game.  The dodgeables class does this by storing/referencing several other objects (e.g. birds, rockets, alienMissiles) which represent groups of individual dodgeables/enemies.  For example, the birds class manages all bird dodgeables, the rockets class manages all rocket dodgeables, and so forth…

*Dodgeables Constructor Showing Groups of Dodgeables*

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageEighteen.png)

##### Spawning/Pools

When a dodgeable is spawned, it is spawned via it's group's class and maintained using Arrays and Pools.  For example, the "birds" group/class maintains all birds dodgeables that are spawned and active.  Pools are a Libgdx class that enables objects to be reused when they are no longer active.  This improves the efficiency of memory usage, since new objects do not need to be created and destroyed nearly as often as they would be if objects were not reused.  Creating and destroying objects often can cause major performance issues.  (In fact, when I first created the game I did not use Pools and instead I instantiated and destroyed objects.   This caused the game to often crash on lower memory mobile devices during testing due to memory exceptions.  I reworked the game to use Pools to resolve this issue).  


When an object is spawned in the game, it is retrieved from a Pool of objects.  For example, when a LevelOneBird is spawned, it is retrieved from a Pool of LevelOneBirds using the Pool's obtain() method.  This method will provide an inactive LevelOneBird object that is available for reuse, or if there are none available then it will create a new one.   

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageNineteen.png)


When the dodgeables update() method is run, the update() method for all of the groups of dodgeables is called.  When the birds' update() method is called, it will check if a bird is able to be inactivated and able to be reused in the Pool.  A bird is considered to be ready for inactivation if it is no longer within the dimensions of the screen.  Once the bird is no longer within the dimensions of the screen, it is freed and available for reuse in the pool.  

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageTwenty.png)


Arrays are also used to keep track of the active dodgeables.  Arrays are used slightly differently than the pools.  They are primarily used to keep track of all Active Dodgeables for purposes of in-game powerups.  For example, there is an 'in-game' powerUp that immediately destroys all active enemies.  An Active Dodgeables Array can be used to intuitively destroy all active enemies and free them to be reused in  their respective pools.  For more details on how this works, please check out the powerUp() method of the Pigeon class.   

As mentioned above, when a dodgeable is spawned, it is obtained from a Pool and subsequently initialized using the object's init() method.  This will provide the spawn parameters to the object.  All the dodgeable enemies in the Apex Pigeon game are unique and are initialized in different ways, but most objects are initialized by applying a force to the center of the object's body to accelerate it across the screen.  For example, LevelOneBirds are initialized in a random vertical position and receive a horizontal force applied to them to accelerate them across the screen.  The force with which they are accelerated is generated using a "force multiplier" which is a method used to ensure that the LevelOneBird's speed proportionally increases as the Pigeon's speed increases over time.  This gives the illusion that the Pigeon is flying faster using the magic of relative velocity.  
 
![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageTwentyOne.png)

##### Collisions
	
Collisions are detected using a contactListener, one of Box2D's helpful tools.  A contact listener checks if any two objects on the screen have come into contact with one another, and if so, provides data about the contact that occurred.  


Every object in the game is instantiated using a categoryBit and a maskBit.   A categoryBit is essentially a way to group different types of object based on how they should handle collisions.  For example, a Meteor dodgeable/enemy has a categoryBit of CATEGORY_METEOR, which is a short.  Each enemy/dodgeable has it's own categoryBit.


A maskBit represents how each object should handle collisions with other objects (i.e. other categoryBits).  For example, a Meteor dodgeable has a maskBit of CATEGORY_PIGEON | CATEGORY_LEVEL_ONE_BIRD | CATEGORY_LEVEL_TWO_BIRD. What this means, is that a Meteor object can come into contact with only a pigeon, level one bird, or level two bird.  It cannot contact any other enemies (i.e. it cannot collide or exert forces on any other objects).  


Different objects/enemies in the game have different categoryBits and maskBits depending on how they should physically interact with other objects (see screenshot below, these variables are all stored in the GameVariables class).  It is important to note that a contact will only occur if an object's body has a maskBit applied that allows for contact with the object's categoryBit with which it collides.

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageTwentyTwo.png)
  
  
If two objects are able to contact one another, then the contactListener within the GameScreen class will provide information regarding what type of contact has occurred.  The beginContact method of the contactListener provides contact information when the contact has begun.  Several booleans within the beginContact method are used to track important types of contacts that occur throught the game (i.e. pigeonInvolvedInCollision and powerUpInvolvedInCollision).


Boolean logic is used at the bottom of the  beginContact method to determine how to handle the type of contact that occurred.  Many different scenarios can occur depending on what type of contact has occurred (e.g.  Pigeon can receive a powerUp, Pigeon can be teleported, Other Enemies can be killed if the Pigeon is powered up with a shield) but most importantly, if the Pigeon does not have a shield and comes into contact with any enemy, the game is officially over and the gameOver() method is called.  

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageTwentyThree.PNG)


When the game is over, the GameOverScreen displays.  

## High Scores / Networking:

#### High Score

Keeping track of the score in the game is relatively straight forward.  A HighScore class is used to keep track of the current score, and it contains an update() and a render() method that are called within the GameScreen's update() and render() method when the gameplay is running.  The update method updates the score (called currentScore) based on the Pigeon's current speed and the deltaTime that has passed.  It also updates the scoreString which is displayed in the upper right hand corner of the game.      

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageTwentyFour.png)

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageTwentyFive.png)

#### Check if New High Score
	
When the game is over, the checkForNewHighScoreAndUpdateNetworkAndDatabase() method is called and does exactly what the method title states, it checks if there is a new High Score and updates preferences/database/network accordingly. When the game is over, firstly, the current High Score is grabbed via the SettingsManager.  The SettingsManager pulls the score from the Preferences.  If the currentScore that the user attained is greater than the current high score, then the currentScore becomes the new current high score and a new high score was earned.


The high score is then added to the shared preferences and database (in the Android module of the game, an SQLite database is used to keep track of all scores using a Provider.  I will not go into details on how this is done but please see the Android module for details). 


Finally, the submitNewHighScoreToNetwork() method is called with submits the score to the online leaderboard using an HTTP GET Request.


#### Submitting Score to Leaderboard

An online database called DreamLo is used to keep track of high scores for the game.  To use DreamLo, an HTTP GET Request is sent to the server with the user's name and score, and the score is added to the online leaderboard.  The URL containing the score and user's name is built using a StringBuilder, and then a new HTTP GET Request is created using the URL and sent to the network.  

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageTwentySix.png)

The user's name is retrieved using a TextField within the MainMenuScreen.  When the game is launched, the user is prompted to enter their name.  This name is then stored in the SettingsManager Preferences after it is entered by the user.  I'm not going to detail the code here, since it is relatively straight forward.  It can be seen within the MainMenuScreen class.

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageTwentySeven.PNG)
 
#### HighScoresScreen and Retrieving High Scores from Leaderboard / Network

The online leaderboard and user scores can be seen using the HighScoresScreen which can be accessed via the main menu.  The user can either use the Local button to see the local scores (scores from SettingsManager Preferences or scores from the Android Database) or they can use the Global button to see the global scores from the network.  If the user clicks the "Rank" button, they will see their current Rank on the leaderboard and their current score.  If they click the "Top" button, they will see the Global top 1000 scores for all players.  These scores are retrieved using an HTTP Get Request and HttpResponseListener.  Depending on which button is clicked, the appropriate method and corresponding Http GET Request is called, and the HttpResponseListener will return the response. 

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageTwentyEight.png)

*Call Appropriate Score Retrieval Method (Http or Database Request) Depending On Button Selected*

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageTwentyNine.png)

*Example Retrieval Method (Http GET Request) for Top Scores*

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageThirty.png)

If the "Rank" button is clicked and scores are retrieved for an individual user, they are returned in PIPE-Delimited format.  If the "Top" button is clicked and scores are retrieved for all users, they are returned in JSON format.  In either case, the scores are parsed accordingly and subsequently loaded into a scroll pane that will display them.  


*Example of Parsing the Http Score Response*

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageThirtyOne.png)


#### Originally Using Google Play Services for Leaderboard

I would like to point out that Google Play Services were originally used for the game Leaderboard, but I ultimately decided to use DreamLo instead because Google Play Services were not compatible with the Desktop and HTML5/GWT versions of the game.  If you look at my prior GitHub commits, there is a commit titled "First Published Release" which contains all of the code to integrate with Google Play Services.  I think it is valuable because it shows my skillsets and capability to integrate with Google's APIs (Leaderboards, Mobile Ads):     

*"First Published Release" Commit*

https://github.com/patpatchpatrick/alphapigeon/tree/ac70049c9cdb571c4ba56b3f83b3f7f0cdf74cf9


Within the AndroidLauncher class of the game commit, you can see all the code that I originally wrote to integrate with Google Play's APIs.  Essentially, I queried scores from Google Play Services depending on which button was clicked (Daily, Weekly, All Time).  Then I used an interface called MobileCallbacks to pass the information in ArrayList<String> format from the Android module to the Core module of the game.  Once the information was received via the Core module of the game within the HighScoreScreen (which implemented the mobile callbacks interface), it was added to the scroll pane on the screen.    

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageThirtyTwo.png)
 
![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageThirtyThree.png)

## Other Miscellaneous Challenges

#### Settings

There are several important settings that are used throughout the game.  I decided to use a SettingsManager class to manage all of these Settings.  The class contains important boolean, float and String settings/variables that are often referenced throughout the game.  The boolean settings are used to determine if a setting is enabled (e.g. gameSoundsSettingIsOn is used to determine if game sounds should be played).  The float settings typically correspond with the boolean settings (e.g. there is a gameVolume setting that is used in conjunction with the gameSoundsSettingIsOn boolean).  These settings are used together when game sounds are played via the "Sounds" class.  
		
*Settings Variables*

![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageThirtyFour.png)
	
There is an updateSettings() method that is called at key points of the game to ensure the settings are properly synced with the user's Preferences.  This class is called at appropriate points in the game, typically when new screens are loaded.  
	
![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageThirtyFive.png)
	
There are also toggle methods that are used if settings need to be toggled or changed when a user accesses the SettingsScreen and updates particular settings. 
	
![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageThirtySix.png)
	
#### HTML/GWT

Creating an HTML/GWT version of the game ended up being a major challenge.  HTML is certainly the hardest module of Libgdx to develop a version of your game for as it has the most obstacles that need to be overcome.  I ran into many issues when creating an HTML/GTW version of the game that I ultimately ended up resolving.
	
##### GWT issues:
	
- Not disposing of all disposable objects causes GWT exceptions.  When I first implemented the game I had several Sounds that I was not properly disposing of causing GWT exceptions that I eventually resolved by properly disposing  of all disposable objects.    
- GWT does not support threading.  My game had several methods run on separate runnables that needed to be reworked.
- GWT does not support HTTP networking.  I resolved this by testing the HTML version of my game in Chrome and running the developer console.  I was receiving HTTP networking exceptions and resolved them by using HTTPS requests instead of HTTP requests.
- Inability to easily test the HTML version.  I had to learn how to run the gwtSuperDev gradle task to debug the app directly in the browser.  Super dev mode compiles the Java code to JavaScript and injects it into the browser to be tested/debugged.
- Arrow keys scroll the HTML browser along with the game.  Had to learn how to prevent this in the "index.html" JavaScript using a function to prevent the default use of certain keys 
	
![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageThirtySeven.png)
	
	
#### UFOs/Energy Beams/Energy Balls

I had a really unique UFO enemy/dodgeable that I used in my game.  Each UFO had the ability to shoot an energy beam in any direction.  The energy beam originates as an energy ball that slowly grows over time until it eventually becomes an energy beam.  This was an interesting and fun challenge to set up from both a rendering perspective and an object perspective.  
	
![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/gameplay1.gif)
	
For all UFOs, I created an Array of EnergyBalls and EnergyBeams associated with each UFO that could be used to track the balls/beams linked to the UFO.  Then, using the initialization/spawn parameters for the UFO I could determine how many balls/beams to generate for each UFO and in which direction they should spawn. 
	
![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageThirtyNine.png)
	
Using the UFOs update() method, I began generating the energy balls associated with each UFO after a set period of time (e.g. currentTime - ufo.spawnTime).  


Once the balls were spawned, I would increase the width and the height of each particular energy ball associated with the UFO.  This would render the energy ball so that it would appear as growing/charging to the player.  Once the width of the energy ball was equal to the width of an energy beam, I would consider the energy ball to be charged.  Matching the energy ball's width with the energy beam's width ensured that a smooth animation/transition would occur.  


Once the energy ball was charged, I would then render a 12 frame transitional animation that would display an energy ball transforming into an energy beam.  I used the energyBall.setCharged method, to set the object itself as charged and I used this boolean indicator to render the transitional animation in the render() method.  After this 12 frame transitional animation was complete, I would spawn and render the energy beam, making for a smooth animation.   
	
*UFOs Update Method*
	
![Screenshots](https://raw.githubusercontent.com/patpatchpatrick/alphapigeon/master/docs/readmeimg/RMImageForty.png)
	
	 
#### Other Stuff
	
I created a lot of other fun/unique code for the game that I don't have time to fully detail out, but I encourage you to check out if you get a chance!  Some cool code that I recommend checking out:
	
- Notifications class: *created notifications that display at the top, left, and bottom of screen if an object is going to be spawned from that direction.  Notifications are spawned and play a sound a few seconds before the object is spawned to give the player a heads up about an incoming object.*
	
- PowerUps class: *(many of the powerUp methods can be seen in the Pigeon class) - created two primary powerUps for the Pigeon.  A shield that makes it invincible for a set period of time (and kills enemies the Pigeon touches while shielded) and a powerUp that kills all enemies on the screen.  Developed a way for these powerUps to be spawned at random intervals.*
	
- Teleports class: *spawns two teleports at random vertical heights on opposite sides of the screen.  When the Pigeon contacts either teleport, it teleports to the other teleport.*
	
- AlienMissiles class: *spawns a missile that shoots four other smaller missiles away from the missile when it explodes.  These four missiles subsequently explode after a set duration of time.*
	
- ScrollingBackground class: *renders the background of the game using two images that slowly move to the left over time.  When one image moves too far to the left, it is recycled.  The scrollingBackground accelerates proportionally to the Pigeon to give the illusion that the Pigeon is accelerating using relative acceleration.*  




