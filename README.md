# SpellcastSolver

A fast and efficient engine designed to find the best possible word in Discord's **Spellcast** activity.

## 📌 Table of Contents
- [About](#about)
- [Features](#features)
- [Solver Performance](#solver-performance)
- [Installation](#installation)
- [Usage](#usage)
- [Technologies Used](#technologies-used)
- [Screenshots](#screenshots)
- [License](#license)

## 📖About

**SpellcastSolver** is a high-performance word solver for the **Spellcast** game, 
utilizing a **Trie** for fast word lookups by passing the **TrieNode** directly, and a **hashmap**
to store the best version of each word. By using trie‑letter **bitmaps** for valid child nodes, 
the solver speeds up 2–3‑swap searches by up ~25%. It also leverages **multithreading** for parallel processing,
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
- ✅ **Board Shuffle Recommendation**  
  When the current board has a low score or evaluation, the engine will suggest spending 
  **1 gem** to shuffle the board. This can improve letter positioning and increase your 
  potential to score more points.

## 📈Solver Performance

- 📊 Boards played on round 3.
- 🖥️ Benchmarking was done with 100 boards on an M2 MacBook Air.

| Gems [Swaps] | Avg Words Found | Avg Points/Word | Avg Eval Score/Word | Time/Board (Multithreading) | Time/Board (Single threaded) |
|--------------|-----------------|-----------------|---------------------|-----------------------------|------------------------------|
| 0 [0]        | 121             | 34              | 91.41               | 1.48 ms                     | 0.43 ms                      |
| 3 [1]        | 2,374           | 59              | 122.76              | 5.41 ms                     | 8.84 ms                      |
| 6 [2]        | 14,288          | 74              | 143.37              | 59.17ms                     | 206.45ms                     |
| 9 [3]        | 46,601          | 86              | 160.00              | 828.19 ms                   | 4516.78 ms                   |


## 🛠️Installation

**Prerequisites:**
- **No Java installation required** — the app includes a custom runtime built with Java 21 via [`jlink`](https://docs.oracle.com/en/java/javase/21/docs/specs/jlink/jlink.html).

🔽 **[Download the latest release](https://github.com/dootz1/SpellcastSolver/releases/latest)** from the GitHub Releases page. Choose the appropriate installer for your platform:

| Platform    | Installer                             |
|-------------|---------------------------------------|
| **Windows** | `SpellcastSolver-<version>.exe`       |
| **macOS**   | `SpellcastSolver-<version>.dmg`       |
| **Linux**   | `spellcastsolver_<version>_amd64.deb` |

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

2. **Add Tile Modifiers**  
Right-click on any tile to apply special modifiers. You can also use keyboard shortcuts for quick access:

| Modifier               | Description            | Hotkey    |
|------------------------|------------------------|-----------|
| 💎 **Gem**             | Adds gem status        | `1`       |
| ❄️ **Frozen**          | Freezes the tile       | `!`       |
| 🔠 **Letter Bonus**    | Double / Triple Letter | `2` / `3` |
| 🔤 **Word Bonus**      | Double / Triple Word   | `@` / `#` |
| ♻️ **Clear Modifiers** | Removes all modifiers  | `0`       |

>💡 **Tip:** Hover over the informational message at the bottom of the board to see a tooltip with available modifier hotkeys.

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
7. **Clear / Play Move**
    - **Clear**: Deselect the current move and reset your tile selection.
    - **Play**: Apply the selected move — updates the **round number**, adjusts your **gem inventory**,
   and removes the used letters from the board for a smoother full-game experience.

## 🛠️Technologies Used

This application is built with the following key technologies:

- **JavaFX, FXML & ControlsFX with CSS styling** – Used for building a modern, responsive user interface with declarative layouts and rich UI components.
- **jpackage & jlink** – Tools for creating native installers and bundling a custom Java runtime.
- **Maven** – Handles dependency management and builds the application.
- **GitHub Actions** – Automates building, packaging, and releasing across platforms via CI/CD.

## 📸Screenshots

![Alt text](screenshots/screenshot.png?raw=true "Screenshot 1")
![Alt text](screenshots/screenshot2.png?raw=true "Screenshot 2")

## 📝License

This project is licensed under the [MIT License](LICENSE).
