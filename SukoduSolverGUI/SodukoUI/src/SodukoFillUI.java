import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Essintials.GA;

import javax.swing.JTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SodukoFillUI extends JFrame {

	/**
	 * 
	 */
	private static final int default_cellSpacing=5;
	private static final int default_panelSpacing=15;
	private static final int default_cellSize=50;
	private static final int default_btnWideth=100;
	private static final int default_btnHeight=30;
	private static final long serialVersionUID = 1L;
	//Border Color and status
	private static final int gray=0;
	private static final int red=1;
	private static final int green=2;
	private static final int yellow=3;
	//Items
	private Color ourbgColor=Color.WHITE;
	private JPanel contentPane;
	private JPanel pnlSqrs[]=new JPanel[9];
	private JTextField cells[][]=new JTextField[9][9];//Each Row is a Sqrt Panel
	private int cellsStyle[][]=new int[9][9];
	private int info_sudoku[][]=new int[9][9];
	private SodukoFillUI thisFrame;
	private  GA ga = new GA();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SodukoFillUI frame = new SodukoFillUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SodukoFillUI() {
		thisFrame=this;
		setResizable(false);
		setSize(800,800);
		setLocationRelativeTo ( null );
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		//getContentPane().setLayout(null);
		getContentPane().setBackground(ourbgColor);
		settingOurSodukoView();
		JPanel tmp=new JPanel();
		addAllPanels(tmp);
		tmp.setLocation(400-((3*(3*default_cellSize+2*default_cellSpacing)+2*default_panelSpacing)/2)-10, 400-((3*(3*default_cellSize+2*default_cellSpacing)+2*default_panelSpacing)/2)-30);
		getContentPane().add(tmp);
		//Buttons Panel
		JPanel btmpnl=new JPanel();
		addButtonsWithPanel(btmpnl,800-(tmp.getY()+tmp.getHeight()),tmp);
		btmpnl.setLocation(400-((3*(3*default_cellSize+2*default_cellSpacing)+2*default_panelSpacing)/2)-10, (400-((3*(3*default_cellSize+2*default_cellSpacing)+2*default_panelSpacing)/2)-30)+tmp.getHeight());
		getContentPane().add(btmpnl);
		
		
	}
	private void addButtonsWithPanel(JPanel tmp,int leftOver,JPanel showWarn) {
		JButton confirmbtn=new JButton("Confirm");
		JButton resetbtn=new JButton("Reset");
		tmp.setLayout(null);
		tmp.setBackground(ourbgColor);
		tmp.setSize(3*(3*default_cellSize+2*default_cellSpacing)+2*default_panelSpacing,leftOver);
		confirmbtn.setBounds(default_panelSpacing+default_cellSpacing,leftOver/2-default_btnHeight,
				default_btnWideth, default_btnHeight);
		resetbtn.setBounds(tmp.getWidth()-(default_panelSpacing+default_cellSpacing+default_btnWideth),leftOver/2-default_btnHeight,
				default_btnWideth, default_btnHeight);
		//Functions
		confirmbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==confirmbtn) {
					int cnt=0;
					boolean flag=true;
					for(int i=0;i<9;i++) {
						for(int j=0;j<9;j++) {
							
							if(cellsStyle[i][j]==1 || cellsStyle[i][j]==3) {
								flag=false;
								break;
							}else if(!legalSquare(i,j)) {
								cellsStyle[i][j]=yellow;
								cells[i][j].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
								JOptionPane.showMessageDialog(showWarn,"Same Value in Square","WARNING.",JOptionPane.WARNING_MESSAGE);
								return;	
							}else if(!legalRow(i,j)) {
								cellsStyle[i][j]=yellow;
								cells[i][j].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
								JOptionPane.showMessageDialog(showWarn,"Same Value in Row","WARNING.",JOptionPane.WARNING_MESSAGE);
								return;	
							}else if(!legalColumn(j, i)) {
								cellsStyle[i][j]=yellow;
								cells[i][j].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
								JOptionPane.showMessageDialog(showWarn,"Same Value in Column","WARNING.",JOptionPane.WARNING_MESSAGE);
								return;	
							}else if(info_sudoku[i][j]!=0){
								cnt++;
							}
						}
					}
					if(!flag) {
						JOptionPane.showMessageDialog(showWarn,
							    "Illegal values in fields!",
							    "WARNING.",
							    JOptionPane.WARNING_MESSAGE);
					}else if(cnt<17){
						JOptionPane.showMessageDialog(showWarn,
							    "At Least you need 17 clues",
							    "GUIDE",
							    JOptionPane.ERROR_MESSAGE);
					}else {
						confirmbtn.setEnabled(false);
						resetbtn.setEnabled(false);
						//All Values Are Okay then disable Sudoku
						for(int i=0;i<9;i++) {
							for(int j=0;j<9;j++) {
								cells[i][j].setEditable(false);
							}
						}
						//Set Values right order
						int[][] sudoku_info_draft=new int[9][9];
						int rowInd=0;
						for(int i=0;i<9;i++) {
							int clmInd=0;
							for(int j=((int)i/3)*3;j<((int)i/3)*3+3;j++) {
								for(int k=(i%3)*3;k<((i%3)+1)*3;k++) {
									sudoku_info_draft[rowInd][clmInd]=info_sudoku[j][k];
									clmInd++;
								}
								
							}
							rowInd++;
						}
						//Run our code for GA
						Thread thread = new Thread(){
						    public void run(){
						    	   int mutation_rate=120;
								   ArrayList<Integer> crossover_points=new ArrayList<Integer>();
								   //Smart GA (cross point is +1)
								   int crossLimit=0;
								   for(int i=0;i<8;i++) {
									   for(int j=0;j<9;j++) {
										   if(sudoku_info_draft[i][j]==0) {
											   crossLimit++;
										   }
									   }
									   crossover_points.add(crossLimit);
								   }
								   int population_size=500;
								   if(ga.sudokuGenericAlgorithm(sudoku_info_draft, mutation_rate, crossover_points, population_size)) {
									   ga.printSolution(ga.getPopGA().getInfo_sudoku(),ga.getTheSolution());
									   System.out.println("Legal: "+ga.getTheSolution().getLegal()+"\nIlLegal: "+ga.getTheSolution().getIllegal());
									   int offset=0;
									   for(int i=0;i<9;i++) {
										   for(int j=0;j<9;j++) {
											   if(ga.getPopGA().getInfo_sudoku()[i][j]==0) {
												   thisFrame.cells[(((int)i/3)*3)+((int)j/3)][((i%3)*3)+(j%3)]
														   .setText(""+ga.getTheSolution().getValueAt(offset));
												   offset++;
											   }
											  
										   }
									   }
									   resetbtn.setEnabled(true);
								   }else {
									   System.out.println("Error Accuired!");
								   }
						    }
						  };
						  thread.start();
						  //GUI Thread Start in Parrelle
						  Thread threadProcessGUI = new Thread(){
								 public void run(){
									 try {
											JFrame frame=new JFrame();
											frame.setSize(800, 800);
											frame.setLocationRelativeTo(null);
											frame.getContentPane().setLayout(null);
											Processing pnl = new Processing(800,800,Color.WHITE);
											frame.add(pnl);
											frame.setVisible(true);
											//Run in Parrelle
											while(thread.isAlive()) {
												pnl.getDetailLBL().setText(""+(ga.getGeneration()));
											}
											System.out.println(""+(ga.getGeneration()));
											//After Finishing Kill yourself please
											frame.dispose();
										} catch (Exception e) {
											e.printStackTrace();
										}
								 }
							};
						  threadProcessGUI.start();
						  //After Finding Solution
						 // thisFrame.removeAll();
						  //thisFrame.dispose();
		
					}
				}
				
			}
		});
		resetbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==resetbtn) {
					for(int i=0;i<9;i++) {
						for(int j=0;j<9;j++) {
							info_sudoku[i][j]=0;
							cellsStyle[i][j]=0;
							cells[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
							cells[i][j].setText(null);
							cells[i][j].setEditable(true);
							
						}
					}
				}
				confirmbtn.setEnabled(true);
			}
		});
		tmp.add(confirmbtn);
		tmp.add(resetbtn);
	}
	private void addAllPanels(JPanel tmp) {
		int locatx,locaty;
		tmp.setLayout(null);
		tmp.setBackground(ourbgColor);
		tmp.setSize(3*(3*default_cellSize+2*default_cellSpacing)+2*default_panelSpacing, 3*(3*default_cellSize+2*default_cellSpacing)+2*default_panelSpacing);
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				locatx=((3*default_cellSize+2*default_cellSpacing+default_panelSpacing)*j);
				locaty=((3*default_cellSize+2*default_cellSpacing+default_panelSpacing)*i);
				pnlSqrs[i*3+j].setLocation(locatx,locaty);
				tmp.add(pnlSqrs[i*3+j]);
			}
		}
	}
	private void settingOurSodukoView() {
		int locatx,locaty;
		//JTextField localField;
		Font f = new Font("MS UI Gothic", Font.BOLD, default_cellSize);
		for(int i=0;i<9;i++) {
			locatx=0;locaty=0;
			pnlSqrs[i]=new JPanel();
			pnlSqrs[i].setLayout(null);
			pnlSqrs[i].setBackground(ourbgColor);
			pnlSqrs[i].setSize(3*default_cellSize+2*default_cellSpacing,3*default_cellSize+2*default_cellSpacing);
			for(int j=0;j<9;j++) {
				locatx=((default_cellSize+default_cellSpacing)*(j%3));
				locaty=((default_cellSize+default_cellSpacing)*((int)j/3));
				cells[i][j]=new JTextField();
				cells[i][j].setSize(default_cellSize, default_cellSize);
				cells[i][j].setHorizontalAlignment(JTextField.CENTER);
				cells[i][j].setFont(f);
				cells[i][j].setLocation(locatx, locaty);
				cells[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				//JTextField localField=cells[i][j];
				int celli=i;
				int cellj=j;
				cells[i][j].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if(e.getSource()==cells[celli][cellj]) {
							for(int i=0;i<9;i++) {
								for(int j=0;j<9;j++) {
									switch(cellsStyle[i][j]) {
									case 0:cells[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));break;
									case 1:cells[i][j].setBorder(BorderFactory.createLineBorder(Color.RED, 2));break;
									case 2:cells[i][j].setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));break;
									case 3:cells[i][j].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));break;
									}
								}
							}
							((JTextField)e.getSource()).setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 2));
						}
					}
				});
				cells[i][j].addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						JTextField localField=(JTextField) e.getSource();
						if(localField.getText().length()==1) {
							if(Character.isDigit(localField.getText().charAt(0))){
								//info_sudoku[celli][cellj]=Integer.parseInt(localField.getText());
								
									cellsStyle[celli][cellj]=green;
									info_sudoku[celli][cellj]=Integer.parseInt(localField.getText());
									localField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
								
									
								
							}else {
								cellsStyle[celli][cellj]=red;
								info_sudoku[celli][cellj]=0;
								localField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
							}
						}else if(localField.getText().length()==0){
							cellsStyle[celli][cellj]=gray;
							info_sudoku[celli][cellj]=0;
							localField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
						}else {
							cellsStyle[celli][cellj]=red;
							info_sudoku[celli][cellj]=0;
							localField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
						}
					}
					
				});
				pnlSqrs[i].add(cells[i][j]);
			}
		}
	}
	private boolean legalSquare(int row,int clm) {
		if(info_sudoku[row][clm]==0)return true;
		for(int i=0;i<9;i++) {
			if(info_sudoku[row][i]==info_sudoku[row][clm] && clm!=i)
				return false;
		}
		return true;
	}
	private boolean legalColumn(int row,int clm) {
		if(info_sudoku[row][clm]==0)return true;
		for(int i=(row%3);i<9;i+=3) {
			for(int j=(clm%3);j<9;j+=3) {
				if(info_sudoku[i][j]==info_sudoku[row][clm]&& (row!=i||clm!=j))
					return false;
			}
		}
		return true;
	}
	private boolean legalRow(int row,int clm) {
		if(info_sudoku[row][clm]==0)return true;
		for(int i=((int)row/3)*3;i<(((int)row/3)+1)*3;i++) {
			for(int j=((int)clm/3)*3;j<(((int)clm/3)+1)*3;j++) {
				if(info_sudoku[i][j]==info_sudoku[row][clm]&& (row!=i||clm!=j))
					return false;
			}
		}
		return true;
	}
	public void setValueAtIndex(int i,int j,int value) {
		getInfo_sudoku()[i][j]=value;
	}
	public int getValueAtIndex(int i,int j) {
		return getInfo_sudoku()[i][j];
	}
 	public int[][] getInfo_sudoku() {
		return info_sudoku;
	}
	public void setInfo_sudoku(int info_sudoku[][]) {
		this.info_sudoku = info_sudoku;
	}
}
