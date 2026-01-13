# Blackjack Simulator

A JavaFX-based Blackjack game simulator with a graphical interface. Play Blackjack against a dealer with realistic game mechanics and betting options.

## Features

- Interactive GUI with card animations
- Realistic Blackjack gameplay mechanics
- Betting system with configurable starting balance
- Side bets and insurance options
- Adjustable game speed
- Multiple hand support (splitting)

## Requirements

- **Java 21 or higher** (JDK recommended)
- **Maven** (included via Maven Wrapper - no separate installation needed)

## Installation & Running

### Windows

1. **Install Java**
   - Download and install Java 21 JDK or higher from [Adoptium](https://adoptium.net/temurin/releases)
   - The installer will automatically set up Java for you

2. **Download the Game**
   - To download:<br> hit the green code button and download as zip
   - Or clone:
     ```bash
     git clone https://github.com/hqluke/BlackjackSimulator
     ```

3. **Run the Game**
   - Simply double-click `run.bat` or run it from Command Prompt:
     ```
     run.bat
     ```
   - The script will automatically detect your Java installation and start the game

### Linux / Mac

1. **Install Java**
   
   **Ubuntu/Debian:**
   ```bash
   sudo apt update
   sudo apt install openjdk-21-jdk
   ```
   
   **Arch Linux:**
   ```bash
   sudo pacman -S jdk21-openjdk
   ```
   
   **macOS (using Homebrew):**
   ```bash
   brew install openjdk@21
   ```

2. **Download the Game**
     ```bash
     git clone https://github.com/hqluke/BlackjackSimulator
     ```


3. **Run the Game**
   ```bash
   chmod +x run.sh  # Make the script executable (first time only)
   ./run.sh
   ```
   - The script will automatically detect your Java installation and start the game


## Game Controls

- **Deal**: Start a new hand
- **Hit**: Request another card
- **Stand**: Keep your current hand
- **Double Down**: Double your bet and receive one more card
- **Split**: Split pairs into separate hands (when available)
- **Insurance**: Protect against dealer Blackjack (when dealer shows Ace) ((only cowards take this))

## Future: Not planning to update but if I did
- store last game setup options to quickly replay custom settings
- allow playing multiple hands at the same time (would have to shrink the ui a lot)
