package Essintials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GA {
	private Population popGA;
	private RouletteWheel rltwhl;
	private int temproryMat[][];
	private int maxGoal;
	private Chromosome theSolution;
	private boolean firstGen;
	private int generation;
	//CONSTRUCTOR
	public GA() {
		setPopGA(null);
		setRltwhl(null);
		setTemproryMat(null);
		setMaxGoal(-244);
		setTheSolution(null);
	}
	//This GA Code
	public boolean sudokuGenericAlgorithm(int info_sudoku[][],int mutation_rate,ArrayList<Integer> crossover_points,int population_size) {
		if(!isOkInfoSudoku(info_sudoku) || (mutation_rate>1000 || mutation_rate<0) || population_size<0 || !isOkCrossOver(crossover_points)) {//Illegal input
			return false;
		}else {//Legal Input we can start
			//Create Our population
			if(population_size<2)
				setPopGA(new Population());//use default values don't care for the rest
			else//other wise use what we have
				setPopGA(new Population(info_sudoku, mutation_rate, crossover_points, population_size));
			//Check that Chromosome value is reasonable
			if(getPopGA().getChromosome_size()<0 || getPopGA().getChromosome_size()>81 )
				return false;
			//In case our initilizing didn't start
			if(!getPopGA().initilize_population())
				return false;
			//Otherwise meaning our population is ready and filled with initiale' solutions
			//=============================================================================================================================
			//Evaluate our Population (Inizilized)
			evaluate_population(getPopGA());
			//Sort our population so we won't take the best one (in index 0)
			Collections.sort(getPopGA().getPopulation(), Collections.reverseOrder());
			generation=1;
			firstGen=true;
			do {
				setRltwhl(createRoulette(getPopGA().getPopulation()));
				getPopGA().setPopulation(FittestOnlyPopulation2());
				if(generation%9999==0) {
					System.out.println("Legal: "+getTheSolution().getLegal()+", IlLegal: "+getTheSolution().getIllegal());
				}
				generation++;	
				firstGen=false;
			}while(getMaxGoal()<getPopGA().getChromosome_size());//our goal is to reach a fitness same as the size of 
			//=====================
			return true;
		}
	}
	//FUNCTIONS
	public void printSolution(int info_sudoku[][],Chromosome solution) {
		int offset=0;
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				if(info_sudoku[i][j]==0) {
					System.out.print(solution.getValueAt(offset)+"|");
					offset++;
				}else {
					System.out.print(info_sudoku[i][j]+"|");
				}
			}
			System.out.println();
		}
	}
	private ArrayList<Chromosome> FittestOnlyPopulation2(){
		ArrayList<Chromosome> newpop=new ArrayList<Chromosome>();
		ArrayList<Chromosome> bestFitPop=new ArrayList<Chromosome>();
		Chromosome parent1,parent2,offsprings[];
		//Add First Generation
		if(!firstGen)
			ChangeBaseToRecombine();
		for(int i=0;i<getPopGA().getPopulation_size();i+=2) {
			parent1=rouletteWheelSelection(getPopGA().getPopulation());
			do {
				parent2=rouletteWheelSelection(getPopGA().getPopulation());
			}while(parent1.equals(parent2)); 
			offsprings=recombination(parent1,parent2,getPopGA().getCrossover_points());
			offsprings[0]=mutation(offsprings[0],getPopGA().getMutation_rate());
			offsprings[1]=mutation(offsprings[1],getPopGA().getMutation_rate());
			//Elitest Selection
			evaluate_chromosome(offsprings[0],getPopGA());
			evaluate_chromosome(offsprings[1],getPopGA());
			newpop.add(offsprings[0]);
			newpop.add(offsprings[1]);
		}
		for(Chromosome crm:getPopGA().getPopulation()) {
			newpop.add(crm);
		}
		Collections.sort(newpop, Collections.reverseOrder());
		for(int i=0;i<getPopGA().getPopulation_size();i++)
			bestFitPop.add(newpop.get(i));
		setMaxGoal(bestFitPop.get(0).getLegal()-bestFitPop.get(0).getIllegal());
		setTheSolution(bestFitPop.get(0));
		return bestFitPop;
	}
	private Chromosome rouletteWheelSelection(ArrayList<Chromosome> population) {
		double random_num=getRltwhl().getRandomProb();//gets prob randomly
		double sum_prob=0.0;
		//No including best one
		for(int i=1;i<population.size();i++) {
			sum_prob+=getRltwhl().calculateProb(population.get(i).getLegal()-population.get(i).getIllegal());
			if(random_num<=sum_prob)
				return population.get(i);
		}
		//Suppose it didn't work (Not likely)
		//then return second best fitted
		return population.get(1);
	}
	private RouletteWheel createRoulette(ArrayList<Chromosome> population) {
		//Don't include best fitted we will use it for combination 
		int info[]=SumAllFittness(population);
		RouletteWheel tmp=new RouletteWheel();
		tmp.setSum(info[0]);
		tmp.setMin(info[1]);
		tmp.setMax(info[2]);
		tmp.setPopSize(population.size());
		return tmp;
	}
	private int[] SumAllFittness(ArrayList<Chromosome> population) {
		//Don't include best fitted we will use it for combination 
		//Our population is sorted from best fitted to worst
		int info[]=new int[3];//0:sum,1:min,2:max
		info[0]=0;//Just in case (Java initil value for int is zero)
		info[2]=(population.get(1).getLegal()-population.get(1).getIllegal());
		info[1]=(population.get(population.size()-1).getLegal()-population.get(population.size()-1).getIllegal());
		for(int i=1;i<population.size();i++) {
			info[0]+=(population.get(i).getLegal()-population.get(i).getIllegal());
		}
		return info;
	}
 	private boolean	isOkCrossOver(ArrayList<Integer> crossover_points) {
		if(crossover_points.isEmpty()) {
			return false;
		}else {
			for(int nm:crossover_points) {
				if(nm<0)
					return false;
			}
		}
		return true;
	}
	private boolean isOkInfoSudoku(int info_sudoku[][]) {
		if(info_sudoku.length==9) {
			//Checking Matrix
			for(int tmp[]:info_sudoku) {
				//Check sizes
				if(tmp.length!=9)
					return false;
				//Check legal values
				for(int nm:tmp) {
					if(nm<0 || nm>9)
						return false;
				}
			}
			
		}else {
			return false;
		}
		return true;
	}
	private Chromosome mutation(Chromosome solution,int mutation_rate) {//mutation rate is out if 1000
		//Copy The best Solution by far
		ArrayList<Integer> localSolution=new ArrayList<Integer>();
		for(int num:solution.getVector()) {
			localSolution.add(num);
		}
		Chromosome newSol=new Chromosome(localSolution,0,0);
		Random rand=new Random();
		int cnt=0;
		int crmStrt=0;
		int crmEnd;
		if(rand.nextInt(1001)<=mutation_rate) {//A mutation happen
			int chosenRow;
			//Chose a random splitter [1,8]
			int splitter=rand.nextInt(8)+1;
			//true a chosen row false not
			boolean rows[]=new boolean[9];
			//Choosing random rows for the cross-over
			if(splitter<=4) {//splitter=1/2/3/4
				do {
					chosenRow=rand.nextInt(9)+1;
					if(!rows[chosenRow-1]) {
						rows[chosenRow-1]=true;
						cnt++;
					}
				}while(cnt!=splitter);
			}else {//splitter=5/6/7/8
				do {
					chosenRow=rand.nextInt(9)+1;
					if(!rows[chosenRow-1]) {
						rows[chosenRow-1]=true;
						cnt++;
					}
				}while(cnt!=(9-splitter));
				for(int i=0;i<9;i++) {
					rows[i]=!rows[i];
				}
			}
			for(int i=0;i<9;i++) {//Go through each row
				if(i==8)
					crmEnd=getPopGA().getChromosome_size();
				else
					crmEnd=getPopGA().getCrossover_points().get(i);
				if(rows[i]) {
					boolean use[]=new boolean[9];
					int[][] allClm=allColumnsValues(solution,i,getPopGA().getCrossover_points());
					boolean digits[][]=new boolean[allClm.length][9];
					//Count the digits
					for(int j=0;j<allClm.length;j++) {
						for(int k=0;k<allClm[0].length;k++) {
							if(!rows[k]) {
								digits[j][allClm[j][k]-1]=true;
							}
						}
					}
					//Go through
					for(int j=0;j<allClm.length;j++) {
						if(digits[j][allClm[j][i]-1]) {
							use[allClm[j][i]-1]=true;
							allClm[j][i]=0;
						}else {
							//Do not use we fixed it
							digits[j][allClm[j][i]-1]=true;
						}
					}
					//
					int[] sol=findBestMutCombo(allClm,use,digits,i,allClm.length);
					for(int j=crmStrt;j<crmEnd;j++) {
						newSol.getVector().set(j,sol[j-crmStrt]);
					}
					rows[i]=false;
				}
				crmStrt=crmEnd;
			}
		}
		return newSol;
	}
	private int[] findBestMutCombo(int[][] solMats,boolean[] use,boolean[][] digits,int row,int rowCrmSize) {
		//use is the values we can use
		//digits is the values already in each sub columns
		int finalRow[]=new int[rowCrmSize];
		//first go through to see if one of the sub columns need a certain value 
		for(int i=0;i<9;i++) {
			if(use[i]) {
				for(int j=0;j<rowCrmSize;j++) {
					boolean sure= !digits[j][i];
					for(int k=0;k<rowCrmSize;k++) {
						if(k!=j)
							sure&=digits[k][i];
					}
					if(sure) {
						if(solMats[j][row]==0) {
							solMats[j][row]=i+1;
							use[i]=false;
							digits[j][i]=true;
						}
					}
				}
			}
		}
		Random rand=new Random();
		int numLeft=0;
		for(int i=0;i<9;i++) {
			if(use[i]) {
				numLeft++;
			}
		}
		while(numLeft!=0){
			int choose=rand.nextInt(numLeft)+1;
			int i=0;
			while(choose!=0 && i<9) {
				if(use[i]) {
					choose--;
					if(choose!=0)
						i++;
				}else {
					i++;
				}
			}
			boolean notDone=true;
			int randomSubColumn=rand.nextInt(rowCrmSize);//0/1/2/.../rowCrmSize-1
			for(int j=0;j<rowCrmSize && notDone;j++) {
				if(!digits[randomSubColumn][i]) {
					if(solMats[randomSubColumn][row]==0) {
						solMats[randomSubColumn][row]=i+1;
						use[i]=false;
						digits[randomSubColumn][i]=true;
						notDone=false;
						numLeft--;
					}
				}else{
					randomSubColumn=(randomSubColumn+1)%rowCrmSize;
				}
			}
			if(notDone) {
				for(int j=0;j<rowCrmSize && notDone;j++) {
					if(solMats[j][row]==0) {
						solMats[j][row]=i+1;
						use[i]=false;
						digits[j][i]=true;
						notDone=false;
						numLeft--;
					}
				}
			}
		}
		//
		for(int i=0;i<rowCrmSize;i++)
			finalRow[i]=solMats[i][row];
		return finalRow;
	}
	private int[][] allColumnsValues(Chromosome solution,int row,ArrayList<Integer> crossover_points) {
		int[][] cloumns;
		//Set columns
		if(row==0) {
			cloumns=new int[crossover_points.get(0)][];
		}else if(row==8) {
			cloumns=new int[getPopGA().getChromosome_size()-crossover_points.get(crossover_points.size()-1)][];
		}else {
			cloumns=new int[crossover_points.get(row)-crossover_points.get(row-1)][];
		}
		int offset=0;
		for(int i=0;i<9;i++) {
			if(getPopGA().getInfo_sudoku()[row][i]==0) {
				cloumns[offset]=returnClmSol(solution,i,crossover_points);
				offset++;
			}
		}
		return cloumns;
	}
	private int[] returnClmSol(Chromosome solution,int clm,ArrayList<Integer> crossover_points) {
		int[] clmn=new int[9];
		int crmStrt=0;
		int crmEnd;
		for(int i=0;i<9;i++) {
			if(i==8)
				crmEnd=getPopGA().getChromosome_size();
			else
				crmEnd=crossover_points.get(i);
			
			if(getPopGA().getInfo_sudoku()[i][clm]==0) {
				int offset=0;
				for(int j=0;j<clm;j++) {
					if(getPopGA().getInfo_sudoku()[i][j]==0)
						offset++;
				}
				clmn[i]=solution.getValueAt(crmStrt+offset);
			}else {
				clmn[i]=getPopGA().getInfo_sudoku()[i][clm];
			}
			crmStrt=crmEnd;
		}
		return clmn;
	}
	private Chromosome[] recombination(Chromosome prnt1,Chromosome prnt2,ArrayList<Integer> crossover_points) {
		Chromosome tmp[]=new Chromosome[2];
		tmp[0]=recombineApprove(getTheSolution(),prnt1,crossover_points);
		tmp[1]=recombineApprove(getTheSolution(),prnt2,crossover_points);
		return tmp;
	}
	private Chromosome recombineApprove(Chromosome base,Chromosome prnt,ArrayList<Integer> crossover_points) {
		//Copy The best Solution by far
		ArrayList<Integer> localSolution=new ArrayList<Integer>();
		for(int num:base.getVector()) {
			localSolution.add(num);
		}
		Chromosome newSol=new Chromosome(localSolution,0,0);
		Random rand=new Random();
		int cnt=0;
		//Chose a random splitter [1,8]
		int splitter=rand.nextInt(8)+1;
		//true a chosen row false not
		boolean rows[]=new boolean[9];
		int chosenRow;
		int crmStrt=0;
		int crmEnd;
		//Choosing random rows for the cross-over
		if(splitter<=4) {//splitter=1/2/3/4
			do {
				chosenRow=rand.nextInt(9)+1;
				if(!rows[chosenRow-1]) {
					rows[chosenRow-1]=true;
					cnt++;
				}
			}while(cnt!=splitter);
		}else {//splitter=5/6/7/8
			do {
				chosenRow=rand.nextInt(9)+1;
				if(!rows[chosenRow-1]) {
					rows[chosenRow-1]=true;
					cnt++;
				}
			}while(cnt!=(9-splitter));
			for(int i=0;i<9;i++) {
				rows[i]=!rows[i];
			}
		}
		for(int i=0;i<9;i++) {
			if(i==8)
				crmEnd=getPopGA().getChromosome_size();
			else
				crmEnd=crossover_points.get(i);
			if(rows[i]) {
				//We made sure in intitilalizing part that each row have contain one number of [1,9]
				boolean use[]=new boolean[9];
				int[][][] solMats=getRow3SqrsPrnt(prnt,newSol,i,crossover_points);
				boolean digits[][]=new boolean[3][9];
				//For each sub-square
				for(int j=0;j<3;j++) {
					//get the other digits in same sub-square(without the one we are at)
					for(int k=0;k<3;k++) {
						for(int l=0;l<3;l++) {
							if(k!=(i%3)) {
								if(!digits[j][solMats[j][k][l]-1]) {
									digits[j][solMats[j][k][l]-1]=true;
								}
							}
						}
					}
					//go through the row and change values to zero
					for(int k=0;k<3;k++) {
						if(digits[j][solMats[j][i%3][k]-1] && getPopGA().getInfo_sudoku()[i][(j*3)+k]==0) {
							use[solMats[j][i%3][k]-1]=true;
							solMats[j][i%3][k]=0;
						}else {
							digits[j][solMats[j][i%3][k]-1]=true;
						}
					}
				}
				//Get best Combination for the new row satisfying 3rd rule
				int finalRow[]=findBestRowCombo(solMats,use,digits,i,crmEnd-crmStrt);
				//Change newSol
				for(int j=crmStrt;j<crmEnd;j++) {
					newSol.getVector().set(j,finalRow[j-crmStrt]);
				}
			}
			crmStrt=crmEnd;
		}
		return newSol;
	}
	private void ChangeBaseToRecombine() {
		if(generation%(3000000/getPopGA().getPopulation_size())!=0) {
			Chromosome best,AVG,worst;
			best=getPopGA().getPopulation().get(0);
			worst=getPopGA().getPopulation().get(getPopGA().getPopulation_size()-1);
			int sum=0;
			for(Chromosome crm:getPopGA().getPopulation()) {
				sum+=(crm.getLegal()-crm.getIllegal());
			}
			sum=Math.round(sum/getPopGA().getPopulation_size());
			int i=getPopGA().getPopulation_size()-1;
			do {
				AVG=getPopGA().getPopulation().get(i);
				i--;
			}while(((AVG.getLegal()-AVG.getIllegal())<=sum)&& i!=0);
			//Best Chromosome distance from the Average
			int offBTA=(best.getLegal()-best.getIllegal())-sum;
			//Worst Chromosome distance from the Average
			int offATW=sum-(worst.getLegal()-worst.getIllegal());
			
			//AVERAGE DISTANCES
			int avgdisSum=0;
			int firstDis=best.getLegal()-best.getIllegal();
			for(int index=1;index<getPopGA().getPopulation_size();index++) {
				avgdisSum+=(firstDis-(getPopGA().getPopulation().get(index).getLegal()-getPopGA().getPopulation().get(index).getIllegal()));
				firstDis=getPopGA().getPopulation().get(index).getLegal()-getPopGA().getPopulation().get(index).getIllegal();
			}
			int avgDis=Math.round(avgdisSum/(getPopGA().getPopulation_size()-1));
			
			//first check the best & AVG Distance from goal
			int offGTB=getPopGA().getChromosome_size()-(best.getLegal()-best.getIllegal());
			//incase our best is really near the goal few unplaced locations
			if(offGTB<=9) {
				if(avgDis==0) {
					//Try to Solve it
					//setTheSolution(solveIt(best));
					//evaluate_chromosome(getTheSolution(), getPopGA());
					//setMaxGoal(getTheSolution().getLegal()-getTheSolution().getIllegal());
					//If couldn't change pop and start over
					setTheSolution(newBase());
					//getPopGA().getPopulation().set(0,getTheSolution());
					evaluate_chromosome(getTheSolution(), getPopGA());
					setMaxGoal(getTheSolution().getLegal()-getTheSolution().getIllegal());
					
				}else {
					setTheSolution(AVG);
					//Chromosome tmp=getPopGA().getPopulation().get(0);
					//getPopGA().getPopulation().set(0,getTheSolution());
					//getPopGA().getPopulation().set(getPopGA().getPopulation_size()-1,tmp);
					setMaxGoal(AVG.getLegal()-AVG.getIllegal());
				}
			}else 
			//|<-----offGTB----->|<--offBTA-->|
			if(offGTB>=((offBTA+offATW)-1)) {
				//Meaning our population is going no where so far
				if(0<=avgDis && avgDis<=1) {
					//Get a new Base
					setTheSolution(newBase());
					//getPopGA().getPopulation().set(0,getTheSolution());
					evaluate_chromosome(getTheSolution(), getPopGA());
					setMaxGoal(getTheSolution().getLegal()-getTheSolution().getIllegal());
					
				}else {
					setTheSolution(worst);
					//Chromosome tmp=getPopGA().getPopulation().get(0);
					//getPopGA().getPopulation().set(0,getTheSolution());
					//getPopGA().getPopulation().set(getPopGA().getPopulation_size()-1,tmp);
					setMaxGoal(worst.getLegal()-worst.getIllegal());
				}
				
			}else {//Continue with same population and best Solution
				
			}
		}else {//if generation reached 6,000
			//Drop the search
			//Kill them all, all of them 
			//"Not just the Men but also the women and the children too"
			getPopGA().setReady(false);
			getPopGA().getPopulation().clear();
			getPopGA().initilize_population();
			evaluate_population(getPopGA());
			Collections.sort(getPopGA().getPopulation(), Collections.reverseOrder());
			setRltwhl(createRoulette(getPopGA().getPopulation()));
			firstGen=true;
			generation=1;
		}
	}
	private Chromosome newBase() {
		Chromosome sol=new Chromosome();
		//Add Zeroes
		for(int i=0;i<getPopGA().getChromosome_size();i++) {
			sol.addValue(0);
		}
		//Each row start and end in Chromosome
		int crmStrt=0;
		int crmEnd;
		//Go throught each row
		for(int i=0;i<9;i++) {
			if(i==8)
				crmEnd=getPopGA().getChromosome_size();
			else
				crmEnd=getPopGA().getCrossover_points().get(i);
			boolean[] use=new boolean[9];
			int[][] solMat=allColumnsValues(sol,i,getPopGA().getCrossover_points());
			boolean[][] digits=new boolean[solMat.length][9];
			for(int j=0;j<solMat.length;j++) {
				//Check values 
				for(int k=0;k<9;k++) {
					if(solMat[j][k]!=0) {
						digits[j][solMat[j][k]-1]=true;
					}
				}
			}
			for(int j=0;j<9;j++) {
				if(getPopGA().getInfo_sudoku()[i][j]!=0) {
					use[getPopGA().getInfo_sudoku()[i][j]-1]=true;
				}
			}
			//Invert to know which we can use
			for(int j=0;j<9;j++) {
				use[j]=!use[j];
			}
			int[] good=findBestMutCombo(solMat,use,digits,i,solMat.length);
			for(int j=crmStrt;j<crmEnd;j++) {
				sol.getVector().set(j,good[j-crmStrt]);
			}
			crmStrt=crmEnd;
		}
		return sol;
	}
	private int[] findBestRowCombo(int[][][] solMats,boolean[] use,boolean[][] digits,int row,int rowCrmSize) {
		//use is the values we can use
		//digits is the values already in each sub-square
		int finalRow[]=new int[rowCrmSize];
		//first go through to see if one of the three sub squares need a certain value 
		for(int i=0;i<9;i++) {
			if(use[i]) {
				//in case first sub-square needs this value only
				if(!digits[0][i] && digits[1][i] && digits[2][i]) {
					int j=0;
					for(;j<3 && solMats[0][row%3][j]!=0;j++);
					if(j!=3) {
						solMats[0][row%3][j]=i+1;
						use[i]=false;
						digits[0][i]=true;
					}
				}
				//in case Second sub-square needs this value only
				if(digits[0][i] && !digits[1][i] && digits[2][i]) {
					int j=0;
					for(;j<3 && solMats[1][row%3][j]!=0;j++);
					if(j!=3) {
						solMats[1][row%3][j]=i+1;
						use[i]=false;
						digits[1][i]=true;
					}
				}	
				//in case Third sub-square needs this value only
				if(digits[0][i] && digits[1][i] && !digits[2][i]) {
					int j=0;
					for(;j<3 && solMats[2][row%3][j]!=0;j++);
					if(j!=3) {
						solMats[2][row%3][j]=i+1;
						use[i]=false;
						digits[2][i]=true;
					}
				}
			}
		}
		Random rand=new Random();
		int numLeft=0;
		for(int i=0;i<9;i++) {
			if(use[i]) {
				numLeft++;
			}
		}
		while(numLeft!=0){
			int choose=rand.nextInt(numLeft)+1;
			int i=0;
			while(choose!=0 && i<9) {
				if(use[i]) {
					choose--;
					if(choose!=0)
						i++;
				}else {
					i++;
				}
			}
			int randomSubSquare=rand.nextInt(3);//0/1/2
			if(!digits[randomSubSquare][i]) {
				int j=0;
				for(;j<3 && solMats[randomSubSquare][row%3][j]!=0;j++);
				if(j!=3) {
					solMats[randomSubSquare][row%3][j]=i+1;
					use[i]=false;
					digits[randomSubSquare][i]=true;
					numLeft--;
					continue;
				}
			}
			randomSubSquare=(randomSubSquare+1)%3;
			if(!digits[randomSubSquare][i]) {
				int j=0;
				for(;j<3 && solMats[randomSubSquare][row%3][j]!=0;j++);
				if(j!=3) {
					solMats[randomSubSquare][row%3][j]=i+1;
					use[i]=false;
					digits[randomSubSquare][i]=true;
					numLeft--;
					continue;
				}
			}	
			randomSubSquare=(randomSubSquare+1)%3;
			if(!digits[randomSubSquare][i]) {
				int j=0;
				for(;j<3 && solMats[randomSubSquare][row%3][j]!=0;j++);
				if(j!=3) {
					solMats[randomSubSquare][row%3][j]=i+1;
					use[i]=false;
					digits[randomSubSquare][i]=true;
					numLeft--;
					continue;
				}
			}
			//in case
			int j=0;
			for(;j<9 && solMats[j/3][row%3][j%3]!=0;j++);
			if(j!=9) {
				solMats[j/3][row%3][j%3]=i+1;
				use[i]=false;
				digits[j/3][i]=true;
				numLeft--;
				continue;
			}
			
		}
		int offset=0;
		for(int i=0;i<9;i++) {
			if(getPopGA().getInfo_sudoku()[row][i]==0) {
				finalRow[offset]=solMats[(int)i/3][row%3][i%3];
				offset++;
			}
				
		}
		return finalRow;
	}
	private int[][][] getRow3SqrsPrnt(Chromosome crm,Chromosome newSol,int row,ArrayList<Integer> crossover_points){
		int threeMat[][][]=new int[3][3][3];
		int firstRow=((int)row/3)*3;
		int offsetInCrm=0;
		//Where to start copying from the chromosome
		int startSqrt=0;
		switch((int)row/3) {
		case 0: 
			startSqrt=0;
			break;
		case 1: 
			startSqrt=crossover_points.get(2);
			break;
		case 2: 
			startSqrt=crossover_points.get(5);
			break;
		}
		//Start Copying
		for(int j=0;j<3;j++) {
			for(int i=0;i<3;i++) {
				for(int k=0;k<3;k++) {
					if(getPopGA().getInfo_sudoku()[firstRow+j][(i*3)+k]==0) {
						if(row==firstRow+j) {
							threeMat[i][j][k]=crm.getValueAt(startSqrt+offsetInCrm);
						}else {
							threeMat[i][j][k]=newSol.getValueAt(startSqrt+offsetInCrm);
						}
						offsetInCrm++;
					}else {
						threeMat[i][j][k]=getPopGA().getInfo_sudoku()[firstRow+j][(i*3)+k];
					}
					
				}
			}
		}
		return threeMat;
	}
	private void evaluate_chromosome(Chromosome cs,Population pop) {
		int column[]=new int[9];
		int row[]=new int[9];
		int sqr[][]=new int[3][3];
		cs.setIllegal(0);
		cs.setLegal(0);
		//Set our work Matrix
		setTemproryMat(fillMat(pop.getInfo_sudoku(),cs));
		//====================
		boolean penltyClm,penltyRow,penltySqr;
		int dividerRow,dividerClm;
		int rowk, clmk, rowi, clmi;
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				if(pop.getInfo_sudoku()[i][j]==0) {
					column=getValuesInColumn(j);
					row=getValuesInRow(i);
					sqr=getValuesInSquare(i,j);
					penltyClm=penltyRow=penltySqr=false;
					for(int k=0;k<9;k++) {
						//Column Pass & Penelty: Rule1
						if(column[k]==column[i]) {
							if(k!=i)
								penltyClm=true;
						}
						//Row Pass & Penelty: Rule2
						if(row[k]==row[j]) {
							if(k!=j)
								penltyRow=true;
						}
						//Square Pass & Penelty: Rule3
						if(i>3)
							dividerRow=((int)i/3);
						else
							dividerRow=1;
						if(j>3)
							dividerClm=((int)j/3);
						else
							dividerClm=1;
						rowk=(int)k/3;
						clmk=k%3;
						rowi=i%(3*dividerRow);
						clmi=j%(3*dividerClm);
						if(sqr[rowk][clmk]==sqr[rowi][clmi]) {
							if((rowk!=rowi)||(clmk!=clmi))
								penltySqr=true;
						}
					}
					if(penltyClm) {
						cs.addPenelty();
					}
					if(penltyRow){
						cs.addPenelty();
					}
					if(penltySqr) {
						cs.addPenelty();
					}
					if(!penltyClm && !penltyRow && !penltySqr){
						cs.addPass();
					}
					//offset++;
				}
			}
		}
	}
	private void evaluate_population(Population pop) {
		int column[]=new int[9];
		int row[]=new int[9];
		int sqr[][]=new int[3][3];
		Chromosome tmpTop=pop.getPopulation().get(0);
		//For
		for(Chromosome cs:pop.getPopulation()) {
			//Checking the Sudoku Three Rules
			//Set our work Matrix
			setTemproryMat(fillMat(pop.getInfo_sudoku(),cs));
			//====================
			//Reset Just in case
			cs.setIllegal(0);
			cs.setLegal(0);
			//Now evaluate each one
			//int offset=0;
			boolean penltyClm,penltyRow,penltySqr;
			int dividerRow,dividerClm;
			int rowk, clmk, rowi, clmi;
			for(int i=0;i<9;i++) {
				for(int j=0;j<9;j++) {
					if(pop.getInfo_sudoku()[i][j]==0) {
						column=getValuesInColumn(j);
						row=getValuesInRow(i);
						sqr=getValuesInSquare(i,j);
						penltyClm=penltyRow=penltySqr=false;
						for(int k=0;k<9;k++) {
							//Column Pass & Penelty: Rule1
							if(column[k]==column[i]) {
								if(k!=i)
									penltyClm=true;
							}
							//Row Pass & Penelty: Rule2
							if(row[k]==row[j]) {
								if(k!=j)
									penltyRow=true;
							}
							//Square Pass & Penelty: Rule3
							if(i>3)
								dividerRow=((int)i/3);
							else
								dividerRow=1;
							if(j>3)
								dividerClm=((int)j/3);
							else
								dividerClm=1;
							rowk=(int)k/3;
							clmk=k%3;
							rowi=i%(3*dividerRow);
							clmi=j%(3*dividerClm);
							if(sqr[rowk][clmk]==sqr[rowi][clmi]) {
								if((rowk!=rowi)||(clmk!=clmi))
									penltySqr=true;
							}
						}
						if(penltyClm) {
							cs.addPenelty();
						}
						if(penltyRow){
							cs.addPenelty();
						}
						if(penltySqr) {
							cs.addPenelty();
						}
						if(!penltyClm && !penltyRow && !penltySqr){
							cs.addPass();
						}
						//offset++;
					}
				}
			}
			if((tmpTop.getLegal()-tmpTop.getIllegal())<(cs.getLegal()-cs.getIllegal())) {
				tmpTop=cs;
			}
		}
		setTheSolution(tmpTop);
		setMaxGoal(getTheSolution().getLegal()-getTheSolution().getIllegal());
		
	}
	private int[][] getValuesInSquare(int rw,int clm) {
		int sqr[][]=new int[3][3];
		int row=((int)rw/3)*3;
		int column=((int)clm/3)*3;
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				sqr[i][j]=getTemproryMat()[row+i][column+j];
			}
		}
		//
		return sqr;
	}
	private int[] getValuesInRow(int rw) {
		int row[]=new int[9];
		for(int i=0;i<9;i++) {
			row[i]=getTemproryMat()[rw][i];
		}
		//
		return row;
	}
	private int[] getValuesInColumn(int clm) {
		int column[]=new int[9];
		for(int i=0;i<9;i++) {
			column[i]=getTemproryMat()[i][clm];
		}
		//
		return column;
	}
	private int[][] fillMat(int tmp[][],Chromosome crm){
		int offset=0;
		int temproryMat[][]=new int[9][9];
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				if(tmp[i][j]!=0) {
					temproryMat[i][j]=tmp[i][j];
				}else {
					temproryMat[i][j]=crm.getValueAt(offset);
					offset++;
				}
			}
		}
		//we have full sudoku
		return temproryMat;
	}
	//GETTERS & SETTERS
	public Population getPopGA() {
		return popGA;
	}
	public void setPopGA(Population popGA) {
		this.popGA = popGA;
	}
	public int[][] getTemproryMat() {
		return temproryMat;
	}
	public void setTemproryMat(int temproryMat[][]) {
		this.temproryMat = temproryMat;
	}
	public RouletteWheel getRltwhl() {
		return rltwhl;
	}
	public void setRltwhl(RouletteWheel rltwhl) {
		this.rltwhl = rltwhl;
	}
	public int getMaxGoal() {
		return maxGoal;
	}
	public void setMaxGoal(int maxGoal) {
		this.maxGoal=maxGoal;
	}
	public Chromosome getTheSolution() {
		return theSolution;
	}
	public void setTheSolution(Chromosome theSolution) {
		this.theSolution=theSolution;
	}
	public int getGeneration() {
		return generation;
	}
}
