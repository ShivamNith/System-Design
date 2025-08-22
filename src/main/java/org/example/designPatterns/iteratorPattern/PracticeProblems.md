# Iterator Pattern - Practice Problems

## Problem 1: Social Media Feed Iterator
**Difficulty: Easy**

Design a social media feed system using the Iterator Pattern. The system should support:
- Posts from different platforms (Twitter, Facebook, Instagram)
- Filtering posts by date range, user, or platform
- Chronological and reverse-chronological iteration

**Requirements:**
- Create a `Post` class with platform, user, content, timestamp, likes
- Implement `SocialMediaFeed` that aggregates posts from multiple platforms
- Support multiple iterator types: `ChronologicalIterator`, `ReverseChronologicalIterator`, `FilteredIterator`
- Handle edge cases like empty feeds and invalid date ranges

**Test Cases:**
```java
feed.addPost(new Post("Twitter", "john_doe", "Hello World!", timestamp1, 100));
Iterator<Post> iterator = feed.chronologicalIterator();
Iterator<Post> filtered = feed.filteredIterator(post -> post.getLikes() > 50);
```

## Problem 2: Playlist Iterator with Shuffle
**Difficulty: Easy-Medium**

Create a music playlist system that supports different playback modes using iterators.

**Requirements:**
- Implement `Song` class with title, artist, duration, genre
- Create `Playlist` class that can iterate in: sequential, shuffle, repeat modes
- Support filtering by genre, artist, or duration
- Implement `ShuffleIterator` that randomizes song order without repetition
- Add ability to skip songs and track play history

**Test Cases:**
```java
playlist.addSong(new Song("Imagine", "John Lennon", 183, "Rock"));
Iterator<Song> shuffle = playlist.shuffleIterator();
Iterator<Song> rockSongs = playlist.genreIterator("Rock");
```

## Problem 3: Matrix Iterator with Different Traversal Patterns
**Difficulty: Medium**

Implement a 2D matrix class with multiple traversal patterns using the Iterator Pattern.

**Requirements:**
- Create `Matrix<T>` class that stores 2D data
- Implement iterators for: row-major, column-major, diagonal, spiral, zigzag traversals
- Support sub-matrix iteration (iterate over a rectangular region)
- Add boundary checking and empty matrix handling
- Implement `FilterIterator` that skips elements based on a predicate

**Test Cases:**
```java
Matrix<Integer> matrix = new Matrix<>(3, 3);
Iterator<Integer> spiral = matrix.spiralIterator();
Iterator<Integer> diagonal = matrix.diagonalIterator();
Iterator<Integer> subMatrix = matrix.subMatrixIterator(1, 1, 2, 2);
```

## Problem 4: File System Iterator with Depth Control
**Difficulty: Medium**

Design a file system iterator that can traverse directories with various options.

**Requirements:**
- Create `FileSystemNode` with file/directory distinction
- Implement `FileSystemIterator` with options: depth-first, breadth-first, files-only, directories-only
- Support maximum depth limiting and file extension filtering
- Add size-based filtering and date-based filtering
- Handle symbolic links and circular references safely
- Implement lazy loading for large directory structures

**Test Cases:**
```java
FileSystem fs = new FileSystem("/home/user");
Iterator<FileSystemNode> dfs = fs.depthFirstIterator();
Iterator<FileSystemNode> javaFiles = fs.extensionIterator(".java");
Iterator<FileSystemNode> limited = fs.depthLimitedIterator(2);
```

## Problem 5: Graph Traversal Iterator
**Difficulty: Medium-Hard**

Implement graph traversal iterators for different algorithms using the Iterator Pattern.

**Requirements:**
- Create `Graph<T>` class with adjacency list representation
- Implement iterators for: BFS, DFS, topological sort (for DAGs)
- Support starting from specific nodes and visiting restrictions
- Add path tracking and cycle detection
- Implement `ShortestPathIterator` using Dijkstra's algorithm
- Handle disconnected graphs and weighted edges

**Test Cases:**
```java
Graph<String> graph = new Graph<>();
graph.addEdge("A", "B", 5);
Iterator<String> bfs = graph.bfsIterator("A");
Iterator<String> topological = graph.topologicalIterator();
Iterator<GraphPath<String>> shortestPaths = graph.shortestPathIterator("A");
```

## Problem 6: Database Result Set Iterator with Pagination
**Difficulty: Medium-Hard**

