# Typeracer-style Typing Games

## By Alexander Jacobson

This application will help a user improve their typing speed and accuracy. 
Upon running. a main menu will pop up asking the user to choose which game mode
they wish to play (each game mode has a corresponding button). At the moment, 
there are two game modes, with more possibly to come. At any time during these
games, the user is provided a button at the very bottom of the screen to bring
them back to the main menu:

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
made, random** words from wordlist.10000 will spawn periodically at the top of the
screen with random x-coordinates, and start travelling downwards. It is the user's
job to finish typing each word before it hits the bottom. Only one word can be 
typed at a time. In other words, if the user is in the middle 
of typing one word, they can't start typing a different one. If a word hits the 
bottom, the user loses a life. 

The game's difficulty determines how fast the words fall, how frequently they 
spawn, and how many words must be typed for the user to win. The user's accuracy, 
lives left, and words needed to win are displayed at the top of the screen. Once
the game is over, the user is given the option to play again.

**No two words on screen will have the same first character. Otherwise, there 
would be cases where the user is trying to type one word but the program ends up
typing a different one. Also, to make the game semi-fair, only 6-14 character long 
words are considered.

### -Boss Fight (just for fun)

In development, I will try to finish it by Halloween 2023.