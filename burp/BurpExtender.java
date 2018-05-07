package burp;
 
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.URI;
import U2C.Unicode; //unicode解码的实现类
import U2C.GUI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class BurpExtender implements IBurpExtender, IHttpListener, ITab, IContextMenuFactory 
{
   private IBurpExtenderCallbacks callbacks;
   private IExtensionHelpers helpers;
   private PrintWriter stdout;//用于输出，主要用于代码调试
   
   
	public JFrame frame;
	public String ExtenderName = "U2C v0.3 by bit4";
	public String github = "https://github.com/bit4woo/U2C";
	public JLabel lblNewLabel_1;
	public JCheckBox chckbx_proxy;
	public JCheckBox chckbx_repeater;
	public JCheckBox chckbx_intruder;
	private JPanel content_panel;
	private JCheckBox chckbx_display;
	private JCheckBox chckbx_scanner;
	
   
   // implement IBurpExtender
   @Override
   public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
   {
	   stdout = new PrintWriter(callbacks.getStdout(), true);
	   stdout.println(ExtenderName);
	   stdout.println(github);
        this.callbacks = callbacks;
        helpers = callbacks.getHelpers();
        callbacks.setExtensionName(ExtenderName);//插件名称
        callbacks.registerHttpListener(this); //如果没有注册，下面的processHttpMessage方法是不会生效的。处理请求和响应包的插件，这是必要的。
        callbacks.registerContextMenuFactory(this);
        addMenuTab();
        //callbacks.customizeUiComponent(component);
   }

@Override
   public void processHttpMessage(int toolFlag,boolean messageIsRequest,IHttpRequestResponse messageInfo)
   { 
      if (toolFlag == (toolFlag&checkEnabledFor())){ //不同的toolflag代表了不同的burp组件。参考链接https://portswigger.net/burp/extender/api/constant-values.html#burp.IBurpExtenderCallbacks
         if (messageIsRequest){ //对请求包进行处理
         
         }
         else{
            //处理返回，响应包
            IResponseInfo analyzedResponse = helpers.analyzeResponse(messageInfo.getResponse()); //getResponse的返回类型是Byte[]
            List<String>header= analyzedResponse.getHeaders();
            short statusCode = analyzedResponse.getStatusCode();
            int bodyOffset = analyzedResponse.getBodyOffset();
           try{
              String resp= new String(messageInfo.getResponse());//Byte[] to String
                  String body = resp.substring(bodyOffset);
                  boolean utf8 = false;
                  if(header.indexOf("Content-Type") >=0) {
                	  utf8 = header.get(header.indexOf("Content-Type")).toLowerCase().contains("charset=utf-8");
                  }
                  if(needtoconvert(body)) {//utf8 ?
                      body = body.replace("\"","\\\"");
                      String UnicodeBody = Unicode.unicodeDecode(body);
                      String newBody = new String();
                      if(displayConvertedOnly()){
                    	  newBody = UnicodeBody;
                      }else {
                    	newBody = body +"\r\n-----above is origin-----below is changed-----by bit4-----\r\n" +UnicodeBody; //将新的解密后的body附到旧的body后面
                      }
                      
                      byte[] bodybyte = newBody.getBytes();
                      messageInfo.setResponse(helpers.buildHttpMessage(header, bodybyte));
                      //messageInfo.setHighlight("blue");
                  }
           }catch(Exception e){
              stdout.println(e);
           }
         }
      }
   }
	

public void changeDisplay() {
	//any method to change the display config?
}
   
public static boolean needtoconvert(String str) {
	Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
	Matcher matcher = pattern.matcher(str);
	
	if (matcher.find() ){
		String found = matcher.group();
		if (("\\u4e00").compareTo(found)<= 0 && found.compareTo("\\u9fa5")<=0)
			return true;
		else {
			return false;
		}
	}else {
		return false;
	}
}
   
private void GUI() {
	frame = new JFrame();
	frame.setBounds(100, 100, 450, 300);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	
	content_panel = new JPanel();
	content_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
	content_panel.setLayout(new BorderLayout(0, 0));
	
	frame.getContentPane().add(content_panel, BorderLayout.CENTER);
	
	JPanel panel = new JPanel();
	content_panel.add(panel, BorderLayout.NORTH);
	FlowLayout fl_panel = (FlowLayout) panel.getLayout();
	fl_panel.setAlignment(FlowLayout.LEFT);
	panel.setBorder(new LineBorder(new Color(0, 0, 0)));
	
	JLabel lblNewLabel = new JLabel("Enable for : ");
	panel.add(lblNewLabel);
	
	chckbx_proxy = new JCheckBox("Proxy");
	//chckbx_proxy.setSelected(true);
	panel.add(chckbx_proxy);
	
	chckbx_repeater = new JCheckBox("Repeater");
	panel.add(chckbx_repeater);
	
	chckbx_intruder = new JCheckBox("Intruder");
	panel.add(chckbx_intruder);
	
	chckbx_scanner = new JCheckBox("Scanner");
	panel.add(chckbx_scanner);
	
	JLabel lblNewLabel_display = new JLabel("|");
	panel.add(lblNewLabel_display);
	
	chckbx_display = new JCheckBox("Only Display Converted Body");
	chckbx_display.setSelected(true);
	panel.add(chckbx_display);
			
	JPanel panel_1 = new JPanel();
	content_panel.add(panel_1, BorderLayout.SOUTH);
	panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
	FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
	flowLayout.setAlignment(FlowLayout.LEFT);
	
			
	lblNewLabel_1 = new JLabel("    "+github);
	lblNewLabel_1.setFont(new Font("", Font.BOLD, 12));
	lblNewLabel_1.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			try {
				URI uri = new URI(github);
				Desktop desktop = Desktop.getDesktop();
				if(Desktop.isDesktopSupported()&&desktop.isSupported(Desktop.Action.BROWSE)){
					desktop.browse(uri);
				}
			} catch (Exception e2) {
				// TODO: handle exception
				//callbacks.printError(e2.getMessage());
			}
			
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			lblNewLabel_1.setForeground(Color.BLUE);
		}
		@Override
		public void mouseExited(MouseEvent e) {
			lblNewLabel_1.setForeground(Color.BLACK);
		}
	});
	panel_1.add(lblNewLabel_1);
	
	JPanel panel_2 = new JPanel();
	content_panel.add(panel_2, BorderLayout.CENTER);
	GridBagLayout gbl_panel_2 = new GridBagLayout();
	gbl_panel_2.columnWidths = new int[]{660, 0};
	gbl_panel_2.rowHeights = new int[]{23, 23, 15, 0, 0, 0, 0, 0};
	gbl_panel_2.columnWeights = new double[]{0.0, Double.MIN_VALUE};
	gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
	panel_2.setLayout(gbl_panel_2);
	
	Label label = new Label("To display chinese correctly, you should do the following config: ");
	label.setFont(new Font("Dialog", Font.PLAIN, 14));
	GridBagConstraints gbc_label = new GridBagConstraints();
	gbc_label.anchor = GridBagConstraints.NORTHWEST;
	gbc_label.insets = new Insets(0, 0, 5, 0);
	gbc_label.gridx = 0;
	gbc_label.gridy = 0;
	panel_2.add(label, gbc_label);
	Label label1 = new Label("1. User options-->Display-->HTTP Message Display-->Change font, select a font that support chinese,eg:Microsoft Yahei.");
	label1.setFont(new Font("Dialog", Font.PLAIN, 14));
	GridBagConstraints gbc_label1 = new GridBagConstraints();
	gbc_label1.anchor = GridBagConstraints.NORTHWEST;
	gbc_label1.insets = new Insets(0, 0, 5, 0);
	gbc_label1.gridx = 0;
	gbc_label1.gridy = 1;
	panel_2.add(label1, gbc_label1);
	
	JLabel Label2 = new JLabel("2. \"Character Sets\" should be set to \"Use the platorm default\" or \"UTF-8\" commonly.");
	Label2.setFont(new Font("Dialog", Font.PLAIN, 14));
	GridBagConstraints gbc_Label2 = new GridBagConstraints();
	gbc_Label2.anchor = GridBagConstraints.NORTHWEST;
	gbc_Label2.insets = new Insets(0, 0, 5, 0);
	gbc_Label2.gridx = 0;
	gbc_Label2.gridy = 2;
	panel_2.add(Label2, gbc_Label2);
	
	JLabel lbllike = new JLabel("if you like this extender, Please give me a star on github. thanks! any issue or suggestion also appreciated!");
	lbllike.setFont(new Font("Dialog", Font.PLAIN, 14));
	GridBagConstraints gbc_lbllike = new GridBagConstraints();
	gbc_lbllike.anchor = GridBagConstraints.NORTHWEST;
	gbc_lbllike.gridx = 0;
	gbc_lbllike.gridy = 6;
	panel_2.add(lbllike, gbc_lbllike);
}



