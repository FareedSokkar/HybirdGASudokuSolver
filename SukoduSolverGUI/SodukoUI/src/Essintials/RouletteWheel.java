package Essintials;

import java.util.Random;

public class RouletteWheel {
	private int sum;
	private int min;
	private int max;
	private int popSize;
	private Random rnd;
	//CONSTRUCTORS
	public RouletteWheel() {
		setSum(0);
		setMin(0);
		setMax(0);
		setPopSize(0);
		this.rnd=new Random();
		
	}
	//Functions
	public double getRandomProb() {
		return rnd.nextDouble();
	}
	public double calculateProb(int num) {
		if(getMin()<0) {
			return ((double)(num-(getMin()-1))/(getSum()-((getMin()-1)*(getPopSize()-1))));
		}else if(getMin()>0){
			return ((double)(num+getMin())/(getSum()+(getMin()*(getPopSize()-1))));
		}else {
			return ((double)(num+1)/(getSum()+(1*(getPopSize()-1))));
		}
	}
	//GETTERS & SETTERS
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getPopSize() {
		return popSize;
	}
	public void setPopSize(int popSize) {
		this.popSize = popSize;
	}
	
}
