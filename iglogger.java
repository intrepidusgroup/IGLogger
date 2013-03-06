import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* 
 * Wrapper for debugging methods. 
 * I'm suggesting making this all "wtf" level, but feel free to change this :-)
 * 
 * =========== Usage ====================
 * 
 * Compile this to an APK, use APKTool to decompile, place the "iglogger.smali" in the
 * root of the application you want logging from (after you APKTool'ed it). Then to 
 * log out, simply add a line of Smali where you want to log out. The easiest case
 * is simple to add:
 * 
 * invoke-static {}, Liglogger;->d()I 
 * 
 * Alternatively, you can log variables, but you do need to ensure you get the types
 * correct. Example, if v1 is already a string, use:
 * 
 * invoke-static {v1}, Liglogger;->d(Ljava/lang/String;)I 
 * 
 * If you want a log "TAG" message other than the one below, each logging method also
 * as a method which will take a string as the first parameter (this matches the standard
 * android.util.Log calls), however, to ensure you don't overwrite a register already in use,
 * it is recommended you increase the "locals" count by 1 at the start of the method.
 * 
 *  .locals 10  # previously was 9
 *     
 * 	const-string v9, "!!!IGLOGGER - v1 array length : !!!"
 *	invoke-static {v9, v1}, Liglogger;->d(Ljava/lang/String;I)I 
 *
 * This ensures you are not overwriting application data which may have already been in "v9". 
 * In the previous example, "v1" was an of type "int".
 * 
 * 
 * =========== Tips for Errors ====================
 * 
 * If you get a validation error such as:
 *  W/dalvikvm(12928): VFY: register1 v1 type 17, wanted ref
 * This means you got the type of "v1" wrong and it is of type "17". The look up for these
 * types is listed at the URL below (also marking these in the comments for the types supported
 * in this logging class)
 * 
 * http://source.android.com/tech/dalvik/dex-format.html
 * ("Value Formats" table)
 * 
 */

public class iglogger {
	// Change this as needed. This is the default log message string
	// In logcat, set your filter for "tag:!!!" to view the messages from this class
	private static String LOG_TAG = "!!! IGLogger";
	private static String TRACE_TAG = "!!! IGTraceLogger";
	private static String VERSION = "IGLogger 2.01 - 02/07/2013";
	
	 /* Default Case
	  *  - Use this call to just print you where here in a method
	  * 
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {}, Liglogger;->d()I  
	  * 
	  */	
	 static public int d() {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();
		 
		 // Unfortuantely we cant get the details for an overloaded method
		 // this would be helpful for obfuscated classes
		 logtag = logtag + "->" + t.getStackTrace()[1].getMethodName();
		 
		 // Line number so far as "-1" but this might work on some apps. 
		 // We'll leave it for now.
		 logtag = logtag + " Line " + t.getStackTrace()[1].getLineNumber();
	     return android.util.Log.wtf(logtag, logtag);
	 }

		
	 /* Trace Method
	  *  - Use this call to just print you where here in a method
	  *  - Main use is for putting this at the start of each method to help trace an obfuscated app
	  * 
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {}, Liglogger;->trace_method()I  
	  * 
	  */	
	 static public int trace_method() {
		 Throwable t = new Throwable();
		 String logtag = TRACE_TAG + " in Method: " + t.getStackTrace()[1].getClassName();
		 
		 // Unfortuantely we cant get the details for an overloaded method
		 // this would be helpful for obfuscated classes
		 logtag = logtag + "->" + t.getStackTrace()[1].getMethodName();
		 
		 // Line number so far as "-1" but this might work on some apps. 
		 // We'll leave it for now.
		 logtag = logtag + " Line " + t.getStackTrace()[1].getLineNumber();
	     return android.util.Log.wtf(logtag, logtag);
	 }
	 
	 /* Trace Intent
	  *   Logs out a string being used to INIT an intent. Log right before or after this line
	  *   invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V
	  *   
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v1}, Liglogger;->trace_intent(Ljava/lang/String;)I  
	  * 
	  */ 	 	 
	 static public int trace_intent(String m) {
		 Throwable t = new Throwable();
		 String logtag = TRACE_TAG + " creating INTENT: " + t.getStackTrace()[1].getClassName();		  
	     return android.util.Log.wtf(logtag, notEmpty(m));
	 }	 

	 /* Trace String Compare
	  *   Logs out a string being compared. Log right before or after this line
	  *   invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
	  *   
	  *   There is a known issue with this and Android 2.3 (and lower?) where this seems to go unlogged
	  *   Issue maybe with the throwable object and some classes. Logging can still work, but you wont
	  *   be able to automatically get the class/method name of the caller.
	  *   
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v1, v2}, Liglogger;->trace_stringcompare(Ljava/lang/String;Ljava/lang/String;)I  
	  * 
	  */ 	 	 
	 static public int trace_stringcompare(String m, String n) {
		 Throwable t = new Throwable();
		 String logtag = TRACE_TAG + " string compare: " + t.getStackTrace()[1].getClassName();		  
	     return android.util.Log.wtf(logtag, notEmpty(m) + " == " + notEmpty(n));
	 }	
	 
	 /* Special Case: stacktrace
	  *  - Use this print a stack trace to the log when you hit this code, but no 
	  *  error is thrown. Useful in obfuscated classes where its hard to tell what
	  *  called into your current method.
	  * 
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {}, Liglogger;->stacktrace()I  
	  * 
	  */	
	 static public int stacktrace() {
		 Throwable t = new Throwable();
		 for(int x = 1; x < t.getStackTrace().length; x++){
			String logtag = LOG_TAG + ": STACKTRACE: " + x;
		 	android.util.Log.wtf(logtag, t.getStackTrace()[x].getClassName());
		 }
	     return 1;
	 }
	 
	 /* String Case
	  *   Smali Type Value: 0x17
	  *   
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v0}, Liglogger;->d(Ljava/lang/String;)I  
	  * 
	  */ 	 	 
	 static public int d(String m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();		  
	     return android.util.Log.wtf(logtag, notEmpty(m));
	 }
	 
	 static public int d(String t, String m) {
	     //writeLog("D", t, m);
	     return android.util.Log.wtf(t, notEmpty(m));
	 }


	 
	 /* Boolean Case
	  *   Smali Type Value: 0x1f
	  * 
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v0}, Liglogger;->d(Ljava/lang/Boolean;)I  
	  * 
	  */ 		 
	 static public int d(Boolean m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();			 
	     return android.util.Log.wtf(logtag, notEmpty(m.toString()));
	 }	
	 
	 static public int d(String t, Boolean m) {
	     return android.util.Log.wtf(t, notEmpty(m.toString()));
	 }

	 /* Char Case
	  *   Smali Type Value: 0x17
	  * 
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v0}, Liglogger;->d(C)I 
	  * 
	  */ 		 
	 static public int d(char m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();	
	     return android.util.Log.wtf(logtag, notEmpty(Character.toString(m)));
	 }	
	 
	 static public int d(String t, char m) {
	     return android.util.Log.wtf(t, notEmpty(Character.toString(m)));
	 }	 
	 
	 /* Int Case
	  *   Smali Type Value: 0x04
	  *   
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v0}, Liglogger;->d(I)I 
	  * 
	  */ 
	 static public int d(int m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();			 
		 String s = notEmpty(Integer.toString(m));
	     return android.util.Log.wtf(logtag, s);
	 }

	 static public int d(String t, int m) {
		 String s = notEmpty(Integer.toString(m));
	     return android.util.Log.wtf(t, s);
	 }
	 
	 /* Long Case
	  *   Smali Type Value: 0x06
	  *   
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v0}, Liglogger;->d(J)I
	  * 
	  */	 
	 static public int d(long m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();			 
		 String s = notEmpty(Long.toString(m));
	     return android.util.Log.wtf(logtag, s);
	 }

	 static public int d(String t, long m) {
		 String s = notEmpty(Long.toString(m));
	     return android.util.Log.wtf(t, s);
	 }	 

	 /* Float Case
	  *   Smali Type Value: 0x10
	  * 
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v0}, Liglogger;->d(F)I
	  * 
	  */ 		 
	 static public int d(float m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();	
	     return android.util.Log.wtf(logtag, Float.toString(m));
	 }	
	 
	 static public int d(String t, float m) {
	     return android.util.Log.wtf(t, Float.toString(m));
	 }	
	 
	 
	 /* Double Case
	  *   Smali Type Value: 0x11
	  * 
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v0}, Liglogger;->d(D)I
	  * 
	  */ 		 
	 static public int d(double m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();	
	     return android.util.Log.wtf(logtag, Double.toString(m));
	 }	
	 
	 static public int d(String t, double m) {
	     return android.util.Log.wtf(t, Double.toString(m));
	 }

	 
	 /* Short Case
	  *   Smali Type Value: 0x02
	  * 
	  * *** SMALI CODE TO ADD ***
	  * 
	  * 
	  */ 		 
	 static public int d(short m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();	
	     return android.util.Log.wtf(logtag, Short.toString(m));
	 }	
	 
	 static public int d(String t, short m) {
	     return android.util.Log.wtf(t, Short.toString(m));
	 }

	 /* Byte Case
	  *   Smali Type Value: 0x00
	  * 
	  * *** SMALI CODE TO ADD ***
	  * 
	  * 
	  */ 		 
	 static public int d(byte m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();	
	     return android.util.Log.wtf(logtag, Byte.toString(m));
	 }	
	 
	 static public int d(String t, byte m) {
	     return android.util.Log.wtf(t, Byte.toString(m));
	 }
	 
	 /* String Array Case
	  *   Smali Type Value: 0x1c
	  *   
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v0}, Liglogger;->d([Ljava/lang/String;)I
	  * 
	  */
	 static public int d(String []m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();	
		 
		 for(int x=0; x < m.length; x++){
			 String currentlogtag = logtag + " - array " + x;
			 android.util.Log.wtf(currentlogtag, notEmpty(m[x]));
		 }
	     return 1;
	 }	

	 static public int d(String t, String []m) {
		 for(int x=0; x < m.length; x++){
			 String logtag = t + " - array " + x;
			 android.util.Log.wtf(logtag, notEmpty(m[x]));
		 }
	     return 1;
	 }	 
	 

	 /* Collection of Strings Case
	  *   Smali Type Value: 0x1d
	  *   
	  *  Hint, look at the ".signature" of the method in your decompiled application to
	  *  see if your collection is a type <String>. You may need to add a new method here
	  *  if it's not
	  *  
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v0}, Liglogger;->d(Ljava/util/Collection;)I
	  * 
	  */	 
	 static public int d(Collection<String> m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();	
		 
		 int x = 0;
		 for (Iterator<String> iterator = m.iterator(); iterator.hasNext();) {
			 String currentlogtag = logtag + " - collection " + x++;
			 android.util.Log.wtf(currentlogtag, notEmpty(iterator.next()));
			 }		 
	     return 1;
	 }	
	 
	 static public int d(String t, Collection<String> m) {
		 int x = 0;
		 for (Iterator<String> iterator = m.iterator(); iterator.hasNext();) {
			 String logtag = t + " - collection " + x++;
			 android.util.Log.wtf(logtag, notEmpty(iterator.next()));
			 }		 
	     return 1;
	 }	
	 
	 
	 /* Object Case
	  *  
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v0}, Liglogger;->d(Ljava/lang/Object;)I
	  * 
	  */
	 static public int d(Object m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();	
		 
		 try{
			 android.util.Log.wtf(logtag, m.toString());
		 } catch (Exception e) {
			 android.util.Log.wtf(logtag, "Error, could not convert to string");
		 }
	     return 1;
	 }	 

	 static public int d(String t, Object m) {
		 try{
			 android.util.Log.wtf(t, m.toString());
		 } catch (Exception e) {
			 android.util.Log.wtf(t, "Error, could not convert to string");
		 }
	     return 1;
	 }
	 
	
	 /* Byte Array Case
	  *   - will print the byte array as a hex string
	  *   
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v0}, Liglogger;->d([B)I
	  * 
	  */	 
	 static public int d(byte[] m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName() + " - byte[] in Hex: ";			 
	     return android.util.Log.wtf(logtag, notEmpty(ListToHex(m)));
	 }

	 static public int d(String t, byte[] m) {
	     return android.util.Log.wtf(t, notEmpty(ListToHex(m)));
	 }		 
	 
	 
	 /* URL Case
	  *  
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v0}, Liglogger;->d(Ljava/net/URL;)I
	  * 
	  */
	 static public int d(URL m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": " + t.getStackTrace()[1].getClassName();	
		 
		 try{
			 android.util.Log.wtf(logtag, m.toString());
		 } catch (Exception e) {
			 android.util.Log.wtf(logtag, "Error, could not convert URL to string");
		 }
	     return 1;
	 }

	 static public int d(String t, URL m) {
		 try{
			 android.util.Log.wtf(t, m.toString());
		 } catch (Exception e) {
			 android.util.Log.wtf(t, "Error, could not convert URL to string");
		 }
	     return 1;
	 }	 

	 /* URLConnection Case
	  *  
	  * *** SMALI CODE TO ADD ***
	  * invoke-static {v0}, Liglogger;->d(Ljava/net/HttpURLConnection;)I
	  * 
	  */
	 static public int d(HttpURLConnection m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": URLConnection :" + t.getStackTrace()[1].getClassName();
		 return d(logtag,(URLConnection) m);
	 }	 

	 static public int d(String logtag, HttpURLConnection m) {
		 return d(notEmpty(logtag),(URLConnection) m);
	 }
	 
	 static public int d(URLConnection m) {
		 Throwable t = new Throwable();
		 String logtag = LOG_TAG + ": URLConnection :" + t.getStackTrace()[1].getClassName();
		 return d(logtag,m);
	 }
	 
	 static public int d(String logtag, URLConnection m) {
		 
		 try{
			 android.util.Log.wtf(logtag, m.toString());
			 
			 //Print Headers
			 Map<String, List<String>> headers = m.getRequestProperties();
			 for (String key : headers.keySet()) {
				 // The header may appear more than once, print all the things
				 List<String> values = headers.get(key);
				 Iterator<String> itr = values.iterator();
			     while(itr.hasNext()) {
			    	 android.util.Log.wtf(logtag + " Header: " + key, notEmpty(itr.next()));
			       }
			 }
			 
		 } catch (Exception e) {
			 android.util.Log.wtf(logtag, "Error, could not convert URLConnection to string");
		 }
	     return 1;
	 }
	 
	//Utility Classes
	public static String ListToHex(byte[] data){
		String string = "";
        for (int i=0;i<data.length;i++) {
			byte b = data[i];
			StringBuffer s = new StringBuffer(Integer.toHexString((b >= 0) ? b : 256 + b));
			if(s.length() == 1) s.insert(0,'0');
			string = string + s;
        }
        return string;
	}

	public static byte[] HexToList(String s){
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
			}
		return data;
	}	
	
	public static String notEmpty(String s){
		if(s == null || s.length() < 1){
			return "<empty value>";
		}
		return s;
	}
}
