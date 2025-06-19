# File Type Analyzer

A modern Java application that analyzes files to determine their type based on binary patterns. It uses four different string-matching algorithms and compares their performance.

![File Type Analyzer Screenshot](screenshot.png)

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

## Demo

[View Live Demo](https://your-demo-url.com) (Replace with your actual demo URL)

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle 8.x

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/file-type-analyzer.git
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

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Acknowledgments

- Thanks to all contributors who have helped with the project
- Special thanks to the Spring Boot team for their excellent framework