Create a database result set iterator that handles large datasets efficiently.

**Requirements:**
- Implement `DatabaseResultSet` that simulates database query results
- Create `PaginatedIterator` that loads data in chunks to manage memory
- Support sorting by different columns during iteration
- Implement `GroupedIterator` that groups results by a field
- Add aggregate functions (sum, count, average) that work with iterators
- Handle database connection failures and retry mechanisms

**Test Cases:**
```java
DatabaseResultSet results = database.executeQuery("SELECT * FROM users");
Iterator<Record> paginated = results.paginatedIterator(100);
Iterator<Group<Record>> grouped = results.groupedIterator("department");
```

## Problem 7: Text Processing Iterator Pipeline
**Difficulty: Hard**

Design a text processing system using chained iterators for document analysis.

**Requirements:**
- Create `Document` class that can be tokenized into words, sentences, paragraphs
- Implement iterator pipeline: `TokenIterator` -> `FilterIterator` -> `TransformIterator`
- Support multiple tokenization strategies (whitespace, regex, NLP-based)
- Add text transformations: stemming, case normalization, stop word removal
- Implement `WindowIterator` that provides sliding windows of tokens
- Create `NGramIterator` for generating n-grams
- Support parallel processing with thread-safe iterators

**Test Cases:**
```java
Document doc = new Document("Natural language processing is fascinating!");
Iterator<String> tokens = doc.tokenIterator();
Iterator<String> filtered = doc.filteredIterator(token -> !isStopWord(token));
Iterator<List<String>> bigrams = doc.ngramIterator(2);
Iterator<List<String>> windows = doc.windowIterator(5);
```

## Problem 8: Event Stream Iterator with Time Windows
**Difficulty: Hard**

Implement an event streaming system with time-based iterators for real-time data processing.

**Requirements:**
- Create `Event` class with timestamp, type, data, and metadata
- Implement `EventStream` that handles continuous event flow
- Create time-window iterators: sliding window, tumbling window, session window
- Support event filtering, aggregation, and correlation across time windows
- Add late-arriving event handling and watermark-based processing
- Implement `ComplexEventIterator` for pattern matching across events
- Handle backpressure and memory management for high-throughput streams

**Test Cases:**
```java
EventStream stream = new EventStream();
stream.addEvent(new Event("click", timestamp, userData));
Iterator<Window<Event>> sliding = stream.slidingWindowIterator(Duration.minutes(5));
Iterator<Event> pattern = stream.patternIterator("click->view->purchase");
Iterator<AggregatedEvent> aggregated = stream.aggregatedIterator(Duration.minutes(1));
```

## Additional Challenges

### Performance Optimization Challenges:
1. **Memory-Efficient Iterator**: Design iterators that handle millions of elements without loading everything into memory
2. **Concurrent Iterator**: Create thread-safe iterators that allow multiple threads to iterate simultaneously
3. **Lazy Evaluation Iterator**: Implement iterators that compute elements on-demand for expensive operations

### Advanced Pattern Combinations:
1. **Iterator + Observer**: Create iterators that notify observers when certain elements are encountered
2. **Iterator + Command**: Implement iterators where each element represents a command to be executed
3. **Iterator + Strategy**: Design iterators that can switch between different iteration algorithms at runtime

### Real-World Scenarios:
1. **Log File Analyzer**: Create iterators for parsing and analyzing large log files with different formats
2. **CSV/JSON Parser**: Implement streaming parsers that use iterators for memory-efficient processing
3. **API Response Iterator**: Design iterators that handle paginated API responses automatically

## Testing Guidelines

For each problem, ensure your solution includes:

1. **Unit Tests**: Test basic functionality and edge cases
2. **Performance Tests**: Verify memory usage and execution time
3. **Thread Safety Tests**: If applicable, test concurrent access
4. **Error Handling Tests**: Test invalid inputs and error conditions
5. **Integration Tests**: Test iterator combinations and complex scenarios

## Evaluation Criteria

Your solutions will be evaluated based on:

1. **Correctness**: Does the iterator work as specified?
2. **Efficiency**: Optimal time and space complexity
3. **Robustness**: Proper error handling and edge case management
4. **Extensibility**: Easy to add new iterator types
5. **Code Quality**: Clean, readable, and well-documented code
6. **Design Patterns**: Proper implementation of Iterator Pattern principles