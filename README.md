# SpellcastSolver

A fast and efficient engine designed to find the best possible word in Discord's **Spellcast** activity.

## 📌 Table of Contents
- [About](#about)
- [Features](#features)
- [Solver Performance](#solver-performance)
- [Installation](#installation)
- [Usage](#usage)
- [Screenshots](#screenshots)
- [License](#license)

## 📖About

**SpellcastSolver** is a high-performance word solver for the **Spellcast** game, 
utilizing a **Trie** for fast word lookups by passing the **TrieNode** directly, and a **hashmap**
to store the best version of each word. It leverages **multithreading** for parallel processing,
and uses a scoring system that prioritizes long-term value, sometimes selecting lower-point 
words if they yield more gems for future moves with swaps.

## 🚀Features

- ✅ **Interactive Board GUI**  
  Input and visualize the game state with an intuitive, user-friendly interface.

- ✅ **Board Controls**  
  Easily clear the board, apply modifiers, or generate a random board layout.

- ✅ **Advanced Tile Modifiers**  
  Add strategic elements to the game with:
    - 💎 **Gem tiles**
    - 🔠 **Letter multipliers** (2×, 3×)
    - 🔤 **Word multipliers** (2×, 3×)
    - ❄️ **Frozen tiles**

- ✅ **Multithreaded Processing**  
  Efficient parallel processing to evaluate word possibilities faster.

- ✅ **Inventory Management**  
  Track your available gems and the current game round.

- ✅ **Live Word Highlighting**  
  Selected words are visually highlighted on the board for clarity.

- ✅ **Flexible Word Sorting**  
  Sort the generated words based on:
    - 📖 **Lexicographical order**
    - 📊 **Evaluation score**
    - 💯 **Points**
    - 💎 **Gem count**
    - 🔄 **Swap usage**

- ✅ **Strategic Scoring System**

  Words are ranked not just by immediate points, but also by gems gained—favoring long-term advantage 
  through swaps over short-term score.

## 📈Solver Performance

- 📊 Boards played on round 3.
- 🖥️ Benchmarking was done with 100 boards on an M2 MacBook Air.

| Gems [Swaps] | Avg Words Found | Avg Points/Word | Avg Eval Score/Word | Time/Board (Multithreading) | Time/Board (Single threaded) |
|--------------|-----------------|-----------------|---------------------|-----------------------------|------------------------------|
| 0 [0]        | 121             | 34              | 91.41               | 1.48 ms                     | 0.43 ms                      |
| 3 [1]        | 2,374           | 59              | 122.76              | 5.41 ms                     | 8.84 ms                      |
| 6 [2]        | 14,288          | 74              | 143.37              | 66.96 ms                    | 250.29 ms                    |
| 9 [3]        | 46,601          | 86              | 160.00              | 1,092.39 ms                 | 4,188.90 ms                  |


## 🛠️Installation

**Prerequisites:**
- **No Java installation required** — the app includes a custom runtime built with Java 21 via [`jlink`](https://docs.oracle.com/en/java/javase/21/docs/specs/jlink/jlink.html).

🔽 **[Download the latest release](https://github.com/dootz1/SpellcastSolver/releases/latest)** from the GitHub Releases page. Choose the appropriate installer for your platform:

| Platform | Installer |
|----------|-----------|
| **Windows** | `SpellcastSolver-<version>.exe` |
| **macOS**   | `SpellcastSolver-<version>.dmg` |
| **Linux**   | `spellcastsolver_<version>_amd64.deb` or `SpellcastSolver-<version>.tar.gz` |

> ℹ️ Replace `<version>` with the actual version number, e.g., `1.0.0`.

---

### ▶️ Install & Run the Application

#### 🪟 Windows
1. Download and run the `.exe` installer.
2. Follow the setup wizard to complete installation.
3. Launch **SpellcastSolver** from your Start Menu or desktop shortcut.

#### 🍎 macOS
1. Download the `.dmg` file.
2. Open it and drag **SpellcastSolver** into your `Applications` folder.
3. Open it from Launchpad or Spotlight.

#### 🐧 Linux (Debian-based)
1. Download the `.deb` file.
2. Install it using:
   ```bash
   sudo dpkg -i spellcastsolver_<version>_amd64.deb
    ```
3. Launch it via your system’s app menu or run:
   ```bash
   spellcastsolver
    ```

## 🧑‍💻Usage

1. **Input the board state**  
   Type in the letters to match the current game board.

2. **Add tile modifiers**  
   Right-click on any tile to add modifiers such as:
    - 💎 Gems
    - 🔠 Double/Triple Letter (DL/TL)
    - 🔤 Double/Triple Word (DW/TW)

3. **Set game context**  
   Enter the current **round number** and configure your **gem inventory**.

4. **Solve the board**  
   Click the **Solve** button to generate possible words.

5. **Explore results**  
   Click any row in the result table to highlight the word's path on the board.
    - 🔴 Swapped letters are highlighted in red.

6. **Sort/Search your results**  
   Click on column headers to sort by:
    - 🧠 `evalScore` (engine-recommended move)
    - 💯 `Points`
    - 💎 `Gems`
    - 🔄 `Swaps`
    - 📖 `Lexicographical order`

   Type in the search bar to find matches for your desired word.


## 📸Screenshots

![Alt text](screenshots/screenshot.png?raw=true "Optional Title")

## 📝License

This project is licensed under the [MIT License](LICENSE).