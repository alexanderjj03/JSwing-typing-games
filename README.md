# Typeracer-style Typing Games

## By Alexander Jacobson

This application will help a user improve their typing speed and accuracy. 
Upon running. a main menu will pop up asking the user to choose which game mode
they wish to play (each game mode has a corresponding button). At the moment, 
there are three game modes, with more possibly to come. At any time during these
games, the user is provided a button at the very bottom of the screen to bring
them back to the main menu.

This originally started as a project for a class, at which time only the Typing
Race was (quite poorly) implemented. Ever since then, I have developed the
Falling Words and Boss Fight game modes on the side. I intend to expand on the
Boss Fight mode slightly more, but am currently prioritizing other projects.

### -Typing Race (develops speed & accuracy)

This game starts off by asking the user how many words they wish to type,
and providing a slider (ranging from 20 to 260) for them to indicate their 
preference. Once the user makes their decision, that number of words
will appear as a passage on screen. Each word is randomly selected from 
wordlist.10000.txt, a list of 10,000 common words (**fair warning, a few of them
are mild profanity**). It is the user's job to type this passage as quickly and
accurately as possible. The user's words per minute (WPM) and accuracy are 
displayed at the top of the screen.

As the passage is being typed, each character's color will change in real 
time, depending on if it's typed correctly or not. If the user types a 
character correctly, it will turn blue. If the user types the character 
incorrectly, it will turn red and stay that way until it is typed correctly. This
convention is used for every game mode.

Once the entire passage has been typed, the user will be shown a graph of 
their WPM across the passage. They will also be given an option (via a 
button) to play this game again.

### -Falling Words Survival (develops accuracy & reaction time)

The user is first prompted on screen to select the difficulty they wish to play at;
easy, medium, or hard (see below for further specification). Once this decision is 
made, random* words from wordlist.10000 will spawn periodically at the top of the
screen with random x-coordinates, and start travelling downwards. It is the user's
job to finish typing each word before it hits the bottom. Only one word can be 
typed at a time. In other words, if the user is in the middle 
of typing one word, they can't start typing a different one. If a word hits the 
bottom, the user loses a life. 

The game's difficulty determines how fast the words fall, how frequently they 
spawn, and how many words must be typed for the user to win. The user's accuracy, 
lives left, and words needed to win are displayed at the top of the screen. Once
the game is over, the user is given the option to play again.

*See note marked ** under Boss Fight.

### -Boss Fight (just for fun)

The user must put their typing skills to use to defeat a 
custom boss called the Wizard of Words. The player and boss icons are placed on opposite
sides of the screen (a 2D "arena" of sorts), and the boss will periodically spawn 
words** from wordlist.10000 at random points in the arena. These words travel in 
semi-random directions and damage the player on contact. If the player loses all their
health, they lose. 

To prevent this, the user can move the player icon around by mouse clicking on a point 
in the arena, and the player icon will move towards that point. The user must also 
type the words that the boss spawns. Once one of these words is fully typed, it becomes
harmless and moves directly away from the player icon. If it touches the boss on its 
way out of the arena, the boss takes damage and the player's health regenerates. To
consistently damage the boss, careful player positioning is required. As the boss loses
health, more mechanics are added that make the fight progressively harder (e.g. making 
the boss' words bounce off the arena walls). Once the boss' loses all their 
health, the player wins.

**No two words on screen will have the same first character. Otherwise, there
would be cases where the user is trying to type one word but the program ends up
typing a different one. This also means that once you start typing a word, you must
finish typing it unless it disappears. Furthermore, to make the game semi-fair, only 
6-14 character long words are considered.
