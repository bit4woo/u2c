package U2C;

import java.awt.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import burp.IMessageEditorController;
import burp.IMessageEditorTab;
import burp.ITextEditor;

public class U2CTab implements IMessageEditorTab
{
    private ITextEditor txtInput;
    private IExtensionHelpers helpers;
    public U2CTab(IMessageEditorController controller, boolean editable, IExtensionHelpers helpers, IBurpExtenderCallbacks callbacks)
    {
        txtInput = callbacks.createTextEditor();
        txtInput.setEditable(editable);
        this.helpers = helpers;
    }

    @Override
    public String getTabCaption()
    {
        return "U2C";
    }

    @Override
    public Component getUiComponent()
    {
        return txtInput.getComponent();
    }

    @Override
    public boolean isEnabled(byte[] content, boolean isRequest)
    {
    	String resp= new String(content);
    	if(!isRequest && needtoconvert(resp)) {
    		return true;
    	}else {
    		return false;
    	}
    	
    }

    @Override
    public void setMessage(byte[] content, boolean isRequest)
    {
    	String resp= new String(content);
    	String UnicodeResp = "";
    	if(needtoconvert(resp)) {
    		UnicodeResp = Unicode.unicodeDecode(resp);
    	}
    	txtInput.setText(UnicodeResp.getBytes());
    }

    @Override
    public byte[] getMessage()
    {
    	byte[] text = txtInput.getText();
        return text;
    }

    @Override
    public boolean isModified()
    {
        return txtInput.isTextModified();
    }

    @Override
    public byte[] getSelectedData()
    {
        return txtInput.getSelectedText();
    }       
    
    
    
    public static boolean needtoconvert(String str) {
    	Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
    	Matcher matcher = pattern.matcher(str.toLowerCase());
    	
    	if (matcher.find() ){
    		String found = matcher.group();
    		//£¡@#£¤%¡­¡­&*£¨£©¡ª¡ª-=£¬¡££»£º¡°¡®{}¡¾¡¿+
    		String chineseCharacter = "\\uff01\\u0040\\u0023\\uffe5\\u0025\\u2026\\u2026\\u0026\\u002a\\uff08\\uff09\\u2014\\u2014\\u002d\\u003d\\uff0c\\u3002\\uff1b\\uff1a\\u201c\\u2018\\u007b\\u007d\\u3010\\u3011\\u002b";
    		if (("\\u4e00").compareTo(found)<= 0 && found.compareTo("\\u9fa5")<=0)
    			return true;
    		else if(chineseCharacter.contains(found)){
    			return true;
    		}else{
    			return false;
    		}
    	}else {
    		return false;
    	}
    }
}