IGLogger
========

Class to help with adding logging function in smali output from 3rd party Android apps.

Usage
=====

Compile this to an APK, use APKTool to decompile, place the "iglogger.smali" in the
root of the application you want logging from (after you APKTool'ed it). Then to 
log out, simply add a line of Smali where you want to log out. The easiest case
is simple to add:

	invoke-static {}, Liglogger;->d()I 

Alternatively, you can log variables, but you do need to ensure you get the types
correct. Example, if v1 is already a string, use:

	invoke-static {v1}, Liglogger;->d(Ljava/lang/String;)I 

If you want a log "TAG" message other than the one below, each logging method also
as a method which will take a string as the first parameter (this matches the standard
android.util.Log calls), however, to ensure you don't overwrite a register already in use,
it is recommended you increase the "locals" count by 1 at the start of the method.

 	.locals 10  # previously was 9
    
	const-string v9, "!!!IGLOGGER - v1 array length : !!!"
	invoke-static {v9, v1}, Liglogger;->d(Ljava/lang/String;I)I 

This ensures you are not overwriting application data which may have already been in "v9". 
In the previous example, "v1" was an of type "int".

"Trace" methods are designed to work with the APKSmash script (injects logging
automatically based on Android API calls). Some of these can be useful for logging items
by hand as well. For example, APKSmash will inject the "trace_intent_sendactivity" method
and pass the Intent to it before the Activity is started. The code might look like this.

    invoke-static {v1}, Liglogger;->trace_intent_sendactivity(Landroid/content/Intent;)I
    invoke-virtual {v0, v1}, Landroid/app/Activity;->startActivity(Landroid/content/Intent;)V

The Intent trace loggers will attempt to parse the Intent in two ways. The first is a simple
call to toString (you might see this for the Intent in the logs already). The second way
is to export the proper "am" (Activity Manager) command to allow resending the Intent
from an adb shell (note: this is still a little buggy but should get you started. Watch
for error messages in the am command)
    

Tips for Errors 
===============

If you get a validation error such as:

	W/dalvikvm(12928): VFY: register1 v1 type 17, wanted ref
 
This means you got the type of "v1" wrong and it is of type "17". The look up for these
types is listed at the URL below (also marking these in the comments for the types supported
in this logging class)

http://source.android.com/tech/dalvik/dex-format.html

("Value Formats" table)

If logging looks strange or missing from Logcat in DDMS/Monitor, try "adb logcat". 

Version History
===============

v2.55 
 
 + Added printing of the line number to all trace functions. The new APKSmash script should insert line numbers, making this output more useful.
 
v2.50 
 
 + Added a lot more "trace" classes (for use mainly with APKSmash).
 + Handles more types of objects and parsing (Intents, SQL calls)
	