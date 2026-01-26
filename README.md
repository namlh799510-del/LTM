# Rock Paper Scissors Multi-Client Game

A multiplayer Rock-Paper-Scissors game built with Java Socket programming and Swing GUI.

## Tech Stack

- **Java 21** (LTS)
- **Java Socket API** - Multi-client server architecture
- **Java Swing** - GUI interface
- **Multi-threading** - Concurrent client handling

## Features

- Multi-client server supporting multiple simultaneous games
- Real-time game synchronization between players
- Modern GUI with emoji-enhanced buttons
- Win/Loss/Draw score tracking
- Automatic player pairing
- Graceful connection handling and disconnection

## Project Structure

```
rock-paper-scissors-game/
├── src/
│   ├── game/
│   │   ├── GameLogic.java       # Core game logic and winner determination
│   │   └── GameResult.java      # Game result data structure
│   ├── server/
│   │   ├── GameServer.java      # Multi-client server
│   │   └── ClientHandler.java   # Individual client handler (threaded)
│   ├── client/
│   │   ├── GameClientCLI.java   # Console-based client
│   │   ├── GameClientUI.java    # GUI client (Swing)
│   │   └── TestClient.java      # Testing utility
│   └── utils/
│       └── Protocol.java        # Communication protocol definitions
├── out/                         # Compiled .class files (IntelliJ)
└── README.md                    # This file
```

## Setup & Installation

### Prerequisites

- Java 21 or higher
- IntelliJ IDEA (recommended) OR any Java IDE/text editor with terminal

### Verify Java Installation

```bash
java -version
# Should show: openjdk version "21.x.x" or higher
```

## How to Run

### Option 1: Using IntelliJ IDEA (Recommended)

#### 1. Open the Project

```
1. Open IntelliJ IDEA
2. File → Open
3. Select the rock-paper-scissors-game folder
4. Wait for indexing to complete
```

#### 2. Configure Run Configurations

**Server Configuration:**
```
1. Run → Edit Configurations
2. Click "+" → Application
3. Name: GameServer
4. Main class: server.GameServer
5. Module: rock-paper-scissors-game
6. JRE: 21
7. Click "Apply"
```

**Client GUI Configuration:**
```
1. Click "+" → Application
2. Name: GameClientUI
3. Main class: client.GameClientUI
4. Module: rock-paper-scissors-game
5. JRE: 21
6. IMPORTANT: Check "Allow multiple instances"
7. Click "OK"
```

#### 3. Run the Game

```
1. Start Server: Select "GameServer" from dropdown → Click Run 
2. Start Client 1: Select "GameClientUI" from dropdown → Click Run 
3. Start Client 2: Click Run again (multiple instances enabled)
```

### Option 2: Using Command Line

#### 1. Compile the Project

**Linux/Mac:**
```bash
# Navigate to project directory
cd rock-paper-scissors-game

# Compile all Java files
javac -d out -sourcepath src src/**/*.java
```

**Windows:**
```cmd
cd rock-paper-scissors-game
javac -d out -sourcepath src src\game\*.java src\server\*.java src\client\*.java src\utils\*.java
```

#### 2. Run the Server

**Linux/Mac:**
```bash
java -cp out server.GameServer
```

**Windows:**
```cmd
java -cp out server.GameServer
```

#### 3. Run the GUI Clients

Open **separate terminals** for each client:

**Linux/Mac:**
```bash
java -cp out client.GameClientUI
```

**Windows:**
```cmd
java -cp out client.GameClientUI
```

## How to Play

1. **Start the Server** - The server will listen on port 12345
2. **Launch 2 Clients** - Open two GUI client windows
3. **Enter Your Name** - Type your name in the text field
4. **Click "Join Game"** - Connect to the server
5. **Wait for Pairing** - The server pairs you with another player
6. **Make Your Choice** - Click Rock, Paper, or Scissors
7. **See the Result** - Winner is determined and scores are updated
8. **Play Again** - Buttons re-enable after 2 seconds for the next round

## Game Rules

- **Rock** beats **Scissors**
- **Scissors** beats **Paper**
- **Paper** beats **Rock**
- Same choices result in a **Draw**

## Technical Details

### Server Architecture

- Listens on port **12345**
- Accepts multiple client connections
- Creates a new thread for each client
- Pairs clients automatically when two are waiting
- Handles disconnections gracefully

### Communication Protocol

Messages follow the format: `TYPE:CONTENT`

**Message Types:**
- `CONNECT` - Client joins with name
- `WAITING` - Waiting for opponent
- `OPPONENT_FOUND` - Matched with opponent
- `CHOICE` - Player's move (ROCK/PAPER/SCISSORS)
- `RESULT` - Game outcome
- `DISCONNECT` - Client leaving

### Threading Model

- **Server**: Main thread accepts connections, spawns ClientHandler threads
- **Client**: Main thread handles GUI, background thread listens for server messages
- **GUI Updates**: All Swing updates use `SwingUtilities.invokeLater()` for thread safety

## Testing

### Test Scenario 1: Basic Game
```
1. Start server
2. Start 2 clients
3. Both join with names
4. Both make choices
5. Verify correct winner determined
6. Play multiple rounds
```

### Test Scenario 2: Multiple Games
```
1. Start server
2. Start 4 clients
3. Verify pairing: (Client1 vs Client2) and (Client3 vs Client4)
4. Both pairs play simultaneously
```

### Test Scenario 3: Disconnection Handling
```
1. Start server and 2 clients
2. Start a game
3. Close one client mid-game
4. Verify other client receives disconnection message
5. Verify server handles cleanup properly
```

## Troubleshooting

### Connection Refused Error

**Problem:** Client can't connect to server

**Solution:**
- Make sure the server is running first
- Check if port 12345 is available
- Verify localhost/127.0.0.1 connectivity

### Port Already in Use

**Problem:** Server won't start - "Address already in use"

**Solution:**
```bash
# Find process using port 12345
# Mac/Linux:
lsof -i :12345

# Windows:
netstat -ano | findstr :12345

# Kill the process and restart server
```

### GUI Not Responding

**Problem:** GUI freezes or buttons don't work

**Solution:**
- Check server is running
- Restart both server and client
- Check console for error messages

### Compilation Errors

**Problem:** Class not found or compilation fails

**Solution:**
- Verify Java 21 is installed: `java -version`
- Ensure correct package structure
- Clean and rebuild: `rm -rf out && javac -d out -sourcepath src src/**/*.java`

## Development Timeline

- **Day 1-2**: Setup + Basic game logic
- **Day 3**: Multi-client server implementation
- **Day 4**: Console client + Testing
- **Day 5-6**: GUI development + Socket integration
- **Day 7**: Testing + Documentation

## Future Enhancements

- [ ] Best-of-3 or best-of-5 game modes
- [ ] Lobby system with multiple rooms
- [ ] Chat functionality between players
- [ ] Replay/rematch option
- [ ] Statistics and leaderboard
- [ ] GUI themes and customization
- [ ] Network play over internet (not just localhost)

## Author

Built as a learning project for Java Socket programming and multi-threaded server development.

## License

This is an educational project. Feel free to use and modify for learning purposes.