public int checkEnabledFor(){
	//get values that should enable this extender for which Component.
	int status = 0;
	if (chckbx_intruder.isSelected()){
		status += 32;
	}
	if(chckbx_proxy.isSelected()){
		status += 4;
	}
	if(chckbx_repeater.isSelected()){
		status += 64;
	}
	return status;
}

public boolean displayConvertedOnly() {
	return chckbx_display.isSelected();
}


 //以下是各种burp必须的方法 --start
   
   public void addMenuTab()
   {
     SwingUtilities.invokeLater(new Runnable()
     {
       public void run()
       {
    	 BurpExtender.this.GUI();
         BurpExtender.this.callbacks.addSuiteTab(BurpExtender.this); //这里的BurpExtender.this实质是指ITab对象，也就是getUiComponent()中的contentPane.这个参数由CGUI()函数初始化。
         //如果这里报java.lang.NullPointerException: Component cannot be null 错误，需要排查contentPane的初始化是否正确。
       }
     });
   }
   
//ITab必须实现的两个方法
	@Override
	public String getTabCaption() {
		// TODO Auto-generated method stub
		return ("U2C");
	}
	@Override
	public Component getUiComponent() {
		// TODO Auto-generated method stub
		return this.content_panel;
	}
	//ITab必须实现的两个方法

	@Override
	public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//IContextMenuFactory 必须实现的方法
	//各种burp必须的方法 --end
}