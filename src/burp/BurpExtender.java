package burp;

import java.io.PrintWriter;

import U2C.U2CTab;

public class BurpExtender implements IBurpExtender,IMessageEditorTabFactory
{
	private static IBurpExtenderCallbacks callbacks;
	private IExtensionHelpers helpers;
	
	public static String Version = bsh.This.class.getPackage().getImplementationVersion();
	private static PrintWriter stdout;
	private static PrintWriter stderr;
	public static String ExtensionName = "U2C";
	public static String Author = "by bit4woo";
	public String github = "https://github.com/bit4woo/U2C";

	@Override
   public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
   {
		BurpExtender.callbacks = callbacks;
		callbacks.printOutput(getFullExtensionName());
		callbacks.printOutput(github);
		helpers = callbacks.getHelpers();
		callbacks.setExtensionName(getFullExtensionName());
		callbacks.registerMessageEditorTabFactory(this);
   }
	
	@Override
	public IMessageEditorTab createNewInstance(IMessageEditorController controller, boolean editable) {
		return new U2CTab(controller, false, helpers, callbacks);
	}

	private static void flushStd(){
		try{
			stdout = new PrintWriter(callbacks.getStdout(), true);
			stderr = new PrintWriter(callbacks.getStderr(), true);
		}catch (Exception e){
			stdout = new PrintWriter(System.out, true);
			stderr = new PrintWriter(System.out, true);
		}
	}

	public static PrintWriter getStdout() {
		flushStd();//不同的时候调用这个参数，可能得到不同的值
		return stdout;
	}

	public static PrintWriter getStderr() {
		flushStd();
		return stderr;
	}

	//name+version+author
	public static String getFullExtensionName(){
		return ExtensionName+" "+Version+" "+Author;
	}

	public static IBurpExtenderCallbacks getCallbacks() {
		return callbacks;
	}
}