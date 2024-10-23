package student.inti.RecipeLab.utility.gestures;

public interface AppItemTouchHelper {
  boolean onItemMoved(int startPosition, int endPosition);
  void onItemSwiped(int position);
}
