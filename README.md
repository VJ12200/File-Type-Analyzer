# File Type Analyzer

This application analyzes files to determine their type based on binary patterns. It uses four different string-matching algorithms and compares their performance.

## Features

- Upload multiple files for analysis
- Parallel processing using Java threads
- Four different pattern matching algorithms:
  - Brute Force
  - Knuth-Morris-Pratt (KMP)
  - Rabin-Karp (RKM)
  - Sliding Window
- Performance comparison between algorithms
- PostgreSQL database for pattern storage

## Getting Started

### Prerequisites

- Java 17 or higher
- PostgreSQL database
- Gradle

### Installation

1. Clone the repository
2. Create a PostgreSQL database named "fileanalyzer"
3. Update database credentials in `src/main/resources/application.properties` if needed
4. Run the application:
   ```
   ./gradlew bootRun
   ```

### API Usage

Send a POST request to `/api/analyzer/analyze` with files in the "files" parameter:

```
curl -X POST -F "files=@example.pdf" -F "files=@example.jpg" http://localhost:8080/api/analyzer/analyze
```

## How It Works

1. The application reads file patterns from the database
2. When files are uploaded, they are processed in parallel
3. Each file is compared against the patterns using four algorithms
4. The application measures how long each algorithm takes
5. Results include the detected file type and algorithm performance metrics