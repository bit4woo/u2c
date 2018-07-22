package burp;

import U2C.U2CTab;

public class BurpExtender implements IBurpExtender,IMessageEditorTabFactory
{
	private IBurpExtenderCallbacks callbacks;
	private IExtensionHelpers helpers;
	
	public String ExtenderName = "U2C v0.6 by bit4";
	public String github = "https://github.com/bit4woo/U2C";

	@Override
   public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
   {
		this.callbacks = callbacks;
		callbacks.printOutput(ExtenderName);
		callbacks.printOutput(github);
		helpers = callbacks.getHelpers();
		callbacks.setExtensionName(ExtenderName);
		callbacks.registerMessageEditorTabFactory(this);
   }
	
	@Override
	public IMessageEditorTab createNewInstance(IMessageEditorController controller, boolean editable) {
		return new U2CTab(controller, false, helpers, callbacks);
	}
}