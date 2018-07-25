package U2C;

import java.io.IOException;
import java.io.InputStream;


import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

/**
* use ICU4J to detect the encode of byte[] data.
*
*/
public class EncodeDetector {

public static String getEncode(byte[] data){
   CharsetDetector detector = new CharsetDetector();
   detector.setText(data);
   CharsetMatch match = detector.detect();
   String encoding = match.getName();
   System.out.println("The Content in " + match.getName());
   CharsetMatch[] matches = detector.detectAll();
   System.out.println("All possibilities");
   for (CharsetMatch m : matches) {
    System.out.println("CharsetName:" + m.getName() + " Confidence:"
      + m.getConfidence());
   }
   return encoding;
}

public static String getEncode(InputStream data) throws IOException{
   CharsetDetector detector = new CharsetDetector();
   detector.setText(data);
   CharsetMatch match = detector.detect();
   String encoding = match.getName();
   System.out.println("The Content in " + match.getName());
   CharsetMatch[] matches = detector.detectAll();
   System.out.println("All possibilities");
   for (CharsetMatch m : matches) {
    System.out.println("CharsetName:" + m.getName() + " Confidence:"
      + m.getConfidence());
   }
   return encoding;
}
}
