package mighty.traversing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;

public class UniqueQueue<T> {
  private final Deque<T> queue;
  private final HashSet<T> visited;

  public UniqueQueue() {
    queue = new ArrayDeque<>();
    visited = new HashSet<>();
  }

  boolean isEmpty() {
    return queue.isEmpty();
  }

  public T removeNext() {
    return queue.remove();
  }

  public void add(T item) {
    if (hasVisited(item)) {
      throw new RuntimeException();
    }

    visited.add(item);
    queue.addLast(item);
  }

  public boolean hasVisited(T item) {
    return visited.contains(item);
  }
}
