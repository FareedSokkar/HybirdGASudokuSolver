package Essintials;

import java.util.ArrayList;
import java.util.Random;

public class Population {
	//Defaults in case an empty population
	private static final int default_chrome=81;
	private static final int default_mutation=120;//out of 1000
	private static final int default_population=21;
	//Our variables
	private boolean ready=false;
	private int info_sudoku[][];
	private ArrayList<Integer> crossover_points;//Crossover index cross over
	private int mutation_rate;
	private int chromosome_size;
	private int population_size;
	private ArrayList<Chromosome> population;
	//CONSTRUCTORS
	public Population() {
		setInfo_sudoku(new int[9][9]);//Default value of matrix is zero (int)
		//Default cross-over point is in the middle
		ArrayList<Integer> temp=new ArrayList<Integer>();
		temp.add((int)default_chrome/2);
		setCrossover_points(temp);
		//Default values
		setChromosome_size(default_chrome);
		setMutation_rate(default_mutation);
		setPopulation_size(default_population);
		setPopulation(new ArrayList<Chromosome>());
		//setReady(true);
	}
	public Population(int info_sudoku[][],int mutation_rate,ArrayList<Integer> crossover_points,int population_size) {
		//Defaults
		setInfo_sudoku(info_sudoku);
		setMutation_rate(mutation_rate);
		setCrossover_points(crossover_points);
		//getting the size of chromo
		setChromosome_size(default_chrome-calculate_info(info_sudoku));
		//==========================
		setPopulation_size(population_size);
		setPopulation(new ArrayList<Chromosome>());
		//setReady(true);
	}
	public Population(Population temp) {
		setInfo_sudoku(temp.getInfo_sudoku());
		setCrossover_points(temp.getCrossover_points());
		setChromosome_size(temp.getChromosome_size());
		setMutation_rate(temp.getMutation_rate());
		setPopulation_size(temp.getPopulation_size());
		setPopulation(temp.getPopulation());
		setReady(temp.isReady());
	}
	//FUNCTIONS
	public boolean initilize_population() {
		Random rand=new Random();
		ArrayList<Integer> temp;
		if(!isReady()) {//we didn't started the population
			for(int i=0;i<getPopulation_size();i++) {
				temp=new ArrayList<Integer>();
				for(int j=0;j<getChromosome_size();j++) {
					temp.add(0);
				}
				boolean[] rows=new boolean[9];
				int rowCount=9;
				int crmStrt,offset;
				//On each Line
				while(rowCount!=0) {
					offset=0;
					int rowChose=rand.nextInt(rowCount)+1;
					int j=0;
					while(rowChose!=0 && j<8) {
						if(!rows[j]) {
							rowChose--;
							if(rowChose!=0)
								j++;
						}else {
							j++;
						}
					}
					if(j==0)
						crmStrt=0;
					else
						crmStrt=getCrossover_points().get(j-1);
					boolean[] usedigits=new boolean[9];
					//Get fixed digits
					for(int k=0;k<9;k++) {
						if(getInfo_sudoku()[j][k]!=0)
							usedigits[getInfo_sudoku()[j][k]-1]=true;
					}
					int numLeft=0;
					for(int k=0;k<9;k++) {
						if(!usedigits[k]) {
							numLeft++;
						}
					}
					while(numLeft!=0){
						int choose=rand.nextInt(numLeft)+1;
						int l=0;
						while(choose!=0 && l<8) {
							if(!usedigits[l]) {
								choose--;
								if(choose!=0)
									l++;
							}else {
								l++;
							}
						}
						temp.set(crmStrt+offset, l+1);
						offset++;
						usedigits[l]=true;
						numLeft--;
					}
					rows[j]=true;
					rowCount--;
				}
				getPopulation().add(new Chromosome(temp,0,0));
			}
			//we are ready for first run
			setReady(true);
			return true;
		}else {
			return false;
		}
	}
/*	public boolean initilize_population() {
		Random rand=new Random();
		ArrayList<Integer> temp;
		if(!isReady()) {//we didn't started the population
			for(int i=0;i<getPopulation_size();i++) {
				temp=new ArrayList<Integer>();
				//Fill Chromosome values 9-1 (sudoku solutions)
				int cnt;
				for(int j=0;j<9;j++) {
					int tmpRow[]=new int[9],randNm;
					boolean tmpDig[]=new boolean[9];
					cnt=0;
					for(int k=0;k<9;k++) {
						if(getInfo_sudoku()[j][k]!=0)
							tmpDig[getInfo_sudoku()[j][k]-1]=true;
						else
							cnt++;
					}
					for(int k=0;k<cnt;k++) {
						do {
							randNm=rand.nextInt(9)+1;
							tmpRow[k]=randNm;
						}while(tmpDig[randNm-1]);
						tmpDig[randNm-1]=true;
						temp.add(randNm);
					}
						
				}
				//Add chromo to population
				getPopulation().add(new Chromosome(temp,0,0));
			}
			//we are ready for first run
			setReady(true);
			return true;
		}else {
			return false;
		}
	}*/
	private int calculate_info(int temp[][]) {
		int cnt=0;
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				if(temp[i][j]!=0)
					cnt++;
			}
		}
		return cnt;
	}
	//GETTERS & SETTERS
	public ArrayList<Integer> getCrossover_points() {
		return crossover_points;
	}
	public void setCrossover_points(ArrayList<Integer> crossover_points) {
		this.crossover_points = crossover_points;
	}
	public int getMutation_rate() {
		return mutation_rate;
	}
	public void setMutation_rate(int mutation_rate) {
		this.mutation_rate = mutation_rate;
	}
	public ArrayList<Chromosome> getPopulation() {
		return population;
	}
	public void setPopulation(ArrayList<Chromosome> population) {
		this.population = population;
	}
	public int getPopulation_size() {
		return population_size;
	}
	public void setPopulation_size(int population_size) {
		this.population_size = population_size;
	}
	public int getChromosome_size() {
		return chromosome_size;
	}
	public void setChromosome_size(int chromosome_size) {
		this.chromosome_size = chromosome_size;
	}
	public int[][] getInfo_sudoku() {
		return info_sudoku;
	}
	public void setInfo_sudoku(int info_sudoku[][]) {
		this.info_sudoku = info_sudoku;
	}
	public boolean isReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
}
