import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Processing extends JPanel {
	private static final int default_icon_number=5;
	private static final int default_gifWidth=256;
	private static final int default_gifHeight=256;
	private static final int default_icon=50;
	private static final int default_icon_spacing=15;
	private static final int default_detail_width=360;
	
	private static final long serialVersionUID = 8771482360522538633L;
	private JLabel loadingGIF=new JLabel();
	private JPanel loadingPNL=new JPanel();
	private JPanel centerPNL=new JPanel();
	//Icons
	private JPanel iconsPNL=new JPanel();
	private JLabel iconsLBL[]=new JLabel[default_icon_number];
	//Details
	private JPanel detailPNL=new JPanel();
	private JLabel detailLBL[]=new JLabel[default_icon_number];
	
	public Processing(int width,int height,Color clr) {
		setLayout(null);
		setSize(width, height);
		setBackground(clr);
		settingLoading(width,height,clr);
		add(loadingPNL);
		//Color Invert=new Color(255-clr.getRed(),255-clr.getGreen(),255-clr.getBlue());
		//setTheIcons(Invert);
		//setTheDetails(Invert);
		setTheIcons(clr);
		setTheDetails(clr);
		//Center
		centerPNL.setLayout(null);
		centerPNL.setBackground(clr);
		centerPNL.add(detailPNL);
		centerPNL.add(iconsPNL);
		centerPNL.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
		centerPNL.setBounds((width-(default_detail_width+default_icon+30))/2,150, default_detail_width+default_icon+30+4, (default_icon_spacing+ default_icon)*default_icon_number+4);
		add(centerPNL);
		
		
	}
	private void setTheDetails(Color clr) {
		Font f = new Font("MS UI Gothic", Font.BOLD, 20);
		detailPNL.setSize(default_detail_width,(default_icon_spacing+ default_icon)*detailLBL.length);
		detailPNL.setBackground(clr);
		detailPNL.setLayout(null);
		for(int i=0;i<detailLBL.length;i++) {
			JLabel tmp;
			switch(i) {
			case 0:tmp=new JLabel("Generation:"); detailLBL[i]=new JLabel("0");break;
			case 1:tmp=new JLabel("Population:"); detailLBL[i]=new JLabel("500");break;
			case 2:tmp=new JLabel("CrossOver Method:"); detailLBL[i]=new JLabel("Besting 3rd rule");break;
			case 3:tmp=new JLabel("Mutation Rate:"); detailLBL[i]=new JLabel("12%");break;
			case 4:tmp=new JLabel("CrossOver Points:"); detailLBL[i]=new JLabel("End Of row in Gene");break;
			default:tmp=new JLabel("UnLabeled"); break;
			}
			tmp.setSize(default_detail_width/2,default_icon);
			tmp.setLocation(0,i*(default_icon+default_icon_spacing));
			//detailLBL[i]=new JLabel("Hello");
			detailLBL[i].setLocation(default_detail_width/2,i*(default_icon+default_icon_spacing));
			detailLBL[i].setSize( default_detail_width/2,default_icon);
			detailLBL[i].setFont(f);
			detailLBL[i].setForeground(Color.RED);
			tmp.setFont(f);
			detailPNL.add(tmp);
			detailPNL.add(detailLBL[i]);
		}
		
		detailPNL.setLocation(iconsPNL.getLocation().x+default_icon+30,iconsPNL.getLocation().y);
		
	}
	private void setTheIcons(Color clr) {
		iconsPNL.setSize(default_icon,(default_icon_spacing+ default_icon)*iconsLBL.length);
		iconsPNL.setBackground(clr);
		iconsPNL.setLayout(null);
		for(int i=0;i<iconsLBL.length;i++) {
			iconsLBL[i]=new JLabel();
			iconsLBL[i].setSize(default_icon, default_icon);
			iconsLBL[i].setIcon(new ImageIcon(getClass().getClassLoader().getResource("Images/"+i+".png")));
			iconsLBL[i].setLocation(0,i*(default_icon+default_icon_spacing));
			iconsPNL.add(iconsLBL[i]);
		}
		iconsPNL.setLocation(2,2);
		
	}
	private void settingLoading(int width,int height,Color clr) {
		loadingPNL.setLayout(null);
		loadingPNL.setBackground(clr);
		loadingGIF.setSize(default_gifWidth,default_gifHeight);
		ImageIcon ii = new ImageIcon(getClass().getClassLoader().getResource("Images/loading2.gif"));
		loadingGIF.setIcon(ii);
		loadingPNL.setSize(default_gifWidth,default_gifHeight);
		loadingPNL.add(loadingGIF);
		loadingPNL.setLocation((width-default_gifWidth)/2,height-default_gifHeight-50);
	}
	public JLabel getDetailLBL() {
		return detailLBL[0];
	}
}
