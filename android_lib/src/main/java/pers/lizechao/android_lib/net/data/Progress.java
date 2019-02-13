package pers.lizechao.android_lib.net.data;

/**
 * Created by Lzc on 2018/6/15 0015.
 */
public class Progress {
   public final long total;
   public long current;

   public Progress(long total, long current) {
      this.total = total;
      this.current = current;
   }

   @Override
   public String toString() {
      return current+"----"+total;
   }
}
