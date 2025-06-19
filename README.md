# File Type Analyzer

A modern Java application that analyzes files to determine their type based on binary patterns. It uses four different string-matching algorithms and compares their performance.

![File Type Analyzer Screenshot](https://i.ibb.co/27M2rkNr/Main-Screen.png)

## Features

- **Multiple File Analysis**: Upload and analyze multiple files simultaneously
- **Parallel Processing**: Utilizes Java threads for efficient processing
- **Advanced Pattern Matching**: Implements four algorithms:
  - Brute Force: Simple character-by-character comparison
  - Knuth-Morris-Pratt (KMP): Efficient string searching with preprocessing
  - Rabin-Karp (RKM): Hash-based pattern matching
  - Sliding Window: Optimized approach for binary data
- **Performance Metrics**: Compares algorithm execution times
- **Extensive File Type Support**: Recognizes 80+ file formats
- **Modern UI**: Clean, responsive interface with drag-and-drop support


## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle 8.x

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/VJ12200/File-Type-Analyzer
   cd file-type-analyzer
   ```

2. **Build the application**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the application**
   Open your browser and navigate to `http://localhost:8080`

## Usage

### Web Interface

1. Drag and drop files onto the upload area or click to browse
2. Click "Analyze Files" to start the analysis
3. View the results showing file types and algorithm performance

![Result](https://github.com/user-attachments/assets/f9a9deb1-d1f3-4276-a487-a58f8143055e)

### API Usage

Send a POST request to `/api/analyzer/analyze` with files in the "files" parameter:

```bash
curl -X POST -F "files=@example.pdf" -F "files=@example.jpg" http://localhost:8080/api/analyzer/analyze
```

Example response:
```json
[
  {
    "fileName": "example.pdf",
    "fileType": "PDF document",
    "algorithmPerformance": {
      "bruteForce": 1250000,
      "kmp": 980000,
      "rabinKarp": 1100000,
      "slidingWindow": 850000
    }
  },
  {
    "fileName": "example.jpg",
    "fileType": "JPEG image",
    "algorithmPerformance": {
      "bruteForce": 320000,
      "kmp": 280000,
      "rabinKarp": 310000,
      "slidingWindow": 250000
    }
  }
]
```

## How It Works

1. The application loads file signature patterns from a resource file
2. When files are uploaded, they're processed in parallel threads
3. Each file is analyzed using four different pattern matching algorithms
4. The application measures how long each algorithm takes to find a match
5. Results include the detected file type and performance metrics for each algorithm

## Customizing File Patterns

You can add or modify file signatures in `src/main/resources/org/example/Analyzer/Patterns/Patterns.txt`.

Each line follows this format:
```
priority;"hex pattern";"description"
```

Example:
```
1;"25 50 44 46 2D";"PDF document"
```

## Acknowledgments

- [This](https://en.wikipedia.org/wiki/List_of_file_signatures) Wikipedia page for the list of file signatures
